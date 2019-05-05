package edu.rug.pyne.structure;

import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

/**
 *
 * @author Patrick Beuks (s2288842) <code@beuks.net>
 */
public class VertexMethodTest {

    private static StructureTestUtility stu;

    @BeforeAll
    public static void init() {
        stu = new StructureTestUtility();
    }

    @AfterAll
    public static void destroy() {
        stu.closeGraph();
    }

    @BeforeEach
    public void setUp() {
        stu.destroyGraph();
        stu.generateGraph();
    }

    /**
     * Test the label set on the method class
     */
    @Test
    public void testLabel() {
        assertEquals(4, stu.getFramedGraph().traverse((g) -> g.V().hasLabel("method")).toList(VertexMethod.class).size());
    }

    /**
     * Test of getName method, of class VertexMethod.
     */
    @Test
    public void testGetName() {

        VertexMethod method1fromFG = stu.getFromFG("name", "method1", VertexMethod.class);
        VertexMethod method2fromFG = stu.getFromFG("name", "method2", VertexMethod.class);

        String expResult1 = "method1";
        String expResult2 = "method2";
        String result1 = method1fromFG.getName();
        String result2 = method2fromFG.getName();
        assertEquals(expResult1, result1);
        assertEquals(expResult2, result2);

        long count = stu.hasFromGraph("name", "method1").count().next();
        assertEquals(1, count);

    }

    /**
     * Test of setName method, of class VertexMethod.
     */
    @Test
    public void testSetName() {

        VertexMethod method1fromFG = stu.getFromFG("name", "method1", VertexMethod.class);
        Vertex method2fromGraph = stu.getFromGraph("name", "method2");

        method1fromFG.setName("newMethod1Name");
        method2fromGraph.property("name", "newMethod2Name");

        VertexMethod method1fromFGRenamed = stu.getFromFG("name", "newMethod1Name", VertexMethod.class);
        VertexMethod method2fromFGRenamed = stu.getFromFG("name", "newMethod2Name", VertexMethod.class);

        Vertex method1fromGraphRenamed = stu.getFromGraph("name", "newMethod1Name");
        Vertex method2fromGraphRenamed = stu.getFromGraph("name", "newMethod2Name");

        assertNotNull(method1fromFGRenamed);
        assertNotNull(method2fromFGRenamed);
        assertNotNull(method1fromGraphRenamed);
        assertNotNull(method2fromGraphRenamed);
        assertEquals(method1fromFG.getElement(), method1fromGraphRenamed);
        assertEquals(method1fromFGRenamed.getElement(), method1fromGraphRenamed);

    }

    /**
     * Test of getContainedIn method, of class VertexMethod.
     */
    @Test
    public void testGetContainedIn() {

        VertexMethod method1FromFG = stu.getFromFG("name", "method1", VertexMethod.class);
        VertexMethod method2FromFG = stu.getFromFG("name", "method2", VertexMethod.class);
        VertexMethod method3FromFG = stu.getFromFG("name", "method3", VertexMethod.class);
        VertexMethod method4FromFG = stu.getFromFG("name", "method4", VertexMethod.class);

        VertexClass class1FromFG = stu.getFromFG("name", "class1", VertexClass.class);
        VertexClass class2FromFG = stu.getFromFG("name", "class2", VertexClass.class);
        VertexClass class3FromFG = stu.getFromFG("name", "class3", VertexClass.class);

        EdgeContainedIn method1ToClass1 = method1FromFG.getContainedIn();
        EdgeContainedIn method2ToClass2 = method2FromFG.getContainedIn();
        EdgeContainedIn method3ToClass3 = method3FromFG.getContainedIn();
        EdgeContainedIn method4ToClass3 = method4FromFG.getContainedIn();

        assertEquals(class1FromFG, method1ToClass1.getVertexClass());
        assertEquals(class2FromFG, method2ToClass2.getVertexClass());
        assertEquals(class3FromFG, method3ToClass3.getVertexClass());
        assertEquals(class3FromFG, method4ToClass3.getVertexClass());

    }

    /**
     * Test of getContainedInClass method, of class VertexMethod.
     */
    @Test
    public void testGetContainedInClass() {

        VertexMethod method1FromFG = stu.getFromFG("name", "method1", VertexMethod.class);
        VertexMethod method2FromFG = stu.getFromFG("name", "method2", VertexMethod.class);
        VertexMethod method3FromFG = stu.getFromFG("name", "method3", VertexMethod.class);
        VertexMethod method4FromFG = stu.getFromFG("name", "method4", VertexMethod.class);

        VertexClass class1FromFG = stu.getFromFG("name", "class1", VertexClass.class);
        VertexClass class2FromFG = stu.getFromFG("name", "class2", VertexClass.class);
        VertexClass class3FromFG = stu.getFromFG("name", "class3", VertexClass.class);

        assertEquals(class1FromFG, method1FromFG.getContainedInClass());
        assertEquals(class2FromFG, method2FromFG.getContainedInClass());
        assertEquals(class3FromFG, method3FromFG.getContainedInClass());
        assertEquals(class3FromFG, method4FromFG.getContainedInClass());

    }

    /**
     * Test of setContainedIn method, of class VertexMethod.
     */
    @Test
    public void testSetContainedIn() {

        VertexMethod method3FromFG = stu.getFromFG("name", "method3", VertexMethod.class);
        VertexClass class2FromFG = stu.getFromFG("name", "class2", VertexClass.class);
        VertexClass class3FromFG = stu.getFromFG("name", "class3", VertexClass.class);

        assertEquals(class3FromFG, method3FromFG.getContainedInClass());
        assertNotEquals(class2FromFG, method3FromFG.getContainedInClass());

        Vertex class3AfterTravel = stu.hasFromGraph("name", "method3").out("containedIn").next();
        assertEquals("class3", class3AfterTravel.property("name").value());

        method3FromFG.setContainedIn(class2FromFG);

        long edgeCount = stu.hasFromGraph("name", "method3").outE("containedIn").count().next();
        assertEquals(1, edgeCount);

        assertNotEquals(class3FromFG, method3FromFG.getContainedInClass());
        assertEquals(class2FromFG, method3FromFG.getContainedInClass());

        Vertex class2AfterTravel = stu.hasFromGraph("name", "method3").out("containedIn").next();
        assertEquals("class2", class2AfterTravel.property("name").value());

    }

}
