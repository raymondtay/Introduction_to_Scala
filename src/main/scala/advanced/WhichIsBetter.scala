import com.typesafe.config._
import scala.util.Random._
import akka.actor._
import java.util.concurrent._
import org.apache.commons.jcs.JCS
import org.apache.commons.jcs.access._

case class Record(i: Int, d: Double, f: Float, l: Long)  
object Reaper {
    case class WatchMe(x: ActorRef) 
}
class Reaper(src: ArrayBlockingQueue[Record],dest: ArrayBlockingQueue[Record], now : Long) extends Actor {
  import Reaper._

  def this() = this(null, null, System.currentTimeMillis)

  // Keep track of what we're watching
  val watched = collection.mutable.ArrayBuffer.empty[ActorRef]
 
  // Derivations need to implement this method.  It's the
  // hook that's called when everything's dead
  def allSoulsReaped() {
    println(s"1.How fast was it? ${(System.currentTimeMillis - now)} milliseconds")
    (src == null, dest == null) match {
        case (true, true) => 
        case (false, false) => println(s"2.Both datasets matched? ${dest.size - src.size == dest.size}")
    }
    context.system.shutdown
  }
 
  // Watch and check for termination
  final def receive = {
    case WatchMe(ref) =>
      context.watch(ref)
      watched += ref
    case Terminated(ref) =>
      watched -= ref
      if (watched.isEmpty) allSoulsReaped()
  }
}

object OneProducerMultipleConsumerViaJCSNActors extends App {
    val size  = 4000000
    val src = new ArrayBlockingQueue[Record](size)
    import scala.concurrent.ExecutionContext.Implicits.global 
    import Reaper._
    val count : java.util.concurrent.atomic.AtomicInteger = new java.util.concurrent.atomic.AtomicInteger(0)

    class PullData(val cache : CacheAccess[Int,Record]) extends Actor {
        override def preStart() = { Thread.sleep(100); self ! "pull" }
        def receive = {
            case "pull" =>
                if (count.get == 4000000) context.stop(self) 
                src.poll match {
                    case x  if x != null  => cache.put(count.incrementAndGet, x) ;self ! "pull"
                    case _ => Thread.sleep(100); self ! "pull"
                }
        }
    }

    val as = ActorSystem("Test")
    val now = System.currentTimeMillis
    val reaper = as.actorOf(Props(new Reaper(src,src,now)))
    val cache : CacheAccess[Int,Record] = JCS.getInstance("test")
    Seq.fill(8){ val r = as.actorOf(Props(new PullData(cache))) ; reaper ! WatchMe(r) }

    new Iterator[Record] {
        var curr = 0
        def hasNext = if (curr == 4000000) false else {curr = curr + 1; true}
        def next = Record(nextInt, nextDouble, nextFloat, nextLong)
    }.toStream.foreach{ record => src.put(record) }

}

object OneProducerMultipleConsumerViaConcurrentLinkedQueueNDistributedActors extends App {
    val size  = 4 * 1000 * 1000
    val src = new ConcurrentLinkedQueue[Record]()
    val count : java.util.concurrent.atomic.AtomicInteger = new java.util.concurrent.atomic.AtomicInteger(0)
    val consumedCount : java.util.concurrent.atomic.AtomicInteger = new java.util.concurrent.atomic.AtomicInteger(0)
    import scala.concurrent.ExecutionContext.Implicits.global 
    import Reaper._

    class PullData extends Actor {
        val cup = new ConcurrentLinkedQueue[Record]()
        override def preStart() = { 
            Seq.fill(2)(context.actorOf(Props(new IConsumer).withDispatcher("my-dispatcher"))) map { reaper ! WatchMe(_) }
            Thread.sleep(100)
            println(s"\n\tDrinker actor has started!")
            self ! "pull" 
        }
        def receive = {
            case "pull" =>
                if (count.get == 4 * 1000 * 1000) context.stop(self) 
                src.poll match {
                    case x  if x != null  => cup.add(x); count.incrementAndGet; self ! "pull"
                    case _ => Thread.sleep(100); self ! "pull"
                }
        }
	    class IConsumer extends Actor {
            val iconsume = new ConcurrentLinkedQueue[Record]()
	        override def preStart() = { Thread.sleep(100); self ! "pull" } 
	        var count = 0 
	        def receive = {
	            case "pull" => 
	                if (consumedCount.get == 4 * 1000 * 1000) context.stop(self) 
	                cup.poll match {
	                    case x  if x != null => iconsume.add(x); consumedCount.incrementAndGet; self ! "pull"
	                    case _ => Thread.sleep(100); self ! "pull"
	                }
	        }
	    }

    }
    val config = ConfigFactory.load("application")
    val as = ActorSystem("Test", config)
    val now = System.currentTimeMillis
    val reaper = as.actorOf(Props(new Reaper()))
    Seq.fill(8){ val r = as.actorOf(Props(new PullData)); reaper ! WatchMe(r) }

