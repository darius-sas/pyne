package edu.rug.pyne.structure;

import com.syncleus.ferma.DelegatingFramedGraph;
import com.syncleus.ferma.FramedGraph;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;

/**
 *
 * @author Patrick Beuks (s2288842) <code@beuks.net>
 */
public class StructureTestUtility {
    

    private final Graph graph = TinkerGraph.open();
    private final FramedGraph framedGraph;

    public StructureTestUtility() {
        Set<Class<?>> types = new HashSet<>(Arrays.asList(new Class<?>[]{
            VertexClass.class,
            VertexPackage.class,
            VertexMethod.class,
            EdgeBelongsTo.class,
            EdgeContainedIn.class
        }));
        framedGraph = new DelegatingFramedGraph(graph, true, types);
    }

    public Graph getGraph() {
        return graph;
    }

    public FramedGraph getFramedGraph() {
        return framedGraph;
    }

    public <T> T getFromFG(String propertyKey, Object value, Class<T> cls) {
        return framedGraph.traverse(
                (g) -> g.V().has(propertyKey, value)
        ).next(cls);
    }

    public Vertex getFromGraph(String propertyKey, Object value) {
        return graph.traversal().V().has(propertyKey, value).next();
    }

    public GraphTraversal<Vertex, Vertex> hasFromGraph(String propertyKey, Object value) {
        return graph.traversal().V().has(propertyKey, value);
    }
    
    public void destroyGraph() {
        graph.traversal().V().drop().iterate();
        graph.traversal().E().drop().iterate();
    }
    
    public void generateGraph() {
        
        VertexClass class1 = framedGraph.addFramedVertex(VertexClass.class);
        class1.setName("class1");
        VertexClass class2 = framedGraph.addFramedVertex(VertexClass.class);
        class2.setName("class2");
        VertexClass class3 = framedGraph.addFramedVertex(VertexClass.class);
        class3.setName("class3");

        VertexPackage package1 = framedGraph.addFramedVertex(VertexPackage.class);
        package1.setName("package1");
        VertexPackage package2 = framedGraph.addFramedVertex(VertexPackage.class);
        package2.setName("package2");
        
        VertexMethod method1 = framedGraph.addFramedVertex(VertexMethod.class);
        method1.setName("method1");
        VertexMethod method2 = framedGraph.addFramedVertex(VertexMethod.class);
        method2.setName("method2");
        VertexMethod method3 = framedGraph.addFramedVertex(VertexMethod.class);
        method3.setName("method3");
        VertexMethod method4 = framedGraph.addFramedVertex(VertexMethod.class);
        method4.setName("method4");
        
        framedGraph.addFramedEdge(class1, package1, "belongsTo", EdgeBelongsTo.class);
        framedGraph.addFramedEdge(class2, package1, "belongsTo", EdgeBelongsTo.class);
        framedGraph.addFramedEdge(class3, package2, "belongsTo", EdgeBelongsTo.class);
        
        framedGraph.addFramedEdge(method1, class1, "containedIn", EdgeContainedIn.class);
        framedGraph.addFramedEdge(method2, class2, "containedIn", EdgeContainedIn.class);
        framedGraph.addFramedEdge(method3, class3, "containedIn", EdgeContainedIn.class);
        framedGraph.addFramedEdge(method4, class3, "containedIn", EdgeContainedIn.class);
    }
    
}
