package compute.algorithm;

import compute.algorithm.interfaces.DistributionFunction;

import java.util.Map;
import java.util.TreeMap;

public class DistributionFunctionImpl implements DistributionFunction<Integer> {

    public static DistributionFunctionImpl getInstance() {

        return new DistributionFunctionImpl();
    }

    @Override
    public TreeMap<Integer, Double> getDistributionFunction(Iterable<Integer> iterable) {
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
        double z = 0D;
        mediaMap.forEach((x, y) -> {
            Double c = y / total + z;
            resultMap.put(x, c);
        });
        return resultMap;
    }

    @Override
    public TreeMap<Integer, Double> getDistributionFunction(int[][] iterable, Map<String, Integer> schema) {
        return null;
    }

}
