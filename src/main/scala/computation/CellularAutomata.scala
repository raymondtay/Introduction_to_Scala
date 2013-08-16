
import java.util.concurrent.{BrokenBarrierException, CyclicBarrier}

// This is the skeleton of how a cellularautomata *might* be represented
// when writing Scala in the Java way
trait Board {
    def getMaxX() : Int
    def getMaxY() : Int
    def setNewValue(x: Int, y: Int, result: Int) 
    def hasConverged() : Boolean
    def commitNewValues() : Unit
    def getSubBoard(total: Int, index: Int) : Board
}

class CellularAutomata(board: Board) {
	class Worker(val board: Board) extends Runnable {
	    import Utils._
	    override def run() {
	        while( ! board.hasConverged() ) {
	            for { 
	                x <- 0 to board.getMaxX() 
	                y <- 0 to board.getMaxY()
	            } yield board.setNewValue(x, y, computeValue(x, y))
	        
	            try { barrier.wait() }
	            catch {
	                case e: InterruptedException => return
	                case ex: BrokenBarrierException => return
	            } 
	        }
	    }
	}
    
    val mainBoard: Board = board
    val barrier : CyclicBarrier = new CyclicBarrier(procCount, new Runnable() {
                                    override def run() { mainBoard.commitNewValues() }})
    val workers = for(i <- 0 to procCount) yield new Worker(mainBoard.getSubBoard(procCount, i))
    lazy val procCount = Runtime.getRuntime().availableProcessors()
}

object Utils {
    def computeValue(x : Int, y: Int) : Int = ???
}

    
