package graph.scc;

import graph.utils.Metrics;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class TarjanSCCTest {

    @Test
    public void testSingleNode() {
        List<List<Integer>> adj = new ArrayList<>();
        adj.add(new ArrayList<>()); // 1 node, no edges

        Metrics metrics = new Metrics();
        TarjanSCC tarjan = new TarjanSCC(adj, metrics);
        List<List<Integer>> sccs = tarjan.computeSCCs();

        assertEquals(1, sccs.size());
        assertEquals(Collections.singletonList(0), sccs.get(0));
    }

    @Test
    public void testTwoNodeCycle() {
        List<List<Integer>> adj = new ArrayList<>();
        adj.add(new ArrayList<>(Collections.singletonList(1))); // 0 -> 1
        adj.add(new ArrayList<>(Collections.singletonList(0))); // 1 -> 0

        Metrics metrics = new Metrics();
        TarjanSCC tarjan = new TarjanSCC(adj, metrics);
        List<List<Integer>> sccs = tarjan.computeSCCs();

        assertEquals(1, sccs.size());
        assertTrue(sccs.get(0).containsAll(Arrays.asList(0,1)));
    }

    @Test
    public void testMultipleSCCs() {
        List<List<Integer>> adj = new ArrayList<>();
        adj.add(new ArrayList<>(Collections.singletonList(1))); // 0 -> 1
        adj.add(new ArrayList<>(Collections.singletonList(0))); // 1 -> 0
        adj.add(new ArrayList<>(Collections.singletonList(3))); // 2 -> 3
        adj.add(new ArrayList<>()); // 3 no edges

        Metrics metrics = new Metrics();
        TarjanSCC tarjan = new TarjanSCC(adj, metrics);
        List<List<Integer>> sccs = tarjan.computeSCCs();

        assertEquals(3, sccs.size()); // SCCs: [0,1], [2], [3]
    }
}
