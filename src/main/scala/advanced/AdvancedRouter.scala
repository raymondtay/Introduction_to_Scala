import akka.actor._
import akka.routing._
import akka.routing.ConsistentHashingPool
import akka.routing.ConsistentHashingRouter._

class Cache[K,V] extends Actor with ActorLogging {
    var cache = Map.empty[K,V]

    def receive = {
        case Entry((k:K), (v:V)) => 
            log.info("Entry added")
            cache += (k -> v)
        case Get((k:K)) => 
            log.info(s"entry returned is ${cache.get(k)}")
            sender ! cache.get(k)
        case Evict((k:K)) => 
            log.info(s"entry evicted is ${k}")
            cache -= k
    }
}

case class Evict[K](k:K)
case class Get[K](k:K) extends ConsistentHashable {
    override def consistentHashKey = k
}
case class Entry[K,V](k: K, v: V)

object TestConsistentHashCache extends App {
    def hashMapping : ConsistentHashMapping = {
        case Evict(k) ⇒ k
    }

    val a = ActorSystem("test")
    val r = a.actorOf(ConsistentHashingPool(10, hashMapping = hashMapping).props(Props(new Cache[String,String])), name = "cache")

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
