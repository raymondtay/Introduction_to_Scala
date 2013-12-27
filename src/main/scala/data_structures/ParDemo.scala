object Par2 {
    import java.util.concurrent.{Callable,ExecutorService,Future,TimeUnit}
    import scala.collection.JavaConversions._

    class ParOps[A](p: Par[A]) {
        def map2[B,C](b: Par[B])(f: (A, B) ⇒ C) = Par2.map2(p, b)(f) 
        def map[B](f: A ⇒ B) : Par[B] = Par2.map(p)(f)
    }

    // Forgoing the type definition in // -- 1 -- // 
    // we could delay the computation by using an ExecutorService 
    // which returns a Future[Something]; it has another benefit which is the
    // ability to cancel the computaiton if its desired.
    type Par[A] = ExecutorService ⇒ Future[A]
    def run[A](es: ExecutorService)(a: Par[A]) : Future[A] = a(es)

    def unit[A](a: A) : Par[A] = (es: ExecutorService) ⇒ UnitFuture(a)

    private case class UnitFuture[A](get: A) extends Future[A] {
        // implemented the methods in the interface, java.util.concurrent.Future
        def isDone = true
        def get(timeout: Long, units: TimeUnit) = get
        def isCancelled = false
        def cancel(evenIfRunning: Boolean) : Boolean = false
    }

    // The following `map` function can be lifted to the `map` function definition below
    // following our example with `sortPar`
    // def map[A,B](a: Par[A])(f: A ⇒ B) : Par[B] = (es: ExecutorService) ⇒ UnitFuture(f(a(es).get))

    def map[A,B](a: Par[A])(f: A ⇒ B) : Par[B] = map2(a, unit(()))( (a, _) ⇒ f(a) )

    // Does not respect timeouts and to do that, we need a new implementation
    // that DOES respect timeout see `map2t`
    def map2[A,B,C](a: Par[A], b: Par[B])(f: (A, B) ⇒ C) : Par[C] = 
        (es: ExecutorService) ⇒  UnitFuture(f( a(es).get, b(es).get ))
    
    /*
        Here's the `test` you can run and noticed that the same ExecutorService is being used
        to compute the two expressions in sequence and not in parallel.
		scala> val a : Par[Int] = (es: ExecutorService) ⇒ es.submit(new Callable[Int] { def call = {Thread.sleep(15000); 44}})
		scala> val b : Par[Int] = (es: ExecutorService) ⇒ es.submit(new Callable[Int] { def call = {Thread.sleep(20000);55}})
		scala> map2t(a, b)(35)(_ + _)(new ForkJoinPool)
    */

    def map2t[A,B,C](a: Par[A], b: Par[B])(timeout: Long)(f: (A, B) ⇒ C) : Par[C] = 
        (es: ExecutorService) ⇒  UnitFuture(f( ((forkWithTimeout(a)(timeout))(es)).get , ((forkWithTimeout(b)(timeout))(es)).get ))

    def forkWithTimeout[A](a: ⇒ Par[A])(timeout: Long) : Par[A] = (es: ExecutorService) ⇒ 
        es.invokeAll(Seq(new Callable[A] { def call = a(es).get }), timeout, TimeUnit.SECONDS).head

    def fork[A](a: ⇒ Par[A]) : Par[A] = (es: ExecutorService) ⇒ 
        es.submit(new Callable[A] { def call = a(es).get } ) 

    def asyncF[A,B](f: A ⇒ B): A ⇒ Par[B] = (a: A) ⇒ unit(f(a))

    // With the rewrite of the `map` function a few lines above this, we can 
    // now lift `sortPar` to the following expression instead of our original :
    // def sortPar(parList: Par[List[Int]]) : Par[List[Int]] = map2( parList, unit(()) )( (a, _) ⇒ a.sorted ) 
    def sortPar(parList: Par[List[Int]]) : Par[List[Int]] = map(parList)(_.sorted ) 

    def flatMap[A,B](a: Par[A])(f: A ⇒ List[B]) : Par[List[B]] = map(a)(f)

    def parMap[A,B](l: List[A])(f: A ⇒ B) : Par[List[B]] = fork {
        sequence(l.map(asyncF(f)))
    }

    def parFilter[A](l : List[A])(f: A ⇒ Boolean) : Par[List[A]] = fork {
        map(sequence(l.map(asyncF((a:A) ⇒ if (f(a)) List(a) else List()))))(_.flatten)
    }
    def sequence[A](l: List[Par[A]]) : Par[List[A]] = l.foldRight[Par[List[A]]](unit(List()))((h,t) ⇒ map2(h, t)(_ :: _))

    /* //-- 1 --/
    trait Par[A] {
        // for taking an unevaluated `A` and returning a 
        // computation that might evaluate in a separate thread. We called it
        // "unit" because it in a sense creates a unit-of-parallelism that just
        // wraps a single value
        def unit[A](a: ⇒ A ) : Par[A]

        // for extracting the resulting value from a parallel computation
        def run[A](a: Par[A]) : A

        def run[A](es: ExecutorService)(a: Par[A]) : A 

        // The ability to express explicit parallelism where it makes sense.
        def fork[A](a: ⇒ Par[A]) : Par[A]

        // Combines two parallel computations with a binary operator
        def map2[A,B,C](a: Par[A], b: Par[B])(f: (A,B) ⇒ C) : Par[C]

        // Create a computation that results in concurrent evaluation of `a`
        def async[A](a: ⇒ A) : Par[A] = fork(unit(a))
    }
    */
}

