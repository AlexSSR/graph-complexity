package graph.graphstream.util;

import com.google.common.io.Files;
import compute.constractor.interfaces.FlexibleGraph;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class GraphStreamFlexibleGraphImpl implements FlexibleGraph<Node> {

    private Graph graph;
    private List<String> list;
    private HashMap<String, Integer> map;

    public GraphStreamFlexibleGraphImpl(Graph graph, String fileName) {
        this.graph = graph;
        File file = new File(fileName);
        try {
            this.list = Files.readLines(file, Charset.forName("utf-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public GraphStreamFlexibleGraphImpl(Graph graph) {
        this.graph = graph;
    }

    @Override
    public String getNodeMarker(Node node) {
        return node.toString();
    }

    @Override
    public int getDegree(Node vertex) {
        return vertex.getDegree();
    }

    @Override
    public int getVertexSize() {
        return graph.getNodeCount();
    }

    @Override
    public void display() {
        graph.display();
    }

    @Override
    public Iterator<String> getIndegree(Node node) {
        Iterator<Edge> edgeList = node.getEnteringEdgeIterator();
        ArrayList<String> result = new ArrayList<>();
        edgeList.forEachRemaining(x -> {
            Node sourceNode = x.getSourceNode();
            result.add(getNodeMarker(sourceNode));
        });
        return result.iterator();
    }

    @Override
    public Iterator<String> getOutdegree(Node node) {
        Iterator<Edge> edgeList = node.getEnteringEdgeIterator();
        ArrayList<String> result = new ArrayList<>();
        edgeList.forEachRemaining(x -> {
            Node targetNode = x.getTargetNode();
            result.add(getNodeMarker(targetNode));
        });
        return result.iterator();
    }

    @Override
    public int getDistance(String node1, String node2) {
        if (map == null) {
            HashMap<String, Integer> hashMap = new HashMap<>(16);
            for (String line : list) {
                String[] message = line.split(" ");
                String key = message[0] + message[1];
                map.put(key, Integer.parseInt(message[2]));
            }
            map = hashMap;
        }
        if (map.size() != 0) {
            String key = node1 + node2;
            Integer distance = map.get(key);
            return distance;
        }
        return 1;
    }

    public Node getNode(String name) {
        return graph.getNode(name);
    }
}
