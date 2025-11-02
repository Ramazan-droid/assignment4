package graph.scc;

import com.fasterxml.jackson.databind.*;
import graph.utils.Metrics;

import java.io.File;
import java.net.URL;
import java.util.*;

public class SCCMain {

    public static void main(String[] args) throws Exception {
        // List all your generated JSON files
        String[] jsonFiles = {
                "small_1.json", "small_2.json", "small_3.json",
                "medium_1.json", "medium_2.json", "medium_3.json",
                "large_1.json", "large_2.json", "large_3.json"
        };

        ObjectMapper mapper = new ObjectMapper();

        for (String fileName : jsonFiles) {
            URL resource = SCCMain.class.getClassLoader().getResource(fileName);
            if (resource == null) {
                System.out.println("File not found: " + fileName);
                continue;
            }

            JsonNode root = mapper.readTree(new File(resource.toURI()));

            // Correctly read number of nodes
            int n = root.get("nodes").asInt();

            // Build adjacency list
            List<List<Integer>> adj = new ArrayList<>();
            for (int i = 0; i < n; i++) adj.add(new ArrayList<>());

            for (JsonNode e : root.get("edges")) {
                int u = e.get("from").asInt();
                int v = e.get("to").asInt();
                adj.get(u).add(v);
            }

            // Metrics for SCC
            Metrics metrics = new Metrics();
            metrics.startTimer();

            // Run Tarjan SCC
            TarjanSCC tarjan = new TarjanSCC(adj, metrics);
            List<List<Integer>> sccs = tarjan.computeSCCs();

            metrics.endTimer();

            // Print SCCs and their sizes
            System.out.println("\nGraph: " + fileName);
            System.out.println("Strongly Connected Components:");
            for (List<Integer> comp : sccs) {
                Collections.sort(comp); // optional
                System.out.println(comp + " (size " + comp.size() + ")");
            }

            // Build Condensation DAG
            List<Set<Integer>> condDAG = tarjan.buildCondensation(sccs);
            System.out.println("\nCondensation DAG (nodes = SCCs):");
            for (int i = 0; i < condDAG.size(); i++)
                System.out.println("SCC " + i + " -> " + condDAG.get(i));

            // Print metrics
            System.out.println("\nSCC Metrics:");
            metrics.printSCCMetrics();
        }
    }
}
