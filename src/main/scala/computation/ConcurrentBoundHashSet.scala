import scala.concurrent._
import duration._
import java.util.{Collections,HashSet}
import java.util.concurrent.Semaphore
import scala.reflect._
import scala.collection.JavaConversions._

class BoundedHashSet[A](bound: Int) {
    val sem: Semaphore = new Semaphore(bound)
    val set = Collections.synchronizedSet(new HashSet[A]())

    def size() = {
        sem.acquire()
        set.size()
        sem.release()
    }

    def add(o : A) : Boolean = {
        sem.acquire()
        var wasAdded = false
        try {
            wasAdded = set.add(o)
            return wasAdded
        } finally {
            if (!wasAdded) sem.release()
        }
    }

    def remove[A](o : A) : Boolean = {
       val removed : Boolean = set.remove(o)
        if (removed) sem.release()
        return removed 
    }
}

object ConcurrentAccessBoundedHashSet extends App {
    val set = new BoundedHashSet[Int](1000)
    import ExecutionContext.Implicits.global 
    // When it goes from 1000 to 10,000 it becomes unresponsive
    val futures = (1 to 1000) map { v => Future { if (v % 2 == 0) set.add(v) else set.remove(v+1) } } 

    val computations = Future.sequence(futures)
    Await.result(computations, Duration.Inf)
    println(s"Size of set is ${set.size()}")
}
 
