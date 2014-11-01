
sealed trait Input 
case class Coin() extends Input
case class Turn() extends Input
case class DoNothing()
case class Machine(locked: Boolean, candies : Int, coins:Int)
import StateObj._

/**
    The rules of the machines are as follows:
    + Inserting a coin into a locked machine will cause it to unlock if there's any candy left
    + Turning the knob on an unlocked machine will cause it to dispense candy and become unlocked
    + Turning the knob on a locked machine or inserting a coin into an unlocked machine does nothing.
    + A machine that's out of candy ignores all inputs
*/

object CandyDispenser extends App {

    // This "gold" reference is important as it checks the logic 
    // and helps in getting familiar with the state machine
    def getCandy2(c: Coin)(m: Machine) : Machine =
        m.locked match {
            case true => println("machine locked"); dispenseCandy(Machine(false, m.candies, m.coins))
            case false => println("machine not locked"); dispenseCandy(m)
        }

    def getCandy(t: Turn)(m: Machine) : Machine = 
        m.locked match {
            case true => println("machine locked.Do nothing."); m
            case false => dispenseCandy(m)
        }
    
    def dispenseCandy(m: Machine) = m.candies match {
        case x if x > 0 => 
            println(s"Candy dispensed and now locked, b4 = ${m.candies}, now = ${x -1}.")
            Machine(true, x - 1, m.coins)
        case x if x == 0 => 
            println("No more candy.Do nothing.")
            Machine(m.locked, 0, m.coins)
    }

    def simulateMachine(inputs: List[Input]) {
        val m = Machine(locked = false, candies = 10, coins = 0)

        def go(li: List[Input])(m: Machine) : Machine = {
            li match {
                case (h:Coin) :: t => println("A coin is inserted..."); go(t)(getCandy2(h)(m))
                case (h:Turn) :: t => println("The knob is turned..."); go(t)(getCandy(h)(m))
                case Nil => m 
            }
        }
        go(inputs)(m)
    }

    override def main(arg: Array[String]) = {
	    val size = 30
	    def pickCoinOrTurn = {
	        import scala.util.Random._
	        Seq(Coin(),Turn())(nextInt(2))
	    }
	    val inputs : Seq[Input] = Seq.fill(size)(pickCoinOrTurn)
	    simulateMachine(inputs toList) 
	    //def simulateMachine(inputs: List[Input]) : State[Machine, (Int, Int)] 
    }
}

