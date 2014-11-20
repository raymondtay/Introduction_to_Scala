package movetofront

import org.scalatest._

class MoveToFrontSpec extends FlatSpec with Matchers {

    "A MoveToFront" should "be empty, when created" in {
        val q = new MoveToFront[Int]
        q.isEmpty should be (true)
    }

    "A MovetoFront" should "return 1 item randomly when 1 item is put into the bag" in {
        val q = new MoveToFront[Int]
        q.isEmpty should be (true)

        q.enqueue(42)
        q.isEmpty should be (false)

        val x = q.dequeue()
        x should be (42)
    }

    "A MoveToFront" should "return 1 item when duplicates are passed into the ADT" in {
        val q = new MoveToFront[Int]
        q.isEmpty should be (true)

        q.enqueue(42)
        q.isEmpty should be (false)
        
        q.enqueue(42)
        q.isEmpty should be (false)
    
        val x = q.dequeue()
        x should be (42)
        q.isEmpty should be (true)
    }

    "A MoveToFront" should "return 2 item in the order-of-placement even when duplicates are passed into the ADT" in {
        val q = new MoveToFront[Int]
        q.isEmpty should be (true)

        q.enqueue(42)
        q.enqueue(43)
        q.isEmpty should be (false)
        
        q.enqueue(42)
        q.enqueue(43)
        q.isEmpty should be (false)
    
        val x = q.dequeue()
        x should be (43)
        val y = q.dequeue()
        y should be (42)
        q.isEmpty should be (true)
    }

}

