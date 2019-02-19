package benchmark

import java.io.{File, PrintWriter}

import prototype.async.client.CarConfiguration


/**
  * This Trait provides Methods for the Benchmark.
  */
trait Simulation {
  /**
    * Default Implementation for getting Result in a proper Format.
    * The List of Tuples is converted into a String which represents the data
    * @param result List of Tuple e.g Measuretime and Throughput
    * @return String which represents the data
    */
  def getResult(result: List[(Int, Int)]): String={
    val sb = new StringBuilder
    val tab = "\t"
    sb.append("Test time%sThroughput %s Throughput/s \n".format(tab, tab))
    result.foreach(tuple => sb.append("%d %s %d %s %f \n"
      .format(tuple._1, tab*2, tuple._2, tab, (tuple._2.toDouble/tuple._1) * 1000)))
    sb.toString()
  }

  /**
    * Print the Result of a Benchmark to StdOut.
    * @param result List of Tuple e.g Measuretime and Throughput
    */
  def printResult(result: List[(Int, Int)]): Unit={
    println(getResult(result))
  }

  /**
    * Store the Result of a Benchmark into a specific file
    * @param fileName Filename to the store the Results
    * @param result List of Tuple e.g Measuretime and Throughput
    */
  def saveResult(fileName: String, result: List[(Int, Int)]): Unit ={
    val writer = new PrintWriter(new File(fileName))
    writer.write(getResult(result))
    writer.close()
  }

  def benchmark(runtime: Int, config: CarConfiguration): Int
  def warmUp(): Unit
  def run(config: CarConfiguration = new CarConfiguration()): Unit
}
