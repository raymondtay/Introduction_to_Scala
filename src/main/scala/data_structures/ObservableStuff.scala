import scala.collection.mutable._
import scala.collection.script._

object TestObservables extends App {

    val buf = new ArrayBuffer[Int] with ObservableBuffer[Int]

    buf.subscribe(new Subscriber[Message[Int], ObservableBuffer[Int]] {
        def notify(pub: ObservableBuffer[Int], event: Message[Int]) = 
            println(s"...$event")
        })

    buf += 1
    buf ++= Array(2)
    buf ++= ArrayBuffer(3,4)
    buf ++= collection.immutable.Vector(1,2,3)
    0 +=: buf
    buf remove 0
}

