package graph

class Bag[Item] {
    import scala.collection.mutable.LinkedList
    private[this] var ll = new LinkedList[Item]()
  
    def isEmpty : Boolean = ll.head == null
    def size  = ll.size
    def add(item : Item) : Unit = ll :+ item
    def iterator : Iterator[Item] = ll.toIterator
    def elements  = ll toSeq
}

object Bag {
    def apply[A](items : A*) = {
        val b = new Bag[A]
        for(item ← items) b add item
        b
    }
}
