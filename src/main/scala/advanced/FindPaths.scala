import util.Random._
import scala.util._
import scala.language.postfixOps
import math._

/*
  Two ways to run this:
  Part 1:
  (a) Tested on scala 2.10.4 REPL, load the file and trigger the `Find` object using the usual way

  Part 2:
  (b) On the commandline:
  (b.1) scalac ./FindPaths.scala
  (b.2) scala -cp . Find
*/
object TT {
    type Row = Int
    type Col = Int
    type Position = (Row, Col)
}

import TT._

class Walker(val matrix   : Array[Array[Int]], 
             val rbound   :Int, val cbound: Int)(val position: Position) {

    private def fst(p: Position) = p._1
    private def snd(p: Position) = p._2
    private lazy val smallestElement = matrix.map(l => l.min).min
    var paths = collection.mutable.ListBuffer.empty[List[(Int,Int)]]

    // Locates the destination given the "row" & "col" increments
    // from the given "position". We'll also check that we can descend
    // to this destination
    def getNewPosition(position: Position, row: Int, col: Int) : Option[Position] = {
        val (cr, cc) = ( fst(position),snd(position) ) // current row & column

        def endOfRoad(row: Int, col: Int) = {
            (row <= 0 || col <= 0 || row >= rbound || col >= cbound) match {
                case true => true
                case _    => matrix(row)(col) == smallestElement  
            }
        }

        def canDescend(from: Position, to: Position) = matrix(fst(to))(snd(to)) < matrix(fst(from))(snd(from))

        def getNext = {
            //println("Examining " + (cr,cc) + " for validity")

	        val newrow = {
	        ((cr + row) < rbound) && ((cr + row) >= 0) match {
	            case true => cr + row
	            case _    => -1
	        }}
	
	        val newcol = {
	        ((cc + col) < cbound) && ((cc + col) >= 0) match {
	            case true => cc + col
	            case _    => -1
	        }}

	        (newrow, newcol) match {
                case (-1, _)  => None
                case (_, -1)  => None
	            case _        => 
                    canDescend(position, (newrow, newcol)) match {
                        case true  => Some((newrow, newcol))
                        case false => None
                    }
	        } 
        }

        endOfRoad(cr,cc) match {
            case true  => None
            case false => getNext
        }
    }

    // Finds all the neighbours who are around me and i can descend to!
    def neighbours = (position: Option[Position]) => Seq(goLeft, goRight, goUp, goDown, upperLeft, upperRight, bottomLeft, bottomRight).map(f => f(position))

    // Construct a map of the valid neighbours
    def findAllNeighbours(position: Position) = {
        import collection.mutable.{HashMap, MultiMap, Set}
        var xs = new HashMap[Position,Set[Position]] with MultiMap[Position,Position]

        def go(parent: Position) {
            neighbours(Some(parent)).filter(_ != None).map{ 
                x => 
                    xs.addBinding(parent, x.get) 
                    go(x.get)
            } 
        }
        go(position)
        xs toMap
    }

    def findOptimizedPaths(paths: Set[List[Position]]) : List[(Int, Int, List[Position])] = 
        paths.toList.map{ 
            list =>
                val headE = list(0)
                val lastE = list(list.size - 1)
                (list.size, abs(matrix(fst(lastE))(snd(lastE)) - matrix(fst(headE))(snd(headE))), list)
        }.sortBy(t => t._1).sortBy(t => t._2) // sort by length, then by greatest drop

    // Locates all the paths from the "start" position
    // by locating all accessible end-points
    def findAllPaths = {
        val map = findAllNeighbours(position)

	    def visitMap(key : Position, acc: Seq[Position]) : Seq[Any] = {
	        //println(key)
	        Try(map(key).toList) match {
	            case Failure(e)      => acc
	            case Success(Nil)    => acc
	            case Success(h :: t) =>
	                val result = t.map(k => visitMap(k, acc ++ Seq(k))) :+ visitMap(h, acc :+ h)
                    register(result)
                    result
	        }
	    }
        
        visitMap(position, Seq(position))
    }

    // Goes thru a list of list of ... 
    // and once we find the "right" list, register it.
    def register(suspect: List[Any]) : Unit = {
        suspect match {
            case (h:(_,_))::t =>
            case h :: t =>
                val ha = h.asInstanceOf[List[Position]]
                Try(ha(0)) match {
                    case Success(v) => paths = paths :+ ha; register(t)
                    case Failure(e) => suspect.map(l => register(l.asInstanceOf[List[Any]]))
                }
            case Nil => Nil
        }
    }


    private def g    = (row: Int) => (col: Int) => (position: Option[Position]) => position.isEmpty match { case false => getNewPosition(position.get, row, col); case true => None}

    def goLeft       = (position: Option[Position]) => g(0)(-1)(position)
            
    def goRight      = (position: Option[Position]) => g(0)(1)(position)

    def goUp         = (position: Option[Position]) => g(-1)(0)(position)

    def goDown       = (position: Option[Position]) => g(1)(0)(position)

    def upperLeft    = (position: Option[Position]) => g(-1)(-1)(position)

    def upperRight   = (position: Option[Position]) => g(-1)(1)(position)

    def bottomLeft   = (position: Option[Position]) => g(1)(-1)(position)

    def bottomRight  = (position: Option[Position]) => g(1)(1)(position)
}

