package graph.guava;

import com.google.common.graph.*;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class Graph {

    public static void main(String[] args) {

        Integer NODE_COUNT = 10;

        MutableValueGraph<String, Integer> graph = ValueGraphBuilder.undirected() //有向
                .allowsSelfLoops(true) //允许自环
                .expectedNodeCount(NODE_COUNT) //期望节点数
                .nodeOrder(ElementOrder.<String>insertion()) //节点顺序
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

        //经典的图G的拓扑信息内容
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

        //此处应该设置为懒加载，这样更有利于计算当前的数值
        for (Map.Entry<Integer, Integer> entry : hashMap.entrySet()) {
            BigDecimal value = new BigDecimal(entry.getValue());
            BigDecimal sum3 = new BigDecimal(sum2);
            BigDecimal divide = value.divide(sum3, 3, BigDecimal.ROUND_HALF_UP);
            map.put(entry.getKey(), divide);
        }
        System.out.println(map);

    }
}
