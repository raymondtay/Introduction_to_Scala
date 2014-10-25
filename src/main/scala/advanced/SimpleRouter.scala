import akka.routing._
import akka.actor._

class Master extends Actor with ActorLogging {
    var router = {
        val routees = Vector.fill(5) {
            val r = context.actorOf(Props[Worker])
            context watch r
            ActorRefRoutee(r)
        }
        Router(RoundRobinRoutingLogic(), routees)
    }

    def receive = {
        case w : Work =>
            println("sent work!")
            router.route(Work(), sender())
        case Terminated(a) =>
            router = router.removeRoutee(a)
            val r = context.actorOf(Props[Worker])
            context watch r
            router = router.addRoutee(r)
    }

}

class Worker extends Actor with ActorLogging {
    def receive = {
        case w : Work => println("Got work!")
        case _ => println("What ?")
    }
}

case class Work() 

object SimpleRouter extends App {

val as = ActorSystem("SimpleRouter")
val master = as.actorOf(Props[Master], "master")
master ! "ready?"
master ! "go!"
for { i <- 1 to 2 } master ! Work()
}
