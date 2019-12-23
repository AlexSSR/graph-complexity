package compute.algorithm.interfaces;

import java.util.Map;
import java.util.TreeMap;

public interface DistributionFunction<A> {

    TreeMap<A, Double> getDistributionFunction(Iterable<A> iterable);

    TreeMap<A, Double> getDistributionFunction(int[][] iterable, Map<String,Integer> schema);
}