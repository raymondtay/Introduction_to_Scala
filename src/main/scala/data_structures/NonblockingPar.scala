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

    def chooser[A,B](par: Par[A])(choices: A ⇒ Par[B]) : Par[B] = es ⇒ {
        import scala.concurrent.SyncVar
        val item = new SyncVar[Par[B]]
        eval(es)(par(es){ k ⇒ item.put(choices(k)) })
        item.get(es)
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
// SIDEBAR:
// Recognizing the expressiveness and limitations of an algebra
// As you practice more functional programming, one of the skills you will
// develop is the ability to recognize what funcitons are expressible from
// an algebra, and what the limitations of that algebra are. For instance, in the 
// above example, it may not have been obvious at first that afunciton
// like `choose` could not be expressed purely in terms of `map`, `map2` and unit 
// and it may not have been obvious that `choose` was just a special case of `flatMap`
// Over time, observations like this will come very quickly, and you will also get 
// better at spotting how to modify your algebra to make some needed combinator expressible
// These skills will be helpful for all of your API Design work.
// As a practical consideration, being able to reduce an API to a minmal set of primitvie funcitons
// is extremely useful. As we noted earlier when we implmented parMap in terms of existing 
// combinators, it's frequently the case that primitive combinators encapsulate some rather
// tricky logic, and reusing them means we don't have to duplicate this logic.

