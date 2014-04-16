package experimental

import akka.actor._

sealed trait KV {
    type A[K <: D, V <: D, D] <: D
}

class Key1 extends KV {
    type A[K <: D, V <: D, D] = K
}

class Value1 extends KV {
    type A[K <: D, V <: D, D] = V
}

object GenSACache {

    type TK[T <: KV] = T#A[String,ActorRef,Any]
 
    class Cache[K <: TK[Key1], V <: TK[Value1]] extends Actor with ActorLogging {
        var map = Map.empty[TK[Key1],TK[Value1]]
        def receive = {
            case Get1((k:TK[Key1])) ⇒ 
                println("Get1 begin:")
                sender ! map.get(k)
                println("Get1 end.")
            case Entry1((k:TK[Key1]),(v: TK[Value1])) ⇒ 
                println("Entry1 begin:")
                map += (k -> v)
                println(s"Entry1 end with size: ${map.size}")
            case Evict1((k:TK[Key1])) ⇒ 
                println("Evict1 begin:")
                map -= k
                println(s"Evict1 end with size: ${map.size}")
        }
    }

    case class Entry1[K <: TK[Key1], V <: TK[Value1]](k: K, v: V)

    case class Get1[K <: TK[Key1]](k: K)

    case class Evict1[K <: TK[Key1]](k: K)
}

object GenASCache {

    type TK[T <: KV] = T#A[ActorRef,String,Any]
 
    class Cache[K <: TK[Key1], V <: TK[Value1]] extends Actor with ActorLogging {
        var map = Map.empty[TK[Key1],TK[Value1]]
        def receive = {
            case Get1((k:TK[Key1])) ⇒ 
                println("Get1 begin:")
                sender ! map.get(k)
                println("Get1 end.")
            case Entry1((k:TK[Key1]),(v: TK[Value1])) ⇒ 
                println("Entry1 begin:")
                map += (k -> v)
                println(s"Entry1 end with size: ${map.size}")
            case Evict1((k:TK[Key1])) ⇒ 
                println("Evict1 begin:")
                map -= k
                println(s"Evict1 end with size: ${map.size}")
        }
    }

    case class Entry1[K <: TK[Key1], V <: TK[Value1]](k: K, v: V)

    case class Get1[K <: TK[Key1]](k: K)

    case class Evict1[K <: TK[Key1]](k: K)
}
