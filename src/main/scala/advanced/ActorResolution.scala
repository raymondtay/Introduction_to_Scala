import akka.actor._
import akka.util._
import scala.util._
import scala.concurrent._
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

object ActorResolutionDemo extends App {

    class A extends Actor {
        def receive = { case x => println(s"Actor A received: ${x}") }
    }

    class B extends Actor { 
        def r : ActorRef = { 
            var ref: ActorRef = Actor.noSender
            //ref = scala.concurrent.Await.result((as.actorSelection("/user/A").resolveOne()(Timeout(300))).mapTo[ActorRef], 100 millis)
            (as.actorSelection("/user/A").resolveOne()(Timeout(3000)).onComplete {
              case scala.util.Success(resolved) => resolved
              case scala.util.Failure(error) => None
            })
            println(s"Returning reference ${ref}")
            ref
        } 
        def receive = { case x => r ! x }
    }

    val as = ActorSystem("test")
    as.actorOf(Props[A], "A")
    val b = as.actorOf(Props[B], "B")
    b ! "Something i said"
}
