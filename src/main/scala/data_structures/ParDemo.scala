object Par2 {
    import java.util.concurrent.{Callable,ExecutorService,Future,TimeUnit}
    import scala.collection.JavaConversions._

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

