import scala.reflect.macros.blackbox.Context
import scala.annotation.StaticAnnotation
import scala.language.experimental.macros 

class Benchmark extends StaticAnnotation {
  def macroTransform(annottees: Any*) = macro Benchmark.impl
}

object Benchmark {
  def impl(c: Context)(annottees: c.Expr[Any]*): c.Expr[Any] = {
    import c.universe._

    val result = {
      annottees.map(_.tree).toList match {
        case q"$mods def $methodName[..$tpe](...$args): $returnType = {..$body}":: Nil => {
          q"""
            $mods def $methodName[..$tpe](...$args): $returnType = {
              val start = System.nanoTime()
              val result = {..$body}
              val end = System.nanoTime()
              println(${methodName.toString} + " elapsed time: " + (end - start) + " ns")
              result
            }
          """
        } 
        case _ => c.abort(c.enclosingPosition, "Annotation @Benchmark can be used only with methods")
      }
    }
    c.Expr[Any](result)
  }
}

