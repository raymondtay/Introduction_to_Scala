/*
package sorting

object StringSort extends App {

    val names = Seq("Anderson", "Brown", "Davis", "Garcia", "Harris", "Jackson", "Jones", "Martin", "Miller", "Moore", "Thomas", "Wilson")
    val sections  = Seq(1,2,3,4)

    def keyIndexedCounting = {
    val name2Sections = (for { n <- names; s <- sections } yield (n,s)).groupBy(_._2) // this is MSD-sorting but i'm not sure if its stable-sort or not
    //  name2Sections: scala.collection.immutable.Map[Int,Seq[(String, Int)]] = 
    //  Map(2 -> List((a,2), (b,2), (c,2), (d,2), (e,2)), 
    //      4 -> List((a,4), (b,4), (c,4), (d,4), (e,4)), 
    //      1 -> List((a,1), (b,1), (c,1), (d,1), (e,1)), 
    //      3 -> List((a,3), (b,3), (c,3), (d,3), (e,3)))
    }
    def lsdSort(data: Seq[String], lengthOfElement: Int) :Seq[String] = {
        def go(xx : Seq[String], count : Int) : Seq[String] = 
            count match {
                case -1 ⇒ xx
                case i ⇒ go(xx.groupBy(_.charAt(i)), count - 1)
            }
        go(data, lengthOfElement)
    }
}
*/
