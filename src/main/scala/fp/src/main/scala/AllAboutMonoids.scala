package monoidsonly

import scala.language._

trait Monoid[A] {
    def mappend(a: A, b: A ) : A
    def mzero : A
}

object Monoid {

	implicit object IntMonoid extends Monoid[Int] {
	    def mappend(a: Int, b: Int) : Int = a + b 
	    def mzero : Int = 0
	}

	implicit object StringMonoid extends Monoid[String] {
	    def mappend(a: String, b: String) : String = a + b
	    def mzero : String = ""
	}

}
object SampleMonoidFns {

  // Exercise 10.17 in Chapter 10 of the book
  def functionMonoid[A,B](b: Monoid[B]) : Monoid[A => B] =
    new Monoid[A => B] {
      def mzero : A => B = (a:A) => b.mzero
      def mappend(f : A => B, g : A => B): A => B = 
        (a: A) => b.mappend(
                    b.mappend(
                        f(a), f(a)), 
                        b.mappend(g(a), g(a)
                    ))
    }

  def sum[A: Monoid](xs: List[A]) = {
      val m = implicitly[Monoid[A]]
      xs.foldLeft(m.mzero)(m.mappend)
  }

}

object FoldLeftList {
    def foldLeft[A,B](xs: List[A], z: B, f: (B, A) => B) = xs.foldLeft(z)(f)

    def sumViaFoldLeft[A : Monoid](xs: List[A]) : A = {
        val m = implicitly[Monoid[A]]
        FoldLeftList.foldLeft(xs, m.mzero, m.mappend)
    }
}

trait FoldLeft[F[_]] {
    def foldLeft[A,B](xs: F[A], z: B, f: (B, A) => B) : B
}

object FoldLeft {
    implicit val foldLeftList = new FoldLeft[List] {
        def foldLeft[A,B](xs: List[A], z: B, f: (B, A) => B) = xs.foldLeft(z)(f)
    }
}

object AbstractionOverContainerOverOps {

    // this function demonstrates the conflated use of power of lifting 
    // both the container and operations on that container into separate 
    // pieces of functionality => greater code reuse 
    //                         => the developer, you, write less tests
    def sum[C[_] : FoldLeft, A: Monoid](xs: C[A]) : A = {
        val m = implicitly[Monoid[A]]
        val f = implicitly[FoldLeft[C]]
        f.foldLeft(xs, m.mzero, m.mappend)
    }

}

// this typeclass allows me the capability to "chain"
// multiple monoids together and its operation is predictable
trait MonoidOp[A] {
    val F : Monoid[A]
    val value : A 
    def |+|(a2: A) = F.mappend(value, a2)
}

object MonoidOp {

    implicit def toMonoidOp[A: Monoid](a: A) : MonoidOp[A] = new MonoidOp[A] {
        val F = implicitly[Monoid[A]]
        val value = a
    }

}

