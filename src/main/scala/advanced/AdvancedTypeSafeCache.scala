import akka.actor._
import scala.reflect.runtime.universe._
import scala.reflect.runtime.currentMirror

trait service[k,v] extends Actor {
    def lookup(k1:k): Option[v] = None
    def remove(k1:k) : Unit = {}
    def insert(k1:k, v1:v) = {}
    def receive : PartialFunction[Any,Unit] = { case _ => }
}

object A {
    implicit object serviceSA extends service[String,ActorRef] {
	    var map = collection.immutable.Map.empty[String,ActorRef]
	    override def lookup(k:String): Option[ActorRef] = map.get(k)
	    override def remove(k:String) : Unit = map = map - k
	    override def insert(k:String, v:ActorRef) = map = map + (k -> v)
        override def receive = {
            case entry(k:String,v:ActorRef) => insert(k,v)
            case evict(k:String) => remove(k)
            case get(k:String) => sender ! lookup(k)
        }
    }
}
object B {
    implicit object serviceAS extends service[ActorRef,String] {
	    var map = collection.immutable.Map.empty[ActorRef,String]
	    override def lookup(k:ActorRef): Option[String] = map.get(k)
	    override def remove(k:ActorRef) : Unit = map = map - k
	    override def insert(k:ActorRef, v:String) = map = map + (k -> v)
        override def receive = {
            case entry(k:ActorRef,v:String) => insert(k,v)
            case evict(k:ActorRef) => remove(k)
            case get(k:ActorRef) => sender ! lookup(k)
        }
    }
}
case class entry[k,v](k1:k, v1:v) extends service[k,v]
case class get[k](k1:k) extends service[k,Nothing]
case class evict[k](k1:k) extends service[k,Nothing]

object TestXXX extends App {

    val as = ActorSystem("test")

    as.shutdown
}

class asyncservice[k : TypeTag,v : TypeTag] extends Actor with ActorLogging {

    import A._
    import B._

    def recv1[k,v](implicit m : service[k,v]) : Receive = m.receive

    def receive = {
        val kCls = typeOf[k] =:= typeOf[String]
        val vCls = typeOf[v] =:= typeOf[ActorRef]

        (kCls,vCls) match {
            case (true, true) => recv1(serviceSA)
            case (_,_) => recv1(serviceAS)
        }
    }

}
 
