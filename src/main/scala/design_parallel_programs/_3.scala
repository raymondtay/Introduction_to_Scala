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
    def map2WithTimeout[A,B,C](a: Par[A], b:Par[B])(f: (A,B) => C)(implicit remainingtime : Timeout) : Par[C] = {
        def measure[A](f: => A) : Tuple3[Long,A,Long] = {
            val curr = System.currentTimeMillis
            val result = f
            val now = System.currentTimeMillis
            (curr, result, now)
        }
        def timeIsOut(left: Long)(right: Long) = 
            left > right match {
                case true => throw new TimeoutException("Out of time!")
                case false =>
            }
        (es: ExecutorService) => {
            val (curr, af, now) = measure(a(es))
            val timeTook = now - curr
            timeIsOut(timeTook)(remainingtime.length)
            val (curr2, bf, now2) = measure(b(es))
            val timeTook2 = now2 - curr2
            timeIsOut(timeTook2)(remainingtime.length - timeTook)
            UnitFuture(f(af.get, bf.get))
        }
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

