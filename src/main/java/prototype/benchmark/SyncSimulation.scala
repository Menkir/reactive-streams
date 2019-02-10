package prototype.benchmark

import java.lang.management.ManagementFactory
import java.net.InetSocketAddress
import java.util.Scanner

import com.typesafe.scalalogging.Logger
import prototype.sync.client.Car
import prototype.sync.server.Server

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Future, Promise}
import scala.util.{Failure, Random, Success, Try}
class SyncSimulation() extends Simulation {
  var server: Server = _
  val logger = Logger[SyncSimulation]
  val host = new InetSocketAddress("192.168.0.199", 1338)
  val list  = List.tabulate(10)(n => Random.nextInt(5000)+2000)

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
    } else if(local && !singleThreaded){
      logger.info("START LOCAL MULTI THREAD BENCHMARK")
      startServer()
      warmup()
      //val results = todo multi threadng implementation

    } else if(!local && !singleThreaded){
      logger.info("START REMOTE MULTI THREAD BENCHMARK")
    }
  }

  override def benchmark(runtime: Int): Int ={
    logger.info("START BENCHMARK")
    val car = new Car(host)
    Future{
      car.connect()
      car.send()
    }

    Thread sleep runtime
    logger.info("Throughput: {} processed requests", car.getFlowrate)
    logger.info("END BENCHMARK")
    car.close()
    car.getFlowrate
  }

  override def warmup(): Unit={
    logger.info("WARMUP")
    val car = new Car(host)
    car.connect()
    car.send(50000) // should be 500_000
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
  }
}
