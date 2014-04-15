import akka.actor._
import akka.routing._
import akka.routing.ConsistentHashingPool
import akka.routing.ConsistentHashingRouter._

class Cache extends Actor with ActorLogging {
    var cache = Map.empty[String,String]

    def receive = {
        case Entry(k, v) => 
            log.info("Entry added")
            cache += (k -> v)
        case Get(k) => 
            log.info(s"entry returned is ${cache.get(k)}")
            sender ! cache.get(k)
        case Evict(k) => 
            log.info(s"entry evicted is ${k}")
            cache -= k
    }
}

case class Evict(k:String)
case class Get(k:String) extends ConsistentHashable {
    override def consistentHashKey = k
}
case class Entry(k: String, v: String)

object TestConsistentHashCache extends App {
    def hashMapping : ConsistentHashMapping = {
        case Evict(k) ⇒ k
    }

    val a = ActorSystem("test")
    val r = a.actorOf(ConsistentHashingPool(10, hashMapping = hashMapping).props(Props[Cache]), name = "cache")

    r ! ConsistentHashableEnvelope(Entry("a", "aaa"), hashKey = "a")
    r ! ConsistentHashableEnvelope(Entry("b", "baa"), hashKey = "b")
    r ! ConsistentHashableEnvelope(Entry("c", "caa"), hashKey = "c")
    r ! ConsistentHashableEnvelope(Entry("d", "daa"), hashKey = "d")
    r ! ConsistentHashableEnvelope(Entry("e", "eaa"), hashKey = "e")

    r ! Get("a")

    r ! Evict("a")

    r ! Get("a")

    //r ! Kill

    //a.shutdown
}
