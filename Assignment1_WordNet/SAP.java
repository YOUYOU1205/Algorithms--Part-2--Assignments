import edu.princeton.cs.algs4.BreadthFirstDirectedPaths;
import edu.princeton.cs.algs4.Digraph;

public class SAP {
    // constructor takes a digraph (not necessarily a DAG)
    private Digraph d;
    private int min;
    private int anc;
    public SAP(Digraph G) {
        d = new Digraph(G);
    }

    // length of shortest ancestral path between v and w; -1 if no such path
    public int length(int v, int w) {
        bfs(v, w);
        return min == Integer.MAX_VALUE ? -1 : min;
    }

    private void bfs(int v, int w) {
        BreadthFirstDirectedPaths vbs = new BreadthFirstDirectedPaths(d, v);
        BreadthFirstDirectedPaths wbs = new BreadthFirstDirectedPaths(d, w);
         // two directions
        this.min = Integer.MAX_VALUE;
        this.anc = -1;
        for (int i = 0; i < d.V(); i++) {
            if (wbs.hasPathTo(i) && vbs.hasPathTo(i) && wbs.distTo(i) + vbs.distTo(i) < min) {
                this.min = wbs.distTo(i) +  vbs.distTo(i);
                this.anc = i;
            }
        }
    }

    // a common ancestor of v and w that participates in a shortest ancestral path; -1 if no such path
    public int ancestor(int v, int w) {
        bfs(v, w);
        return this.anc;
    }

    private void bfs(Iterable<Integer> v, Iterable<Integer> w) {
        if (v == null || w == null)
            throw new IllegalArgumentException();
        BreadthFirstDirectedPaths vbs = new BreadthFirstDirectedPaths(d, v);
        BreadthFirstDirectedPaths wbs = new BreadthFirstDirectedPaths(d, w);

        this.min = Integer.MAX_VALUE;
        this.anc = -1;
        for (int i = 0; i < d.V(); i++) {
            if (wbs.hasPathTo(i) && vbs.hasPathTo(i) && wbs.distTo(i) + vbs.distTo(i) < min) {
                this.min = wbs.distTo(i) + vbs.distTo(i);
                this.anc = i;
            }
        }
    }

    // length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        bfs(v, w);
        return this.min == Integer.MAX_VALUE ? -1 : this.min;
    }

    // a common ancestor that participates in shortest ancestral path; -1 if no such path
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        bfs(v, w);
        return this.anc;
    }

    // do unit testing of this class
    public static void main(String[] args){

    }
}
