sealed trait TailRec[A] {
    def map[B](f: A ⇒ B ) : TailRec[B] = 
        flatMap(a ⇒ Return(f(a)))

    def flatMap[B](f: A ⇒ TailRec[B]): TailRec[B] = 
        this match {
            case FlatMap(x, g) ⇒ FlatMap(x, (a: Any) ⇒ g(a) flatMap f)
            case x ⇒ FlatMap(x, f)
        }
}
case class Return[A](a: A ) extends TailRec[A]

case class Suspend[A](resume: () ⇒ TailRec[A]) extends TailRec[A]

case class FlatMap[A,B](sub: TailRec[A], k: A ⇒ TailRec[B]) extends TailRec[B]

