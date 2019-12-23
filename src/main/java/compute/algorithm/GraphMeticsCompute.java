package compute.algorithm;

import compute.algorithm.interfaces.AlgorithmLogic;
import compute.constractor.interfaces.FlexibleGraph;
import compute.constractor.interfaces.MatrixBuilder;

import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.locks.ReentrantLock;

public class GraphMeticsCompute<T> {

    //用于内部迭代顶点计算
    private Iterable<T> vertexIterator;

    //用于处理融合不同的graph实现，返回相同的性质
    private FlexibleGraph graph;

    //只考虑度，不考虑入度和出度、边长的分布
    private TreeMap<Integer, Integer> degreeMap;

    //缓存离散型概率函数
    private TreeMap<Integer, Double> degreeProbability;

    //缓存离散型分布函数 key为查询条件，value为结果
    private TreeMap<Integer, Double> degreeDistributeFunction;

    //用于缓存记录，保证每个key只被消费一次
    private TreeSet<Integer> ExeactlyOnce = new TreeSet<>(Integer::compareTo);

    //用于记录图的相对距离矩阵
    private int[][] distanceMatrix;

    //
    private Integer vertexNumber;

    //
    ReentrantLock lock = new ReentrantLock();

    public Double MAX_DD_ENTROPY;

    public GraphMeticsCompute(Iterable<T> vertexIterator, FlexibleGraph graph) {
        this.vertexIterator = vertexIterator;
        this.graph = graph;
        this.vertexNumber = graph.getVertexSize();
        MAX_DD_ENTROPY = Math.log10(vertexNumber - 1);
    }

    public GraphMeticsCompute(Iterable<T> vertexIterator, FlexibleGraph graph, MatrixBuilder matrix) {
        this.vertexIterator = vertexIterator;
        this.graph = graph;
        this.vertexNumber = graph.getVertexSize();
        MAX_DD_ENTROPY = Math.log10(vertexNumber - 1);
        this.distanceMatrix = matrix.getMatrix();
    }

    public void GraphDisplay() {
        graph.display();
    }

    private void initdegreeDistributeFunction() {
        lock.lock();
        try {
            if (degreeMap == null) {
                initDegreeMap();
            }
            degreeDistributeFunction = new TreeMap<Integer, Double>();
        } finally {
            lock.unlock();
        }
    }

    private void initdegreeProbability() {
        lock.lock();
        try {
            if (degreeMap == null) {
                initDegreeMap();
            }
            degreeProbability = new TreeMap<Integer, Double>();
        } finally {
            lock.unlock();
        }
    }

    private void initDegreeMap() {
        lock.lock();
        try {
            TreeMap<Integer, Integer> map = new TreeMap<>(Integer::compareTo);
            Integer vertexNum = 0;
            //求解分布
            for (T vertex : vertexIterator) {
                int degree = graph.getDegree(vertex);
                if (map.containsKey(degree)) {
                    map.put(degree, map.get(degree) + 1);
                } else {
                    map.put(degree, 1);
                }
                vertexNum += 1;
            }
            degreeMap = map;
            vertexNumber = vertexNum;
        } finally {
            lock.unlock();
        }
    }

    //求解网络总度值
    private Integer getTotalDegree() {
        if (degreeMap == null) {
            initDegreeMap();
        }
        Integer totalDegree = 0;
        for (Map.Entry<Integer, Integer> set : degreeMap.entrySet()) {
            totalDegree += (set.getValue() * set.getKey());
        }
        return totalDegree;
    }

    private void dynamicProgramming() {
        if (degreeDistributeFunction == null) {
            initdegreeDistributeFunction();
        }
        Double probabilityIntegral = 0D;
        for (Map.Entry<Integer, Integer> entry : degreeMap.entrySet()) {
            Integer key = entry.getKey();
            Integer value = entry.getValue();
            double possi = value / vertexNumber.doubleValue();
            probabilityIntegral += possi;
            degreeDistributeFunction.put(key, probabilityIntegral);
        }
    }

    private void dynamicProgrammingProbability() {
        if (degreeProbability == null) {
            initdegreeProbability();
        }
        for (Map.Entry<Integer, Integer> entry : degreeMap.entrySet()) {
            Integer key = entry.getKey();
            Integer value = entry.getValue();
            double possi = value / vertexNumber.doubleValue();
            degreeProbability.put(key, possi);
        }
    }

    private Double distributeFunctionComputeAltered(Integer degree) {
        if (degreeDistributeFunction == null) {
            initdegreeDistributeFunction();
        }
        if (degreeDistributeFunction.containsKey(degree)) {
            return degreeDistributeFunction.get(degree);
        }
        if (degreeMap.firstKey() == degree && degreeDistributeFunction.get(degree) == null) {
            degreeDistributeFunction.put(degree, degreeMap.get(degree) / vertexNumber.doubleValue());
            return degreeDistributeFunction.get(degree);
        }
        if (degreeDistributeFunction.lowerKey(degree) != null) {
            Integer lastKey = degreeDistributeFunction.lowerKey(degree);
            Double lastSumValue = degreeDistributeFunction.get(lastKey);
            degreeDistributeFunction.put(degree, lastSumValue + degreeMap.get(degree) / vertexNumber.doubleValue());
            return degreeDistributeFunction.get(degree);
        }

        Double lastSumValue = distributeFunctionCompute(degreeMap.lowerKey(degree));
        degreeDistributeFunction.put(degreeMap.lowerKey(degree), lastSumValue);
        double currentValue = lastSumValue + degreeMap.get(degree) / vertexNumber.doubleValue();
        degreeDistributeFunction.put(degree, currentValue);

        return currentValue;
    }

