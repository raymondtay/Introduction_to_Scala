/**
    The classic recursive method for searching in a connected graph (visiting all its vertices and edges)
    mimics Tremaux maze exploration but is even simpler to describe. 
    To search a graph, invoke a recursive method that visits vertices. To visit a vertex:
    a) Mark it as having been visited
    b) Visit (recursively) all the vertices that are adjacent to it and that have not yet been marked
*/
public class DFS {
    private boolean[] marked;
    private int count;

    public DFS(Graph G, int v) { 
        marked  = new boolean[G.V()];
        dfs(G, v);
    }

    private void dfs(Graph G, int v) {
        marked[v] = true;
        count++;
        for(int w : G.adj(v)) 
            if (!marked[w]) dfs(G, w);
    }

    public boolean marked(int w) { return marked[w]; }
    public int count() { return count; }
}

