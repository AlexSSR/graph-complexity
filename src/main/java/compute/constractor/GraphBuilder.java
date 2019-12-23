package compute.constractor;

import com.google.common.io.Files;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

public class GraphBuilder {

    private final String DLIMITER = "->";
    private List<String> dataList;
    private String USER_DEFINE_DLIMITER;

    public GraphBuilder read(String filename) {
        File file = new File(filename);
        try {
            this.dataList = Files.readLines(file, Charset.forName("utf-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return this;
    }

    public GraphBuilder delimiter(String delimiter) {
        this.USER_DEFINE_DLIMITER = delimiter;
        return this;
    }

    public Graph getGraph() {
        SingleGraph graph = new SingleGraph("Tutorial 1");

        for (String line : dataList) {

            String delimiter;

            if (USER_DEFINE_DLIMITER == null) {
                delimiter = DLIMITER;
            } else {
                delimiter = USER_DEFINE_DLIMITER;
            }

            for (String rowData : dataList) {
                String[] elements = rowData.split(delimiter);
                String node1Name = elements[0].trim();
                Node node1 = graph.getNode(node1Name);
                String node2Name = elements[1].trim();
                Node node2 = graph.getNode(node2Name);
                if (node1 == null) {
                    graph.addNode(node1Name);
                    node1 = graph.getNode(node1Name);
                }
                if (node2 == null) {
                    graph.addNode(node2Name);
                    node2 = graph.getNode(node2Name);
                }
                if (!node1.hasEdgeBetween(node2)) {
                    graph.addEdge(node1Name + "->" + node2Name, node1, node2);
                }
            }
        }
        return graph;
    }
}
