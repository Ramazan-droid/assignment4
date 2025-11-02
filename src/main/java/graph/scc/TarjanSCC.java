package graph.scc;

import graph.utils.Metrics;
import java.util.*;

public class TarjanSCC {

    private final List<List<Integer>> adj;
    private final int n;
    private int index = 0;
    private final int[] indices;
    private final int[] lowlink;
    private final boolean[] onStack;
    private final Deque<Integer> stack = new ArrayDeque<>();
    private final List<List<Integer>> sccs = new ArrayList<>();
    private final Metrics metrics;

    // Constructor — must assign all final fields
    public TarjanSCC(List<List<Integer>> adj, Metrics metrics) {
        this.adj = adj;
        this.n = adj.size();
        this.indices = new int[n];
        Arrays.fill(this.indices, -1);
        this.lowlink = new int[n];
        this.onStack = new boolean[n];
        this.metrics = metrics;
    }

    // Public method to compute SCCs
    public List<List<Integer>> computeSCCs() {
        for (int v = 0; v < n; v++) {
            if (indices[v] == -1) {
                strongConnect(v);
            }
        }
        return sccs;
    }

    // Internal recursive Tarjan DFS
    private void strongConnect(int v) {
        indices[v] = index;
        lowlink[v] = index++;
        stack.push(v);
        onStack[v] = true;
        metrics.dfsVisits++; // count DFS visit

        for (int w : adj.get(v)) {
            metrics.dfsEdges++; // count edge
            if (indices[w] == -1) {
                strongConnect(w);
                lowlink[v] = Math.min(lowlink[v], lowlink[w]);
            } else if (onStack[w]) {
                lowlink[v] = Math.min(lowlink[v], indices[w]);
            }
        }

        if (lowlink[v] == indices[v]) {
            List<Integer> scc = new ArrayList<>();
            int w;
            do {
                w = stack.pop();
                onStack[w] = false;
                scc.add(w);
            } while (w != v);
            sccs.add(scc);
        }
    }

    // Build condensation DAG
    public List<Set<Integer>> buildCondensation(List<List<Integer>> sccs) {
        Map<Integer, Integer> nodeToSCC = new HashMap<>();
        for (int i = 0; i < sccs.size(); i++) {
            for (int node : sccs.get(i)) nodeToSCC.put(node, i);
        }

        List<Set<Integer>> condAdj = new ArrayList<>();
        for (int i = 0; i < sccs.size(); i++) condAdj.add(new HashSet<>());

        for (int u = 0; u < adj.size(); u++) {
            int sccU = nodeToSCC.get(u);
            for (int v : adj.get(u)) {
                int sccV = nodeToSCC.get(v);
                if (sccU != sccV) condAdj.get(sccU).add(sccV);
            }
        }

        return condAdj;
    }
}
