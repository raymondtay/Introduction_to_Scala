
package impliciteffects

trait Session {
    val s : Int
    def get : Int = s
    def close : Unit = "closed"
}
class BaseSession extends Session { override val s = 42 }
object UseSession extends App {

    def createSession() = new BaseSession

    def withSession[T](f: Session => T) : T = {
        //val s = createSession() // comment this line and 21, 22 
        val s = implicitly[Session]
        println(s"\nSESS ID = ${s}")
        try { f(s) } finally s.close
    }

    implicit val v = createSession
    println(s"\nimplicit session is $v")
    withSession{ implicit s : Session =>
        println(s"Session is ${s}, value of session is ${s.get}")
    }
}

