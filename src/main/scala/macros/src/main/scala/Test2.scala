
trait Animal {
  val name : String
}

@TalkingAnimalSpell
case class Dog(name: String, favouriteFood: String) extends Animal { 
  def apport = println("Apporting...")
}

object AnimalMain extends App {

  Dog("barky", "satay").sayHello
  Dog("lucky", "burger").apport

}