object Find {
    val rows = 1000
    val cols = 1000
    val limit = 1500

    // fill it up with non-zero values
    def fillItUp : Int = {
        import util.Random._
        nextInt(limit) match {
            case 0 => fillItUp
            case x => x
        }
    }

    def readIndexData = {
        import scala.io.Source._
        var b = collection.mutable.ListBuffer.empty[Array[String]]
        fromFile("./index.txt").
        getLines().foreach(l => b += l.split(","))
        b.toArray.flatten
    }

    // reads in the data from the data file "map.txt"
    def readData: Array[Array[Int]] = {
        var b = collection.mutable.ListBuffer.empty[Array[Int]]
        import scala.io.Source._
        fromFile("./map.txt").
        getLines().foreach(l => b += l.split(" ").map(_.toInt))
        b.toArray
    }

    def writeToIndexFile(row:Int,col:Int,size:Int) {
        import java.io._
        val indexfile = new File(s"./index.txt")
        col == cols match {
            case true  => val bw = new BufferedWriter(new FileWriter(indexfile)); bw.write(s"${row+size},0,${size}"); bw.close
            case false => val bw = new BufferedWriter(new FileWriter(indexfile)); bw.write(s"${row},${col+size},${size}"); bw.close
        }
    }

    def loadSteepestDescent {
        import java.io._
        Try(new FileInputStream("./steepestdescent.txt")) match {
            case Success(is) =>
                val ois = new ObjectInputStream(is)
                pathSteepestDescent.set(ois.readObject.asInstanceOf[(Int,Int,List[Position])])
                is.close
                ois.close
            case Failure(e) => pathSteepestDescent.set((-99, -99, List((0,0))))
        }
    }

    def storeSteepestDescent {
        import java.io._
        val os = new FileOutputStream("./steepestdescent.txt")
        val oos = new ObjectOutputStream(os)
        oos.writeObject(pathSteepestDescent.get)
        os.close
        oos.close
    }
 
    // this is an overkill but considering how easy it is to 
    // enabling parallelism in Scala implicitly e.g. 'par'..
    // "better be safe than sorry" ? i hate premature optimization though 
    val pathSteepestDescent = 
        new java.util.concurrent.atomic.AtomicReference[(Int,Int,List[Position])]((-99,-99,List((0,0))))
 
    def writeToDataFile(data:List[(Int, Int, List[Position])])
                       (row:Int, col: Int, size: Int)
                       (optfn : (Int,Int) => Int) = {
        import java.io._
        loadSteepestDescent
        data.toSet.map{(t: (Int,Int,List[Position])) => optfn(t._1, pathSteepestDescent.get._1) == t._1 match {
                        case true  => 
                            optfn(t._2, pathSteepestDescent.get._2) == t._2 match {
                                case true  => pathSteepestDescent.set(t)
                                case false =>
                            }
                        case false =>
                      }
                }
        println(s"Steepest Descent info: ${pathSteepestDescent.get} in Seg($row,$col) of size($size)")
        storeSteepestDescent
    }

    // pretty printer for n x n matrix
    def pretty_print(m : Array[Array[Int]]) = m.map(_.mkString("|")).map(_ + "\n").mkString
   

    def main(args: Array[String]) :Unit = {

        val indexData = readIndexData
        val (startRow, startCol, rowColCount) = (indexData(0).toInt, indexData(1).toInt, indexData(2).toInt)
        val matrix = readData

        //println(s"${pretty_print(matrix)}")

        val count = new java.util.concurrent.atomic.AtomicInteger(rowColCount*rowColCount)

        val allcoords = 
        for {
            x <- (startRow until startRow+rowColCount)
            y <- (startCol until startCol+rowColCount)
        } yield (x,y)

        val allpaths = 
        allcoords.map{ t => 
            val w = new Walker(matrix, rows, cols)(t)
            w.findAllPaths
            //println(s"Remaining ${count.decrementAndGet}...")
            //(t,  w.findOptimizedPaths(w.paths.toSet))
            writeToDataFile(w.findOptimizedPaths(w.paths.toSet))(startRow, startCol, rowColCount )(math.max)
        }
        writeToIndexFile(startRow,startCol,rowColCount)
        println(s"Completed data mining for a matrix of $rowColCount X $rowColCount starting from ($startRow, $startCol)")
    }

}

