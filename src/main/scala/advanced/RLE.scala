package compression
//
// Simple Run-Length Encoding (a.k.a RLE)
//
import scala.io.Source._
import scala.util.matching.Regex
class RLE {
    def compress(s: String) : String = {
        val buffer = collection.mutable.ListBuffer[String]()
        val src = fromString(s)
        var (ele, index) = (src.next, 0)
        src.map{ 
            case c if (c == ele) => index = index + 1
            case c => 
                buffer += Seq(index, ele toString).mkString
                index = index + 1
                ele = c
        }.toList
        buffer += Seq(index, ele toString).mkString
        buffer.mkString
    }
    def decompress(s: String) : String = {
        val pattern = new Regex("""(\d{1,})(.)""", "size", "c")
        var index = 0
        pattern.findAllIn(s).foldLeft(Seq[String]()){
            (acc, p) => 
                pattern.findFirstIn(p) match {
                    case Some(pattern(size, c)) =>  
                        val acc2 = Seq.fill(size.toString.toInt - index + 1)(c) 
                        index = size.toString.toInt + 1
                        acc ++ acc2
                    case None => acc
                }
        } mkString
    }
}

object RLE extends App {
    override def main(args: Array[String]) = {
        val rle = new RLE
        val output = rle.compress(args mkString)
        val input = rle.decompress(output)
        println(s"Before RLE, ${args mkString} and after compression its ${output}\n")
        println(s"After RLE decompression, its ${input} \n")
    } 
}

