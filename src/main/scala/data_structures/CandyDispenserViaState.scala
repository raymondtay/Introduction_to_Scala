
import StateObj._


object CandyDispenserViaStateM extends App {

    val start : State[Machine,(Int,Int)] = State((m: Machine) => ((m.candies, m.coins),m))

    def OnCoin = 
    State(
        (m: Machine) =>
            m.candies match {
                case x if x > 0 => 
                    println(s"Dispensing candy on coin..., b4 ${m.candies}, after: ${m.candies -1}") 
                    ((m.candies -1 , m.coins + 1),Machine(false, m.candies - 1, m.coins + 1))
                case _ =>
                    println("No more candy. do nothing.")
                    ((m.candies, m.coins),Machine(false, m.candies, m.coins))
            }
    )
    def OnTurn = 
    State(
        (m: Machine) =>
            m.locked match {
                case true  => println("Machine is locked..");   ToUnlockNDispense(m)
                case false => println("Machine is unlocked.."); ToDispense(m) 
            }
    )
    def ToDispense(m: Machine) = 
    State(
        (m: Machine) => 
            m.candies match {
                case x if x > 0 => 
                    println(s"Dispensing candy on coin..., b4 ${m.candies}, after: ${m.candies -1}") 
                    ((m.candies -1 , m.coins),Machine(m.locked, m.candies - 1, m.coins))
                case _ =>
                    println("No more candy. do nothing.")
                    ((m.candies, m.coins),Machine(m.locked, m.candies, m.coins))
            }
    ).run(m)
    def ToUnlockNDispense(m: Machine) = ToDispense(Machine(false, m.candies, m.coins))

    def simulateMachine(inputs: List[Input]): State[Machine, (Int,Int)] = {
		def go(li: List[Input])(m: State[Machine,(Int,Int)]) : State[Machine,(Int,Int)] = {
		    li match {
		        case (h:Coin) :: t => println("A coin is inserted..."); go(t)(OnCoin)
		        case (h:Turn) :: t => println("The knob is turned..."); go(t)(OnTurn)
		        case Nil => m
		    }
		}
		go(inputs)(start)
    }
    override def main(arg: Array[String]) = {
	    val size = 30
        val m = Machine(locked = false, candies = 10, coins = 0)
	    def pickCoinOrTurn = {
	        import scala.util.Random._
	        Seq(Coin(),Turn())(nextInt(2))
	    }
	    val inputs : Seq[Input] = Seq.fill(size)(pickCoinOrTurn)
	    println(simulateMachine(inputs toList).run(m))
    }
    
}

