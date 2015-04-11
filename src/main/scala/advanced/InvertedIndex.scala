import scala.collection.immutable._
import scala.io._
import scala.util._
import Source._
import scala.math.Ordering._

object InvertedIndex extends App {

  private def addTm[A,B](tm: TreeMap[A,Queue[B]], key: A, value : B) = {
    val curr_q : Queue[B] = 
        Try(tm(key)) match {
            case Success(q) => q
            case Failure(_) => Queue()
        }
    tm + (key -> (Queue(curr_q:_*) :+ value))
  }

  override def main(args : Array[String]) : Unit = { 
    var st = TreeMap[String,Queue[String]]()
    var ts = TreeMap[String,Queue[String]]()
    val filename = args(0)
    val sep = args(1)
    println(s"Analyzing $filename where each data is separate by $sep")
    fromFile(filename).getLines.foreach{
        line => 
            val xs = line.split(sep) // array of strings or empty array
            val key = xs.head
            println(s"Analyzing data for $key")
            for { 
              w <- xs.tail 
            } {
              st.contains(key) match {
                case false => 
                    st = st + (key -> Queue.empty[String])
                case true  => 
                    ts.contains(w) match {
                        case false => ts = ts + (w -> Queue.empty[String])
                        case true  => 
                    }

              }
              st = addTm(st, key, w)
              ts = addTm(ts, w, key)
            }
    }
    println("Analysis done.")
    stdin.getLines.foreach {
      query => 
        println(s"query is $query..")
        st.contains(query) match {
          case true  => st(query).map(x => println("   " + x))
          case false => 
            ts.contains(query) match {
              case true  => ts(query).map(x => println("   " + x))
              case false => println("Didn't find anything.Try again or hit ctrl-c to quit")
            }
        }
    }    
  }
}


