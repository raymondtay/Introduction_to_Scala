package falsesharing

import akka.actor._
import scala.concurrent._

object X {

    var buffer = collection.mutable.ListBuffer.iterate(0, 10000)(_ + 1)

    class AS0 extends Actor {
        override def preStart = { println("ASO go!"); self ! "go" }
        def receive = {
            case "go" => println("Going...");buffer = buffer map { _ + 1 }
        }
    }
    
    class AS1 extends Actor {
        override def preStart = { println("AS1 go!"); self ! "go" }
        def receive = {
            case "go" => println("Going...");buffer = buffer.tail map { _ + 1 }
        }
    }

}

object Z {

    var buffer = collection.mutable.ListBuffer.iterate(0, 10001)(_ + 1)

    class AS0 extends Actor {
        override def preStart = { self ! "go" }
        var index = 0
        def receive = {
            case "go" =>
                buffer(index) = 100
                index += 1
                Thread.sleep(100)
                self ! "go"
        }
    }
    
    class AS1 extends Actor {
        override def preStart = { self ! "go" }
        var index = 1
        def receive = {
            case "go" => 
                buffer(index) = 101
                index += 1
                Thread.sleep(100)
                self ! "go"
        }
    }

}

object Starter extends App {
    import Z._
    val as = ActorSystem("0")
    as.actorOf(Props[AS0], "as0")
    as.actorOf(Props[AS1], "as1")

    Thread.sleep(10000)
    println(s"${buffer map(e => print(e + ","))}")

    as.shutdown
}

