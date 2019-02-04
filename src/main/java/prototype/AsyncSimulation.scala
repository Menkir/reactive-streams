package prototype

import com.google.inject.Guice
import prototype.app.ReactiveModule
import prototype.endpoints.reactiveCarImpl.Car
import prototype.endpoints.reactiveServerImpl.Server
import prototype.view.Monitor

object AsyncSimulation{
  def main(args: Array[String]): Unit = {
    val delay: Long = 1000
    val module = new ReactiveModule
    val injector = Guice.createInjector(module)

    val server = injector.getInstance(classOf[Server])
    server.receive()

    val car = injector.getInstance(classOf[Car])
    car.connect()
    car.requestChannel()
    car.subscribeOnServerEndpoint()

    val monitor = new Monitor(server)
    monitor setVisible true

    val disposable = monitor listeningOnIncomingCars()
    val subscription = monitor listeningOnIncomingCoordinates()


    Thread sleep delay
    server.dispose()
    println("Durchsatz " + car.getFlowrate + " beantwortete Requests pro Sekunde")
    scala.io.StdIn.readLine("Type in: ...")

  }
}