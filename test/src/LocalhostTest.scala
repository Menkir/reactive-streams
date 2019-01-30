import java.net.InetSocketAddress

import prototype.endpoints.classicCarImpl.Car
import prototype.endpoints.classicServerImpl.Server

import scala.collection.mutable
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}
import scala.collection.mutable.Map
object LocalhostTest {
  def main(args: Array[String]): Unit = {
    val adress: InetSocketAddress = new InetSocketAddress("127.0.0.1", 1337)
    val server: Server = new Server(adress)
    server.receive()

    var durationMap: mutable.Map[Int, Tuple2[Long, Long]] = mutable.Map.empty
    val cars: List[Car] = List.fill(3)(new Car(adress))
    cars.foreach(car =>
      Future {
        durationMap += (car.hashCode() -> Tuple2(System.currentTimeMillis(), 0))
        car.connect()
      } onComplete {
        case Success(value) =>
          var tuple = durationMap(car.hashCode())
          durationMap(car.hashCode()) = Tuple2(tuple._1, System.currentTimeMillis())
        case Failure(t) => println("An Error occure " + t)
      }
    )

    Thread sleep 10000

    // ANALYSIS DURATION OF CAR REQUESTS

    durationMap.values
      .map(t => t._2-t._1)
      .foreach(each => print(""))
  }
}
