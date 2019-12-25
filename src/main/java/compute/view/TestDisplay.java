package compute.view;

import compute.constractor.GraphBuilder;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

import java.io.File;

public class TestDisplay {

    public static void main(String[] args) {

        String userdir = System.getProperty("user.dir");
        String userdirName = new File(userdir).getPath() + "/src" + "/main" + "/resources/";
        GraphBuilder graphBuilder = new GraphBuilder();
        Graph graph = graphBuilder.read(userdirName + "rbm24.medium.graph").delimiter("\t").getGraph();

//        String styleSheet =
//                "node {" +
//                        "	fill-color: blue;" +
//                        "}" +
//                        "node.marked {" +
//                        "	fill-color: red;" +
//                        "}";
         String styleSheet =
                "graph {"+
                        "   canvas-color: blue;"+
                        "       fill-mode: gradient-radial;"+
                        "       fill-color: white, #EEEEEE;"+
                        "       padding: 20px;"+
                        "   }"+
                        "node {"+
                        "   size-mode: dyn-size;"+
                        "   shape: circle;"+
                        "   size: 40px;"+
                        "   text-size: 20px;"+
                        "   fill-mode: plain;"+
                        "   fill-color: blue;"+
                        "   stroke-mode: plain;"+
                        "   stroke-color: blue;"+
                        "   stroke-width: 3px;"+
                        "}";
        graph.addAttribute("ui.stylesheet", styleSheet);

        for (Node node : graph) {
            node.addAttribute("ui.label", node.getId());
            node.addAttribute("text-size",20);
        }

        graph.setAutoCreate(true);
        graph.setStrict(false);
        graph.display();

    }
}
