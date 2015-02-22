
import akka.actor._
import SupervisorStrategy._

class Super extends Actor with ActorLogging {

    println("Super started...")
    var sub: ActorRef = _

    override val supervisorStrategy = OneForOneStrategy(3) {
        case _ : Exception => println("restarting....");Restart
        case _ => println("escalating....");Escalate
    }


    override def preStart() = {
        sub = context.actorOf(Props[Sub1], "Sub1")
    }

    def receive = {
        case "throw" => sub ! "throw"
        case "kill" => sub ! "kill"
        case npe : NullPointerException => sub ! npe
        case err : java.lang.Error => sub ! err
        case x => println(s"Super received: $x ")
    }
}

class Sub1 extends Actor with ActorLogging {

    println("Sub1 started...")

    def receive = {
        case "throw" => throw new Exception("boom!")
        case "kill" => self ! Kill
        case e : Throwable => throw e
    }
}

object SuperSubDemo extends App {


}


