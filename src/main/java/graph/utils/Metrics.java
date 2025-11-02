package graph.utils;

public class Metrics {
    public long dfsVisits = 0;   // SCC
    public long dfsEdges = 0;    // SCC
    public long kahnPushes = 0;  // Topo
    public long kahnPops = 0;    // Topo
    public long dagRelaxations = 0; // DAG SP
    public long startTime = 0;
    public long endTime = 0;

    public void startTimer() {
        startTime = System.nanoTime();
    }

    public void endTimer() {
        endTime = System.nanoTime();
    }

    public double elapsedMillis() {
        return (endTime - startTime) / 1_000_000.0;
    }

    public void printSCCMetrics() {
        System.out.println("DFS visits: " + dfsVisits);
        System.out.println("DFS edges: " + dfsEdges);
        System.out.println("Time: " + String.format("%.2f ms", elapsedMillis()));
    }
}
