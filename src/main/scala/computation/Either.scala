// The big idea of this chapter is that we can represent failures and exceptions with
// ordinary values, and we can write functions that abstract out common patterns of error
// handling and recovery. Option is not the only data type we could use for this purpose
// and although it gets used frequently, its rather simplistic. 
// One thing with Option is that it doesn't tell us very much about what went wrong
// in the case of an exceptional condition. All it can do is give us None indicating that there
// is no value to be had. 
import annotation._

object EitherObj {

    // Either has only two cases, just like Option. The essential difference is that
    // both cases carry a value. The Either data type represents, in a very general way, values
    // that can be one of two things. WE can say that it is disjoint union of two types.
    // When we use it to indicate success / failure, by convention the Left constructor
    // is reserved for the failure case.

    sealed trait Either[+E,+A] {
        def map[B](f: A => B ) : Either[E,B] = this match {
            case Left(a) => Left(a)
            case Right(a) => Right(f(a))
        }
        
        def flatMap[EE >: E, B](f: A => Either[EE,B]): Either[EE,B] = this match {
            case Left(e) => Left(e) 
            case Right(a) => f(a) 
        }
        
        def orElse[EE >: E, B >: A](f: => Either[EE,B]) : Either[EE,B] = this match {
            case Left(_)  => f
            case Right(a) => Right(a) // the variable 'a' is determined to be of type B i.e. B >: A
        }

        // suppressing warnings doesn't seem like a good idea
        def map2[EE>:E, B, C](b: Either[EE,B])(f: (A,B) => C) : Either[EE,C] = this match {
            case Right(a) => Right(f(a, (b : @unchecked) match { case Right(v) => v }))
            case Left(e)  => Left(e)
        }
            
        def map2ViaComprehension[EE>:E, B, C](b: Either[EE,B])(f: (A,B) => C) : Either[EE,C] = 
        for {
            a <- this
            b1 <- b
        } yield f(a,b1)

        def sequence[E,A](es: List[Either[E,A]]) : Either[E, List[A]] = 
            es match {
                case Nil => Right(Nil)
                case h :: t => h flatMap { a => sequence(t) map (a :: _) }
            }

        def traverse[AA >:A, E,B](a: List[AA])(f: AA => Either[E,B]) : Either[E, List[B]] = a match {
            case Nil => Right(Nil)
            case h :: t => (f(h) map2 traverse(t)(f))(_::_)  
        }
    }
    case class Left[+E](value: E) extends Either[E, Nothing]
    case class Right[+A](value: A) extends Either[Nothing,A]
}

