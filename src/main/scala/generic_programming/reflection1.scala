import scala.reflect._

import scala.reflect.runtime.universe._

class SomeObject { def hehe = "Hehe" }

object A {
  implicit def me1[A](x: A)(implicit tag: TypeTag[A]) = { println(s"type = ${typeOf[A] == tag.tpe}"); new SomeObject}
}

object Test {

  import A._

  def getInnerType[T](list:List[T])(implicit tag:TypeTag[T]) = tag.tpe.toString
  def getPairType[T,U](pair:(T,U))(implicit tag:TypeTag[T], tag2: TypeTag[U]) = (tag.tpe.toString,tag2.tpe.toString)

  def print_1 = println(s"Type of List[X] is ${getInnerType(List(1.0f))}")
  def print_2 = println(s"Type of Pair[A,B] is ${getPairType((1, Array(2)))}")

  def print_3 = println(s"Type of ... is ${444.hehe}")
}


