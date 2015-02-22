import akka.actor._
import scala.concurrent.duration._

object OO {
    trait State
    case class One() extends State
    case class Two() extends State
    case class Data(i: Int)
}

class OO extends Actor with FSM[OO.State, OO.Data] {
    import OO._
    import java.util.concurrent._
    var requests = new ConcurrentLinkedQueue[OO.Data]()

    startWith(One(), Data(32))
    when(One()) {
/*
        case Event(x : Data, current : Data) =>
            println(s"Found $x while in `One`")
            goto(One()) using current
*/
        case Event(StateTimeout, data) => 
            println("\n\tTimeout!")
            goto(One()) using data
        case Event(somemsg:Data, Data(x)) => 
            println(s"\n\tOne: you sent me ${somemsg} with data ${x}")
            //stay using Data(x) forMax(4 seconds)
            //goto(Two()) using Data(944) forMax(5 seconds) replying Data(1044)
            goto(Two()) using Data(944)
    }
    when(Two()) {
        case Event(_, Data(9089)) => println(s"\n\t${System.currentTimeMillis}, Got 9089"); stay forMax(1 seconds); goto(Two()) using Data(9088)
        case Event(_, Data(9088)) => println("\n\tGot 9088"); stop replying (Data(9088))
        case Event(somemsg, Data(x)) => 
            println(s"\n\tTwo: At ${System.currentTimeMillis} you sent me ${somemsg} with data ${x}")
            stay using Data(9089) forMax(10 seconds) replying(somemsg)
    }
}

object TestFSM extends App {
    import OO._
    import akka.pattern._
    import scala.concurrent.ExecutionContext.Implicits.global
    implicit val timeout = akka.util.Timeout(1 seconds)
    val as = ActorSystem()
    val r = as.actorOf(Props[OO], "fsm")
    r ! Data(1)
    r ! "Dataa...."
/*
    r ! "aaaa"
    r ? Data(4444) map {
       case Data(x) => println("Got returned result : " + x)
    }
    r ! Data(1111)
*/
}

