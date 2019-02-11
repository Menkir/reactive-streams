package prototype.benchmark
import java.net.InetSocketAddress
import java.util.Scanner
import com.typesafe.scalalogging.Logger
import prototype.sync.client.Car
import prototype.sync.server.Server

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Random
class SyncSimulation() extends Simulation {
  var server: Server = _
  val logger: Logger = Logger[SyncSimulation]
  val host = new InetSocketAddress("localhost", 1338)
  val list  = List.tabulate(8)(n => Random.nextInt(2000)+5000)

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

      Future.sequence(list.map(runtime => {
        Thread sleep 100 // Zeitverögerung, da Server Socket sonst überwältigt wird https://stackoverflow.com/a/48442827
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
    }
  }

  override def benchmark(runtime: Int): Int ={
    logger.info("START BENCHMARK")
    val car = new Car(host)
    Future {
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
    car.send(500000)
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
