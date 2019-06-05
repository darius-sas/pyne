package edu.rug.pyne.structure;

import edu.rug.pyne.api.structure.VertexPackage;
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
     * Test of getPackageType method, of class VertexPackage.
     */
    @Test
    public void testGetPackageType() {
        VertexPackage package1FromFG = stu.getFromFG("name", "package1", VertexPackage.class);
        Vertex package2FromGraph = stu.getFromGraph("name", "package2");

        assertEquals("SystemPackage", package1FromFG.getPackageType());
        assertEquals("SystemPackage", package1FromFG.getProperty("PackageType", String.class));
        assertEquals("RetrievedPackage", package2FromGraph.property("PackageType").value());
    }

    /**
     * Test of setPackageType method, of class VertexPackage.
     */
    @Test
    public void testSetPackageType() {
        String propertyName = "PackageType";
        String test1 = "SystemPackage";
        String test2 = "RetrievedPackage";
        
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
