package prototype.benchmark
import java.lang.management.ManagementFactory
import java.net.InetSocketAddress

import com.typesafe.scalalogging.Logger
import prototype.async.client.Car
import prototype.async.server.Server

import scala.util.Random
class AsyncSimulation extends Simulation{
  val logger: Logger = Logger[AsyncSimulation]
  val host = new InetSocketAddress("127.0.0.1", 1337)
  def run(): Unit = {
    logger.info("START SERVER")
    val server = new Server(host)
    server receive()

    logger.info("WARMUP")
    warmup()
    val list  = List.tabulate(10)(n => Random.nextInt(500)+500)
                    .map(runtime => (runtime, benchmark(runtime)))

    logger.info("CLEAN UP")
    server dispose()
    printResult(list)
    saveResult(classOf[AsyncSimulation].getCanonicalName + ".txt", list)
  }

  def benchmark(runtime: Int): Int ={
    logger.info("START BENCHMARK")
    val car = new Car(host)
    car connect()
    car requestChannel()
    val disposable = car subscribeOnServerEndpoint()
    Thread sleep runtime
    logger info("Throughput: {} processed requests", car getFlowrate)
    logger info "END BENCHMARK"
    disposable dispose()
    car shutDown()
    car getFlowrate
  }

  def warmup(): Unit={
    val iterations = 3000000
    val car = new Car(host)
    car connect()
    car requestChannel()
    val disposable = car subscribeOnServerEndpoint()
    while(car.getFlowrate < iterations){
      Thread.sleep(1)
      //println(car.getFlowrate)
    }
    disposable.dispose()
    car.shutDown()
  }
}

object AsyncSimulation{
  def main(args: Array[String]): Unit = {
    val simulation = new AsyncSimulation
    simulation run()
    sys.exit(0)
  }
}
