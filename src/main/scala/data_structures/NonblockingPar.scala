/*

    How can we implment a non blocking representation of Par? The idae is simple.
    Instead of turning a Par into a java.util.concurrent.Future that we can get a value 
    out of (which requires blocking), we are going to introduce our own version of 
    Future with which we can register a callback that will be   
    invoked when the result is ready. That's a slight shift in perspective:
*/
package parallelism

object Parallelism {
    import java.util.concurrent.atomic.AtomicReference
    import java.util.concurrent.{CountDownLatch, ExecutorService}

	sealed trait Future[+A] {
        // this code is only accessible within the package `parallelism` which means NO exposure to external parties
	    private[parallelism] def apply(f: A ⇒ Unit) : Unit
	}
	
    def run[A](es: ExecutorService)(p: Par[A]) : A = {
        def ref  = new AtomicReference[A]
        def latch = new CountDownLatch(1)
        p(es) { a ⇒ ref.set(a); latch.countDown } // this is invoking the `apply` method a.k.a registering a callback.
        latch.await
        ref.get
    }
	
	type Par[+A] = ExecutorService ⇒ Future[A] 

}










/* 
    Our Par type looks identical except we are now using our new version of future. 
    which has a different API than the one in java.util.concurrent Rather than calling get
    to obtain the result from our future, our future instead has an apply method that
    receives a funciton k that expects the result of type A and uses it to perform some 
    effect. This kind of function is referred to as a callback or a continuation.

*/
