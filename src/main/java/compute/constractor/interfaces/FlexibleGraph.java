package compute.constractor.interfaces;

import java.util.Iterator;

//用于可插拔的图的模型构建
public interface FlexibleGraph<A> {

    String getNodeMarker(A name);

    int getDegree(A vertex);

    int getVertexSize();

    void display();

    Iterator<String> getIndegree(A node);

    Iterator<String> getOutdegree(A node);

    int getDistance(String node1, String node2);
}
