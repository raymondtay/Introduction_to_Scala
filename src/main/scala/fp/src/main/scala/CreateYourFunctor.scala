package tryouts

import scala.language._

trait Functor[F[_]] { self =>

    def map[A,B](fa: F[A])(f: A => B) : F[B]

}

trait Apply[F[_]] extends Functor[F] { self => 
    // You can think of <*> as a sort of beefed up "fmap".
    // Whereas "fmap" takes a function and a functor and applies
    // the function inside the function value <*> takes a functor that
    // has a function in it and another function and extracts that function from the first function
    // and then maps it over the second one.
    def ap[A,B](fa: => F[A])(f: => F[A => B]) : F[B]
}

