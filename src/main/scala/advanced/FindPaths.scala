import util.Random._
import scala.util._

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
	                t.map(k => visitMap(k, acc ++ Seq(k))) :+ visitMap(h, acc :+ h)
	        }
	    }
        
        visitMap(start, Seq(start))
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
    val rows = 10
    val cols = 10
    val limit = 10

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

        val paths = w.findAllPaths(w.position)

        println(s"All paths from ${w.position} is ${paths}")
    }

}

