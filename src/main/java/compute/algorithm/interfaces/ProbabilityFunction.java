package compute.algorithm.interfaces;

import java.util.Map;
import java.util.TreeMap;

public interface ProbabilityFunction<A> {

    TreeMap<A, Double> getProbabilityFunction(Iterable<A> iterable);

    TreeMap<A, Double> getProbabilityFunction(int[][] iterable, Map<String,Integer> schema);
}
