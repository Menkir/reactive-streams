package benchmark

import java.net.InetSocketAddress
import java.util.concurrent.Executors
import com.typesafe.scalalogging.Logger
import prototype.async.client.CarConfiguration
import prototype.sync.client.Car

import scala.concurrent.{ExecutionContext, ExecutionContextExecutor, Future}
import ExecutionContext.Implicits.global

/**
  * Implementation of Simulation Trait
  *
  * @param hostInfo Contains Information about IP and Port from Server with default
  */
class SyncSimulation(hostInfo: InetSocketAddress = new InetSocketAddress("127.0.0.1", 1337)) extends Simulation {
  val logger: Logger = Logger[SyncSimulation]
  val executors: ExecutionContextExecutor = ExecutionContext.fromExecutor(Executors.newFixedThreadPool(8))
  /**
    * This List stores Measuretimes for each Benchmark
    */
  val durationList: List[Int] = List range(0,10) map(n => Math.pow(2, n toDouble).toInt * 1000)
  /**
    * This Method runs a Benchmark. First it starts a warmup to optimize methods from JIT.
    * Then it starts in multithreaded manner the benchmarks by calling benchmark() with particular measuretime
    * @param config Configuration for the Car (See Javadocs)
    */
  override def run(config: CarConfiguration = new CarConfiguration()): Unit ={
    logger info "START TEST"
    warmUp()

    Future sequence(durationList map(duration => Future((duration, benchmark(duration, config))))) onComplete(
      result => {
        printResult(result get)
        saveResult("SyncBenchmarkResult.txt", result get)
        logger info "END TEST"
      })
  }

  /**
    * This method runs a benchmark by instantiating a Car Instance. Then it connects to the Server and starts sending Measurements synchronously.
    * Each Car is sending in separate Future because Car emits in a blocking manner.
    * After a particular time: measuretime the benchmark stops and return the current Value of the flowrate in the Car.
    * @param measuretime The time the benchmark claims
    * @param config Configuration for the Car (See Javadocs)
    * @return
    */
  override def benchmark(measuretime: Int, config: CarConfiguration): Int ={
    logger.info("START BENCHMARK")
    val car = new Car(hostInfo, config)

    Future{
      car connect()
      car send()
    }(executors)
    Thread sleep measuretime
    logger.info("Throughput: {} processed Measures from Car", car getFlowrate)
    logger.info("END BENCHMARK")
    car.close()
    car.getFlowrate
  }

  /**
    * This Method works like benchmark() and guarantees a JIT-optimized compilation of Methods which are called during the Benchmark.
    * The only difference is, that it send a specific number of Measurements. In this Case 1500 because of the JMV Flag -client.
    * Be aware that your results may differ when you let the Flags away because some methods are not JIT-compiled during the benchmark.
    * The default Threshhold for Compilation is 10.000 https://www.oracle.com/technetwork/java/javase/tech/vmoptions-jsp-140102.html
    */
  override def warmUp(): Unit={
    logger.info("START WARMUP")
    val car = new Car(hostInfo)
    car connect()
    car send 1500
    car close()
    logger.info("END WARMUP")
  }
}

object SyncSimulation{
  def main(args: Array[String]): Unit = {
    val simulation = new SyncSimulation()
    simulation.run()

  }
}