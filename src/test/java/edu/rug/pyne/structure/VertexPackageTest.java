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
public class VertexPackageTest {

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
     * Test the label set on the package class
     */
    @Test
    public void testLabel() {
        assertEquals(2, stu.getFramedGraph().traverse((g) -> g.V().hasLabel("package")).toList(VertexPackage.class).size());
    }

    /**
     * Test of getName method, of class VertexPackage.
     */
    @Test
    public void testGetName() {

        VertexPackage package1fromFG = stu.getFromFG("name", "package1", VertexPackage.class);
        VertexPackage package2fromFG = stu.getFromFG("name", "package2", VertexPackage.class);

        String expResult1 = "package1";
        String expResult2 = "package2";
        String result1 = package1fromFG.getName();
        String result2 = package2fromFG.getName();
        assertEquals(expResult1, result1);
        assertEquals(expResult2, result2);

        long count = stu.hasFromGraph("name", "package1").count().next();
        assertEquals(1, count);
    }

    /**
     * Test of setName method, of class VertexPackage.
     */
    @Test
    public void testSetName() {

        VertexPackage package1fromFG = stu.getFromFG("name", "package1", VertexPackage.class);

        Vertex package2fromGraph = stu.getFromGraph("name", "package2");

        package1fromFG.setName("newPackage1Name");
        package2fromGraph.property("name", "newPackage2Name");

        VertexPackage package1fromFGRenamed = stu.getFromFG("name", "newPackage1Name", VertexPackage.class);
        VertexPackage package2fromFGRenamed = stu.getFromFG("name", "newPackage2Name", VertexPackage.class);

        Vertex package1fromGraphRenamed = stu.getFromGraph("name", "newPackage1Name");
        Vertex package2fromGraphRenamed = stu.getFromGraph("name", "newPackage2Name");

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

        VertexPackage package1FromFG = stu.getFromFG("name", "package1", VertexPackage.class);
        VertexPackage package2FromFG = stu.getFromFG("name", "package2", VertexPackage.class);

        List<EdgeBelongsTo> package1BelongToEdges = package1FromFG.getBelongingEdges();
        List<EdgeBelongsTo> package2BelongToEdges = package2FromFG.getBelongingEdges();

        assertEquals(2, package1BelongToEdges.size());
        assertEquals(1, package2BelongToEdges.size());

        VertexClass class1 = stu.getFromFG("name", "class1", VertexClass.class);
        VertexClass class2 = stu.getFromFG("name", "class2", VertexClass.class);
        VertexClass class3 = stu.getFromFG("name", "class3", VertexClass.class);

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

        VertexPackage package1FromFG = stu.getFromFG("name", "package1", VertexPackage.class);
        VertexPackage package2FromFG = stu.getFromFG("name", "package2", VertexPackage.class);

        List<VertexClass> package1BelongingClasses = package1FromFG.getBelongingClasses();
        List<VertexClass> package2BelongingClasses = package2FromFG.getBelongingClasses();

        assertEquals(2, package1BelongingClasses.size());
        assertEquals(1, package2BelongingClasses.size());

        VertexClass class1 = stu.getFromFG("name", "class1", VertexClass.class);
        VertexClass class2 = stu.getFromFG("name", "class2", VertexClass.class);
        VertexClass class3 = stu.getFromFG("name", "class3", VertexClass.class);

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

        VertexPackage package2FromFG = stu.getFromFG("name", "package2", VertexPackage.class);
        VertexClass class2 = stu.getFromFG("name", "class2", VertexClass.class);

        List<VertexClass> package2BelongingClasses = package2FromFG.getBelongingClasses();
        assertEquals(1, package2BelongingClasses.size());

        package2FromFG.addBelongingClass(class2);

        package2BelongingClasses = package2FromFG.getBelongingClasses();
        assertEquals(2, package2BelongingClasses.size());

    }

    /**
     * Test of getPackageType method, of class VertexPackage.
     */
    @Test
    public void testGetPackageType() {
        VertexPackage package1FromFG = stu.getFromFG("name", "package1", VertexPackage.class);
        Vertex package2FromGraph = stu.getFromGraph("name", "package2");

        assertEquals("static", package1FromFG.getPackageType());
        assertEquals("static", package1FromFG.getProperty("PackageType", String.class));
        assertEquals("", package2FromGraph.property("PackageType").value());
    }

