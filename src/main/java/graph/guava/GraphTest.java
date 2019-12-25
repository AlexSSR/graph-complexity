package graph.guava;

import com.google.common.graph.*;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class GraphTest {

    public static void main(String[] args) {

        Integer NODE_COUNT = 10;

        MutableValueGraph<String, Integer> graph = ValueGraphBuilder.undirected()
                .allowsSelfLoops(true)
                .expectedNodeCount(NODE_COUNT)
                .nodeOrder(ElementOrder.<String>insertion())
                .build();

        graph.putEdgeValue("RBFOX2", "RBM24", 1);
        graph.putEdgeValue("RBFOX2", "CELF1", 1);
        graph.putEdgeValue("RBFOX1", "RBM24", 1);
        graph.putEdgeValue("RBFOX1", "CELF1", 1);
        graph.putEdgeValue("RBM24", "RBFOX3", 1);
        graph.putEdgeValue("CELF1", "RBFOX3", 1);
        graph.putEdgeValue("CELF1", "RBM24", 1);
        graph.putEdgeValue("RBM24", "CELF1", 1);
        graph.putEdgeValue("DSTN", "RBM24", 1);

        int sum = 0;

        //Topological information content of classic graph G
        for (String vertex : graph.nodes()) {
            int degree = graph.degree(vertex);
            double single = Math.log10(degree) * degree;
            sum += single;
        }
        int V = Math.abs(graph.nodes().size());
        double logV = Math.log10(V);
        double result = V * logV - sum;
        System.out.println(result);

        TreeMap<Integer, Integer> hashMap = new TreeMap<>(new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return o1.compareTo(o2);
            }
        });

        int sum2 = 0;

        for (String vertex : graph.nodes()) {

            int degree = graph.degree(vertex);
            sum2 += 1;

            if (hashMap.containsKey(degree)) {
                Integer number = hashMap.get(degree);
                hashMap.put(degree, number + 1);
            } else {
                hashMap.put(degree, 1);
            }
        }
        HashMap<Integer, BigDecimal> map = new HashMap<>();

        //This should be set to lazy loading, which is more conducive to calculating the current value
        for (Map.Entry<Integer, Integer> entry : hashMap.entrySet()) {
            BigDecimal value = new BigDecimal(entry.getValue());
            BigDecimal sum3 = new BigDecimal(sum2);
            BigDecimal divide = value.divide(sum3, 3, BigDecimal.ROUND_HALF_UP);
            map.put(entry.getKey(), divide);
        }
        System.out.println(map);

    }
}
