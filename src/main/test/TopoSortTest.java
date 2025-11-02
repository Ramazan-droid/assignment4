package graph.topo;

import org.junit.jupiter.api.Test;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class TopoSortTest {

    @Test
    public void testSimpleDAG() {
        List<List<Integer>> adj = new ArrayList<>();
        adj.add(new ArrayList<>(Arrays.asList(1, 2))); // 0 -> 1,2
        adj.add(new ArrayList<>(Collections.singletonList(2))); // 1 -> 2
        adj.add(new ArrayList<>()); // 2 no edges

        List<Integer> topo = TopoSortMain.kahnTopoSort(adj, new graph.utils.Metrics());

        assertTrue(topo.indexOf(0) < topo.indexOf(1));
        assertTrue(topo.indexOf(0) < topo.indexOf(2));
        assertTrue(topo.indexOf(1) < topo.indexOf(2));
    }

    @Test
    public void testDisconnectedDAG() {
        List<List<Integer>> adj = new ArrayList<>();
        adj.add(new ArrayList<>()); // 0
        adj.add(new ArrayList<>()); // 1
        adj.add(new ArrayList<>()); // 2

        List<Integer> topo = TopoSortMain.kahnTopoSort(adj, new graph.utils.Metrics());
        assertEquals(3, topo.size());
        assertTrue(topo.containsAll(Arrays.asList(0,1,2)));
    }
}
