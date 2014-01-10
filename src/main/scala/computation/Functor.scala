trait Functor[F[_]] {
    def map[A,B](fa: F[A])(f: A ⇒ B) : F[B]

    def distribute[A,B](m: F[(A,B)]) : (F[A], F[B]) = (map(m)(_._1), map(m)(_._2))
}

object Functor {
    val listFunctor = 
        new Functor[List] {
            def map[A,B](as: List[A])(f: A ⇒ B) : List[B] = as map f
        }

}
    
/*
    Laws help an interface form a new semantic level whose algebra may be reasoned about 
    independently of the concrete instances. For instance, when we took the product of a 
    Monoid[A] and a Monoid[B] to form a Monoid[(A,B)] the monoid laws let us conclude 
    that the fused monoid operation was also associative. We did not need to know anything
    about A and B to conclude this.

    More concretely, we often rely on laws when writing various combinators derived from 
    the functions of some abstract interface like Functor.
*/
         
