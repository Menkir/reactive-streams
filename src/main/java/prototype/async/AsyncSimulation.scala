package prototype.async

import java.net.InetSocketAddress

import prototype.async.client.Car
import prototype.async.server.Server
import prototype.async.view.Monitor
import java.lang.management.ManagementFactory
import java.lang.management.RuntimeMXBean

object AsyncSimulation{
  val host = new InetSocketAddress("127.0.0.1", 1337)
  def main(args: Array[String]): Unit = {
    val delay= 1000
    val server = new Server(host)
    server receive()

    warmup()

    val car = new Car(host)
    car.connect()
    car.requestChannel()
    car.subscribeOnServerEndpoint()

    val monitor = new Monitor(server)

    Thread sleep delay
    server.dispose()
    println("Durchsatz " + car.getFlowrate + " beantwortete Requests pro Sekunde")

  }


  def warmup(): Unit={
    val runtimeMxBean = ManagementFactory.getRuntimeMXBean
    val listOfArguments = runtimeMxBean.getInputArguments
    var iterations = 0
    val argumentQuery = "-XX:CompileThreshold="
    listOfArguments forEach(arg => {
      if (arg.startsWith(argumentQuery))
        iterations = Integer.parseInt(arg.slice(argumentQuery.length, arg.length))
    })
    val car = new Car(host)
    car.connect()
    println(iterations + " Iterations")
    car.requestChannel(iterations)
    car.subscribeOnServerEndpoint()
  }
}
