
object PairDemo {

	class Pair[A,B](x: A, b: B) {
        def fst = x
        def snd = b
    }
	
	type +[A,B] = Pair[A,B]
	
	object Pair {
	    def apply[A,B](x: A, y: B) = new Pair(x,y)
	}

}

object TestMappingOfPairs extends App {
    import PairDemo._

    val listOfPairs: List[String + Int] = List(Pair("a", 1), Pair("b", 2))
    val incrementByOne = listOfPairs map (pair => pair.snd + 1)
    println(s"$incrementByOne")
}

