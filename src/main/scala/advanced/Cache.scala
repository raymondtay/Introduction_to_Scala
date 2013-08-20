package advanced

import math._
import java.util.{HashMap => JHashMap}
import java.util.concurrent.{ConcurrentHashMap => JConcurrentHashMap}
import scala.concurrent._
import scala.util._
import duration._

trait Computable[A,V] {
    def compute(arg: A) : Option[V]
}

class ExpensiveFunction extends Computable[String, BigInt] {
    def compute(arg: String) = {
        // after a lot of thought as to what the BIGGGGGG computation is going to be
        Some(BigInt(arg))
    }
}

class Memoizer1[A,V](computeFn: Computable[A,V]) extends Computable[A,V] {
    val cache = new JHashMap[A,V]()
    val c : Computable[A,V] = computeFn 

    def compute(arg: A) : Option[V] = {
        synchronized { // synchronized block
	        val result = cache.get(arg)
	        result match {
	            case null => val computeR = c.compute(arg); Some(cache.put(arg, computeR.get))
	        }
        }
    }
}

class Memoizer2[A,V](computeFn: Computable[A,V]) extends Computable[A,V] {
    val cache = new JConcurrentHashMap[A,V]()
    val c : Computable[A,V] = computeFn 

    def compute(arg: A) : Option[V] = {
        val result = cache.get(arg)
        result match {
            case null => val computeR = c.compute(arg); Some(cache.put(arg, computeR.get))
        }
    }
}

class Memoizer3[A,V](computeFn : Computable[A,V]) extends Computable[A,V] {
    val cache = new JConcurrentHashMap[A,Future[Option[V]]]()
    val c : Computable[A,V] = computeFn 

    implicit val ec = ExecutionContext.Implicits.global

    def compute(arg: A) : Option[V] = {
        val result : Future[Option[V]] = cache.get(arg)
        result.value match {
            case Some(Success(v)) => v
            case _ => 
                val f = future { c.compute(arg) } 
                cache.put(arg, f)    // to prevent cache pollution, replace this call with the following one
                //cache.putIfAbsent(arg, f) 
                Await.ready(f, Duration.Inf)
                f.value match { 
                    case Some(Success(v)) => v 
                    case _ => None
                }
        }
    }   
}

object Memoizer1Test extends App {
   override def main(args : Array[String] ) = { println("Not implemented") }
}
object Memoizer2Test extends App {
   override def main(args : Array[String] ) = { println("Not implemented") }
}