    private Double distributeFunctionCompute(Integer degree) {
        if (degreeDistributeFunction == null) {
            initdegreeDistributeFunction();
        }
        if (ExeactlyOnce.contains(degree)) {
            return 1D;
        }
        if (degreeDistributeFunction.containsKey(degree)) {
            ExeactlyOnce.add(degree);
            return degreeDistributeFunction.get(degree);
        }
        if (degreeMap.firstKey() == degree && degreeDistributeFunction.get(degree) == null) {
            degreeDistributeFunction.put(degree, degreeMap.get(degree) / vertexNumber.doubleValue());
            ExeactlyOnce.add(degree);
            return degreeDistributeFunction.get(degree);
        }
        if (degreeDistributeFunction.lowerKey(degree) != null) {
            Integer lastKey = degreeDistributeFunction.lowerKey(degree);
            Double lastSumValue = degreeDistributeFunction.get(lastKey);
            degreeDistributeFunction.put(degree, lastSumValue + degreeMap.get(degree) / vertexNumber.doubleValue());
            ExeactlyOnce.add(degree);
            return degreeDistributeFunction.get(degree);
        }
        Double lastSumValue = distributeFunctionCompute(degreeMap.lowerKey(degree));
        degreeDistributeFunction.put(degreeMap.lowerKey(degree), lastSumValue);
        double currentValue = lastSumValue + degreeMap.get(degree) / vertexNumber.doubleValue();
        degreeDistributeFunction.put(degree, currentValue);
        ExeactlyOnce.add(degree);
        return currentValue;
    }

    private Double distributeFunctionComputeProbability(Integer degree) {
        if (degreeProbability == null) {
            initdegreeProbability();
        }
        if (degreeProbability.containsKey(degree)) {
            return degreeProbability.get(degree);
        }
        return degreeMap.get(degree) / vertexNumber.doubleValue();
    }

    //计算度和度的分布(∑{(di+1)[1-p(di)]+delta})
    private double getEdgeAndVertexTotalValue() {
        Double sum = 0d;
        for (T vertex : vertexIterator) {
            int degree = graph.getDegree(vertex);
            double single = (degree + 1) * (1 - distributeFunctionCompute(degree)) + 1 / vertexNumber.doubleValue() * vertexNumber;
            sum += single;
        }
        return sum;
    }

    //计算度和度的分布  - ∑(N/V)*log(N/V)
    //用于累加求和
    private double structEntropyCompute(AlgorithmLogic logic) {
        double entropy = 0D;
        //累加求和
        for (T vertex : vertexIterator) {
            int degree = graph.getDegree(vertex);
            double mediaResult = logic.processFunction(degree);
            double single = Math.log10(mediaResult) * (mediaResult);
            entropy += single;
        }
        return -entropy;
    }

    //G的拓扑信息内容
    public double classicEntropyInfo() {
        AlgorithmLogic computeTopologyLogic = (degree) -> Math.abs(degree) / Math.abs(vertexNumber.doubleValue());
        return structEntropyCompute(computeTopologyLogic);
    }

    //DD度分布熵
    public double getDDEntropy(Boolean openDynamicProgramming) {
        if (openDynamicProgramming) {
            dynamicProgramming();
        }
        AlgorithmLogic computeTopologyLogic = (degree) -> distributeFunctionCompute(degree);
        return structEntropyCompute(computeTopologyLogic);
    }

    //DD度分布熵,修正后的算法
    public double getDDEntropyAltered(Boolean openDynamicProgramming) {
        if (openDynamicProgramming) {
            dynamicProgrammingProbability();
        }
        AlgorithmLogic computeTopologyLogic = (degree) -> distributeFunctionComputeProbability(degree);
        return structEntropyCompute(computeTopologyLogic);
    }

    //WU结构熵
    public double getWUEntropy() {
        final Double totalDegree = getTotalDegree().doubleValue();
        AlgorithmLogic computeTopologyLogic = (degree) -> degree / totalDegree;
        return structEntropyCompute(computeTopologyLogic);
    }

    //SD结构熵
    public double getSDEntropy() {
        //计算度和度的分布(∑{(di+1)[1-p(di)]+delta})
        final double edgeAndVertexTotalValue = getEdgeAndVertexTotalValue();
        AlgorithmLogic computeTopologyLogic = (degree) -> (((degree + 1) * (1 - distributeFunctionCompute(degree)) + 1 / vertexNumber.doubleValue() * vertexNumber) / edgeAndVertexTotalValue);
        return structEntropyCompute(computeTopologyLogic);
    }
}