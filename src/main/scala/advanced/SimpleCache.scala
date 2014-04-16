package experimental

import scala.reflect.runtime.universe._
import scala.reflect.runtime.currentMirror

import akka.actor._

sealed trait Key {
    type K1
}
sealed trait Value {
    type V1
}

case class Entry[K <: Key,V <: Value](k: K, v: V )

case class Evict[K <: Key](k: K)

case class Get[K <: Key](k: K)

class SKey extends Key {
    type K1 = String
}
class SValue extends Value {
    type V1 = String
}

object GenStringActorRef {
    type K[A <: Key] = A#K1
    type V[B <: Value] = B#V1


    trait Cache[K,V] {//extends Actor {
        var map = Map.empty[K,V]
       /* 
        def receive = {
            case Get(k) ⇒ sender ! map.get(k)
            case Evict(k) ⇒ map -= k
            case Entry(k,v) ⇒ map += (k -> v) 
        }
       */ 
    }

    def apply[A <: Key : TypeTag,B <: Value : TypeTag] = {
        println(s"typeOf key is ${typeOf[A]}, typeOf value is ${typeOf[B]}")
        new Cache[K[A],V[B]] {}
    }

}
