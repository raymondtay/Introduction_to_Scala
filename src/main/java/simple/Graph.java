import java.io.*;

public class Graph {
    private final int V;// number of vertices
    private int E; // number of edges
    private Bag<Integer>[] adj; // adjaceny lists

    public static int maxDegree(Graph G) {
        int max = 0;
        for(int v = 0; v < G.V(); v++)
            if (degree(G,v) > max) max = degree(G, v);
        return max;
    }

    public static int avgDegree(Graph G) { return 2 * G.E() / G.V(); }

    public static int numberOfSelfLoops(Graph G) {
        int count = 0;
        for(int v = 0; v < G.V(); v++) 
            for(int w: G.adj(v))
                if(v == w) count++;
        return count/2;
    }

    public static int degree(Graph G, int v) {
        int degree = 0;
        for(int w : G.adj(v)) degree ++;
        return degree;
    }

    // Create a V-vertex graph with no edges
    public Graph(int V) {
        this.V = V; this.E = 0;
        adj = new Bag[V];
        //adj = new Bag<Integer>[V];
        for(int v = 0; v < V; v++)
            adj[v] = new Bag<Integer>();
    }
    // read a graph from input stream `in`
    public Graph(DataInputStream in) throws IOException {
        this(in.readInt()); // read V and construct this graph 
        int E = in.readInt();
        for(int i = 0; i < E; i++) {
            int v = in.readInt();
            int w = in.readInt();
            addEdge(v,w);
        }
    }

    // number of vertices
    int V(){ return V; }
    // number of edges
    int E(){ return E; }
    // add edge v-w to this graph 
    void addEdge(int v, int w){
        adj[v].add(w);
        adj[w].add(v);
        E++;
    } 
    // vertices adjacent to v
    Iterable<Integer> adj(int v) { return adj[v]; }

    // string representation
    public String toString() {
        String s = V + " vertices, " + E + " edges\n";
        for (int v = 0; v < V; v++) {
            s += v + ": ";
            for(int w : this.adj(v) )
                s += w + " ";
            s += "\n";
        }
        return s;
    }
}

