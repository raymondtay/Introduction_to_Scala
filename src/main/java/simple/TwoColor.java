// Two-colorability. Support this query: Can the vertices of a given graph
// be assigned one of two colors in such a way no edge connects vertices of the same color?
// equivalent to the question of whether the graph is bipartite ?
public class TwoColor {
    private boolean[] marked;
    private boolean[] color;
    private boolean isTwoColorable = true;
    public TwoColor(Graph G) {
        marked = new boolean[G.V()];
        color = new boolean[G.V()];
        for(int s = 0; s < G.V(); s++) 
            if(!marked[s]) dfs(G, s);
    }

    private void dfs(Graph G, int v) {
        marked[v] = true;
        for(int w : G.adj(v)) 
            if(!marked[w]) {
                color[w] = !color[v];
                dfs(G, w);
            } else if(color[w] == color[v]) isTwoColorable = false;
    }
    public boolean isBipartite() { return isTwoColorable ; }
}

