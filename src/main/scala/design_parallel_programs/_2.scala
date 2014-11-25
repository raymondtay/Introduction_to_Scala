package beginners

object Epoch_2 {

    def sum(ints : Seq[Int]) : Int = 
        if (ints.size <= 1) ints.headOption getOrElse 0
        else {
            val (l, r) = ints.splitAt(ints.length / 2)
            sum(l) + sum(r) // obviously, the two halves can be summed up, potentially in parallel !!
        }

}
