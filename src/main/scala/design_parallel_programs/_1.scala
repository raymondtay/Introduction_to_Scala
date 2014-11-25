package beginners

object Epoch_1 {

    def sum(ints : Seq[Int]) : Int = ints.foldLeft(0)((a,b) => a + b)

}
