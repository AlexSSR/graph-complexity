package compute.algorithm;

import compute.algorithm.interfaces.ProbabilityFunction;

import java.util.Map;
import java.util.TreeMap;

public class ProbabilityFunctionImpl implements ProbabilityFunction<Integer> {

    public static ProbabilityFunctionImpl getInstance() {

        return new ProbabilityFunctionImpl();
    }

    @Override
    public TreeMap<Integer, Double> getProbabilityFunction(Iterable<Integer> iterable) {
        TreeMap<Integer, Integer> mediaMap = new TreeMap<>(Integer::compare);
        TreeMap<Integer, Double> resultMap = new TreeMap<>(Integer::compare);
        Integer sum = 0;
        for (Integer ele : iterable) {
            if (mediaMap.containsKey(ele)) {
                mediaMap.put(ele, mediaMap.get(ele) + 1);
            } else {
                mediaMap.put(ele, 1);
            }
            sum += 1;
        }
        final double total = sum.doubleValue();
        mediaMap.forEach((x, y) -> resultMap.put(x, y / total));
        return resultMap;
    }

    @Override
    public TreeMap<Integer, Double> getProbabilityFunction(int[][] iterable, Map<String, Integer> schema) {
        return null;
    }

}
