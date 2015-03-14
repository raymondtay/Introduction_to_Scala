import scala.util._

/*
    Local minimum of an array. 
    Write a program that, given an array a[] of N distinct integers,finds
    a local minimum: anindexisuchthata[i-1] < a[i] < a[i+1]. Your program should use ~2lg N compares in the worst case..
*/
object LocalMin {

    def islocalminimum(left:Int, mid:Int, right:Int) : Boolean = left < mid && mid < right

    val sentinel = (-1,-1,-1)

    def search(a : Array[Int]) = {

        def go(leftbound: Int, rightbound: Int) : (Int,Int,Int)  = {
	        val mid = math.floor(((rightbound-leftbound)/2)).toInt
	        val left = mid - 1
	        val right = mid + 1
            if (mid == leftbound) return sentinel
            println(s"Examining $left, $mid, $right")
	        Try( islocalminimum(a(left), a(mid), a(right)) ) match {
	            case Success(v) => (a(left), a(mid), a(right))
	            case Failure(_) => go(leftbound, mid)
	        }
        }

        go(0, a.size-1)
    }

    def main(args: Array[String]) {
        import scala.util.Random._
            
        val N = args(0).toInt

        val arr = (0 to N).permutations.toArray
        
        println(search(arr(nextInt(N)).toArray))
    }

}
