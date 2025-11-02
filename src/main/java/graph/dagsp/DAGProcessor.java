package graph.dagsp;

import com.fasterxml.jackson.databind.*;
import graph.scc.TarjanSCC;
import graph.utils.Metrics;

import java.io.File;
import java.net.URL;
import java.util.*;

public class DAGProcessor {

    // ===== Helper inner class =====
    static class DAG {
        List<List<int[]>> adj; // [to, weight]
        int n;

        DAG(int n) {
            this.n = n;
            adj = new ArrayList<>();
            for (int i = 0; i < n; i++) adj.add(new ArrayList<>());
        }

        void addEdge(int u, int v, int w) {
            adj.get(u).add(new int[]{v, w});
        }

        List<Integer> topoSort(Metrics metrics) {
            int[] indeg = new int[n];
            for (int u = 0; u < n; u++)
                for (int[] e : adj.get(u)) indeg[e[0]]++;

            Queue<Integer> q = new LinkedList<>();
            for (int i = 0; i < n; i++) if (indeg[i] == 0) q.add(i);

            List<Integer> topo = new ArrayList<>();
            while (!q.isEmpty()) {
                int u = q.poll();
                topo.add(u);
                for (int[] e : adj.get(u)) {
                    indeg[e[0]]--;
                    metrics.incrementPushPop();
                    if (indeg[e[0]] == 0) q.add(e[0]);
                }
            }
            return topo;
        }

        int[] shortestPath(int src, List<Integer> topo, Metrics metrics) {
            int[] dist = new int[n];
            Arrays.fill(dist, Integer.MAX_VALUE);
            dist[src] = 0;

            for (int u : topo) {
                if (dist[u] != Integer.MAX_VALUE) {
                    for (int[] e : adj.get(u)) {
                        int v = e[0], w = e[1];
                        if (dist[v] > dist[u] + w) {
                            dist[v] = dist[u] + w;
                            metrics.incrementRelaxations();
                        }
                    }
                }
            }
            return dist;
        }

        // Find the critical (longest) path in DAG
        Result longestPath(Metrics metrics) {
            int[] dist = new int[n];
            int[] parent = new int[n];
            Arrays.fill(dist, Integer.MIN_VALUE);
            Arrays.fill(parent, -1);

            List<Integer> topo = topoSort(metrics);
            for (int u : topo) {
                if (dist[u] == Integer.MIN_VALUE) dist[u] = 0;
                for (int[] e : adj.get(u)) {
                    int v = e[0], w = e[1];
                    if (dist[v] < dist[u] + w) {
                        dist[v] = dist[u] + w;
                        parent[v] = u;
                        metrics.incrementRelaxations();
                    }
                }
            }

            int maxLen = Integer.MIN_VALUE, endNode = -1;
            for (int i = 0; i < n; i++) {
                if (dist[i] > maxLen) {
                    maxLen = dist[i];
                    endNode = i;
                }
            }

            List<Integer> path = new ArrayList<>();
            for (int cur = endNode; cur != -1; cur = parent[cur]) path.add(cur);
            Collections.reverse(path);
            return new Result(maxLen, path);
        }
    }

    static class Result {
        int length;
        List<Integer> path;
        Result(int len, List<Integer> p) { length = len; path = p; }
    }

    // ===== MAIN =====
    public static void main(String[] args) throws Exception {
        String[] files = {
                "small_1.json", "small_2.json", "small_3.json",
                "medium_1.json", "medium_2.json", "medium_3.json",
                "large_1.json", "large_2.json", "large_3.json"
        };

        ObjectMapper mapper = new ObjectMapper();

        for (String file : files) {
            URL resource = DAGProcessor.class.getClassLoader().getResource(file);
            if (resource == null) {
                System.out.println("File not found: " + file);
                continue;
            }

            JsonNode root = mapper.readTree(new File(resource.toURI()));
            int n = root.get("nodes").asInt();
            List<List<Integer>> adj = new ArrayList<>();
            for (int i = 0; i < n; i++) adj.add(new ArrayList<>());

            for (JsonNode e : root.get("edges")) {
                int u = e.get("from").asInt();
                int v = e.get("to").asInt();
                int w = e.has("weight") ? e.get("weight").asInt() : 1;
                adj.get(u).add(v);
            }

            System.out.println("\nGraph: " + file);

            // Step 1: Compress cycles (SCC)
            Metrics metrics = new Metrics();
            TarjanSCC tarjan = new TarjanSCC(adj, metrics);
            List<List<Integer>> sccs = tarjan.computeSCCs();
            List<Set<Integer>> cond = tarjan.buildCondensation(sccs);

            // Step 2: Build DAG
            DAG dag = new DAG(cond.size());
            for (int i = 0; i < cond.size(); i++)
                for (int v : cond.get(i))
                    dag.addEdge(i, v, 1);

            // Step 3: Shortest path from last SCC
            metrics.startTimer();
            List<Integer> topo = dag.topoSort(metrics);
            int src = cond.size() - 1; // arbitrary example source
            int[] shortest = dag.shortestPath(src, topo, metrics);
            metrics.endTimer();

            System.out.println("Single-source shortest distances from node " + src + ":");
            for (int i = 0; i < shortest.length; i++) {
                if (shortest[i] == Integer.MAX_VALUE)
                    System.out.println("Node " + i + ": INF");
                else
                    System.out.println("Node " + i + ": " + shortest[i]);
            }
            metrics.printDAGMetrics();

            // Step 4: Longest (critical) path
            metrics.startTimer();
            Result critical = dag.longestPath(metrics);
            metrics.endTimer();

            System.out.println("\nCritical path (longest) length: " + critical.length);
            System.out.println("Critical path: " + critical.path);
            metrics.printDAGMetrics();
            System.out.println("------------------------------------------------------");
        }
    }
}

