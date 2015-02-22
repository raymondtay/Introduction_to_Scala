import scala.concurrent._

/*
    What is meant by a 'regular' register is the following 2 conditions
    i) it is never the case that R(i) -> W(i) i.e no read call returns a value from the future
    ii) it is never the case that for some j W(i) -> W(j) -> R(i) i.e no read call returns a value from the distant past
        that is , one that precedes the most recently written non-overlapping value. 

    An atomic register satisfies 1 additional condition
    iii) if R(i) -> R(j) then i <= j i.e. an earlier read cannot return a value later than that returned by a later read.

    But what it also means is that writes do not happen atomically. Instead, while 
    the write call is in progress, the values being read may "flicker" between old and new
    before being replaced by the new value
*/
class RegMRSWRegister extends Register[Byte] {
    
    private val RANGE = Byte.MaxValue - Byte.MinValue + 1
    var r_bit = Array.fill[Boolean](RANGE)(false)
    r_bit(0) = true

    def write(x : Byte) {
        r_bit(x) = true
        for { i <- x - 1 to 0 } { r_bit(i) = false }
    }    

    def read() : Byte = r_bit.indexWhere(_ == true) toByte
}


object RegMRSWRegisterDemo extends App {
    import java.util.concurrent._
    import scala.util.Random._
    import scala.collection.JavaConverters._
    val register = new RegMRSWRegister
    val ec = ExecutionContext.fromExecutorService(new ForkJoinPool)

    val tasks = Seq.fill[Callable[Unit]](256)(makeCallable)

    ec.invokeAll(tasks.toList asJava)

    def makeCallable = new Callable[Unit] {
        def call: Unit = {
            val b = nextInt(256).toByte
            register.write(b)
            assert(b == register.read)
        }
    }
}

 
