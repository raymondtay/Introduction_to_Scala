package sorting 

import scala.util.Sorting._

class StringSort {
    /**
        LSD-sort
        @param data - seq of strings
        @param lengthOfElement 
    */
    def lsd(data : Seq[String], lengthOfElement : Int) : Seq[String] = {
        def go(xx: Seq[String], index : Int) : Seq[String] = index match {
            case -1 ⇒ xx
            case _  ⇒ go(xx.groupBy(_.charAt(index)).values.flatten.toSeq, index - 1)
        }
        go(data, lengthOfElement)
    }

    /** 
        key-indexed counting
        @param xs - seq of strings
        @param ys - seq of ints
    */
    def keyIndexed(xs: Seq[(String,Int)]) : Seq[(String,Int)] = {
        val groupedData = xs.groupBy(_._2)
        val ks = groupedData.keys.toArray
        quickSort(ks)
        (ks map (groupedData(_))).flatten toSeq
    }

    val data = Seq("4PGC938", "2IYE230", "3CIO720", "1ICK750", "1OHV845", "4JZY524", "1ICK750", "3CIO720", "1OHV845", "1OHV845", "2RLA629", "2RLA629", "3ATW723")
    val sortedData = lsd(data, 6)
}

