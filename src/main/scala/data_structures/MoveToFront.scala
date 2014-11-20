package movetofront


import scala.io._
import Source._

// Read in a sequence of characters from somewhere
// and maintain the characters in a linked list with no duplicates
// When you read in a previously unseen character, insert it at 
// the front of the list. When you read in a duplicate character
// delete it from the list and reinsert at the beginning.

class MoveToFront[Item] {

    private[this] var list = List[Item]()
    def isEmpty: Boolean = list.isEmpty
    
    def enqueue(item: Item) = {
        list = list.filter(_ != item)
        list = item :: list
    }

    def dequeue() : Item = {
        val x = list.head
        list = list.tail
        x
    } 

    def dumpcontents = {println("\ndumping begin");list map println;println("\ndumping end");}
}


