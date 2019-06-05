package edu.rug.pyne.structure;

import com.syncleus.ferma.DefaultClassInitializer;
import com.syncleus.ferma.DelegatingFramedGraph;
import com.syncleus.ferma.FramedGraph;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.T;
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
        framedGraph = new DelegatingFramedGraph<>(graph, true, true);
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

    public void closeGraph() {
        try {
            framedGraph.close();
        } catch (IOException ex) {
            Logger.getLogger(StructureTestUtility.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void generateGraph() {

        VertexClass class1 = framedGraph.addFramedVertex(new DefaultClassInitializer<>(VertexClass.class), T.label, "class");
        class1.setName("class1");
        class1.setClassModifier("none");
        class1.setClassType(VertexClass.ClassType.RetrievedClass);
        VertexClass class2 = framedGraph.addFramedVertex(new DefaultClassInitializer<>(VertexClass.class), T.label, "class");
        class2.setName("class2");
        class2.setClassModifier("enum");
        class2.setClassType(VertexClass.ClassType.SystemClass);
        VertexClass class3 = framedGraph.addFramedVertex(new DefaultClassInitializer<>(VertexClass.class), T.label, "class");
        class3.setName("class3");
        class3.setClassModifier("Interface");
        class3.setClassType(VertexClass.ClassType.SystemClass);
        VertexClass class4 = framedGraph.addFramedVertex(new DefaultClassInitializer<>(VertexClass.class), T.label, "class");
        class4.setName("class4");
        class4.setClassModifier("Abstract");
        class4.setClassType(VertexClass.ClassType.SystemClass);

        VertexPackage package1 = framedGraph.addFramedVertex(new DefaultClassInitializer<>(VertexPackage.class), T.label, "package");
        package1.setName("package1");
        package1.setPackageType("SystemPackage");
        package1.setNumOfClassesInPackage(2);
        package1.setNumTotalDep(3);
        VertexPackage package2 = framedGraph.addFramedVertex(new DefaultClassInitializer<>(VertexPackage.class), T.label, "package");
        package2.setName("package2");
        package2.setPackageType("RetrievedPackage");
        package2.setNumOfClassesInPackage(1);
        package2.setNumTotalDep(1);


        framedGraph.addFramedEdge(class1, package1, "belongsTo", EdgeBelongsTo.class);
        framedGraph.addFramedEdge(class2, package1, "belongsTo", EdgeBelongsTo.class);
        framedGraph.addFramedEdge(class3, package2, "belongsTo", EdgeBelongsTo.class);

    }

}
