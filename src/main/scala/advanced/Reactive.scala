import scala.language._

// Drew my inspiration from 
// http://www.reactivemanifesto.org/#reactive-applications
// It's not done yet.

trait Reactive[I,O]

trait Stimulus[S] extends Reactive[S,Nothing] {
    val repr : S
    def trigger : S 
}

object Stimulus {
    def apply[S](s: S) = new Stimulus[S] { 
        override val repr = s
        def trigger = repr
    }
}

trait Response[S, R] extends Reactive[S,R] {
    val reprS : S 
    def respond(s: S, f: S => R) : R = f(s)
}

object Response {
    def apply[S <: Stimulus[_],R](stimuli : S, f: => S => R) = 
        new Response[S,R] {
            override val reprS = stimuli
            def run = respond(reprS, f)
        }
}

object TestSystem extends App {

    val result = Response(Stimulus("This is a simple string that will be printed on the output"), (s:Stimulus[String]) => println(s.trigger))

    println(s"Result is => ${result.run}")
}

