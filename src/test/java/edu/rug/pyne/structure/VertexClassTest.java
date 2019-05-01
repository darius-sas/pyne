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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Patrick Beuks (s2288842) <code@beuks.net>
 */
public class VertexClassTest {

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

        class1.setBelongsTo(package1);
        class2.setBelongsTo(package1);
        class3.setBelongsTo(package2);

    }

    /**
     * Test of getName method, of class VertexClass.
     */
    @Test
    public void testGetName() {
        System.out.println("getName");

        VertexClass class1fromFG = getFromFG("name", "class1", VertexClass.class);
        VertexClass class2fromFG = getFromFG("name", "class2", VertexClass.class);

        String expResult1 = "class1";
        String expResult2 = "class2";
        String result1 = class1fromFG.getName();
        String result2 = class2fromFG.getName();
        assertEquals(expResult1, result1);
        assertEquals(expResult2, result2);

        long count = hasFromGraph("name", "class1").count().next();
        assertEquals(1, count);
    }

    /**
     * Test of setName method, of class VertexClass.
     */
    @Test
    public void testSetName() {
        System.out.println("setName");

        VertexClass class1fromFG = getFromFG("name", "class1", VertexClass.class);
        Vertex class2fromGraph = getFromGraph("name", "class2");

        class1fromFG.setName("newClass1Name");
        class2fromGraph.property("name", "newClass2Name");

        VertexClass class1fromFGRenamed = getFromFG("name", "newClass1Name", VertexClass.class);
        VertexClass class2fromFGRenamed = getFromFG("name", "newClass2Name", VertexClass.class);

        Vertex class1fromGraphRenamed = getFromGraph("name", "newClass1Name");
        Vertex class2fromGraphRenamed = getFromGraph("name", "newClass2Name");

        assertNotNull(class1fromFGRenamed);
        assertNotNull(class2fromFGRenamed);
        assertNotNull(class1fromGraphRenamed);
        assertNotNull(class2fromGraphRenamed);
        assertEquals(class1fromFG.getElement(), class1fromGraphRenamed);
        assertEquals(class1fromFGRenamed.getElement(), class1fromGraphRenamed);
    }

    /**
     * Test of getBelongsTo method, of class VertexClass.
     */
    @Test
    public void testGetBelongsTo() {
        System.out.println("getBelongsTo");
        VertexClass class1FromFG = getFromFG("name", "class1", VertexClass.class);
        VertexClass class2FromFG = getFromFG("name", "class2", VertexClass.class);
        VertexClass class3FromFG = getFromFG("name", "class3", VertexClass.class);
        VertexPackage package1FromFG = getFromFG("name", "package1", VertexPackage.class);
        VertexPackage package2FromFG = getFromFG("name", "package2", VertexPackage.class);

        EdgeBelongsTo class1ToPackage1 = class1FromFG.getBelongsTo();
        EdgeBelongsTo class2ToPackage1 = class2FromFG.getBelongsTo();
        EdgeBelongsTo class3ToPackage2 = class3FromFG.getBelongsTo();

        assertEquals(package1FromFG, class1ToPackage1.getPackage());
        assertEquals(package1FromFG, class2ToPackage1.getPackage());
        assertEquals(package2FromFG, class3ToPackage2.getPackage());
    }

    /**
     * Test of getBelongsToPackage method, of class VertexClass.
     */
    @Test
    public void testGetBelongsToPackage() {
        System.out.println("getBelongsToPackage");
        VertexClass class1FromFG = getFromFG("name", "class1", VertexClass.class);
        VertexClass class2FromFG = getFromFG("name", "class2", VertexClass.class);
        VertexClass class3FromFG = getFromFG("name", "class3", VertexClass.class);
        VertexPackage package1FromFG = getFromFG("name", "package1", VertexPackage.class);
        VertexPackage package2FromFG = getFromFG("name", "package2", VertexPackage.class);

        assertEquals(package1FromFG, class1FromFG.getBelongsToPackage());
        assertEquals(package1FromFG, class2FromFG.getBelongsToPackage());
        assertEquals(package2FromFG, class3FromFG.getBelongsToPackage());
    }

    /**
     * Test of setBelongsTo method, of class VertexClass.
     */
    @Test
    public void testSetBelongsTo() {
        System.out.println("setBelongsToPackage");

        VertexClass class2FromFG = getFromFG("name", "class2", VertexClass.class);
        VertexPackage package1FromFG = getFromFG("name", "package1", VertexPackage.class);
        VertexPackage package2FromFG = getFromFG("name", "package2", VertexPackage.class);

        assertEquals(package1FromFG, class2FromFG.getBelongsToPackage());
        assertNotEquals(package2FromFG, class2FromFG.getBelongsToPackage());

        Vertex package1AfterTravel = hasFromGraph("name", "class2").out("belongsTo").next();
        assertEquals("package1", package1AfterTravel.property("name").value());

        class2FromFG.setBelongsTo(package2FromFG);

        long edgeCount = hasFromGraph("name", "class2").outE("belongsTo").count().next();
        assertEquals(1, edgeCount);
        
        assertNotEquals(package1FromFG, class2FromFG.getBelongsToPackage());
        assertEquals(package2FromFG, class2FromFG.getBelongsToPackage());

        Vertex package2AfterTravel = hasFromGraph("name", "class2").out("belongsTo").next();
        assertEquals("package2", package2AfterTravel.property("name").value());
    }

}
