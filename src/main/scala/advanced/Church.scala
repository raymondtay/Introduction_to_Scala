
// Church's Numerals in action via Scala
object SimpleChurch extends App {

    def λ = (x:Int) ⇒ x
    def zero_ = λ(0)
    def one_  = zero_ + 1
    def two_  = one_ + 1     
    def three_  = two_ + 1     
    println(s"Zero is ${zero_}, One is ${one_}, Two is ${two_}")

}

