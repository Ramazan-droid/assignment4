# Assignment 4

## Ramazan Kozhabek
## SE-2426

## Description
In this assignment I implemented Tarjan's,Kahns's and Shortest Path algorithms.

## Brief description of datasets.

- **small_1.json**: 9 vertices, 14 edges, DAG,weight model:edge.  
- **small_2.json**: 7 vertices, 8 edges, DAG,weight model:uniform.  
- **small_3.json**: 7 vertices, 8 edges, Cyclic,weight model:edge.  

- **medium_1.json**: 17 vertices, 70 edges, Cyclic,weight model:uniform.  
- **medium_2.json**: 19 vertices, 193 edges, Cyclic,weight model:uniform.  
- **medium_3.json**: 16 vertices, 70 edges, Cyclic,weight model:uniform.  

- **large_1.json**: 32 vertices, 358 edges, DAG,weight model:edge.  
- **large_2.json**: 21 vertices, 189 edges, Cyclic,weight model:uniform.  
- **large_3.json**: 44 vertices, 643 edges, DAG,weight model:edge.


## Results:
## Task1.1
| Dataset   | Vertices | Edges | DFS Visits | DFS Edges | Time (ms) |
|------------|-----------|--------|-------------|------------|-----------|
| small_1    | 9         | 14     | 9           | 14         | 0.4       |
| small_2    | 7         | 8      | 7           | 8          | 0.02      |
| small_3    | 7         | 8      | 7           | 8          | 0.02      |
| medium_1   | 17        | 70     | 19          | 189        | 0.06      |
| medium_2   | 19        | 193    | 19          | 193        | 0.07      |
| medium_3   | 16        | 70     | 16          | 70         | 0.04      |
| large_1    | 32        | 358    | 32          | 358        | 0.09      |
| large_2    | 21        | 189    | 21          | 1876       | 0.07      |
| large_3    | 44        | 643    | 44          | 643        | 0.15      |

## Task1.2

| Dataset   | Vertices | Edges | Push/Pop Operations | Time (ms) |
|------------|-----------|--------|----------------------|-----------|
| small_1    | 9         | 14     | 26                   | 0.41      |
| small_2    | 7         | 8      | 12                   | 0.02      |
| small_3    | 7         | 8      | 8                    | 0.02      |
| medium_1   | 17        | 70     | 0                    | 0.01      |
| medium_2   | 19        | 193    | 0                    | 0.01      |
| medium_3   | 16        | 70     | 2                    | 0.01      |
| large_1    | 32        | 358    | 522                  | 0.09      |
| large_2    | 21        | 189    | 0                    | 0.01      |
| large_3    | 44        | 643    | 962                  | 0.20      |

## Task1.3
## Used edge weights
| Dataset   | Vertices | Edges | Critical Path Length | Relaxations | Time (ms) |
|------------|-----------|--------|-----------------------|--------------|-----------|
| small_1    | 9         | 14     | 4                     | 6            | 0.68      |
| small_2    | 7         | 8      | 2                     | 6            | 0.02      |
| small_3    | 7         | 8      | 1                     | 3            | 0.02      |
| medium_1   | 17        | 70     | 0                     | 0            | 0.01      |
| medium_2   | 19        | 193    | 0                     | 0            | 0.01      |
| medium_3   | 16        | 70     | 1                     | 2            | 0.01      |
| large_1    | 32        | 358    | 20                    | 235          | 0.14      |
| large_2    | 21        | 189    | 0                     | 0            | 0.01      |
| large_3    | 44        | 643    | 25                    | 438          | 0.21      |

## 3. Analysis

### 3.1 Strongly Connected Components (SCC)
- Detects and compresses cycles in the graph.
- In **dense graphs**, large SCCs indicate strong interdependence between tasks.
- In **sparse graphs**, SCCs are smaller or absent, simplifying scheduling.
- Once SCCs are compressed, the resulting graph becomes a **DAG** ready for topological and path analysis.

### 3.2 Topological Ordering
- Establishes valid execution sequences in the DAG.
- **Sparse DAGs** → fewer dependencies, easier ordering.
- **Dense DAGs** → multiple valid orders, slightly higher computation time.
- Topological order serves as a foundation for efficient shortest and longest path algorithms.

### 3.3 Shortest and Longest Paths in DAGs
- Shortest Path = minimal total duration or cost (efficient schedule).
- Longest Path = critical path (maximum total time, reveals bottlenecks).
- Both algorithms operate in **O(V + E)** time complexity, outperforming Dijkstra or Bellman-Ford since no cycles exist.
- Analysis showed:
  - Larger SCCs → longer preprocessing time.
  - Sparse DAGs → faster overall computation and simpler paths.

---

## 4. Conclusions

### 4.1 When to Use Each Method
| Method | Use Case | Purpose |
|:--|:--|:--|
| **SCC Detection** | Cyclic dependency graphs | Simplifies structure |
| **Topological Sorting** | DAGs | Valid task execution order |
| **DAG Shortest Path** | Scheduling optimization | Minimal duration |
| **DAG Longest Path** | Critical path detection | Bottleneck analysis |
