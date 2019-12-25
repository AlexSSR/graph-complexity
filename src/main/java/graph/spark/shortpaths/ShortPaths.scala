package graph.spark.shortpaths

import org.apache.spark.graphx.Graph
import org.apache.spark.graphx.lib.ShortestPaths
import org.apache.spark.{SparkConf, SparkContext}

object ShortPaths {

  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("ShortPaths").setMaster("local[4]")
    val sc = new SparkContext(conf)

    sc.setLogLevel("warn")

    // the reality
    val shortestPaths = Set(
      (1, Map(1 -> 0, 4 -> 2)), (2, Map(1 -> 1, 4 -> 2)), (3, Map(1 -> 2, 4 -> 1)),
      (4, Map(1 -> 2, 4 -> 0)), (5, Map(1 -> 1, 4 -> 1)), (6, Map(1 -> 3, 4 -> 1)))

    //Construct an edge sequence for an undirected graph
    val edgeSeq = Seq((1, 2), (1, 5), (2, 3), (2, 5), (3, 4), (4, 5), (4, 6)).flatMap {
      case e => Seq(e, e.swap)
    }

    //Constructing an undirected graph
    val edges = sc.parallelize(edgeSeq).map { case (v1, v2) => (v1.toLong, v2.toLong) }
    val graph = Graph.fromEdgeTuples(edges, 1)

    //Set of points requiring the shortest path
    val landmarks = Seq(1, 4).map(_.toLong)

    // compute
    val results = ShortestPaths.run(graph, landmarks).vertices.collect.map {
      case (v, spMap) => (v, spMap.mapValues(i => i))
    }

    val shortestPath1 = ShortestPaths.run(graph, landmarks)

    // compare with the reality
    println("\ngraph edges");
    println("edges:");
    graph.edges.collect.foreach(println)
    //    graph.edges.collect.foreach(println)
    println("vertices:");
    graph.vertices.collect.foreach(println)
    //    println("triplets:");
    //    graph.triplets.collect.foreach(println)
    println();

    println("\n shortestPath1");
    println("edges:");
    shortestPath1.edges.collect.foreach(println)
    println("vertices:");
    shortestPath1.vertices.collect.foreach(println)
    //    println("vertices:")

    assert(results.toSet == shortestPaths)
    println("results.toSet:" + results.toSet);
    println("end");

    sc.stop()
  }
}
