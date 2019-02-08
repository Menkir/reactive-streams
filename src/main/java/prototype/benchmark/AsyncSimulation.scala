package prototype.benchmark

import java.io.{ByteArrayOutputStream, PrintStream}
import java.lang.management.ManagementFactory
import java.net.InetSocketAddress

import com.typesafe.scalalogging.Logger
import prototype.async.client.Car
import prototype.async.server.Server
class AsyncSimulation extends Simulation{
  val logger: Logger = Logger[AsyncSimulation]
  val host = new InetSocketAddress("192.168.0.199", 1337)
  def run(): Unit = {
    val baos = new ByteArrayOutputStream()
    System.setOut(new PrintStream("output.txt"))
    logger.info("START SERVER")
    val server = new Server(host)
    server receive()

    logger.info("WARMUP")
    warmup()

    val list  = List[Int](
      2000

    )
        .map(runtime => (runtime, benchmark(runtime)))

    logger.info("CLEAN UP")
    server dispose()
    printResult(list)
    saveResult(classOf[AsyncSimulation].getCanonicalName + ".txt", list)
  }

  def benchmark(runtime: Int): Int ={
    logger.info("START BENCHMARK")
    val car = new Car(host)
    car.connect()
    car.requestChannel()
    val disposable = car.subscribeOnServerEndpoint()
    Thread sleep runtime
    logger.info("Throughput: {} processed requests", car.getFlowrate)
    logger.info("END BENCHMARK")
    disposable dispose()
    car shutDown()

    car.getFlowrate
  }

  def warmup(): Unit={
    val runtimeMxBean = ManagementFactory.getRuntimeMXBean
    val listOfArguments = runtimeMxBean.getInputArguments
    val compilationBean = ManagementFactory.getCompilationMXBean

    var iterations = 0
    val argumentQuery = "-XX:CompileThreshold="
    listOfArguments forEach(arg => {
      if (arg.startsWith(argumentQuery))
        iterations = Integer.parseInt(arg.slice(argumentQuery.length, arg.length))
    })
    val car = new Car(host)
    car connect()
    car requestChannel()
    car subscribeOnServerEndpoint()

    while(car.getFlowrate < iterations){
      println(car.getFlowrate)
    }
  }
}

object AsyncSimulation{
  def main(args: Array[String]): Unit = {
    val simulation = new AsyncSimulation
    simulation run()
    sys.exit(0)
  }
}
