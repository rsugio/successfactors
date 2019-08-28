import com.github.systemdir.gml.YedGmlWriter;
import com.github.systemdir.gml.model.EdgeGraphicDefinition;
import com.github.systemdir.gml.model.GraphicDefinition;
import com.github.systemdir.gml.model.NodeGraphicDefinition;
import com.github.systemdir.gml.model.YedGmlGraphicsProvider;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.tg.TinkerGraph;
import com.tinkerpop.blueprints.util.io.graphml.GraphMLTokens;
import com.tinkerpop.blueprints.util.io.graphml.GraphMLWriter;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.junit.Before;
import org.junit.Test;

import java.awt.*;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

class ExampleGraphicsProvider implements YedGmlGraphicsProvider<String, DefaultEdge, Object> {
    @Override
    public NodeGraphicDefinition getVertexGraphics(String vertex) {
        return new NodeGraphicDefinition.Builder()
                .setFill(Color.LIGHT_GRAY)
                .setLineColor(Color.black)
                .setFontStyle(GraphicDefinition.FontStyle.ITALIC)
                .build();
    }

    @Override
    public EdgeGraphicDefinition getEdgeGraphics(DefaultEdge edge, String edgeSource, String edgeTarget) {
        return new EdgeGraphicDefinition.Builder()
                .setTargetArrow(EdgeGraphicDefinition.ArrowType.SHORT_ARROW)
                .setLineType(GraphicDefinition.LineType.DASHED)
                .build();
    }

    @Override
    public NodeGraphicDefinition getGroupGraphics(Object group, Set<String> groupElements) {
        // we have no groups in this example
        return null;
    }
}

public class TestGraph {
    Path tmp = Paths.get("tmp");

    @Before
    public void init() throws IOException {
        if (!Files.isDirectory(tmp))
            Files.createDirectory(tmp);
    }

    /**
     * See https://github.com/Systemdir/GML-Writer-for-yED
     *
     * @throws IOException
     */
    @Test
    public void example0() throws IOException {
        SimpleGraph<String, DefaultEdge> graph = new SimpleGraph<String, DefaultEdge>(DefaultEdge.class);
        graph.addVertex("a01");
        graph.addVertex("a02");
        graph.addVertex("a03");
        graph.addVertex("a04");
        graph.addVertex("a05");

        graph.addEdge("a01", "a02");
        graph.addEdge("a01", "a03");
        graph.addEdge("a01", "a04");
        graph.addEdge("a01", "a05");
        graph.addEdge("a02", "a03");
        graph.addEdge("a02", "a04");
        graph.addEdge("a02", "a05");
        graph.addEdge("a03", "a04");
        graph.addEdge("a03", "a05");
        graph.addEdge("a04", "a05");
        ExampleGraphicsProvider graphicsProvider = new ExampleGraphicsProvider();
        YedGmlWriter<String, DefaultEdge, Object> writer = new YedGmlWriter.Builder<>(graphicsProvider, YedGmlWriter.PrintLabels.PRINT_VERTEX_LABELS).build();
        Writer w = Files.newBufferedWriter(Paths.get("tmp/example0.graphml"), StandardCharsets.UTF_8);
        writer.export(w, graph);
        w.close();
    }

    /**
     * https://github.com/tinkerpop/blueprints/wiki/GraphML-Reader-and-Writer-Library
     *
     * @throws IOException
     */
    @Test
    public void example1() throws IOException {
        Map<String, String> vertexKeyTypes = new HashMap<String, String>();
        vertexKeyTypes.put("age", GraphMLTokens.INT);
        vertexKeyTypes.put("lang", GraphMLTokens.STRING);
        vertexKeyTypes.put("name", GraphMLTokens.STRING);
        Map<String, String> edgeKeyTypes = new HashMap<String, String>();
        edgeKeyTypes.put("weight", GraphMLTokens.FLOAT);

        Graph graph = new TinkerGraph();
        Vertex a = graph.addVertex(null);
        Vertex b = graph.addVertex(null);
        a.setProperty("name", "marko");
        b.setProperty("name", "peter");
        Edge e = graph.addEdge(null, a, b, "knows");
        GraphMLWriter writer = new GraphMLWriter(graph);
        writer.setVertexKeyTypes(vertexKeyTypes);
        writer.setEdgeKeyTypes(edgeKeyTypes);
        OutputStream w = Files.newOutputStream(Paths.get("tmp/example1.graphml"));
        writer.outputGraph(w);
        w.close();
    }

    /**
     * https://github.com/gephi/gephi/wiki/Graph-API -- very huge footprint
     *
     * @throws IOException
     */
    @Test
    public void example2() throws IOException {
//        GraphModel model = GraphModel.Factory.newInstance();
//        model.addEdgeType("azaza");
//        org.gephi.graph.api.Graph o = model.getGraph();
    }

    @Test
    public void example3() throws Exception {
        Map<String, String> vertexKeyTypes = new HashMap<String, String>();
        vertexKeyTypes.put("age", GraphMLTokens.INT);
        Graph graph = new TinkerGraph();
        Vertex a = graph.addVertex(null);
        a.setProperty("name", "marko");
        Vertex b = graph.addVertex(null);
        b.setProperty("name", "peter");
        Edge e = graph.addEdge(null, a, b, "knows");
        GraphMLWriter writer = new GraphMLWriter(graph);
        writer.setVertexKeyTypes(vertexKeyTypes);
//        writer.setEdgeKeyTypes(edgeKeyTypes);
        OutputStream w = Files.newOutputStream(Paths.get("tmp/example3.graphml"));
        writer.outputGraph(w);
        w.close();

    }
}
