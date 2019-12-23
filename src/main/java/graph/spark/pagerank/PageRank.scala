package graph.spark.pagerank

import java.io.File

import org.apache.spark.graphx.GraphLoader
import org.apache.spark.sql.SparkSession

object PageRank {

  def main(args: Array[String]): Unit = {

    // Creates a SparkSession.
    val spark = SparkSession
      .builder
      .appName(s"${this.getClass.getSimpleName}")
      .master("local[*]")
      .getOrCreate()
    val sc = spark.sparkContext

    //filePath
    val userdir: String = System.getProperty("user.dir")
    val userdirName: String = new File(userdir).getPath + "/src" + "/main" + "/resources/"

    // $example on$
    // Load the edges as a graph
    val graph = GraphLoader.edgeListFile(sc, userdirName + "followers.txt")
    // Run PageRank
    val ranks = graph.pageRank(0.0001).vertices
    // Join the ranks with the usernames
    val users = sc.textFile(userdirName + "users.txt").map { line =>
      val fields = line.split(",")
      (fields(0).toLong, fields(1))
    }
    val ranksByUsername = users.join(ranks).map {
      case (id, (username, rank)) => (username, rank)
    }
    // Print the result
    println(ranksByUsername.collect().mkString("\n"))
    // $example off$
    spark.stop()


  }
}
