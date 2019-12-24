package compute;

import compute.algorithm.GraphMeticsCompute;
import compute.constractor.interfaces.FlexibleGraph;
import compute.constractor.GraphBuilder;
import graph.graphstream.util.GraphStreamFlexibleGraphImpl;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

import java.io.File;

public class TestComputer {

    public static void main(String[] args) {

        String userdir = System.getProperty("user.dir");
        String userdirName = new File(userdir).getPath() + "/src" + "/main" + "/resources/";
        GraphBuilder graphBuilder = new GraphBuilder();
        Graph graph3 = graphBuilder.read(userdirName + "rbm24.media.graph").delimiter("\t").getGraph();
        Iterable<Node> eachNode3 = (Iterable<Node>) graph3.getEachNode();
        FlexibleGraph flexibleGraph3 = new GraphStreamFlexibleGraphImpl(graph3);
        GraphMeticsCompute<Node> compute3 = new GraphMeticsCompute<>(eachNode3, flexibleGraph3);
        System.out.println(compute3.MAX_DD_ENTROPY);
        System.out.println(compute3.getDDEntropy(true));
        System.out.println(compute3.getDDEntropyAltered(true));
        System.out.println(compute3.getWUEntropy());
        System.out.println(compute3.getSDEntropy());
    }
}
