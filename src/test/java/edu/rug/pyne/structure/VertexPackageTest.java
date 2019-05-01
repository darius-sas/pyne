package edu.rug.pyne.structure;

import java.util.List;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Patrick Beuks (s2288842) <code@beuks.net>
 */
public class VertexPackageTest {

    private final static StructureTestUtility STU = new StructureTestUtility();

    @BeforeEach
    public void setUp() {
        STU.destroyGraph();
        STU.generateGraph();
    }

    /**
     * Test of getName method, of class VertexPackage.
     */
    @Test
    public void testGetName() {

        VertexPackage package1fromFG = STU.getFromFG("name", "package1", VertexPackage.class);
        VertexPackage package2fromFG = STU.getFromFG("name", "package2", VertexPackage.class);

        String expResult1 = "package1";
        String expResult2 = "package2";
        String result1 = package1fromFG.getName();
        String result2 = package2fromFG.getName();
        assertEquals(expResult1, result1);
        assertEquals(expResult2, result2);

        long count = STU.hasFromGraph("name", "package1").count().next();
        assertEquals(1, count);
    }

    /**
     * Test of setName method, of class VertexPackage.
     */
    @Test
    public void testSetName() {

        VertexPackage package1fromFG = STU.getFromFG("name", "package1", VertexPackage.class);

        Vertex package2fromGraph = STU.getFromGraph("name", "package2");

        package1fromFG.setName("newPackage1Name");
        package2fromGraph.property("name", "newPackage2Name");

        VertexPackage package1fromFGRenamed = STU.getFromFG("name", "newPackage1Name", VertexPackage.class);
        VertexPackage package2fromFGRenamed = STU.getFromFG("name", "newPackage2Name", VertexPackage.class);

        Vertex package1fromGraphRenamed = STU.getFromGraph("name", "newPackage1Name");
        Vertex package2fromGraphRenamed = STU.getFromGraph("name", "newPackage2Name");

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

        VertexPackage package1FromFG = STU.getFromFG("name", "package1", VertexPackage.class);
        VertexPackage package2FromFG = STU.getFromFG("name", "package2", VertexPackage.class);

        List<EdgeBelongsTo> package1BelongToEdges = package1FromFG.getBelongingEdges();
        List<EdgeBelongsTo> package2BelongToEdges = package2FromFG.getBelongingEdges();

        assertEquals(2, package1BelongToEdges.size());
        assertEquals(1, package2BelongToEdges.size());

        VertexClass class1 = STU.getFromFG("name", "class1", VertexClass.class);
        VertexClass class2 = STU.getFromFG("name", "class2", VertexClass.class);
        VertexClass class3 = STU.getFromFG("name", "class3", VertexClass.class);

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

        VertexPackage package1FromFG = STU.getFromFG("name", "package1", VertexPackage.class);
        VertexPackage package2FromFG = STU.getFromFG("name", "package2", VertexPackage.class);

        List<VertexClass> package1BelongingClasses = package1FromFG.getBelongingClasses();
        List<VertexClass> package2BelongingClasses = package2FromFG.getBelongingClasses();

        assertEquals(2, package1BelongingClasses.size());
        assertEquals(1, package2BelongingClasses.size());

        VertexClass class1 = STU.getFromFG("name", "class1", VertexClass.class);
        VertexClass class2 = STU.getFromFG("name", "class2", VertexClass.class);
        VertexClass class3 = STU.getFromFG("name", "class3", VertexClass.class);

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

        VertexPackage package2FromFG = STU.getFromFG("name", "package2", VertexPackage.class);
        VertexClass class2 = STU.getFromFG("name", "class2", VertexClass.class);

        List<VertexClass> package2BelongingClasses = package2FromFG.getBelongingClasses();
        assertEquals(1, package2BelongingClasses.size());

        package2FromFG.addBelongingClass(class2);

        package2BelongingClasses = package2FromFG.getBelongingClasses();
        assertEquals(2, package2BelongingClasses.size());

    }

}
