// 
// Higher kinded abstractions that can be built from
// simpler abstractions are commonly referred to as combinators
// as the word suggests that these functions combine other 
// intrinsic functions.
// Specifically, Applicatives are simply another kind of combinator
// that uses `map2` , `apply` and `unit` to build higher abstractions
// 
trait Applicative[F[_]] extends Functor[F] {

    def apply[A,B](fab: F[A ⇒ B])(fa: F[A]): F[B] = map2(fab, fa)( (ab, a) ⇒ ab(a) )

    //
    // `map2` is another combinator example where we only used
    // `apply`, `unit` ( two abstractions we defined in here )
    // and lastly `curried` which is inherent in Scala functions where the
    // arity is greater than 1.
    //
    def map2[A,B,C](fa: F[A], fb:F[B])(f: (A,B) ⇒ C): F[C] = {
        def cf = (f.apply _).curried 
        apply(apply(unit(cf))(fa))(fb)
    }

    // `map3` is again implemented interms of `apply` and `unit` 
    def map3[A,B,C,D](fa: F[A], fb: F[B], fc: F[C])(f: (A, B, C) ⇒ D) : F[D] = {
        def cf = (f.apply _).curried
        apply(apply(apply(unit(cf))(fa))(fb))(fc)
    }

    // Similarly, `map4` is again implemented interms of `apply` and `unit` 
    def map4[A,B,C,D,E](fa: F[A], fb: F[B], fc: F[C], fd: F[D])(f: (A, B, C, D) ⇒ E) : F[E] = {
        def cf = (f.apply _).curried
        apply(apply(apply(apply(unit(cf))(fa))(fb))(fc))(fd)
    }

    def join[A](mma: F[F[A]]) : F[A] = flatMap(mma)(fa ⇒ fa)

    def flatMap[A,B](fa: F[A])(f: A ⇒ F[B]) : F[B] 

    def unit[A](a: ⇒ A) : F[A]

    def map[A,B](fa: F[A])(f: A ⇒ B): F[B] = map2(fa, unit(()))((a, _) ⇒ f(a))

    // `traverse` is an example of a combinator
    def traverse[A,B](as: List[A])(f: A ⇒ F[B]) : F[List[B]] = 
        as.foldRight(unit(List[B]()))((a, fbs) ⇒ map2(f(a), fbs)(_ :: _))

    def sequence[A](fas: List[F[A]]) : F[List[A]] = 
        fas.foldRight(unit(List[A]()))( (a, fas) ⇒ map2(a, fas)(_ :: _) )

    def replicateM[A](n: Int, fa: F[A]): F[List[A]] = sequence(List.fill(n)(fa))

    def product[A,B](fa: F[A], fb: F[B]): F[(A,B)] = map2(fa, fb)((_, _))
    
}

// brief explanation is in order.
// avoiding namespace clashing as there's another `Monad` in `Monad.scala` so we named it `Monad2`
// since applicatives derive from functors, and now monads derive from applicatives
// tells us that monads are actually applicative functors
trait Monad2[F[_]] extends Applicative[F] {

    // the functions `flatMap` and `join` are implemented in terms of one another
    def flatMap[A,B](fa: F[A])(f: A ⇒ F[B]) : F[B] = join(map(fa)(f))

    override def join[A](mma: F[F[A]]) : F[A] = flatMap(mma)(fa ⇒ fa)

    def compose[A,B,C](f: A ⇒ F[B], g: B ⇒ F[C]): A ⇒ F[C] = a ⇒ flatMap(f(a))(g)

    override def map[A,B](fa: F[A])(f: A ⇒ B): F[B] = flatMap(fa)(a ⇒ unit(f(a)))

    override def map2[A,B,C](fa: F[A], fb: F[B])(f: (A,B) ⇒ C): F[C] = 
        flatMap(fa)(a ⇒ map(fb)(b ⇒ f(a,b)))
}

