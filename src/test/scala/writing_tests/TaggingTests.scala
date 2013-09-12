import org.scalatest.FlatSpec
import scala.collection.mutable.Stack

class StackSpec extends FlatSpec {

    "A stack" should "pop values in last-in-first-out order" in {
        val stack = new Stack[Int]()
        stack.push(0)
        stack.push(1)
        assert(stack.pop() == 1)
        assert(stack.pop() == 0)
    }

    it should "throw NoSuchElementException if an empty stack is popped" in {
        val es = new Stack[Int]
        intercept[NoSuchElementException] {
            es.pop()
        }
    }

    // To ignore a test, replace `it` with `ignore`
    ignore should "(IGNORE) throw NoSuchElementException if an empty stack is popped" in {
        val es = new Stack[Int]
        intercept[NoSuchElementException] {
            es.pop()
        }
    }

    // To tag tests, here's how
    import org.scalatest.Tag

    object SlowTest extends Tag("com.slowtest")
    object FastTest extends Tag("com.fasttest") // this is unnecessary since we all LOVE fast tests !

    "The scala language" must "add correctly" taggedAs(SlowTest) in {
        val sum = (1 to 10000).toList.foldLeft(0)(_ + _)
        assert(sum === 50005000)
    }
}

