package compute.view;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

import java.util.concurrent.locks.ReentrantLock;

public class ViewerFactory {

    private String edgeColor = "red";
    private String nodeColor = "blue";
    private Graph graph;
    private static ReentrantLock lock = new ReentrantLock();

    public static Graph getDefaultStyle(Graph graph) {
        lock.lock();
        String styleSheet =
                "node {" +
                        "	fill-color: blue;" +
                        "}" +
                        "node.marked {" +
                        "	fill-color: red;" +
                        "}";
        graph.addAttribute("ui.stylesheet", styleSheet);
        for (Node node : graph) {
            node.addAttribute("ui.label", node.getId());
        }
        graph.setAutoCreate(true);
        graph.setStrict(false);
        lock.unlock();
        return graph;
    }

    public ViewerFactory getDiyInstance(Graph graph) {
        this.graph = graph;
        return this;
    }

    public ViewerFactory setEdgeColor(String color) {
        this.edgeColor = color;
        return this;
    }

    public ViewerFactory setNodeColor(String color) {
        this.nodeColor = color;
        return this;
    }

    public ViewerFactory nameDisplay(Boolean display) {

        if (display) {
            for (Node node : graph) {
                node.addAttribute("ui.label", node.getId());
            }
            return this;
        } else {
            for (Node node : graph) {
                node.addAttribute("ui.label", "");
            }
            return this;
        }
    }

    public Graph getGraph() {
        return this.graph;
    }
}
