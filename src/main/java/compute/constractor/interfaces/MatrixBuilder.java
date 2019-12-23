package compute.constractor.interfaces;

public interface MatrixBuilder<T> {

    void computeDistanceMatrix(Iterable<T> vertexIterator);

    int computeDistance(String node1, String node2);

    int[][] getMatrix();

}