    /**
     * Test of setPackageType method, of class VertexPackage.
     */
    @Test
    public void testSetPackageType() {
        String propertyName = "PackageType";
        String test1 = "static";
        String test2 = "";
        
        VertexPackage package1FromFG = stu.getFromFG("name", "package1", VertexPackage.class);
        VertexPackage package2FromFG = stu.getFromFG("name", "package2", VertexPackage.class);
        Vertex package1FromGraph = stu.getFromGraph("name", "package1");
        Vertex package2FromGraph = stu.getFromGraph("name", "package2");
        
        assertEquals(test1, package1FromFG.getPackageType());
        assertEquals(test1, package1FromFG.getProperty(propertyName, String.class));
        assertEquals(test2, package2FromGraph.property(propertyName).value());
        
        package1FromFG.setPackageType(test2);
        package2FromGraph.property(propertyName, test1);
        
        assertEquals(test2, package1FromFG.getPackageType());
        assertEquals(test2, package1FromFG.getProperty(propertyName, String.class));
        assertEquals(test2, package1FromGraph.property(propertyName).value());
        assertEquals(test1, package2FromFG.getPackageType());
        assertEquals(test1, package2FromFG.getProperty(propertyName, String.class));
        assertEquals(test1, package2FromGraph.property(propertyName).value());
    }

    /**
     * Test of getNumTotalDep method, of class VertexPackage.
     */
    @Test
    public void testGetNumTotalDep() {
        VertexPackage package1FromFG = stu.getFromFG("name", "package1", VertexPackage.class);
        Vertex package2FromGraph = stu.getFromGraph("name", "package2");

        assertEquals(3, package1FromFG.getNumTotalDep());
        assertEquals(3, (int) package1FromFG.getProperty("numTotalDep", int.class));
        assertEquals(1, package2FromGraph.property("numTotalDep").value());
    }

    /**
     * Test of setNumTotalDep method, of class VertexPackage.
     */
    @Test
    public void testSetNumTotalDep() {
        String propertyName = "numTotalDep";
        int test1 = 3;
        int test2 = 1;
        
        VertexPackage package1FromFG = stu.getFromFG("name", "package1", VertexPackage.class);
        VertexPackage package2FromFG = stu.getFromFG("name", "package2", VertexPackage.class);
        Vertex package1FromGraph = stu.getFromGraph("name", "package1");
        Vertex package2FromGraph = stu.getFromGraph("name", "package2");
        
        assertEquals(test1, package1FromFG.getNumTotalDep());
        assertEquals(test1, (int) package1FromFG.getProperty(propertyName, int.class));
        assertEquals(test2, package2FromGraph.property(propertyName).value());
        
        package1FromFG.setNumTotalDep(test2);
        package2FromGraph.property(propertyName, test1);
        
        assertEquals(test2, package1FromFG.getNumTotalDep());
        assertEquals(test2, (int) package1FromFG.getProperty(propertyName, int.class));
        assertEquals(test2, package1FromGraph.property(propertyName).value());
        assertEquals(test1, package2FromFG.getNumTotalDep());
        assertEquals(test1, (int) package2FromFG.getProperty(propertyName, int.class));
        assertEquals(test1, package2FromGraph.property(propertyName).value());
    }

    /**
     * Test of getNumOfClassesInPackage method, of class VertexPackage.
     */
    @Test
    public void testGetNumOfClassesInPackage() {
        VertexPackage package1FromFG = stu.getFromFG("name", "package1", VertexPackage.class);
        Vertex package2FromGraph = stu.getFromGraph("name", "package2");

        assertEquals(2, package1FromFG.getNumOfClassesInPackage());
        assertEquals(2, (int) package1FromFG.getProperty("numOfClassesInPackage", int.class));
        assertEquals(1, package2FromGraph.property("numOfClassesInPackage").value());
    }

    /**
     * Test of setNumOfClassesInPackage method, of class VertexPackage.
     */
    @Test
    public void testSetNumOfClassesInPackage() {
        String propertyName = "numOfClassesInPackage";
        int test1 = 2;
        int test2 = 1;
        
        VertexPackage package1FromFG = stu.getFromFG("name", "package1", VertexPackage.class);
        VertexPackage package2FromFG = stu.getFromFG("name", "package2", VertexPackage.class);
        Vertex package1FromGraph = stu.getFromGraph("name", "package1");
        Vertex package2FromGraph = stu.getFromGraph("name", "package2");
        
        assertEquals(test1, package1FromFG.getNumOfClassesInPackage());
        assertEquals(test1, package1FromFG.getProperty(propertyName, String.class));
        assertEquals(test2, package2FromGraph.property(propertyName).value());
        
        package1FromFG.setNumOfClassesInPackage(test2);
        package2FromGraph.property(propertyName, test1);
        
        assertEquals(test2, package1FromFG.getNumOfClassesInPackage());
        assertEquals(test2, package1FromFG.getProperty(propertyName, String.class));
        assertEquals(test2, package1FromGraph.property(propertyName).value());
        assertEquals(test1, package2FromFG.getNumOfClassesInPackage());
        assertEquals(test1, package2FromFG.getProperty(propertyName, String.class));
        assertEquals(test1, package2FromGraph.property(propertyName).value());
        
    }

}
