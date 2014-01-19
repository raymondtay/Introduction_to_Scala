/*
    A monad is an implementation of one of the minimal sets of monadic
    combinators, satisfying the laws of associativity and identity.
*/
trait Monad[F[_]] extends Functor[F] {
    // follow the types
    def map2[A,B,C](fa: F[A], fb: F[B])(f: (A, B) ⇒ C) : F[C] = flatMap(fa)(a ⇒ map(fb)(b ⇒ f(a,b)))

    def flatMap[A,B](fa: F[A])(f: A ⇒ F[B]) : F[B]

    def unit[A](a: ⇒ A): F[A]

    def map[A,B](fa: F[A])(f: A ⇒ B) : F[B] = flatMap(fa)(a ⇒ unit(f(a))) 

    def sequence[A](lma: List[F[A]]): F[List[A]] = lma.foldRight(unit(List[A]()))((fa, fas) ⇒ map2(fa, fas)(_ :: _))

    // `replicateM` is meant to fill a container with `ma` and its thread-safe owing to GenSeq's impl of `fill`
    def replicateM[A](n: Int, ma: F[A]): F[List[A]] = sequence(List.fill(n)(ma))

    def product[A,B](ma: F[A], mb: F[B]): F[(A, B)] = map2(ma, mb)((_, _))

    def traverse[A,B](la: List[A])(f: A ⇒ F[B]) : F[List[B]] = la.foldRight(unit(List[B]()))( (a, fbs) ⇒ map2(f(a), fbs)(_ :: _))

    def as[A,B](a: F[A])(b: B): F[B] = map(a)(_ ⇒ b)

    // `filterM` acts in a way like what `filter` might mean and the difference is that where `filter` is A => Boolean
    // the predicate function is actually A => F[Boolean] a.k.a "Kleisli Arrows" and we traverse a container of `A`s and only include the element
    // when it can fulfill the predicate
    def filterM[A](as: List[A])(pred: A ⇒ F[Boolean]): F[List[A]] =
        as match {
            case Nil ⇒ unit(Nil)
            case h :: t ⇒ flatMap(pred(h))( v ⇒ if (!v) filterM(t)(pred) else map(filterM(t)(pred))(h :: _ )) 
        }

    //def flatMapViaCompose[A,B](fa: F[A])(f: A ⇒ F[B]) : F[B] = compose((_:A) ⇒ fa)(f)

    def join[A](mma: F[F[A]]) : F[A] = flatMap(mma)(fa ⇒ map(fa)(a ⇒ a))

    def compose[A,B,C](f: A ⇒ F[B])(g: B ⇒ F[C]) : A ⇒ F[C] = a ⇒ flatMap(f(a))( b ⇒ g(b) )

}

case class Id[A](value : A) {
    def map[B](f: A ⇒ B) : Id[B] = Id(f(value))
    def flatMap[B](f: A ⇒ Id[B]) : Id[B] = f(value)
}

import StateObj._

object IntStateMonad extends Monad[({type IntState[A] = State[Int, A]})#IntState] {
    def unit[A](a: ⇒ A) : State[Int,A] = State(s ⇒ (a, s))
    def flatMap[A,B](sa: State[Int, A])(f: A ⇒ State[Int, B]) = sa flatMap f
}

object Monad {
    val optionM = 
        new Monad[Option] {
            override def compose[A,B,C](f: A ⇒ Option[B])(g: B ⇒ Option[C]) : A ⇒ Option[C] =
                a ⇒ f(a) map { case (b:B) ⇒ g(b).get }
            def unit[A](a: ⇒ A) : Option[A] = Some(a)
            def flatMap[A,B](oa: Option[A])(f: A ⇒ Option[B]) : Option[B] = 
                map(oa){a ⇒ (f(a) : @unchecked) match {case Some(v) ⇒ v}} // What is None of `B`??
        }

    val idM = 
        new Monad[Id] {
            override def compose[A,B,C](f: A ⇒ Id[B])(g: B ⇒ Id[C]) : A ⇒ Id[C] = 
                a ⇒ map(f(a))(x ⇒ g(x).value)
            def flatMap[A,B](ia: Id[A])(f: A ⇒ Id[B]) = ia.flatMap(f)
            def unit[A](a: ⇒ A) : Id[A] = Id(a)
        }
}

case class Reader[R,A](run: R ⇒ A)

object Reader {
    def readerMonad[R] = new Monad[({type f[x] = Reader[R,x]})#f] {
        def unit[A](a: ⇒ A) : Reader[R,A] = Reader(r ⇒ a)
        def flatMap[A,B](s: Reader[R,A])(f: A ⇒ Reader[R,B]): Reader[R,B] =
            Reader {
                r ⇒ val srb = f(s.run(r))
                    srb.run(r)
            }
    }
}

