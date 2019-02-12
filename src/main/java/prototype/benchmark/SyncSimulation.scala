package prototype.benchmark
import java.lang.Thread._
import java.net.InetSocketAddress
import java.util.Scanner
import java.util.concurrent.{ExecutorService, Executors, ThreadPoolExecutor}

import com.typesafe.scalalogging.Logger
import prototype.sync.client.Car
import prototype.sync.server.Server

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Random
class SyncSimulation() extends Simulation {
  var server: Server = _
  val logger: Logger = Logger[SyncSimulation]
  val host = new InetSocketAddress("localhost", 1338)
  val list  = List(
    1000,
    2000,
    4000,
    8000,
    16000,
    32000,
    64000,
    128000,
    256000,
    512000
  )
  val context = ExecutionContext.fromExecutor(Executors.newFixedThreadPool(8))

  def startServer(): Unit={
    logger.info("START SERVER")
    server = new Server(host)
    server receive()
  }
  def run(local: Boolean, singleThreaded: Boolean): Unit ={
    if(local && singleThreaded){
      logger.info("START LOCAL SINGLE THREAD BENCHMARK")
      startServer()
      warmup()
      val result = list.map(runtime => (runtime, benchmark(runtime)))
      server.close()
      printResult(result)
      saveResult(classOf[SyncSimulation].getCanonicalName + ".local.single.txt", result)
    } else if(!local && singleThreaded){
      logger.info("START REMOTE SINGLE THREAD BENCHMARK")
      warmup()
      val result = list.map(runtime => (runtime, benchmark(runtime)))
      printResult(result)
      saveResult(classOf[SyncSimulation].getCanonicalName + ".remote.single.txt", result)
    } else if(local && !singleThreaded){
      logger.info("START LOCAL MULTI THREAD BENCHMARK")
      startServer()
      warmup()

      Future.sequence(list.map(runtime => {
        Future((runtime, benchmark(runtime)))
      })).onComplete(
        result => {
          printResult(result.get)
          saveResult(classOf[SyncSimulation].getCanonicalName + ".local.multi.txt", result.get)
          server close()
        }
      )
    } else if(!local && !singleThreaded){
      logger.info("START REMOTE MULTI THREAD BENCHMARK")
      warmup()

      Future.sequence(list.map(runtime => {
        Future((runtime, benchmark(runtime)))
      })).onComplete(
        result => {
          printResult(result.get)
          saveResult(classOf[SyncSimulation].getCanonicalName + ".remote.multi.txt", result.get)
        }
      )
    }
  }

  override def benchmark(runtime: Int): Int ={
    logger.info("START BENCHMARK")
    val car = new Car(host)

    Future{
      car connect()
      car send()
    }(context)
    sleep(runtime)
    logger.info("Throughput: {} processed requests", car.getFlowrate)
    logger.info("END BENCHMARK")
    car.close()
    car.getFlowrate
  }

  override def warmup(): Unit={
    logger.info("WARMUP")
    val car = new Car(host)
    car.connect()
    car.send(1500)
    car.close()
  }
}

object SyncSimulation{
  def main(args: Array[String]): Unit = {
    var local = true //default
    var singleThreaded = true //default

    args.foreach {
      case "remote" => local = false
      case "multi" => singleThreaded = false
    }

    var simulation: SyncSimulation = new SyncSimulation
    simulation run(local, singleThreaded)
    val scanner = new Scanner(System.in)

    while(scanner.hasNext()){

    }
    sys exit 0
  }
}
