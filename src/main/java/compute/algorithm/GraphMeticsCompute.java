package compute.algorithm;

import compute.algorithm.interfaces.AlgorithmLogic;
import compute.constractor.interfaces.FlexibleGraph;
import compute.constractor.interfaces.MatrixBuilder;

import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.locks.ReentrantLock;

public class GraphMeticsCompute<T> {

    //For iterative vertex calculations
    private Iterable<T> vertexIterator;

    //Used to handle different graph implementations, returning the same properties,provided by interface FlexibleGraph
    private FlexibleGraph graph;

    //Only considering the degree(Excluding the in-degree、out-degree and the distribution of the edge weight)
    private TreeMap<Integer, Integer> degreeMap;

    //Cache discrete probability function
    private TreeMap<Integer, Double> degreeProbability;

    //Cache discrete distribution function
    private TreeMap<Integer, Double> degreeDistributeFunction;

    //Used to cache records, ensuring that each key is consumed exactly once.
    private TreeSet<Integer> ExeactlyOnce = new TreeSet<>(Integer::compareTo);

    //Relative distance matrix used to record the graph,provided by CommonMatrixBuilder!
    private int[][] distanceMatrix;

    // vertex number
    private Integer vertexNumber;

    //lock
    ReentrantLock lock = new ReentrantLock();

    public Double MAX_DD_ENTROPY;
    public Double MAX_ENTROPY;

    public GraphMeticsCompute(Iterable<T> vertexIterator, FlexibleGraph graph) {
        this.vertexIterator = vertexIterator;
        this.graph = graph;
        this.vertexNumber = graph.getVertexSize();
        MAX_DD_ENTROPY = Math.log10(vertexNumber - 1);
        MAX_ENTROPY = Math.log10(vertexNumber);
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

    //calculate network totala degree
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

    private Double distributeFunctionComputeProbabilityAltered(Integer degree) {
        if (degreeProbability == null) {
            initdegreeProbability();
        }
        if (ExeactlyOnce.contains(degree)) {
            return 1D;
        }
        if (degreeProbability.containsKey(degree)) {
            ExeactlyOnce.add(degree);
            return degreeProbability.get(degree);
        }
        ExeactlyOnce.add(degree);
        return degreeMap.get(degree) / vertexNumber.doubleValue();
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

    //calculate degree distribution (∑{(di+1)[1-p(di)]+delta})
    private double getEdgeAndVertexTotalValue() {
        Double sum = 0d;
        for (T vertex : vertexIterator) {
            int degree = graph.getDegree(vertex);
            double single = (degree + 1) * (1 - distributeFunctionComputeProbability(degree)) + 1 / vertexNumber.doubleValue() * vertexNumber;
            sum += single;
        }
        return sum;
    }

    //Shannon Entropy Formula
    private double structEntropyCompute(AlgorithmLogic logic) {
        double entropy = 0D;
        for (T vertex : vertexIterator) {
            int degree = graph.getDegree(vertex);
            double mediaResult = logic.processFunction(degree);
            double single = Math.log10(mediaResult) * (mediaResult);
            entropy += single;
        }

        ExeactlyOnce.clear();
        return -entropy;
    }

    //Topological Information Entropy
    public double classicEntropyInfo() {
        AlgorithmLogic computeTopologyLogic = (degree) -> Math.abs(degree) / Math.abs(vertexNumber.doubleValue());
        return structEntropyCompute(computeTopologyLogic);
    }

    //Degree Distribution Information Entropy,but it is wrong
    public double getDDEntropy(Boolean openDynamicProgramming) {
        if (openDynamicProgramming) {
            dynamicProgramming();
        }
        AlgorithmLogic computeTopologyLogic = (degree) -> distributeFunctionCompute(degree);
        return structEntropyCompute(computeTopologyLogic);
    }

    //Degree Distribution Information Entropy,and it has been altered
    public double getDDEntropyAltered(Boolean openDynamicProgramming) {
        if (openDynamicProgramming) {
            dynamicProgrammingProbability();
        }
        AlgorithmLogic computeTopologyLogic = (degree) -> distributeFunctionComputeProbabilityAltered(degree);
        return structEntropyCompute(computeTopologyLogic);
    }

    //WU Information Entropy
    public double getWUEntropy() {
        final Double totalDegree = getTotalDegree().doubleValue();
        AlgorithmLogic computeTopologyLogic = (degree) -> degree / totalDegree;
        return structEntropyCompute(computeTopologyLogic);
    }

    //SD Information Entropy (∑{(di+1)[1-p(di)]+delta})
    public double getSDEntropy() {
        final double edgeAndVertexTotalValue = getEdgeAndVertexTotalValue();
        AlgorithmLogic computeTopologyLogic = (degree) -> (((degree + 1) * (1 - distributeFunctionComputeProbability(degree)) + 1 / vertexNumber.doubleValue() * vertexNumber) / edgeAndVertexTotalValue);
        return structEntropyCompute(computeTopologyLogic);
    }
}