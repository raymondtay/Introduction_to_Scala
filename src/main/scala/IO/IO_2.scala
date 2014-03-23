package iomonad

/**
    Reifying control flow as data constructors
    
    The answer is simple. Instead of letting program control just flow
    through with function calls, we explicitly bake into our data type the 
    control flow that we want to support. For example, instead of making flatMap
    a method that constructs a new IO in terms of run, we can just make it a data
    constructor of the IO data type. Then the interpreter can be a tail-recursive
    loop. Whenever it encounters a constructor like FlatMap(x, y) it will simply interpret
    x and then call k on the result.
*/

sealed trait IO[A] {
    def flatMap[B](f: A ⇒ IO[B]) : IO[B] = FlatMap(this, f)
    def map[B](f: A ⇒ B) : IO[B] = FlatMap(this, (a:A) ⇒ Return(f(a)) )

    def run[A](io: IO[A]) : A = io match {
        case Return(a)     ⇒ a
        case Suspend(r)    ⇒ run(r())
        case FlatMap(x, f) ⇒ x match {
            case Return(a)     ⇒ run(f(a))
            case Suspend(r)    ⇒ run(FlatMap(r(), f))
            case FlatMap(y, g) ⇒ run(FlatMap(y, (a: Any) ⇒ FlatMap(g(a), f)))
        }
    }
}

// A pure computation that immediately returns an A without any further steps. When run sees this 
// constructor it knows the computation has finished
case class Return[A](a: A) extends IO[A]

// A suspension of the computation where resume is a funciton that takes no arguments but has some effect
// and yields the next step. When run sees this constructor, it can execute resume and continue interpreting the
// the resulting IO action.
case class Suspend[A](resume: () ⇒ IO[A]) extends IO[A]

// A composition of two steps. Reifies flatMap as a data constructor rather than a function. When run sees this, it should
// first process the sub-computation sub and then continue withi k once sub reaches a Return.
case class FlatMap[A,B](sub: IO[A], k: A ⇒ IO[B]) extends IO[B]

