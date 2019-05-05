package edu.rug.pyne.structure;

import java.util.List;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeAll;

/**
 *
 * @author Patrick Beuks (s2288842) <code@beuks.net>
 */
public class VertexClassTest {

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
        assertEquals(3, stu.getFramedGraph().traverse((g) -> g.V().hasLabel("class")).toList(VertexClass.class).size());
    }

    /**
     * Test of getName method, of class VertexClass.
     */
    @Test
    public void testGetName() {

        VertexClass class1fromFG = stu.getFromFG("name", "class1", VertexClass.class);
        VertexClass class2fromFG = stu.getFromFG("name", "class2", VertexClass.class);

        String expResult1 = "class1";
        String expResult2 = "class2";
        String result1 = class1fromFG.getName();
        String result2 = class2fromFG.getName();
        assertEquals(expResult1, result1);
        assertEquals(expResult2, result2);

        long count = stu.hasFromGraph("name", "class1").count().next();
        assertEquals(1, count);
    }

    /**
     * Test of setName method, of class VertexClass.
     */
    @Test
    public void testSetName() {

        VertexClass class1fromFG = stu.getFromFG("name", "class1", VertexClass.class);
        Vertex class2fromGraph = stu.getFromGraph("name", "class2");

        class1fromFG.setName("newClass1Name");
        class2fromGraph.property("name", "newClass2Name");

        VertexClass class1fromFGRenamed = stu.getFromFG("name", "newClass1Name", VertexClass.class);
        VertexClass class2fromFGRenamed = stu.getFromFG("name", "newClass2Name", VertexClass.class);

        Vertex class1fromGraphRenamed = stu.getFromGraph("name", "newClass1Name");
        Vertex class2fromGraphRenamed = stu.getFromGraph("name", "newClass2Name");

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

        VertexClass class1FromFG = stu.getFromFG("name", "class1", VertexClass.class);
        VertexClass class2FromFG = stu.getFromFG("name", "class2", VertexClass.class);
        VertexClass class3FromFG = stu.getFromFG("name", "class3", VertexClass.class);
        VertexPackage package1FromFG = stu.getFromFG("name", "package1", VertexPackage.class);
        VertexPackage package2FromFG = stu.getFromFG("name", "package2", VertexPackage.class);

        EdgeBelongsTo class1ToPackage1 = class1FromFG.getBelongsTo();
        EdgeBelongsTo class2ToPackage1 = class2FromFG.getBelongsTo();
        EdgeBelongsTo class3ToPackage2 = class3FromFG.getBelongsTo();

        assertEquals(package1FromFG, class1ToPackage1.getVertexPackage());
        assertEquals(package1FromFG, class2ToPackage1.getVertexPackage());
        assertEquals(package2FromFG, class3ToPackage2.getVertexPackage());
    }

    /**
     * Test of getBelongsToPackage method, of class VertexClass.
     */
    @Test
    public void testGetBelongsToPackage() {

        VertexClass class1FromFG = stu.getFromFG("name", "class1", VertexClass.class);
        VertexClass class2FromFG = stu.getFromFG("name", "class2", VertexClass.class);
        VertexClass class3FromFG = stu.getFromFG("name", "class3", VertexClass.class);
        VertexPackage package1FromFG = stu.getFromFG("name", "package1", VertexPackage.class);
        VertexPackage package2FromFG = stu.getFromFG("name", "package2", VertexPackage.class);

        assertEquals(package1FromFG, class1FromFG.getBelongsToPackage());
        assertEquals(package1FromFG, class2FromFG.getBelongsToPackage());
        assertEquals(package2FromFG, class3FromFG.getBelongsToPackage());
    }

    /**
     * Test of setBelongsTo method, of class VertexClass.
     */
    @Test
    public void testSetBelongsTo() {

        VertexClass class2FromFG = stu.getFromFG("name", "class2", VertexClass.class);
        VertexPackage package1FromFG = stu.getFromFG("name", "package1", VertexPackage.class);
        VertexPackage package2FromFG = stu.getFromFG("name", "package2", VertexPackage.class);

        assertEquals(package1FromFG, class2FromFG.getBelongsToPackage());
        assertNotEquals(package2FromFG, class2FromFG.getBelongsToPackage());

        Vertex package1AfterTravel = stu.hasFromGraph("name", "class2").out("belongsTo").next();
        assertEquals("package1", package1AfterTravel.property("name").value());

        class2FromFG.setBelongsTo(package2FromFG);

        long edgeCount = stu.hasFromGraph("name", "class2").outE("belongsTo").count().next();
        assertEquals(1, edgeCount);

        assertNotEquals(package1FromFG, class2FromFG.getBelongsToPackage());
        assertEquals(package2FromFG, class2FromFG.getBelongsToPackage());

        Vertex package2AfterTravel = stu.hasFromGraph("name", "class2").out("belongsTo").next();
        assertEquals("package2", package2AfterTravel.property("name").value());
    }

    /**
     * Test of getContainingEdges method, of class VertexClass.
     */
    @Test
    public void testGetContainingEdges() {

        VertexClass class1FromFG = stu.getFromFG("name", "class1", VertexClass.class);
        VertexClass class2FromFG = stu.getFromFG("name", "class2", VertexClass.class);
        VertexClass class3FromFG = stu.getFromFG("name", "class3", VertexClass.class);

        List<EdgeContainedIn> class1ContainedInEdges = class1FromFG.getContainingEdges();
        List<EdgeContainedIn> class2ContainedInEdges = class2FromFG.getContainingEdges();
        List<EdgeContainedIn> class3ContainedInEdges = class3FromFG.getContainingEdges();

        assertEquals(1, class1ContainedInEdges.size());
        assertEquals(1, class2ContainedInEdges.size());
        assertEquals(2, class3ContainedInEdges.size());

        VertexMethod method1 = stu.getFromFG("name", "method1", VertexMethod.class);
        VertexMethod method2 = stu.getFromFG("name", "method2", VertexMethod.class);
        VertexMethod method3 = stu.getFromFG("name", "method3", VertexMethod.class);
        VertexMethod method4 = stu.getFromFG("name", "method4", VertexMethod.class);

        assertEquals(method1, class1ContainedInEdges.get(0).getVertexMethod());
        assertEquals(method2, class2ContainedInEdges.get(0).getVertexMethod());

        assertTrue(class3ContainedInEdges.get(0).getVertexMethod().equals(method3)
                || class3ContainedInEdges.get(0).getVertexMethod().equals(method4));
        assertTrue(class3ContainedInEdges.get(1).getVertexMethod().equals(method3)
                || class3ContainedInEdges.get(1).getVertexMethod().equals(method4));

    }

    /**
     * Test of getContainingMethods method, of class VertexClass.
     */
    @Test
    public void testGetContainingMethods() {

        VertexClass class1FromFG = stu.getFromFG("name", "class1", VertexClass.class);
        VertexClass class2FromFG = stu.getFromFG("name", "class2", VertexClass.class);
        VertexClass class3FromFG = stu.getFromFG("name", "class3", VertexClass.class);

        List<VertexMethod> class1ContainingMethods = class1FromFG.getContainingMethods();
        List<VertexMethod> class2ContainingMethods = class2FromFG.getContainingMethods();
        List<VertexMethod> class3ContainingMethods = class3FromFG.getContainingMethods();

        assertEquals(1, class1ContainingMethods.size());
        assertEquals(1, class2ContainingMethods.size());
        assertEquals(2, class3ContainingMethods.size());

        VertexMethod method1 = stu.getFromFG("name", "method1", VertexMethod.class);
        VertexMethod method2 = stu.getFromFG("name", "method2", VertexMethod.class);
        VertexMethod method3 = stu.getFromFG("name", "method3", VertexMethod.class);
        VertexMethod method4 = stu.getFromFG("name", "method4", VertexMethod.class);

        assertEquals(method1, class1ContainingMethods.get(0));
        assertEquals(method2, class2ContainingMethods.get(0));

        assertTrue(class3ContainingMethods.get(0).equals(method3)
                || class3ContainingMethods.get(0).equals(method4));
        assertTrue(class3ContainingMethods.get(1).equals(method3)
                || class3ContainingMethods.get(1).equals(method4));

    }

    /**
     * Test of addContainingMethod method, of class VertexClass.
     */
    @Test
    public void testAddContainingMethod() {

        VertexClass class2FromFG = stu.getFromFG("name", "class2", VertexClass.class);
        VertexMethod class2 = stu.getFromFG("name", "method3", VertexMethod.class);

        List<VertexMethod> class2ContainingMethods = class2FromFG.getContainingMethods();
        assertEquals(1, class2ContainingMethods.size());

        class2FromFG.addContainingMethod(class2);

        class2ContainingMethods = class2FromFG.getContainingMethods();
        assertEquals(2, class2ContainingMethods.size());

    }

}
