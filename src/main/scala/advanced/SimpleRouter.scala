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
            log.info("sent work!")
            router.route(w, sender())
        case Terminated(a) =>
            router = router.removeRoutee(a)
            val r = context.actorOf(Props[Worker])
            context watch r
            router = router.addRoutee(r)
    }

}

class Worker extends Actor with ActorLogging {
    def receive = {
        case w : Work => log.info("Got work!")
        case _ => log.info("What ?")
    }
}

case class Work() 
