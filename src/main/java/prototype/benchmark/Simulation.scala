package prototype.benchmark

import java.io.{File, PrintWriter}

import prototype.async.client.CarConfiguration


trait Simulation {
  val durationList: List[Int] = List range(0,10) map(n => Math.pow(2, n toDouble).toInt * 1000)

  def getResult(result: List[Tuple2[Int, Int]]): String={
    val sb = new StringBuilder
    val tab = "\t"
    sb.append("Test time%sThroughput %s Throughput/s \n".format(tab, tab))
    result.foreach(tuple => sb.append("%d %s %d %s %f \n"
      .format(tuple._1, tab*2, tuple._2, tab, (tuple._2.toDouble/tuple._1) * 1000)))
    sb.toString()
  }

  def printResult(result: List[(Int, Int)]): Unit={
    println(getResult(result))
  }

  def saveResult(fileName: String, result: List[(Int, Int)]): Unit ={
    val writer = new PrintWriter(new File(fileName))
    writer.write(getResult(result))
    writer.close()
  }

  def benchmark(runtime: Int, config: CarConfiguration): Int
}

