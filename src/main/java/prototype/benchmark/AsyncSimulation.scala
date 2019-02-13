package prototype.benchmark
import java.net.InetSocketAddress
import java.time.Duration
import java.util.Scanner
import java.util.concurrent.Executors
import com.typesafe.scalalogging.Logger
import prototype.async.client.{Car, CarConfiguration}
import prototype.async.server.Server
import prototype.routing.RoutingFactory
import scala.concurrent.{ExecutionContext, ExecutionContextExecutor, Future}
import scala.concurrent.ExecutionContext.Implicits.global

class AsyncSimulation extends Simulation{
  val logger: Logger = Logger[AsyncSimulation]
  val host = new InetSocketAddress("192.168.0.199", 1337)
  var server: Server = _
  val list: List[Int] = List(
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
  val executors: ExecutionContextExecutor = ExecutionContext.fromExecutor(Executors.newFixedThreadPool(8))

  def startServer(): Unit={
    logger.info("START SERVER")
    server = new Server(host)
    server receive()
  }
  
  def run(local: Boolean, singleThreaded: Boolean): Unit = {
    if(local && singleThreaded){
      logger.info("START LOCAL SINGLE THREAD BENCHMARK")
      startServer()
      warmup()
      val result = list.map(runtime => (runtime, benchmark(runtime)))
      server.dispose()
      printResult(result)
      saveResult(classOf[AsyncSimulation].getCanonicalName + ".local.single.txt", result)
    } else if(!local && singleThreaded){
      logger.info("START REMOTE SINGLE THREAD BENCHMARK")
      warmup()
      val result = list.map(runtime => (runtime, benchmark(runtime)))
      printResult(result)
      saveResult(classOf[AsyncSimulation].getCanonicalName + ".remote.single.txt", result)
    } else if(local && !singleThreaded){
      logger.info("START LOCAL MULTI THREAD BENCHMARK")
      startServer()
      warmup()
      Future.sequence(list.map(runtime => Future((runtime, benchmark(runtime)))(executors))).onComplete(
        result => {
          printResult(result.get)
          saveResult(classOf[AsyncSimulation].getCanonicalName + ".local.multi.txt", result.get)
          server dispose()
        }
      )
    } else if(!local && !singleThreaded){
      logger.info("START REMOTE MULTI THREAD BENCHMARK")
      warmup()
      Future.sequence(list.map(runtime => Future((runtime, benchmark(runtime)))(executors))).onComplete(
        result => {
          printResult(result.get)
          saveResult(classOf[AsyncSimulation].getCanonicalName + ".remote.multi.txt", result.get)
        }
      )
    }
  }

  def benchmark(runtime: Int): Int ={
    logger.info("START BENCHMARK")
    val car = new Car(host,  new CarConfiguration(Duration.ZERO, RoutingFactory.RouteType.RECTANGLE))
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
    logger.info("WARMUP")
    val iterations = 1500
    val car = new Car(host)
    car connect()
    car requestChannel()
    val disposable = car subscribeOnServerEndpoint()
    while(car.getFlowrate < iterations){
      Thread.sleep(1)
    }
    disposable.dispose()
    car.shutDown()
  }
}

object AsyncSimulation{
  def main(args: Array[String]): Unit = {
    var local = true //default
    var singleThreaded = true //default

    args.foreach {
      case "remote" => local = false
      case "multi" => singleThreaded = false
    }
    val simulation = new AsyncSimulation
    simulation run(local, singleThreaded)

    val scanner = new Scanner(System.in)
    while(scanner.hasNext()){

    }
    sys.exit(0)
  }
}
