package prototype.benchmark
import java.net.InetSocketAddress
import java.util.concurrent.Executors

import com.typesafe.scalalogging.Logger
import prototype.async.client.{Car, CarConfiguration}
import prototype.interfaces.{ICar, IServer}

import scala.concurrent.{ExecutionContext, ExecutionContextExecutor, Future}
import scala.concurrent.ExecutionContext.Implicits.global

class AsyncSimulation extends Simulation{
  val logger: Logger = Logger[AsyncSimulation]
  val hostInfo = new InetSocketAddress("127.0.0.1", 1337)
  var server: IServer = _
  var carClass: ICar = _
  val executors: ExecutionContextExecutor = ExecutionContext.fromExecutor(Executors newFixedThreadPool 8)


  def startServer(): Unit={
    logger.info("START SERVER")
    server receive()
  }

  def run(config: CarConfiguration = new CarConfiguration()): Unit = {
      logger info "START TEST"
      warmUp()
      Future sequence(durationList map(duration => Future((duration, benchmark(duration, config)))(executors))) onComplete(
        result => {
          printResult(result get)
          saveResult("ReactiveBenchmarkResult.txt", result get)
          logger info "END TEST"
        }
      )
  }

  def benchmark(runtime: Int, config: CarConfiguration): Int ={
    logger.info("START BENCHMARK")
    val car = new Car(hostInfo)
    car connect()
    car send()
    Thread sleep runtime
    logger info("Throughput: {} processed requests", car getFlowRate)
    logger info "END BENCHMARK"
    car close()
    car getFlowRate
  }

  def warmUp(): Unit={
    logger.info("WARMUP")
    val iterations = 1500
    val car = new Car(hostInfo)
    car connect()
    car send()
    while(car.getFlowRate < iterations){
      Thread sleep 1
    }
    car close()
  }
}

object AsyncSimulation{
  // Be sure you running this benchmark on -client JVM Argument otherwise no optimazation is guaranteed
  def main(args: Array[String]): Unit = {
    val simulation = new AsyncSimulation()
    simulation.run()

  }
}
