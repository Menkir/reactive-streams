package prototype.sync

import java.net.InetSocketAddress

import com.google.inject.Guice
import prototype.sync.client.Car
import prototype.sync.server.Server
import prototype.sync.view.Monitor

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
object SyncSimulation {
  def main(args: Array[String]): Unit = {
    val delay = 1000
    val host = new InetSocketAddress("127.0.0.1", 1337)
    val server = new Server(host)
    server receive()
    val car = new Car(host)
    Future{car.connect()}
    //val monitor = new Monitor(server)
    //monitor.start()
    Thread sleep delay
    println("Durchsatz " + car.getFlowrate + " beantwortete Requests pro Sekunde")
  }
}
