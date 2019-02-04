package prototype

import java.net.InetSocketAddress

import com.google.inject.Guice
import prototype.app.SyncModule
import prototype.endpoints.classicCarImpl.Car
import prototype.endpoints.classicServerImpl.Server
import prototype.view.{ClassicMonitor, Monitor}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
object SyncSimulation {
  def main(args: Array[String]): Unit = {
    val delay = 1000L
    val module = new SyncModule
    val injector = Guice.createInjector(module)

    val server: Server = injector getInstance classOf[Server]
    val car: Car = injector getInstance classOf[Car]
    val monitor = new ClassicMonitor(server)
    server receive()
    Future{car connect()}
    monitor.start()

    Thread sleep delay
    println("Durchsatz " + car.getFlowrate + " beantwortete Requests pro Sekunde")
  }
}