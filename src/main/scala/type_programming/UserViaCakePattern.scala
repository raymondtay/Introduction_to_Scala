import java.util.concurrent.locks._

trait OLockStrategy[L <: java.util.concurrent.locks.Lock] {
    protected val lck : java.util.concurrent.locks.Lock = null
    def lock : Unit
    def unlock : Unit
}

trait OAllowReentrancy extends OLockStrategy[ReentrantLock] {
    override val lck = new ReentrantLock
    def lock = lck.lock
    def unlock = lck.unlock
}

// Didn't want to expose my locking strategy to the 
// 'User' object so its lodged into 'Atomics' instead
trait OAtomics {
    self: OLockStrategy[_] =>

    def atomicAssign[A](f: => Unit) = 
        try {
            lock
            f
        } finally {
            unlock
        }
} 

class OUser {
    self: Atomics =>

    private var first = "XX"
    private var last  = "XX"
    
    def getFirstName = first
    def getLastName  = last
    def setFirstName(s: String) = {
        def f = this.first = s
        atomicAssign(f)
    }
    def setLastName(s: String) = {
        def f = this.last = s
        atomicAssign(f)
    }
}
 
object OTestUser extends App {
    val user = new User with Atomics with AllowReentrancy 
    user.setFirstName("Ray")
    user.setLastName("Tay")

    println(s"first: ${user.getFirstName}, last: ${user.getLastName}")
}

 
   

