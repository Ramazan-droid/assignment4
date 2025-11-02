package graph.utils;

public class Metrics {
    private long startTime;
    private long endTime;

    // Counters
    public int dfsVisits = 0;      // SCC DFS visits
    public int dfsEdges = 0;       // SCC DFS edges
    public int kahnOps = 0;        // TopoSort push/pop operations
    public int relaxations = 0;    // DAG shortest/longest path relaxations

    // Timer
    public void startTimer() {
        startTime = System.nanoTime();
    }

    public void endTimer() {
        endTime = System.nanoTime();
    }

    public double elapsedMillis() {
        return (endTime - startTime) / 1_000_000.0;
    }

    // Increment methods
    public void incrementDFSVisits() { dfsVisits++; }
    public void incrementDFSEdges() { dfsEdges++; }
    public void incrementPushPop() { kahnOps++; }
    public void incrementRelaxations() { relaxations++; }

    // Print summary per task
    public void printSCCMetrics() {
        System.out.println("DFS visits: " + dfsVisits);
        System.out.println("DFS edges: " + dfsEdges);
        System.out.printf("Time: %.2f ms%n", elapsedMillis());
    }

    public void printTopoMetrics() {
        System.out.println("Push/Pop operations: " + kahnOps);
        System.out.printf("Time: %.2f ms%n", elapsedMillis());
    }

    public void printDAGMetrics() {
        System.out.println("Relaxations: " + relaxations);
        System.out.printf("Time: %.2f ms%n", elapsedMillis());
    }
}

