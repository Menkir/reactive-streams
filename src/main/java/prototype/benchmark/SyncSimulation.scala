package prototype.benchmark

import java.lang.management.ManagementFactory
import java.net.InetSocketAddress

import com.typesafe.scalalogging.Logger
import prototype.sync.client.Car
import prototype.sync.server.Server

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
class SyncSimulation extends Simulation {
  val logger = Logger[SyncSimulation]
  val host = new InetSocketAddress("192.168.0.199", 1338)

  def run(): Unit = {
    logger.info("START SERVER")
    val server = new Server(host)
    server receive()

    logger.info("WARMUP")
    warmup()

    val list  = List[Int](
      2000,
      2000,
      2000,
      2000,
      2000,
      2000,
      2000,
      2000,
      2000,
      2000,
    )
      .map(runtime => (runtime, benchmark(runtime)))

    printResult(list)
    saveResult(classOf[SyncSimulation].getCanonicalName + ".txt", list)
    server.close()
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
    val runtimeMxBean = ManagementFactory.getRuntimeMXBean
    val listOfArguments = runtimeMxBean.getInputArguments
    var warmupElements = 0
    val argumentQuery = "-XX:CompileThreshold="
    listOfArguments forEach(arg => {
      if (arg.startsWith(argumentQuery))
        warmupElements = Integer.parseInt(arg.slice(argumentQuery.length, arg.length))
    })
    val car = new Car(host)
    car.connect()
    car.send(1000000)
    car.close()
  }
}

object SyncSimulation{
  def main(args: Array[String]): Unit = {
    val simulation = new SyncSimulation
    simulation run()
    sys.exit(0)
  }
}
