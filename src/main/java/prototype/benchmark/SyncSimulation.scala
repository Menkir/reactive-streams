package prototype.benchmark
import java.lang.Thread._
import java.net.InetSocketAddress
import java.time.Duration
import java.util.concurrent.Executors

import com.typesafe.scalalogging.Logger
import prototype.async.client.CarConfiguration
import prototype.routing.RoutingFactory
import prototype.sync.client.Car
import prototype.sync.server.Server

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ExecutionContext, ExecutionContextExecutor, Future}
class SyncSimulation() extends Simulation {
  var server: Server = _
  val logger: Logger = Logger[SyncSimulation]
  val host = new InetSocketAddress("localhost", 1337)
  val executors: ExecutionContextExecutor = ExecutionContext.fromExecutor(Executors.newFixedThreadPool(8))

  def startServer(): Unit={
    logger.info("START SERVER")
    server = new Server(host)
    server receive()
  }
  def run(config: CarConfiguration = new CarConfiguration()): Unit ={
    logger info "START REMOTE MULTI THREAD BENCHMARK"
    logger info "START WARMUP"
    warmUp()

    Future sequence(durationList map(duration => Future((duration, benchmark(duration, config)))(executors))) onComplete(
      result => {
        printResult(result get)
        saveResult("ReactiveBenchmarkResult.txt", result get)
        logger info "END TEST"
      }
      )
  }

  override def benchmark(runtime: Int, config: CarConfiguration): Int ={
    logger.info("START BENCHMARK")
    val car = new Car(host, new CarConfiguration(Duration.ofMillis(200), RoutingFactory.RouteType.RECTANGLE))

    Future{
      car connect()
      car send()
    }(executors)
    sleep(runtime)
    logger.info("Throughput: {} processed requests", car.getFlowrate)
    logger.info("END BENCHMARK")
    car.close()
    car.getFlowrate
  }

  def warmUp(): Unit={
    logger.info("WARMUP")
    val car = new Car(host)
    car connect()
    car send 1500
    car close()
  }
}

object SyncSimulation{
  def main(args: Array[String]): Unit = {
    Future{
     // new SyncSimulation run
    } onComplete(() => _)
  }
}
