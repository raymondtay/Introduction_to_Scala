import com.weiglewilczek.slf4s.Logging
import akka.actor.Actor
import akka.pattern._
import scala.util._
import scala.concurrent._
import scala.concurrent.duration._
import scala.reflect._
import java.util.concurrent._

trait SparkJob {
  def doWhenSuccess[A](result:A) : Unit
  def doWhenFailed(result:Throwable) : Unit
}

trait AkkaJob extends SparkJob with Actor with Logging 
trait LocalJob extends SparkJob with Logging {
    def useServicePool(fn: ScheduledExecutorService => Unit)(implicit size : Int) = { 
        // not applicable to akka actors? so its included here.
        val es = Executors.newScheduledThreadPool(size)
        try {
            fn(es)
        } finally {
            es.shutdown()
            logger.info("ScheduledExecutorService has shutdown...")
        }   
    }   
}

object SparkJob {
    implicit val poolSize = 1 
    // Gets rid of the those nasty asInstanceOf on the client code
    implicit def convert[T](j: SparkJob) : T = j.asInstanceOf[T]

    def apply[T:ClassTag](fn: () => Unit, 
                          interval     : FiniteDuration,
                          initialDelay : FiniteDuration = 1000 millis) = { 
       classTag[T].runtimeClass == classOf[AkkaJob] match {
         case true => new AkkaJob {
            protected[support] val TICK_MESSAGE = "tick"
            import context._
            private val tick = context.system.scheduler.schedule(initialDelay, interval, self, TICK_MESSAGE)
            private[this] def func = future {fn}

            func onComplete {
              case Success(t) => doWhenSuccess(t)
              case Failure(t) => doWhenFailed(t)
            }   
            def receive = {
                case TICK_MESSAGE => func.value
                /*
                case TICK_MESSAGE => func.value match {
                                        case None             => logger.info(">>> Run hasn't completed by the timeslot")
                                        case Some(Success(t)) => logger.info(">>> Run completed. <<<")
                                        case Some(Failure(t)) => logger.info("Run failed with exception message:" + t.getMessage)
                                     }
                */
            }
            def doWhenSuccess[A](r: A) = logger.info(">>> Run completed. <<<")
            def doWhenFailed(result:Throwable)  = logger.info(s">>> Run failed with exception message: ${result.getMessage} <<<")
            override def postStop(): Unit = {
                tick.cancel()
            }

         }
         case false => new LocalJob {
           useServicePool{ pool => val fTask = pool.scheduleAtFixedRate(new Runnable { def run() = {fn()}}, 
                                                            initialDelay.length, 
                                                            interval.length, 
                                                            interval.unit) 
                                   try {
                                     doWhenSuccess(fTask.get())
                                   } catch {
                                     case t: Throwable => doWhenFailed(t)
                                   }}                                                                                                   
           def doWhenSuccess[A](r: A) = logger.info(">>> Run completed. <<<")
           def doWhenFailed(result:Throwable)  = logger.info(s">>> Run failed with exception message: ${result.getMessage} <<<")
         }
       }
    }
}
