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
/*
// [error] /Users/raymondtay/Introduction_to_Scala/src/main/scala/advanced/SimpleTypeSafeCache.scala:22: type mismatch;
// [error]  found   : k.type (with underlying type Any)
// [error]  required: K
// [error]                 sender ! map.get(k)
// [error]                                  ^
// [error] /Users/raymondtay/Introduction_to_Scala/src/main/scala/advanced/SimpleTypeSafeCache.scala:25: type mismatch;
// [error]  found   : (Any, Any)
// [error]  required: (K, V)
// [error]                 map += (k -> v)
// [error]                           ^
// [error] /Users/raymondtay/Introduction_to_Scala/src/main/scala/advanced/SimpleTypeSafeCache.scala:28: type mismatch;
// [error]  found   : k.type (with underlying type Any)
// [error]  required: K
// [error]                 map -= k
// [error]                        ^
// [error] three errors found

object TTT {
    class Cache[K,V] extends Actor with ActorLogging {
        var map = Map.empty[K,V]
        def receive = {
            case Get(k) =>
                println(s"Getting -> $k")
                sender ! map.get(k)
            case Entry(k,v) =>
                println(s"Adding $k -> $v")
                map += (k -> v)
            case Evict(k) => 
                println(s"Evicting $k")
                map -= k
        }
    }

    case class Evict[K](k: K)
    case class Entry[K,V](k:K, v: V) 
    case class Get[K](k:K) 
}
*/

trait XXX[K,V] {

    // this is type-safe but it makes using this API 
    // from other parties a pain-in-the-ass since 
    // the user of this would need to say something like this:
    // someActor ! new XXX[String,String]{}.Evict(someKey)
    // instead of something like
    // someActor ! GenSACache.Evict(someKey) 
    // assuming someKey forSome { type someKey <: String }
    class Cache extends Actor with ActorLogging {
        var map = Map.empty[K,V]
        def receive = {
            case Get(k) =>
                println(s"Getting -> $k")
                sender ! map.get(k)
            case Entry(k,v) =>
                println(s"Adding $k -> $v")
                map += (k -> v)
            case Evict(k) => 
                println(s"Evicting $k")
                map -= k
        }
    }

    case class Evict(k: K)
    case class Entry(k:K, v: V) 
    case class Get(k:K) 
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
