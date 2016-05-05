import scala.annotation.StaticAnnotation
import scala.language.experimental.macros
import scala.reflect.macros.blackbox.Context

class TalkingAnimalSpell extends StaticAnnotation {
  //
  // NOTE: macroTransform is injected somehow that allows the engine to pick up the actual impl.
  //       You are screwed if you got the name wrong.
  def macroTransform(annottees: Any*) = macro TalkingAnimalSpell.impl
}
object TalkingAnimalSpell {
  def impl(c:Context)(annottees: c.Expr[Any]*): c.Expr[Any] = {
    import c.universe._

    val result = {
      annottees.map(_.tree).toList match {
        case q"$mods class $tpname[..$tparams] $ctorMods(...$paramss) extends Animal with ..$parents { $self => ..$stats }" :: Nil => {
          val animalType = tpname.toString()
          val newMethods = q"""def yum = println(s" This " + favouriteFood + " is yummy!")"""
          q"""$mods class $tpname[..$tparams] $ctorMods(...$paramss) extends Animal with ..$parents{
            $self => ..$stats
            $newMethods
            def sayHello : Unit = { println("Hello there! I'm " + $animalType + " and my name is " + name + ", i like to eat " +
          favouriteFood) } }"""
        }
        case _ => c.abort(c.enclosingPosition, "Annotation @TalkingAnimal can be used only with case classes which extends Animal trait")
      }
    }
    c.Expr[Any](result)
  }
}
