package BST 

sealed trait Tree[+T]
case object Tip extends Tree[Nothing] 
case class Node[T](left: Tree[T], right: Tree[T], value: T) extends Tree[T]

object Tree {
    def insert[T <% Ordered[T]](x: T, tree: Tree[T]) : Tree[T] = tree match {
        case Tip => Node(Tip, Tip, x)
        case Node(left, right, value) if x < value => Node(insert(x, left), right, value)
        case Node(left, right, value) => Node(left, insert(x, right), value)
    }

    def search[T <% Ordered[T]](x: T, tree: Tree[T]) : Boolean = tree match {
        case Tip => false
        case Node(left, right, value) if x < value => search(x, left)
        case Node(left, right, value) if x > value => search(x, right)
        case _ => true
    }

    def main(args: Array[String]) = {
        if (args.length == 0) Tip
        else {
            populateTree(Tip, (args.map(_.toInt)).toList)
        }
    }

    def populateTree(tree: Tree[Int], items: Seq[Int]) : Tree[_] =
        items match {
            case h :: t => populateTree(insert(h, tree), t)
            case Nil => tree
        }
}

