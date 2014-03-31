
/**
trait IO { self ⇒ 
    def run : Unit 
    def ++(io: IO) = new IO {
        def run = { self.run; io.run }
    }
}
object IO {
    def empty = new IO { def run = () }
}
*/

/** 
    The previous trait IO suppress the developer from
    deciding what the `input` should be. This version 
    should serve nicely from `fpinscala.iomonad` package
*/

import fpinscala.iomonad._

trait IO[A] { self ⇒
    def run : A 
    def map[B](f: A ⇒ B) : IO[B] = new IO[B] { def run = f(self.run) }
    def flatMap[B](f: A ⇒ IO[B]) : IO[B] = new IO[B] { def run = f(self.run).run }

}

object IO extends Monad[IO] {
    def unit[A](a: ⇒ A) : IO[A] = new IO[A] { def run = a }
    def apply[A](a : ⇒ A) : IO[A] = unit(a)
    def flatMap[A,B](ioa: IO[A])(f: A ⇒ IO[B]) = ioa flatMap f
}

object Game {

	def PrintLine[A](msg: A) : IO[A] = new IO[A] { def run = {println(msg);msg} }
	def contest(p1: Player, p2: Player) : IO[String] = PrintLine(winnerMsg(winner(p1, p2)))
	
	def winnerMsg(p: Option[Player]) : String = 
	    p map {
	        case Player(name, _ ) ⇒ s"${name} is the winner!"
	    } getOrElse "It's a draw"
	
	
	case class Player(name: String, score: Int)
	
	def winner(p1: Player, p2: Player) : Option[Player] = 
	    if (p1.score > p2.score) Some(p1)
	    else if (p2.score > p1.score) Some(p2) else Some(p1)

    // An update.
    def ReadLine : IO[String] = IO { readLine }
    def PrintLine(msg: String) = IO { println(msg) }

    def converter : IO[Unit] = for {
        _ ← PrintLine("Enter a temperature in degrees:")
        d ← ReadLine.map(_.toDouble)
        _ ← PrintLine(d.toString)
    } yield() 

    /**
      
    val factorialREPL: IO[Unit] = sequence_(
        IO { println(helpstring) },
            doWhile { IO { readLine } } { line =>
                when (line != "q") { for {
                n <- factorial(line.toInt)
                _ <- IO { println("factorial: " + n) }
        } yield () }
    })
    */
}


