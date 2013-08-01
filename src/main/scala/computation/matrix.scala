import scala.collection.mutable._

class Matrix(private val repr: Array[Array[Double]]) {
  def row(ids: Int) : Seq[Double] = {
    repr(ids)
  }
  def col(idx: Int) : Seq[Double] = {
    repr.foldLeft(ArrayBuffer[Double]()) {
      (buffer, currentRow) =>
        buffer.append(currentRow(idx))
        buffer
    } toArray
  }

  lazy val rowRank = repr.size
  lazy val colRank = if(rowRank >0 ) repr(0).size else 0
  override def toString = "Matrix" + repr.foldLeft("") {
    (msg, row) => msg + row.mkString("\n|", " | ", "|")
  }
}
// The ThreadStrategy interface defines one methods, "execute". This method
// takes a function that returns a value of type "A". This execute method also
// returns a function that returns a value of type "a". The returned function should
// return the same value as the passed in function, but could block the current thread until
// the function is calculated on its desired thread.
trait ThreadStrategy {
  def execute[A](f: Function0[A]) : Function0[A]
}

object SameThreadStrategy extends ThreadStrategy {
  def execute[A](f: Function0[A]) = f
}

object MatrixUtils {
  def multiply(a: Matrix, b: Matrix)(implicit threading: ThreadStrategy = SameThreadStrategy) : Matrix = {
    assert(a.colRank == b.rowRank) // this is native for matrices

    val buffer = new Array[Array[Double]](a.rowRank)
    for( i <- 0 until a.rowRank ) buffer(i) = new Array[Double](b.colRank)

    def computeValue( row: Int, col: Int) : Unit = {
      val pairwiseElements = a.row(row).zip(b.col(col))
      val products = for( (x,y) <- pairwiseElements) yield x * y
      val result = products.sum
      buffer(row)(col) = result
    }
   
    val computations = for {
      i <- 0 until a.rowRank
      j <- 0 until b.colRank
    } yield threading.execute{ () => computeValue(i, j) }

    computations.foreach(_())

   new Matrix(buffer)
  }
}


import java.util.concurrent.{Callable, Executors}

object ThreadPoolStrategy extends ThreadStrategy {
  val pool = Executors.newFixedThreadPool(java.lang.Runtime.getRuntime.availableProcessors)
  def execute[A](f : Function0[A]) = {
    val future = pool.submit(new Callable[A] {
        def call : A = {
          Console.println("Executing function on thread: " + Thread.currentThread.getId)
          f()
        }
      })
    () => future.get
  }
}

