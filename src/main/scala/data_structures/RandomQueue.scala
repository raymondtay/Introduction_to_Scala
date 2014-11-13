package randomq

class RandomQueue[Item] {
    private[this] var items = collection.mutable.ListBuffer[Item]()
    private[this] var size = items.size

    def isEmpty : Boolean = items.isEmpty
    def enqueue(item : Item) {
        size += 1
        items += item 
    }

    def dequeue() : Item = {
        val t = items.remove(0)
        size = size - 1
        t
    }

    def sample() : Item = {
        import scala.util.Random._
        val index = nextInt(size)
        items(index)
    }

}

