package parallelism

object Parallel2 {
    import java.util.concurrent.{Callable,CountDownLatch, Executors, ExecutorService}
    import java.util.concurrent.atomic._

	sealed trait Future[+A] {
	    private[parallelism] def apply(cb: A ⇒ Unit) : Unit
	}
	
	type Par[+A] = ExecutorService ⇒ Future[A]

    def run[A](es: ExecutorService)(p: Par[A]) : A = {
        val ref = new AtomicReference[A]
        val latch = new CountDownLatch(1)
        p(es) { a ⇒ ref.set(a); latch.countDown }
        latch.await
        ref.get
    }

    // Choosing which parallel computation to proceed depending on the 
    // outcome of the forked evaluation of a conditional encoded using `Par`
    def choice[A](cond: Par[Boolean])(whenTrue: Par[A], whenFalse: Par[A]): Par[A] =
        es ⇒ {
            var result : Boolean = false // is this cheating w.r.t fp paradigm?
            eval(es)(cond(es){ a ⇒ result = a })
            result match {
                case true  ⇒ whenTrue(es)
                case false ⇒ whenFalse(es)
            }
        }

    def choiceN[A](n: Par[Int])(choices: List[Par[A]]): Par[A] = 
        es ⇒ {
            var index = 0 // is this cheating w.r.t fp paradigm?
            eval(es)(n(es){ a ⇒ index = a })
            choices(index)(es)
        }

    def unit[A](a: A): Par[A] = 
        es ⇒ new Future[A] {
                def apply(cb: A ⇒ Unit) = cb(a)
             }

    def fork[A](a: ⇒ Par[A]) : Par[A] = 
        es ⇒ new Future[A] {
                def apply(cb: A ⇒ Unit) : Unit = eval(es)(a(es)(cb))
             }


    def eval(es: ExecutorService)(r: ⇒ Unit) : Unit = 
        es.submit(new Callable[Unit] { def call = r })
}
