package computation

/* 
 When we are writing code that needs to process data contains in one of these
 structures, we often don't care about the shape of the structure (Whether it's a tree
 or a list), or whether it's lazy or not, or provides efficient random access, etc
 
 For examle, if we have a structure full of integers and want to calculate their sum, we can
 use foldRight
 
 ints.foldRight(0)(_ + _)
*/

// In this, F[_] has a underscore and that indicates that F is not a type
// but a type constructor that takes one type parameter.
// Just like functions that take other functions as arguments are called higher-order
// functions, something like Foldable is a higher-order type constructor or a 
// higher-kinded type

trait Foldable[F[_]] {
    import Monoid._
    def foldRight[A,B](as: F[A])(z:B)(f: (A,B) => B) : B = foldMap(as)(f.curried)(EndoMonoid[B])(z)
    def foldLeft[A,B](as: F[A])(z:B)(f: (B,A) => B) : B = foldMap(as)(a => (b:B) => f(b,a))(dual(EndoMonoid[B]))(z)
    def foldMap[A,B](as: F[A])(f: A => B)(mb: Monoid[B]) : B = foldRight(as)(mb.id)((a,b) => mb.op(f(a), b))
    def concatenate[A](as: F[A])(m: Monoid[A]) : A = foldLeft(as)(m.id)(m.op)
    def toList[A](f: F[A]) : List[A] = foldLeft(f)(List.empty[A])((as,a) ⇒ as :+ a)
}

sealed trait Tree[+A]
case class Leaf[A](value: A) extends Tree[A]
case class Branch[A](left: Tree[A], right: Tree[A]) extends Tree[A]

object FoldableList extends Foldable[List] {
    override def foldRight[A,B](as: List[A])(z: B)(f: (A, B) ⇒ B): B = as.foldRight(z)(f)
    override def foldLeft[A,B](as: List[A])(z: B)(f: (B, A) ⇒ B) : B = as.foldLeft(z)(f)
    override def foldMap[A,B](as: List[A])(f: A ⇒ B)(m: Monoid[B]) : B = foldRight(as)(m.id)((a,b) => m.op(f(a), b))
    override def concatenate[A](as: List[A])(m: Monoid[A]) : A = foldLeft(as)(m.id)(m.op)
}

object FoldableOption extends Foldable[Option] {
    override def foldRight[A,B](as: Option[A])(z:B)(f: (A, B) ⇒ B) : B = as.foldRight(z)(f)
    override def foldLeft[A,B](as: Option[A])(z:B)(f: (B, A) ⇒ B) : B = as.foldLeft(z)(f)
    override def foldMap[A,B](as: Option[A])(f: A ⇒ B)(m: Monoid[B]) : B = foldRight(as)(m.id)((a,b) => m.op(f(a), b))
    override def concatenate[A](as: Option[A])(m: Monoid[A]) : A = foldLeft(as)(m.id)(m.op)
}

object FoldableTree extends Foldable[Tree] {

    // In Scala, argument evaluation is eager so we are going to eval all 
    // sub-trees on the left before proceeding on the right in that order.
    override def foldRight[A,B](ts: Tree[A])(z: B)(f: (A,B) => B) : B = ts match {
        case Leaf(a) => f(a, z) 
        case Branch(left, right) => foldRight(left)(foldRight(right)(z)(f))(f)
    }

    override def foldLeft[A,B](ts: Tree[A])(z: B)(f: (B, A) => B) : B = ts match {
        case Leaf(a) => f(z, a)
        case Branch(left, right) => foldLeft(left)(foldLeft(right)(z)(f))(f)
    }

    override def foldMap[A,B](ts: Tree[A])(f: A => B)(mb: Monoid[B]) : B = ts match {
        case Leaf(a) => f(a)
        case Branch(left, right) => mb.op( foldMap(left)(f)(mb), foldMap(right)(f)(mb) )
    }
}

