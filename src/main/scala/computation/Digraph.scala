package datastructures.scala

class Digraph(val numOfVertices : Int) {
    private[this] var E : Int = 0
    private[this] var adj : Seq[Bag[Int]] = Seq.fill(numOfVertices)(new Bag[Int]())
    def vertices() = numOfVertices
    def edges() = E
    def addEdge(v : Int, w : Int) {
        adj(v).add(w)
        E += 1
    }

    // its a little crazy here since primitives are immutable, by default but 
    // i think its good practice to clone a datastructure
    def getAdjacencyGraph = {
        var buffer = scala.collection.mutable.ListBuffer.empty[Bag[Int]]
        for(i ← 0 until adj.size) buffer += Bag(adj(i).elements:_*)
        buffer toSeq
    }

    def reverse = {
        val r = new Digraph(vertices)
        for{
            v ← 1 to vertices
            w ← adj(v).iterator
        } r.addEdge(w, v)
        r
    }
} 

class DirectedDFS(G : Digraph, s : Int) {
    private[this] var marked: Array[Boolean] = Array.empty[Boolean]
    dfs(G, s)
    def this(G : Digraph, sources : Iterable[Int]) {
        this(G, 0)
        for { 
            source ← sources if (!marked(source)) 
        } dfs(G, s)
    }

    def dfs(G: Digraph, v: Int) : Unit = {
        marked(v) = true
        for( source ← G.getAdjacencyGraph(v).iterator if (!marked(source)) ) dfs(G, source)
    } 
}

object DirectedDFS extends App {
    override def main(args: Array[String]) = {
        val g = new Digraph(args(0).toInt)
        val sources = new Bag[Int]
    }
}