    new Iterator[Record] {
        var curr = 0
        def hasNext = if (curr == 4 * 1000 * 1000) false else {curr = curr + 1; true}
        def next = Record(nextInt, nextDouble, nextFloat, nextLong)
    }.toStream.foreach{ record => src.add(record) }

}

object OneProducerMultipleConsumerViaArrayBlockingQueueNDistributedActors extends App {
    val size  = 4 * 1000 * 1000
    val src = new ArrayBlockingQueue[Record](size)
    val count : java.util.concurrent.atomic.AtomicInteger = new java.util.concurrent.atomic.AtomicInteger(0)
    val consumedCount : java.util.concurrent.atomic.AtomicInteger = new java.util.concurrent.atomic.AtomicInteger(0)
    import scala.concurrent.ExecutionContext.Implicits.global 
    import Reaper._

    class PullData extends Actor {
        val cup = new ArrayBlockingQueue[Record](4 * 1000 * 1000)
        override def preStart() = { 
            Seq.fill(2)(context.actorOf(Props(new IConsumer).withDispatcher("my-dispatcher"))) map { reaper ! WatchMe(_) }
            Thread.sleep(100)
            println(s"\n\tDrinker actor has started!")
            self ! "pull" 
        }
        def receive = {
            case "pull" =>
                if (count.get == 4 * 1000 * 1000) context.stop(self) 
                src.poll match {
                    case x  if x != null  => cup.put(x); count.incrementAndGet; self ! "pull"
                    case _ => Thread.sleep(100); self ! "pull"
                }
        }
	    class IConsumer extends Actor {
            val iconsume = new ArrayBlockingQueue[Record](4 * 1000 * 1000)
	        override def preStart() = { Thread.sleep(100); self ! "pull" } 
	        var count = 0 
	        def receive = {
	            case "pull" => 
	                if (consumedCount.get == 4 * 1000 * 1000) context.stop(self) 
	                cup.poll match {
	                    case x  if x != null => iconsume.put(x); consumedCount.incrementAndGet; self ! "pull"
	                    case _ => Thread.sleep(100); self ! "pull"
	                }
	        }
	    }

    }
    val config = ConfigFactory.load("application")
    val as = ActorSystem("Test", config)
    val now = System.currentTimeMillis
    val reaper = as.actorOf(Props(new Reaper()))
    Seq.fill(8){ val r = as.actorOf(Props(new PullData)); reaper ! WatchMe(r) }

    new Iterator[Record] {
        var curr = 0
        def hasNext = if (curr == 4 * 1000 * 1000) false else {curr = curr + 1; true}
        def next = Record(nextInt, nextDouble, nextFloat, nextLong)
    }.toStream.foreach{ record => src.put(record) }

}


object OneProducerMultipleConsumerViaArrayBlockingQueueNActors extends App {
    val size  = 4000000
    val src = new ArrayBlockingQueue[Record](size)
    val dest = new ArrayBlockingQueue[Record](size)
    import scala.concurrent.ExecutionContext.Implicits.global 
    import Reaper._

    class PullData(val d : ArrayBlockingQueue[Record]) extends Actor {
        override def preStart() = { Thread.sleep(100); self ! "pull" }
        def receive = {
            case "pull" =>
                if (d.size == 4000000) context.stop(self) 
                src.poll match {
                    case x  if x != null  => d.put(x); self ! "pull"
                    case _ => Thread.sleep(100); self ! "pull"
                }
        }
    }

    val as = ActorSystem("Test")
    val now = System.currentTimeMillis
    val reaper = as.actorOf(Props(new Reaper(src,dest,now)))
    Seq.fill(8){ val r = as.actorOf(Props(new PullData(dest))); reaper ! WatchMe(r) }

    new Iterator[Record] {
        var curr = 0
        def hasNext = if (curr == 4000000) false else {curr = curr + 1; true}
        def next = Record(nextInt, nextDouble, nextFloat, nextLong)
    }.toStream.foreach{ record => src.put(record) }

}

class PullData[A](val src: ArrayBlockingQueue[A], val dest : ArrayBlockingQueue[A]) extends Callable[Unit] {
    override def call() = dest.put(src.poll)
}

object OneProducerOneConsumerViaArrayBlockingQueueNThreads extends App {
    import scala.collection.JavaConverters._
    val size  = 4000000
    val src = new ArrayBlockingQueue[Record](size)
    val dest = new ArrayBlockingQueue[Record](size)

    
    val tpool = Executors.newFixedThreadPool(4)
    tpool.invokeAll(Seq.fill(4)(new PullData(src,dest)) asJava) 
    val now = System.currentTimeMillis
    new Iterator[Record] {
        var curr = 0
        def hasNext = if (curr == 1000000) false else true
        def next = Record(nextInt, nextDouble, nextFloat, nextLong)
    }.toStream.foreach{ record => src.put(record) }
    println(s"2.How fast was it? ${(System.currentTimeMillis - now)/1000} seconds")
}


