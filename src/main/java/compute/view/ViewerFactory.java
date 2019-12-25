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
                "graph {" +
                        "   canvas-color: blue;" +
                        "       fill-mode: gradient-radial;" +
                        "       fill-color: white, #EEEEEE;" +
                        "       padding: 20px;" +
                        "   }" +
                        "node {" +
                        "   size-mode: dyn-size;" +
                        "   shape: circle;" +
                        "   size: 40px;" +
                        "   text-size: 20px;" +
                        "   fill-mode: plain;" +
                        "   fill-color: blue;" +
                        "   stroke-mode: plain;" +
                        "   stroke-color: blue;" +
                        "   stroke-width: 3px;" +
                        "}";
        graph.addAttribute("ui.stylesheet", styleSheet);
        for (Node node : graph) {
            node.addAttribute("ui.label", node.getId());
            node.addAttribute("text-size", 20);
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
