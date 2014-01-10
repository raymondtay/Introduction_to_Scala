trait Monad[F[_]] extends Functor[F] {
    // follow the types
    def map2[A,B,C](fa: F[A], fb: F[B])(f: (A, B) ⇒ C) : F[C] = flatMap(fa)(a ⇒ map(fb)(b ⇒ f(a,b)))
    def flatMap[A,B](fa: F[A])(f: A ⇒ F[B]) : F[B]
    def unit[A](a: ⇒ A): F[A]
    def map[A,B](fa: F[A])(f: A ⇒ B) : F[B] = flatMap(fa)(a ⇒ unit(f(a))) 

    def sequence[A](lma: List[F[A]]): F[List[A]] = lma.foldRight(unit(List[A]()))((fa, fas) ⇒ map2(fa, fas)(_ :: _))

    def replicateM[A](n: Int, ma: F[A]): F[List[A]] = sequence(List.fill(n)(ma))

    def product[A,B](ma: F[A], mb: F[B]): F[(A, B)] = map2(ma, mb)((_, _))

    def traverse[A,B](la: List[A])(f: A ⇒ F[B]) : F[List[B]] = la.foldRight(unit(List[B]()))( (a, fbs) ⇒ map2(f(a), fbs)(_ :: _))

    def filterM[A](as: List[A])(pred: A ⇒ F[Boolean]): F[List[A]] =
        as match {
            case Nil ⇒ unit(Nil)
            case h :: t ⇒ flatMap(pred(h))( v ⇒ if (!v) filterM(t)(pred) else map(filterM(t)(pred))(h :: _ )) 
        }
}

object Monad {
    val optionM = 
        new Monad[Option] {
            def unit[A](a: ⇒ A) : Option[A] = Some(a)
            def flatMap[A,B](oa: Option[A])(f: A ⇒ Option[B]) : Option[B] = 
                map(oa){a ⇒ (f(a) : @unchecked) match {case Some(v) ⇒ v}} // What is None of `B`??
        }
}

