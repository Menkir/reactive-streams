package prototype.benchmark

import java.io.{File, PrintWriter}

trait Simulation {
  def getResult(result: List[Tuple2[Int, Int]]): String={
    val sb = new StringBuilder
    val tab = "\t"
    sb.append("Test time%sThroughput %s Throughput/s \n".format(tab, tab))
    result.foreach(tuple => sb.append("%d %s %d %s %f \n"
      .format(tuple._1, tab*2, tuple._2, tab, (tuple._2.toDouble/tuple._1) * 1000)))
    sb.toString()
  }

  def printResult(result: List[Tuple2[Int, Int]]): Unit={
    println(getResult(result))
  }

  def saveResult(fileName: String, result: List[Tuple2[Int, Int]]): Unit ={
    val writer = new PrintWriter(new File(fileName))
    writer.write(getResult(result))
    writer.close()
  }

  def warmup(): Unit
  def benchmark(runtime: Int): Int
}