package laziness

// 
// There's a lot of likeness to the one in ../data_structures/Stream.scala
// and its due to my lack of organization over these source codes (i'll have to change them somehow)
// 
sealed trait Stream[+A] {
    def headOption : Option[A] = this match {
        case Cons(h, t) => Some(h())
        case Empty => None
    }

    def foldRight[B](z: => B)(f: (A, => B) => B) : B = 
        this match {
            case Cons(h, t) => f(h(), t().foldRight(z)(f))
            case _ => z
        }

    def take(n: Int) : Stream[A] = {
        if (n > 0) { this match {
            case Cons(h, t) if n == 1 => Stream.cons(h(), Stream.empty)
            case Cons(h, t) => Stream.cons(h(), t().take(n -1))
            }
        } else Stream()
    }

    // `unfold` takes an initial state, and a function for producing both 
    // the next state and the next value in the generated stream.
    def unfold[A,S](z: S)(f: S => Option[(A,S)]): Stream[A] = 
        f(z) match {  
            case Some((a,s)) => Stream.cons(a, unfold(s)(f)) 
            case None => Stream()
        }

    def drop(n: Int) : Stream[A] = 
        if (n > 0) this match {
            case Cons(h, t) => t().drop(n-1)
            case Empty => this
        } else Stream() 
    def constant[A](a: A) : Stream[A] = Stream.cons(a, constant(a))
    def flatMap[B](f: A => Stream[B]) : Stream[B] = foldRight(Stream.empty[B])((h, t) => f(h) append t)
    def filter(f: A => Boolean) : Stream[A] = foldRight(Stream.empty[A])((h,t) => if (f(h)) Stream.cons(h,t) else t)
    def append[B>:A](x: => Stream[B]) : Stream[B] = foldRight(x)((h,t) => Stream.cons(h,t))
    def map[B](f: A => B) : Stream[B] = foldRight(Stream.empty[B])((h,t) => Stream.cons(f(h), t))
    def takeWhile(p: A => Boolean) = foldRight(Stream.empty[A])((h,t) => if(p(h)) Stream.cons(h, t) else Stream.empty)
    def forAll(p: A => Boolean) : Boolean = foldRight(false)((a,b) => if (!p(a)) false else b)
    def exists(p: A => Boolean) : Boolean = foldRight(false)((a,b) => p(a) || b)
}
case object Empty extends Stream[Nothing]
case class Cons[+A](h: () => A, t : () => Stream[A]) extends Stream[A]
object Stream  {
    // `from` generates a sequence of numbers starting from `n` i.e. n, n+1, n+2, ... 
    def from(n: Int) : Stream[Int] = cons(n, from(n+1))
        
    def cons[A](hd: => A, tl: => Stream[A]) : Stream[A] = {
        lazy val head = hd
        lazy val tail = tl
        Cons(() => head, () => tail)
    }
    def empty[A]: Stream[A] = Empty
    def apply[A](as: A*) : Stream[A] = if (as.isEmpty) empty else cons(as.head, apply(as.tail: _*))

    val ones : Stream[Int] = cons(1, ones)
}

