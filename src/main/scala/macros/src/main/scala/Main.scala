import scala.util.Random._

object Main extends App {
  Welcome.isEvenLog(12)

  println(":: ------------------ ::")

  val (x, y) = (3, 4)
  Welcome.isEvenLog(x + y)

  println(":: ------------------ ::")

  Welcome.isEvenLog(x + nextInt)

}

