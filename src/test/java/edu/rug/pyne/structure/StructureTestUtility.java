package edu.rug.pyne.structure;

import com.syncleus.ferma.DelegatingFramedGraph;
import com.syncleus.ferma.FramedGraph;
import java.io.IOException;
import java.util.Arrays;
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

        VertexClass class1 = framedGraph.addFramedVertex(VertexClass.class, T.label, "class");
        class1.setName("class1");
        class1.setClassModifier("public");
        class1.setClassType("abstract");
        VertexClass class2 = framedGraph.addFramedVertex(VertexClass.class, T.label, "class");
        class2.setName("class2");
        class2.setClassModifier("private");
        class2.setClassType("interface");
        VertexClass class3 = framedGraph.addFramedVertex(VertexClass.class, T.label, "class");
        class3.setName("class3");
        class3.setClassModifier("protected");
        class3.setClassType("");

        VertexPackage package1 = framedGraph.addFramedVertex(VertexPackage.class, T.label, "package");
        package1.setName("package1");
        package1.setPackageType("static");
        package1.setNumOfClassesInPackage(2);
        package1.setNumTotalDep(3);
        VertexPackage package2 = framedGraph.addFramedVertex(VertexPackage.class, T.label, "package");
        package2.setName("package2");
        package2.setPackageType("");
        package2.setNumOfClassesInPackage(1);
        package2.setNumTotalDep(1);

        VertexMethod method1 = framedGraph.addFramedVertex(VertexMethod.class, T.label, "method");
        method1.setName("method1");
        method1.setAccessModifier("public");
        method1.setArgumentTypes(Arrays.asList("String"));
        method1.setIsStatic(true);
        method1.setMethodType("abstract");
        method1.setReturnType("boolean");
        method1.setContainerClassName("class1");
        VertexMethod method2 = framedGraph.addFramedVertex(VertexMethod.class, T.label, "method");
        method2.setName("method2");
        method2.setAccessModifier("private");
        method2.setArgumentTypes(Arrays.asList("String", "int"));
        method2.setIsStatic(false);
        method2.setMethodType("");
        method2.setReturnType("void");
        method2.setContainerClassName("class2");
        VertexMethod method3 = framedGraph.addFramedVertex(VertexMethod.class, T.label, "method");
        method3.setName("method3");
        method3.setAccessModifier("");
        method3.setArgumentTypes(Arrays.asList("int"));
        method3.setIsStatic(true);
        method3.setMethodType("");
        method3.setReturnType("Vertex");
        method3.setContainerClassName("class3");
        VertexMethod method4 = framedGraph.addFramedVertex(VertexMethod.class, T.label, "method");
        method4.setName("method4");
        method4.setAccessModifier("protected");
        method4.setArgumentTypes(Arrays.asList("String", "Vertex"));
        method4.setIsStatic(false);
        method4.setMethodType("final");
        method4.setReturnType("int");
        method4.setContainerClassName("class3");

        framedGraph.addFramedEdge(class1, package1, "belongsTo", EdgeBelongsTo.class);
        framedGraph.addFramedEdge(class2, package1, "belongsTo", EdgeBelongsTo.class);
        framedGraph.addFramedEdge(class3, package2, "belongsTo", EdgeBelongsTo.class);

        framedGraph.addFramedEdge(method1, class1, "containedIn", EdgeContainedIn.class);
        framedGraph.addFramedEdge(method2, class2, "containedIn", EdgeContainedIn.class);
        framedGraph.addFramedEdge(method3, class3, "containedIn", EdgeContainedIn.class);
        framedGraph.addFramedEdge(method4, class3, "containedIn", EdgeContainedIn.class);

    }

}
