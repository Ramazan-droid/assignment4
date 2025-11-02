package graph.topo;

import com.fasterxml.jackson.databind.*;
import graph.scc.TarjanSCC;
import graph.utils.Metrics;

import java.io.File;
import java.net.URL;
import java.util.*;

public class TopoSortMain {

    public static void main(String[] args) throws Exception {
        // List of all dataset filenames
        String[] datasets = {
                "small_1.json", "small_2.json", "small_3.json",
                "medium_1.json", "medium_2.json", "medium_3.json",
                "large_1.json", "large_2.json", "large_3.json"
        };

        for (String fileName : datasets) {
            System.out.println("Graph: " + fileName);

            URL resource = TopoSortMain.class.getClassLoader().getResource(fileName);
            if (resource == null) {
                System.out.println("File not found: " + fileName);
                continue;
            }

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(new File(resource.toURI()));

            int n = root.get("nodes").asInt();
            List<int[]> edgesList = new ArrayList<>();
            for (JsonNode e : root.get("edges")) {
                edgesList.add(new int[]{e.get("from").asInt(), e.get("to").asInt()});
            }

            // Build adjacency list
            List<List<Integer>> adj = new ArrayList<>();
            for (int i = 0; i < n; i++) adj.add(new ArrayList<>());
            for (int[] e : edgesList) adj.get(e[0]).add(e[1]);

            // Run Tarjan SCC
            Metrics metrics = new Metrics();
            TarjanSCC tarjan = new TarjanSCC(adj, metrics);
            List<List<Integer>> sccs = tarjan.computeSCCs();
            List<Set<Integer>> condDAG = tarjan.buildCondensation(sccs);

            // Convert Set DAG to List DAG for topo
            List<List<Integer>> dagAdj = new ArrayList<>();
            for (Set<Integer> s : condDAG) dagAdj.add(new ArrayList<>(s));

            // Run Kahn topological sort
            metrics.startTimer();
            List<Integer> topoOrder = kahnTopoSort(dagAdj, metrics);
            metrics.endTimer();

            // Derived order of original tasks
            List<Integer> taskOrder = new ArrayList<>();
            for (int idx : topoOrder) {
                taskOrder.addAll(sccs.get(idx));
            }

            // Print results
            System.out.println("Topological order of SCCs: " + topoOrder);
            System.out.println("Order of original tasks after SCC compression: " + taskOrder);
            metrics.printTopoMetrics();
            System.out.println("------------------------------------------------------");
        }
    }

    public static List<Integer> kahnTopoSort(List<List<Integer>> adj, Metrics metrics) {
        int n = adj.size();
        int[] inDegree = new int[n];
        for (int u = 0; u < n; u++) {
            for (int v : adj.get(u)) {
                inDegree[v]++;
                metrics.incrementPushPop();
            }
        }

        Queue<Integer> queue = new LinkedList<>();
        for (int i = 0; i < n; i++) if (inDegree[i] == 0) queue.add(i);

        List<Integer> topo = new ArrayList<>();
        while (!queue.isEmpty()) {
            int u = queue.poll();
            topo.add(u);
            for (int v : adj.get(u)) {
                inDegree[v]--;
                metrics.incrementPushPop();
                if (inDegree[v] == 0) queue.add(v);
            }
        }
        return topo;
    }
}
