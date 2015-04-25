package graph

import com.fasterxml.jackson.databind.{DeserializationFeature, ObjectMapper}
import com.fasterxml.jackson.module.scala.experimental.ScalaObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule

import scala.util._
import scala.io.Source._
import scala.reflect._
import java.io.{FileWriter,File}
import java.io.{DataInputStream => DInputStream}
import Graph._

object Graph {
  private val default_size = 128
}

// 
// Builds a undirected graph ADT either by definining a default size (e.g. 128)
// or by reading the data input 
//
class Graph(val size : Int = default_size) {
  private var adj: Array[Bag[String]] = Array.fill(size)(new Bag[String])
  private var edges = 0
  def this(path : String) {
    this(default_size)
    fromFile(path).getLines.foreach{
      line => val t = line.split(" ")
              addEdge(t(0), t(1))
    }
  }

  def getit = {
    val t = Array.fill(adj.size)(null)
    Array.copy(adj,0,t,0,adj.size)
    t
  }

  def adjacent(v: String) : Iterator[String] = adj(adj.indexOf(v)).iterator

  def addEdge(v: String, w: String) {
    adj(adj.indexOf(v)).add(w)
    adj(adj.indexOf(w)).add(v)
  }
} 

case class Mapping(from: String, to: String)
case class Node(id: Int, label: String)

class GraphVisualizer(g : Graph) {
  val mapper = new ObjectMapper()
  mapper.registerModule(DefaultScalaModule)

  def toJson(value: Map[Symbol, Any]): String = {
    toJson(value map { case (k,v) => k.name -> v})
  }

  def toJson(value: Any): String = {
    mapper.writeValueAsString(value)
  }

  val fileHeader = """
<html>
<head>
<script src="./vis/dist/vis.js"></script>
</head>
<div id="visualization"></div>
<script type="text/javascript">
    // create an array with nodes
"""
  val nodePrefix = """
var nodes = [
"""
  val nodeSuffix = """
];
"""
  val edgePrefix = """
var nodes = [
"""
  val edgeSuffix = """
];
"""
  val fileFooter = """
    // create a network
    var container = document.getElementById('visualization');
    var data = {
        nodes: nodes,
        edges: edges
    };
    var network = new vis.Network(container, data, {});
</script>
</html>
"""
 
  def contents = {
    val adj = g.getit
    def makeNodes = {
      var index = 0
      for {
       v <- adj
      } { 
        index += 1
        Node(index, v)
      }
    }
    def makeEdges = {
      for {
        v <- adj
        n <- g.adjacent(v)
      } yield Mapping(v, n)
    }
    fileHeader + nodePrefix + toJson(makeNodes) + nodeSuffix + edgePrefix + makeEdges + edgeSuffix + fileFooter
  }
 
  def outputToFile(path: String) = {
    val fw = new FileWriter(new File(path))  
    val data = contents
    Try(fw.write(data, 0, data.size)) match {
      case Success(v) => fw.close
      case Failure(e) => fw.close
    }
  }
}

object TestVisualizer extends App {
  val g = new Graph(500)
  val gv = new GraphVisualizer(g)
  gv.outputToFile("./test.html")
}
