
/*

  Here's a brute-force approach, not terribly proud
  of it but it's a start!
  assuming we modelled our square like this:
  [top-lhs, top, top-rhs, left, centre, right, bottom-lhs, bottom, bottom-rhs]
  then we can devise some functions to compute the value.

  Here's the output of a sample attempt:
  Raymonds-MacBook-Air-2:kata tayboonl$ scala
  Welcome to Scala version 2.11.5 (Java HotSpot(TM) 64-Bit Server VM, Java 1.7.0_21).
  Type in expressions to have them evaluated.
  Type :help for more information.
  
  scala> :load magic_square.scala
  Loading magic_square.scala...
  defined object FunctionUtils
  defined object MagicSquare
  
  scala> MagicSquare.main(Array())
  Starting computation...
  Number of valid permutations are 8
  A valid permutation is Vector(2, 7, 6, 9, 5, 1, 4, 3, 8)
  A valid permutation is Vector(2, 9, 4, 7, 5, 3, 6, 1, 8)
  A valid permutation is Vector(4, 3, 8, 9, 5, 1, 2, 7, 6)
  A valid permutation is Vector(4, 9, 2, 3, 5, 7, 8, 1, 6)
  A valid permutation is Vector(6, 1, 8, 7, 5, 3, 2, 9, 4)
  A valid permutation is Vector(6, 7, 2, 1, 5, 9, 8, 3, 4)
  A valid permutation is Vector(8, 1, 6, 3, 5, 7, 4, 9, 2)
  A valid permutation is Vector(8, 3, 4, 1, 5, 9, 6, 7, 2)
  
  scala>

  
*/
object FunctionUtils {
    def top_lhs(implicit xs: Seq[Int]) = xs(0)
    def top(implicit xs: Seq[Int]) = xs(1)
    def top_rhs(implicit xs: Seq[Int]) = xs(2)
    def left(implicit xs: Seq[Int]) = xs(3)
    def centre(implicit xs: Seq[Int]) = xs(4)
    def right(implicit xs: Seq[Int]) = xs(5)
    def bottom_lhs(implicit xs: Seq[Int]) = xs(6)
    def bottom(implicit xs: Seq[Int]) = xs(7)
    def bottom_rhs(implicit xs: Seq[Int]) = xs(8)
    def first_col(implicit xs: Seq[Int])  = Seq(top_lhs , left , bottom_lhs)
    def second_col(implicit xs: Seq[Int]) = Seq(top , centre , bottom)
    def third_col(implicit xs: Seq[Int])  = Seq(top_rhs , right , bottom_rhs)
}

object MagicSquare extends App {
    import FunctionUtils._

    val permutations = (1 to 9).permutations.toList

    def ldiagonal(implicit xs: Seq[Int]) = top_lhs + centre + bottom_rhs
    def rdiagonal(implicit xs: Seq[Int]) = top_rhs + centre + bottom_lhs
    def toprow(implicit xs:Seq[Int])     = top_lhs + top + top_rhs
    def middlerow(implicit xs:Seq[Int])  = left + centre + right
    def bottomrow(implicit xs:Seq[Int])  = bottom_lhs + bottom + bottom_rhs
    def firstcolumn(implicit xs:Seq[Int]) = first_col.sum
    def secondcolumn(implicit xs:Seq[Int]) = second_col.sum
    def thirdcolumn(implicit xs:Seq[Int]) = third_col.sum

    def compute() = {
        permutations.filter(permutation => { implicit val xs = permutation;Seq(ldiagonal,rdiagonal,toprow,middlerow,bottomrow,firstcolumn,secondcolumn,thirdcolumn).sliding(2).map(l => l).toList.filter(ys => ys(0) == ys(1)).size == 7})
    }
    println("Starting computation...")
    val results = compute()
    println(s"Number of valid permutations are ${results.length}")
    results.map(p => println(s"A valid permutation is ${p}"))
}

