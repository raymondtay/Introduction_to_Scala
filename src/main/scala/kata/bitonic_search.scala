
/*
Bitonic search. An array is bitonic if it is comprised of an increasing sequence of integers followed immediately by a decreasing sequence of integers. Write a program that, given a bitonic array of N distinct int values, determines whether a given integer is in the array. Your program should use ~3lg N compares in the worst case
*/

object BitonicSearch {

    def find(xs: Array[Int])(item: Int) = {
        def go(left: Int, right: Int) : Boolean = {
            if ((left == right) || (right == xs.size -1)) return false
	        val mid : Int = left + (right - left)/2
            println(s"Examining indices: ($left, $mid, $right) -> ${xs(mid)}")

	        (item > xs(mid), xs(mid) == item, item < xs(mid)) match {
                case (_,true,_)   => println("Found!");return true
                case (true, _, _) => println("Go right...");go(mid, right)
                case (_, _, true) => println("Go left ...");go(left, mid) 
                case (_, _, _)    => println("Not Found!");return false
	        }
        }
        go(0, xs.size-1)
    }

    def main(args: Array[String]) {
        val N  = args(0).toInt
        val N2 = args(1).toInt
        val item = args(2).toInt
        assert( N > N2 )
        def xs = (0 until N).toArray
        def ys = (0 until N2).toArray
        val bitonicArr = xs ++ ys.reverse

        import scala.util.Sorting._
        quickSort(bitonicArr)
        bitonicArr.map(e => print(e +","))
        println(find(bitonicArr)(item))
    }

}

