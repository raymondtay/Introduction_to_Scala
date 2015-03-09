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

class Walker(val matrix   : Seq[Seq[Int]], 
             val position : Position, 
             val rbound   :Int, val cbound: Int) {

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
            (row < 0 || col < 0 || row >= rbound || col >= cbound) match {
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

    def findOptimizedPaths(paths: Set[List[(Int,Int)]]) : List[(Int, Int, List[(Int,Int)])] = 
        paths.toList.map{ 
            list =>
                val headE = list(0)
                val lastE = list(list.size - 1)
                (abs(matrix(fst(lastE))(snd(lastE)) - matrix(fst(headE))(snd(headE))), list.size, list)
        }.sortBy(t => t._2).sortBy(t => t._1) // sort by length, then by greatest drop

    // Locates all the paths from the "start" position
    // by locating all accessible end-points
    def findAllPaths(start: Position) = {
        val map = findAllNeighbours(start)

	    def visitMap(key : Position, acc: Seq[Position]) : Seq[Any] = {
	        println(key)
	        Try(map(key).toList) match {
	            case Failure(e)      => acc
	            case Success(Nil)    => acc
	            case Success(h :: t) =>
	                val result = t.map(k => visitMap(k, acc ++ Seq(k))) :+ visitMap(h, acc :+ h)
                    register(result)
                    result
	        }
	    }
        
        visitMap(start, Seq(start))
    }

    // Goes thru a list of list of ... 
    // and once we find the "right" list, register it.
    def register(suspect: List[Any]) : Unit = {
        suspect match {
            case (h:(_,_))::t =>
            case h :: t =>
                val ha = h.asInstanceOf[List[(Int,Int)]]
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

    // pretty printer for n x n matrix
    def pretty_print(m : Seq[Seq[Int]]) = m.map(_.mkString("|")).map(_ + "\n").mkString
   
    def givenAStartCoord : Position = (nextInt(rows), nextInt(cols))

    def main(args: Array[String]) = {

        val matrix = Seq.fill(rows, cols)(fillItUp)

        val w = new Walker(matrix, givenAStartCoord, rows, cols)

        println(s"${pretty_print(matrix)}")

        w.findAllPaths(w.position)

        println(s"All paths from ${w.position} is ${w.findOptimizedPaths(w.paths.toSet)}")
    }

}

