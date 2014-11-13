package randomq

import org.scalatest._

class RandomQueueSpec extends FlatSpec with Matchers {

    "A RandomQueue" should "be empty, when created" in {
        val q = new RandomQueue[Int]
        q.isEmpty should be (true)
    }

    "A RandomQueue" should "return 1 item randomly when 1 item is put into the bag" in {
        val q = new RandomQueue[Int]
        q.isEmpty should be (true)

        q.enqueue(42)
        q.isEmpty should be (false)
    }

    "A RandomQueue" should "not return anything when 1 item is put into and removed subsequently from the bag" in {
        val q = new RandomQueue[Int]
        q.isEmpty should be (true)

        q.enqueue(342)
        q.dequeue()
        q.isEmpty should be (true)
    }

}

