package edu.rug.pyne.structure;

import com.syncleus.ferma.DelegatingFramedGraph;
import com.syncleus.ferma.FramedGraph;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Patrick Beuks (s2288842) <code@beuks.net>
 */
public class VertexPackageTest {

    private final static Graph GRAPH = TinkerGraph.open();
    private final static FramedGraph FG;

    static {
        Set<Class<?>> types = new HashSet<>(Arrays.asList(new Class<?>[]{
            VertexClass.class,
            VertexPackage.class,
            EdgeBelongsTo.class
        }));
        FG = new DelegatingFramedGraph(GRAPH, true, types);
    }

    private static <T> T getFromFG(String propertyKey, Object value, Class<T> cls) {
        return FG.traverse(
                (g) -> g.V().has(propertyKey, value)
        ).next(cls);
    }

    private static Vertex getFromGraph(String propertyKey, Object value) {
        return GRAPH.traversal().V().has(propertyKey, value).next();
    }
    
    private static GraphTraversal<Vertex, Vertex> hasFromGraph(String propertyKey, Object value) {
        return GRAPH.traversal().V().has(propertyKey, value);
    }

    @BeforeEach
    public void setUp() {
        GRAPH.traversal().V().drop().iterate();
        GRAPH.traversal().E().drop().iterate();
        
        VertexClass class1 = FG.addFramedVertex(VertexClass.class);
        class1.setName("class1");
        VertexClass class2 = FG.addFramedVertex(VertexClass.class);
        class2.setName("class2");
        VertexClass class3 = FG.addFramedVertex(VertexClass.class);
        class3.setName("class3");
        
        VertexPackage package1 = FG.addFramedVertex(VertexPackage.class);
        package1.setName("package1");
        VertexPackage package2 = FG.addFramedVertex(VertexPackage.class);
        package2.setName("package2");
        
        package1.addBelongingClass(class1);
        package1.addBelongingClass(class2);
        package2.addBelongingClass(class3);
    }

    /**
     * Test of getName method, of class VertexPackage.
     */
    @Test
    public void testGetName() {
        System.out.println("getName");

        VertexPackage package1fromFG = getFromFG("name", "package1", VertexPackage.class);
        VertexPackage package2fromFG = getFromFG("name", "package2", VertexPackage.class);

        String expResult1 = "package1";
        String expResult2 = "package2";
        String result1 = package1fromFG.getName();
        String result2 = package2fromFG.getName();
        assertEquals(expResult1, result1);
        assertEquals(expResult2, result2);

        long count = hasFromGraph("name", "package1").count().next();
        assertEquals(1, count);
    }

    /**
     * Test of setName method, of class VertexPackage.
     */
    @Test
    public void testSetName() {
        System.out.println("setName");

        VertexPackage package1fromFG = getFromFG("name", "package1", VertexPackage.class);

        Vertex package2fromGraph = getFromGraph("name", "package2");

        package1fromFG.setName("newPackage1Name");
        package2fromGraph.property("name", "newPackage2Name");

        VertexPackage package1fromFGRenamed = getFromFG("name", "newPackage1Name", VertexPackage.class);
        VertexPackage package2fromFGRenamed = getFromFG("name", "newPackage2Name", VertexPackage.class);

        Vertex package1fromGraphRenamed = getFromGraph("name", "newPackage1Name");
        Vertex package2fromGraphRenamed = getFromGraph("name", "newPackage2Name");

        assertNotNull(package1fromFGRenamed);
        assertNotNull(package2fromFGRenamed);
        assertNotNull(package1fromGraphRenamed);
        assertNotNull(package2fromGraphRenamed);
    }

    /**
     * Test of getBelongingEdges method, of class VertexPackage.
     */
    @Test
    public void testGetBelongingEdges() {
        System.out.println("getBelongingEdges");
        
        VertexPackage package1FromFG = getFromFG("name", "package1", VertexPackage.class);
        VertexPackage package2FromFG = getFromFG("name", "package2", VertexPackage.class);
        
        List<EdgeBelongsTo> package1BelongToEdges = package1FromFG.getBelongingEdges();
        List<EdgeBelongsTo> package2BelongToEdges = package2FromFG.getBelongingEdges();
        
        assertEquals(2, package1BelongToEdges.size());
        assertEquals(1, package2BelongToEdges.size());
        
        VertexClass class1 = getFromFG("name", "class1", VertexClass.class);
        VertexClass class2 = getFromFG("name", "class2", VertexClass.class);
        VertexClass class3 = getFromFG("name", "class3", VertexClass.class);
        
        assertTrue(package1BelongToEdges.get(0).getVertexClass().equals(class1) 
                || package1BelongToEdges.get(0).getVertexClass().equals(class2));
        assertTrue(package1BelongToEdges.get(1).getVertexClass().equals(class1) 
                || package1BelongToEdges.get(1).getVertexClass().equals(class2));
        
        assertEquals(class3, package2BelongToEdges.get(0).getVertexClass());
        
    }

    /**
     * Test of getBelongingClasses method, of class VertexPackage.
     */
    @Test
    public void testGetBelongingClasses() {
        System.out.println("getBelongingClasses");
        
        VertexPackage package1FromFG = getFromFG("name", "package1", VertexPackage.class);
        VertexPackage package2FromFG = getFromFG("name", "package2", VertexPackage.class);
        
        List<VertexClass> package1BelongingClasses = package1FromFG.getBelongingClasses();
        List<VertexClass> package2BelongingClasses = package2FromFG.getBelongingClasses();
        
        assertEquals(2, package1BelongingClasses.size());
        assertEquals(1, package2BelongingClasses.size());
        
        VertexClass class1 = getFromFG("name", "class1", VertexClass.class);
        VertexClass class2 = getFromFG("name", "class2", VertexClass.class);
        VertexClass class3 = getFromFG("name", "class3", VertexClass.class);
        
        assertTrue(package1BelongingClasses.get(0).equals(class1) 
                || package1BelongingClasses.get(0).equals(class2));
        assertTrue(package1BelongingClasses.get(1).equals(class1) 
                || package1BelongingClasses.get(1).equals(class2));
        
        assertEquals(class3, package2BelongingClasses.get(0));
        
    }

    /**
     * Test of addBelongingClass method, of class VertexPackage.
     */
    @Test
    public void testAddBelongingClass() {
        System.out.println("addBelongingClass");
        
        
        VertexPackage package2FromFG = getFromFG("name", "package2", VertexPackage.class);
        VertexClass class2 = getFromFG("name", "class2", VertexClass.class);
        
        List<VertexClass> package2BelongingClasses = package2FromFG.getBelongingClasses();
        assertEquals(1, package2BelongingClasses.size());
        
        package2FromFG.addBelongingClass(class2);
        
        package2BelongingClasses = package2FromFG.getBelongingClasses();
        assertEquals(2, package2BelongingClasses.size());
        
    }

}
