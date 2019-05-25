package edu.rug.pyne.structure;

import java.util.List;
import org.apache.tinkerpop.gremlin.structure.T;
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
        assertEquals(4, stu.getFramedGraph().traverse((g) -> g.V().has(T.label, "class")).toList(VertexClass.class).size());
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
     * Test of getClassType method, of class VertexClass.
     */
    @Test
    public void testGetClassType() {
        VertexClass class1FromFG = stu.getFromFG("name", "class1", VertexClass.class);
        Vertex class2FromGraph = stu.getFromGraph("name", "class2");
        
        assertEquals(VertexClass.ClassType.RetrievedClass, class1FromFG.getClassType());
        assertEquals(VertexClass.ClassType.RetrievedClass.name(), class1FromFG.getProperty("ClassType", String.class));
        assertEquals(VertexClass.ClassType.SystemClass.name(), class2FromGraph.property("ClassType").value());
    }

    /**
     * Test of setClassType method, of class VertexClass.
     */
    @Test
    public void testSetClassType() {
        String propertyName = "ClassType";
        VertexClass.ClassType test1 = VertexClass.ClassType.RetrievedClass;
        VertexClass.ClassType test2 = VertexClass.ClassType.SystemClass;
        
        VertexClass class1FromFG = stu.getFromFG("name", "class1", VertexClass.class);
        VertexClass class2FromFG = stu.getFromFG("name", "class2", VertexClass.class);
        Vertex class1FromGraph = stu.getFromGraph("name", "class1");
        Vertex class2FromGraph = stu.getFromGraph("name", "class2");
        
        assertEquals(test1, class1FromFG.getClassType());
        assertEquals(test1.name(), class1FromFG.getProperty(propertyName, String.class));
        assertEquals(test2.name(), class2FromGraph.property(propertyName).value());
        
        class1FromFG.setClassType(test2);
        class2FromGraph.property(propertyName, test1.name());
        
        assertEquals(test2, class1FromFG.getClassType());
        assertEquals(test2.name(), class1FromFG.getProperty(propertyName, String.class));
        assertEquals(test2.name(), class1FromGraph.property(propertyName).value());
        assertEquals(test1, class2FromFG.getClassType());
        assertEquals(test1.name(), class2FromFG.getProperty(propertyName, String.class));
        assertEquals(test1.name(), class2FromGraph.property(propertyName).value());
    }

    /**
     * Test of getClassModifier method, of class VertexClass.
     */
    @Test
    public void testGetClassModifier() {
        VertexClass class1FromFG = stu.getFromFG("name", "class1", VertexClass.class);
        Vertex class2FromGraph = stu.getFromGraph("name", "class2");
        
        assertEquals("none", class1FromFG.getClassModifier());
        assertEquals("none", class1FromFG.getProperty("classModifier", String.class));
        assertEquals("enum", class2FromGraph.property("classModifier").value());
    }

    /**
     * Test of setClassModifier method, of class VertexClass.
     */
    @Test
    public void testSetClassModifier() {
        String propertyName = "classModifier";
        String test1 = "none";
        String test2 = "enum";
        
        VertexClass class1FromFG = stu.getFromFG("name", "class1", VertexClass.class);
        VertexClass class2FromFG = stu.getFromFG("name", "class2", VertexClass.class);
        Vertex class1FromGraph = stu.getFromGraph("name", "class1");
        Vertex class2FromGraph = stu.getFromGraph("name", "class2");
        
        assertEquals(test1, class1FromFG.getClassModifier());
        assertEquals(test1, class1FromFG.getProperty(propertyName, String.class));
        assertEquals(test2, class2FromGraph.property(propertyName).value());
        
        class1FromFG.setClassModifier(test2);
        class2FromGraph.property(propertyName, test1);
        
        assertEquals(test2, class1FromFG.getClassModifier());
        assertEquals(test2, class1FromFG.getProperty(propertyName, String.class));
        assertEquals(test2, class1FromGraph.property(propertyName).value());
        assertEquals(test1, class2FromFG.getClassModifier());
        assertEquals(test1, class2FromFG.getProperty(propertyName, String.class));
        assertEquals(test1, class2FromGraph.property(propertyName).value());
    }

}
