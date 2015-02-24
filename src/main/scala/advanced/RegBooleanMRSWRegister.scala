import scala.concurrent._

trait Register[T] { 
    def read : T
    def write(v : T) : Unit 
}

class RegBooleanMRSWRegister extends Register[Boolean] {
    var s_value = false
    val last : ThreadLocal[Boolean] = 
        new ThreadLocal[Boolean] {
            override protected def initialValue() = { false }
        }

    def write(x: Boolean) {
        if (x != last.get) {
            last.set(x)
            s_value = x
        }
    }

    def read = s_value
}

object RegBooleanMRSWRegister extends App {
    import java.util.concurrent._
    import scala.collection.JavaConverters._
    val x = new RegBooleanMRSWRegister
    val ec = ExecutionContext.fromExecutorService(new ForkJoinPool)

    var whatToWrite = false
    def task = new Runnable {
        def run = {
            x.write(whatToWrite)
            println(s"Thread-id:${Thread.currentThread.getId} = before: ${whatToWrite}, after:${x.read}")
        }
    }

    def makeCallable(r : Runnable) = 
        new Callable[Unit] {
            def call : Unit = r.run
        }

    val tasks = (task :: { whatToWrite = true; task } :: Nil) map { t ⇒ makeCallable(t) }
    ec.invokeAll(tasks asJava)

}

