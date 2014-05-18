package sample.cluster.simple


import akka.cluster.Cluster
import akka.cluster.ClusterEvent._
import akka.actor.ActorLogging
import akka.actor._

import akka.persistence._
  class ExampleDestination extends Actor {
    def receive = {
      case cp @ ConfirmablePersistent(payload, sequenceNr, _) =>
        println(s"destination received ${payload} (sequence nr = ${sequenceNr})")
        cp.confirm()
    }
  }

  class ExampleProcessor(pId: String) extends Processor {
    override def processorId = pId

    def receive = {
      case Persistent(payload, sequenceNr) =>
        println(s"processor received ${payload} (sequence nr = ${sequenceNr})")
    }
  }

  class ExampleView(pId: String, vId: String, channelId: String) extends View {
    private var numReplicated = 0

    override def processorId = pId
    override def viewId = vId

    private val destination = context.actorOf(Props[ExampleDestination])
    private val channel = context.actorOf(Channel.props(channelId))

    def receive = {
      case "snap" =>
        saveSnapshot(numReplicated)
      case SnapshotOffer(metadata, snapshot: Int) =>
        numReplicated = snapshot
        println(s"view received snapshot offer ${snapshot} (metadata = ${metadata})")
      case Persistent(payload, sequenceNr) =>
        numReplicated += 1
        println(s"view received ${payload} (sequence nr = ${sequenceNr}, num replicated = ${numReplicated})")
        channel ! Deliver(Persistent(s"replicated-${payload}"), destination.path)
    }
  }
import scala.concurrent.duration._
class SimpleClusterListener extends Actor with ActorLogging {

  val cluster = Cluster(context.system)
  val dest1 = context.actorOf(Props[ExampleDestination], "mydestination-3")
  val proc1 = context.actorOf(Props(new ExampleProcessor("processor-3")), "myprocessor-3")
  val view1 = context.actorOf(Props(new ExampleView("processor-3","view-3", "channel-3")), "myview-3")
  val dest2 = context.actorOf(Props[ExampleDestination], "mydestination-4")
  val proc2 = context.actorOf(Props(new ExampleProcessor("processor-4")), "myprocessor-4")
  val view2 = context.actorOf(Props(new ExampleView("processor-4", "view-4", "channel-4")), "myview-4")


  // subscribe to cluster changes, re-subscribe when restart 
  override def preStart(): Unit = {
    //#subscribe
    cluster.subscribe(self, initialStateMode = InitialStateAsEvents,
      classOf[MemberEvent], classOf[UnreachableMember])
    //#subscribe
  }
  override def postStop(): Unit = cluster.unsubscribe(self)

    import scala.concurrent.ExecutionContext.Implicits.global

  cluster.system.scheduler.schedule(Duration.Zero, 2.seconds, proc1, Persistent("scheduled"))
  cluster.system.scheduler.schedule(Duration.Zero, 5.seconds, view1, "snap")
  cluster.system.scheduler.schedule(Duration.Zero, 3.seconds, proc2, Persistent("scheduled"))
  cluster.system.scheduler.schedule(Duration.Zero, 6.seconds, view2, "snap")

  def receive = {
    case MemberUp(member) =>
      log.info("Member is Up: {}", member.address)
    case UnreachableMember(member) =>
      log.info("Member detected as unreachable: {}", member)
    case MemberRemoved(member, previousStatus) =>
      log.info("Member is Removed: {} after {}",
        member.address, previousStatus)
    case _: MemberEvent => // ignore
  }
}
