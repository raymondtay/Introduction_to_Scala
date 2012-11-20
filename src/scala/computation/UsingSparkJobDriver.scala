import java.util.Calendar
import java.util.concurrent._
import com.iplabs.analytics.support._
import scala.concurrent._
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import akka.actor._

import com.weiglewilczek.slf4s.Logging
import spark._
import SparkContext._

object UC12Driver extends App with Logging {

  import UC12DriverUtils._

  private val usage = """to run local: scala UC12Driver local intervalToRunEachJob [initialDelay]
                         to run akka : scala UC12Driver akka intervalToRunEachJob [initialDelay]
                      """

  override def main(args: Array[String]) : Unit = { 
    args(0) match {
      case "local" =>  
                    runLocalJobs()
      case "akka"  =>  
                    runAkkaJobs()
    }   
  }
  def runLocalJobs() = { 
    val jobRef5: LocalJob = SparkJob[LocalJob](onceEvery5mins, 5 minutes)
    val jobRef15: LocalJob = SparkJob[LocalJob](onceEvery15mins, 15 minutes)
    val jobRef60: LocalJob = SparkJob[LocalJob](onceEvery60mins, 60 minutes)
  }
  def runAkkaJobs() = { 
    val system = ActorSystem("UC12Driver")
    val jobRef5: AkkaJob = SparkJob[AkkaJob](onceEvery5mins, 5 minutes)
    val jobRef15: AkkaJob = SparkJob[AkkaJob](onceEvery15mins, 15 minutes)
    val jobRef60: AkkaJob = SparkJob[AkkaJob](onceEvery60mins, 60 minutes)
  }
  def onceEvery5mins() = { 
    useSparkContext( "ip-10-135-29-74", "uc12-driver", "$SPARK_HOME", Seq.empty[String]){
      sparkContext =>  
            val (year, month, day, hour, minute) = getDateBy(-5)
            val data : PairRDDFunctions[String,Int] = sparkContext.sequenceFile[String,Int]("hdfs://ip-10-135-29-74/user/               intelligentpipe/data/*_${year}${month}${day}${hour}${minute}*.data")
            val map = data.collectAsMap
    
            // process the data
            // write it back to hdfs as an aggregated result
    }
  }

  def onceEvery15mins() = {
    throw new NotImplementedError("Not implemented")
  }

  def onceEvery60mins() = {
    throw new NotImplementedError("Not implemented")
  }

}

object UC12DriverUtils {

  def getDateBy(amount: Int) : (Int,Int,Int,Int,Int) = {
    val currentDate = Calendar.getInstance()
    currentDate.add(Calendar.MINUTE, amount)
    val year  = currentDate.get(Calendar.YEAR)
    val month = currentDate.get(Calendar.MONTH)
    val day   = currentDate.get(Calendar.DAY_OF_MONTH)
    val hour  = currentDate.get(Calendar.HOUR_OF_DAY)
    val minute = currentDate.get(Calendar.MINUTE)
    (year, month, day, hour, minute)
  }

  def useSparkContext(master: String, 
                                jobName: String, 
                                sparkHomeDir:String,                                                                                    
                                classPath: Seq[String])(fn : SparkContext => Unit) = {
    val sc = createSparkContext(master, jobName, sparkHomeDir, classPath)
    try {
      fn(sc)
    } finally {
      sc.stop()
    }
  }

  def createSparkContext(master:String, 
                                   jobName:String, 
                                   sparkHomeDir:String, 
                                   classpath:Seq[String]) : SparkContext = {
    new SparkContext(
      master,
      jobName,
      sparkHomeDir,
      classpath
    )
  }
}