package edu.rug.pyne.structure;

import java.util.Arrays;
import java.util.List;
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

    /**
     * Test of getMethodType method, of class VertexMethod.
     */
    @Test
    public void testGetMethodType() {
        VertexMethod method1FromFG = stu.getFromFG("name", "method1", VertexMethod.class);
        Vertex method2FromGraph = stu.getFromGraph("name", "method2");
        
        assertEquals("abstract", method1FromFG.getMethodType());
        assertEquals("abstract", method1FromFG.getProperty("MethodType", String.class));
        assertEquals("", method2FromGraph.property("MethodType").value());
    }

    /**
     * Test of setMethodType method, of class VertexMethod.
     */
    @Test
    public void testSetMethodType() {
        String propertyName = "MethodType";
        String test1 = "abstract";
        String test2 = "";
        
        VertexMethod method1FromFG = stu.getFromFG("name", "method1", VertexMethod.class);
        VertexMethod method2FromFG = stu.getFromFG("name", "method2", VertexMethod.class);
        Vertex method1FromGraph = stu.getFromGraph("name", "method1");
        Vertex method2FromGraph = stu.getFromGraph("name", "method2");
        
        assertEquals(test1, method1FromFG.getMethodType());
        assertEquals(test1, method1FromFG.getProperty(propertyName, String.class));
        assertEquals(test2, method2FromGraph.property(propertyName).value());
        
        method1FromFG.setMethodType(test2);
        method2FromGraph.property(propertyName, test1);
        
        assertEquals(test2, method1FromFG.getMethodType());
        assertEquals(test2, method1FromFG.getProperty(propertyName, String.class));
        assertEquals(test2, method1FromGraph.property(propertyName).value());
        assertEquals(test1, method2FromFG.getMethodType());
        assertEquals(test1, method2FromFG.getProperty(propertyName, String.class));
        assertEquals(test1, method2FromGraph.property(propertyName).value());
    }

    /**
     * Test of isStatic method, of class VertexMethod.
     */
    @Test
    public void testIsStatic() {
        VertexMethod method1FromFG = stu.getFromFG("name", "method1", VertexMethod.class);
        Vertex method2FromGraph = stu.getFromGraph("name", "method2");
        
        assertEquals(true, method1FromFG.isStatic());
        assertEquals(true, method1FromFG.getProperty("isStatic", boolean.class));
        assertEquals(false, method2FromGraph.property("isStatic").value());
    }

    /**
     * Test of setIsStatic method, of class VertexMethod.
     */
    @Test
    public void testSetIsStatic() {
        String propertyName = "isStatic";
        boolean test1 = true;
        boolean test2 = false;
        
        VertexMethod method1FromFG = stu.getFromFG("name", "method1", VertexMethod.class);
        VertexMethod method2FromFG = stu.getFromFG("name", "method2", VertexMethod.class);
        Vertex method1FromGraph = stu.getFromGraph("name", "method1");
        Vertex method2FromGraph = stu.getFromGraph("name", "method2");
        
        assertEquals(test1, method1FromFG.isStatic());
        assertEquals(test1, method1FromFG.getProperty(propertyName, boolean.class));
        assertEquals(test2, method2FromGraph.property(propertyName).value());
        
        method1FromFG.setIsStatic(test2);
        method2FromGraph.property(propertyName, test1);
        
        assertEquals(test2, method1FromFG.isStatic());
        assertEquals(test2, method1FromFG.getProperty(propertyName, boolean.class));
        assertEquals(test2, method1FromGraph.property(propertyName).value());
        assertEquals(test1, method2FromFG.isStatic());
        assertEquals(test1, method2FromFG.getProperty(propertyName, boolean.class));
        assertEquals(test1, method2FromGraph.property(propertyName).value());
    }

    /**
     * Test of getReturnType method, of class VertexMethod.
     */
    @Test
    public void testGetReturnType() {
        VertexMethod method1FromFG = stu.getFromFG("name", "method1", VertexMethod.class);
        Vertex method2FromGraph = stu.getFromGraph("name", "method2");
        
        assertEquals("boolean", method1FromFG.getReturnType());
        assertEquals("boolean", method1FromFG.getProperty("returnType", String.class));
        assertEquals("void", method2FromGraph.property("returnType").value());
    }

    /**
     * Test of setReturnType method, of class VertexMethod.
     */
    @Test
    public void testSetReturnType() {
        String propertyName = "returnType";
        String test1 = "boolean";
        String test2 = "void";
        
        VertexMethod method1FromFG = stu.getFromFG("name", "method1", VertexMethod.class);
        VertexMethod method2FromFG = stu.getFromFG("name", "method2", VertexMethod.class);
        Vertex method1FromGraph = stu.getFromGraph("name", "method1");
        Vertex method2FromGraph = stu.getFromGraph("name", "method2");
        
        assertEquals(test1, method1FromFG.getReturnType());
        assertEquals(test1, method1FromFG.getProperty(propertyName, String.class));
        assertEquals(test2, method2FromGraph.property(propertyName).value());
        
        method1FromFG.setReturnType(test2);
        method2FromGraph.property(propertyName, test1);
        
        assertEquals(test2, method1FromFG.getReturnType());
        assertEquals(test2, method1FromFG.getProperty(propertyName, String.class));
        assertEquals(test2, method1FromGraph.property(propertyName).value());
        assertEquals(test1, method2FromFG.getReturnType());
        assertEquals(test1, method2FromFG.getProperty(propertyName, String.class));
        assertEquals(test1, method2FromGraph.property(propertyName).value());
    }

    /**
     * Test of getArgumentTypes method, of class VertexMethod.
     */
    @Test
    public void testGetArgumentTypes() {
        VertexMethod method1FromFG = stu.getFromFG("name", "method1", VertexMethod.class);
        Vertex method2FromGraph = stu.getFromGraph("name", "method2");
        
        assertLinesMatch(Arrays.asList("String"), method1FromFG.getArgumentTypes());
        assertLinesMatch(Arrays.asList("String"), method1FromFG.getProperty("argumentTypes", List.class));
        assertLinesMatch(Arrays.asList("String", "int"), (List<String>) method2FromGraph.property("argumentTypes").value());
    }

    /**
     * Test of setArgumentTypes method, of class VertexMethod.
     */
    @Test
    public void testSetArgumentTypes() {
        String propertyName = "argumentTypes";
        List<String> test1 = Arrays.asList("String");
        List<String> test2 = Arrays.asList("String", "int");
        
        VertexMethod method1FromFG = stu.getFromFG("name", "method1", VertexMethod.class);
        VertexMethod method2FromFG = stu.getFromFG("name", "method2", VertexMethod.class);
        Vertex method1FromGraph = stu.getFromGraph("name", "method1");
        Vertex method2FromGraph = stu.getFromGraph("name", "method2");
        
        assertLinesMatch(test1, method1FromFG.getArgumentTypes());
        assertLinesMatch(test1, method1FromFG.getProperty(propertyName, List.class));
        assertLinesMatch(test2, (List<String>) method2FromGraph.property(propertyName).value());
        
        method1FromFG.setArgumentTypes(test2);
        method2FromGraph.property(propertyName, test1);
        
        assertLinesMatch(test2, method1FromFG.getArgumentTypes());
        assertLinesMatch(test2, method1FromFG.getProperty(propertyName, List.class));
        assertLinesMatch(test2, (List<String>) method1FromGraph.property(propertyName).value());
        assertLinesMatch(test1, method2FromFG.getArgumentTypes());
        assertLinesMatch(test1, method2FromFG.getProperty(propertyName, List.class));
        assertLinesMatch(test1, (List<String>) method2FromGraph.property(propertyName).value());
    }

    /**
     * Test of getAccessModifier method, of class VertexMethod.
     */
    @Test
    public void testGetAccessModifier() {
        VertexMethod method1FromFG = stu.getFromFG("name", "method1", VertexMethod.class);
        Vertex method2FromGraph = stu.getFromGraph("name", "method2");
        
        assertEquals("public", method1FromFG.getAccessModifier());
        assertEquals("public", method1FromFG.getProperty("accessModifier", String.class));
        assertEquals("private", method2FromGraph.property("accessModifier").value());
    }

    /**
     * Test of setAccessModifier method, of class VertexMethod.
     */
    @Test
    public void testSetAccessModifier() {
        String propertyName = "accessModifier";
        String test1 = "public";
        String test2 = "private";
        
        VertexMethod method1FromFG = stu.getFromFG("name", "method1", VertexMethod.class);
        VertexMethod method2FromFG = stu.getFromFG("name", "method2", VertexMethod.class);
        Vertex method1FromGraph = stu.getFromGraph("name", "method1");
        Vertex method2FromGraph = stu.getFromGraph("name", "method2");
        
        assertEquals(test1, method1FromFG.getAccessModifier());
        assertEquals(test1, method1FromFG.getProperty(propertyName, String.class));
        assertEquals(test2, method2FromGraph.property(propertyName).value());
        
        method1FromFG.setAccessModifier(test2);
        method2FromGraph.property(propertyName, test1);
        
        assertEquals(test2, method1FromFG.getAccessModifier());
        assertEquals(test2, method1FromFG.getProperty(propertyName, String.class));
        assertEquals(test2, method1FromGraph.property(propertyName).value());
        assertEquals(test1, method2FromFG.getAccessModifier());
        assertEquals(test1, method2FromFG.getProperty(propertyName, String.class));
        assertEquals(test1, method2FromGraph.property(propertyName).value());
    }

    /**
     * Test of getContainerClassName method, of class VertexMethod.
     */
    @Test
    public void testGetContainerClassName() {
        VertexMethod method1FromFG = stu.getFromFG("name", "method1", VertexMethod.class);
        Vertex method2FromGraph = stu.getFromGraph("name", "method2");
        
        assertEquals("class1", method1FromFG.getContainerClassName());
        assertEquals("class1", method1FromFG.getProperty("containerClassName", String.class));
        assertEquals("class2", method2FromGraph.property("containerClassName").value());
    }

    /**
     * Test of setContainerClassName method, of class VertexMethod.
     */
    @Test
    public void testSetContainerClassName() {
        String propertyName = "containerClassName";
        String test1 = "class1";
        String test2 = "class2";
        
        VertexMethod method1FromFG = stu.getFromFG("name", "method1", VertexMethod.class);
        VertexMethod method2FromFG = stu.getFromFG("name", "method2", VertexMethod.class);
        Vertex method1FromGraph = stu.getFromGraph("name", "method1");
        Vertex method2FromGraph = stu.getFromGraph("name", "method2");
        
        assertEquals(test1, method1FromFG.getContainerClassName());
        assertEquals(test1, method1FromFG.getProperty(propertyName, String.class));
        assertEquals(test2, method2FromGraph.property(propertyName).value());
        
        method1FromFG.setContainerClassName(test2);
        method2FromGraph.property(propertyName, test1);
        
        assertEquals(test2, method1FromFG.getContainerClassName());
        assertEquals(test2, method1FromFG.getProperty(propertyName, String.class));
        assertEquals(test2, method1FromGraph.property(propertyName).value());
        assertEquals(test1, method2FromFG.getContainerClassName());
        assertEquals(test1, method2FromFG.getProperty(propertyName, String.class));
        assertEquals(test1, method2FromGraph.property(propertyName).value());
    }

}
