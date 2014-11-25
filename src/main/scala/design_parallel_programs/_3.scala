package beginners

import java.util.concurrent._

case class Timeout(length: Long, unit: TimeUnit) // there's another one in akka.util.Timeout but i didn't want to bring in that namespace.

object Par {
    type Par[A] = ExecutorService => Future[A]

    implicit val timeout = Timeout(2000L, TimeUnit.MILLISECONDS)

    private case class UnitFuture[A](get: A) extends Future[A] {
        def isDone = true
        def get(timeout: Long, units: TimeUnit) = get
        def isCancelled = false
        def cancel(evenIfRunning : Boolean) : Boolean = false
    }

    // `unit` promotes a constant value to a parallel computation
    def unit[A](a: A) : Par[A] = (es:ExecutorService) => UnitFuture(a)

    // `map2` combines the result of two parallel computations with a binary function
    def map2[A,B,C](a: Par[A], b:Par[B])(f: (A,B) => C) : Par[C] = 
        (es: ExecutorService) => UnitFuture(f(a(es).get, b(es).get))

    // respecting timeouts
    def map2WithTimeout[A,B,C](a: Par[A], b:Par[B])(f: (A,B) => C)(implicit remainingtime : Timeout) : Par[C] = 
        (es: ExecutorService) => {
            // deal with a few scenarios
            val curr = System.currentTimeMillis 
            val af = a(es)
            val now = System.currentTimeMillis 
            val timeTook = now - curr
            timeTook > remainingtime.length match {
                case true => throw new TimeoutException("out of time!")
                case false => 
            }
            val curr2 = System.currentTimeMillis
            val bf = b(es)
            val now2 = System.currentTimeMillis
            val timeTook2 = now2 - curr2
            timeTook2 > (remainingtime.length - timeTook) match {
                case true => throw new TimeoutException("out of time!")
                case false => 
            }
            UnitFuture(f(af.get, bf.get))
        }

    // `fork` marks a computation for concurrent evaluation.
    // the following two forms seem to satisfy the requirement, or do they??
    def fork[A](a: => Par[A]) : Par[A] = 
        (es:ExecutorService) => UnitFuture(a(es).get)

    def fork2[A](a: => Par[A]) : Par[A] = 
        (es:ExecutorService) => es.submit(new Callable[A] { def call = a(es).get })
/*
    // `lazyUnit` wraps its unevaluated argument in a Par and marks it for concurrent evaluation
    def lazyUnit[A](a: => A) : Par[A] = fork(unit(a))
    // `run` extracts a value from a Par by actually performing the computation
    def run[A](es:ExecutorService)(a: Par[A]) : A = 
*/
}

