import scala.reflect.macros.blackbox.Context
import scala.language.experimental.macros

object Welcome {
  def isEvenLog(num: Int) : Unit = macro isEvenLogImpl

  def isEvenLogImpl(c: Context)(num: c.Tree) : c.Tree = {
    import c.universe._
    val result = 
    q"""
      if($num % 2 ==0)
        println($num.toString + " is even.")
      else
        println($num.toString + " is even.")
      """
      println(showCode(result))
      println(showRaw(result))
      result
  }
}

