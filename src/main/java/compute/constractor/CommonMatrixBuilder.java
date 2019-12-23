package compute.constractor;

import compute.constractor.interfaces.FlexibleGraph;
import compute.constractor.interfaces.MatrixBuilder;

import java.util.HashMap;

public class CommonMatrixBuilder<T> implements MatrixBuilder<T> {

    private FlexibleGraph<T> graphHelper;
    private int[][] matrix;
    private HashMap<String, Integer> index;
    private int number = 0;

    public CommonMatrixBuilder(FlexibleGraph<T> graphHelper) {
        this.graphHelper = graphHelper;
        int size = graphHelper.getVertexSize();
        matrix = new int[size][size];
    }

    private int getIndex(String name) {
        if (index.containsKey(name)) {
            return index.get(name);
        }
        number += 1;
        index.put(name, number);
        return index.get(name);
    }

    @Override
    public void computeDistanceMatrix(Iterable<T> vertexIterator) {
        for (T t : vertexIterator) {
            String nodeMarker = graphHelper.getNodeMarker(t);
            int i = getIndex(nodeMarker);
            graphHelper.getIndegree(t).forEachRemaining(x -> {
                int j = getIndex(x);
                matrix[i][j] = computeDistance(nodeMarker, x);
            });
        }
    }


    @Override
    public int computeDistance(String node1, String node2) {
        return graphHelper.getDistance(node1, node2);
    }

    @Override
    public int[][] getMatrix() {
        return matrix;
    }
}
