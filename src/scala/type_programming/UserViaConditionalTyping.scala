import java.util.concurrent.locks._

// Doesn't appear to make sense to create an abstraction
// to allow users to receive the status of the acquiring or
// releasing a lock over an object because ...
//
trait LockStrategy[L <: Lock] {
    protected val lck : Lock = null
    def lock : Unit
    def unlock : Unit
}
trait AllowReentrancy extends LockStrategy[ReentrantLock] {
    override val lck = new ReentrantLock
    def lock = lck.lock
    def unlock = lck.unlock
}

// Didn't want to expose my locking strategy to the 
// 'User' object so its lodged into 'Atomics' instead
trait Atomics {
    self: LockStrategy[_] =>

    // the problem is that the assignment 
    // is never revealed here.
    def atomicAssign[A](f: => Unit) = 
        try {
            lock
            f
        } finally {
            unlock
        }
} 

trait UseAtomics {
    type Use[Atomics <: Atom, NoAtomics <: Atom, Atom] <: Atom
}
class ToUse extends UseAtomics {
    type Use[Atomics <: Atom, NoAtomics <: Atom, Atom] = Atomics
}
class NoUse extends UseAtomics {
    type Use[Atomics <: Atom, NoAtomics <: Atom, Atom] = NoAtomics
}

class User {
    protected var first = "XX"
    protected var last  = "XX"
    
    def getFirstName = first
    def getLastName  = last
    def setFirstName(s: String) = {
        this.first = s
    }
    def setLastName(s: String) = {
        this.last = s
    }
}

trait Refinement[A <: User] {
    self: A with Atomics =>

    override def setFirstName(s: String) = {
        def f = this.first = s
        atomicAssign(f)
    }
    override def setLastName(s: String) = {
        def f = this.last = s
        atomicAssign(f)
    }        
}
  
object TestUser extends App {
    type UserA[A <: UseAtomics] = A#Use[User with Atomics with AllowReentrancy, User, Any]
    val user :UserA[ToUse] = new User with Atomics with AllowReentrancy with Refinement[User] 
    /*
        without the Refinement type trait, this is how we would have wrote this
    val user :UserA[ToUse] = new User with Atomics with AllowReentrancy {
                                override def setFirstName(s: String) = {
                                    def f = this.first = s
                                    atomicAssign(f) 
                                }
                                override def setLastName(s: String) = {
                                    def f = this.last = s
                                    atomicAssign(f) 
                                }}
    */
    val user2: UserA[NoUse] = new User

    user.setFirstName("Ray")
    user.setLastName("Tay")

    user2.setFirstName("Patrick")
    user2.setLastName("Tay")

    println(s"first: ${user.getFirstName}, last: ${user.getLastName}")
    println(s"first: ${user2.getFirstName}, last: ${user2.getLastName}")
}

 
   
