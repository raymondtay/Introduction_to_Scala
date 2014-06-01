
sealed trait Tree[+A] {
  def size[U >: A](tree: Tree[U], acc: Int) : Int = 
    tree match {
        case Leaf(a) => acc + 1
        case Node(left, right) => size(left, acc) + size(right, acc)
  }
}

case class Leaf[A](value: A) extends Tree[A]
case class Node[A](left: Tree[A], right: Tree[A]) extends Tree[A]

object Tree {
  def insert(value: Int, tree: Tree[Int]) = {
    tree match {
        case Node(Leaf(l), Leaf(r)) if value > l => Node(Node(Leaf(l), Leaf(value)), Leaf(r))
        case Node(Leaf(l), Leaf(r)) if value > r => Node(Node(Leaf(value), Node(Leaf(l), Leaf(r))))
    }
  }
  def size[A](tree: Tree[A], acc: Int) : Int = 
    tree match {
        case Leaf(a) => acc + 1
        case Node(left, right) => size(left, acc) + size(right, acc)
  }

  def max[A](tree: Tree[A])(implicit ord: A => Ordered[A]) : A = 
    tree match {
        case Leaf(a) => a
        case Node(left, right) => {
            val lhs = max(left)
            val rhs = max(right)
            if (ord(lhs) > rhs) lhs else rhs
        }
    }

  def map[A,B](tree: Tree[A])(f: A => B) : Tree[B] = 
    tree match {
        case Leaf(a) => Leaf(f(a))
        case Node(left, right) => {
            val lhs = map(left)(f)
            val rhs = map(right)(f)
            Node(lhs, rhs)
        }
    }

}

