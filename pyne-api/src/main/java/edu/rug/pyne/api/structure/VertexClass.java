package edu.rug.pyne.api.structure;

import com.syncleus.ferma.AbstractVertexFrame;
import com.syncleus.ferma.DefaultClassInitializer;
import com.syncleus.ferma.FramedGraph;
import com.syncleus.ferma.annotations.Adjacency;
import com.syncleus.ferma.annotations.GraphElement;
import com.syncleus.ferma.annotations.Incidence;
import com.syncleus.ferma.annotations.Property;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.T;
import spoon.reflect.declaration.CtType;

/**
 * This is a structure for a TinkerPop vertex.
 *
 * This vertex is meant to represent a class in the dependency graph.
 *
 * @author Patrick Beuks (s2288842) <code@beuks.net>
 */
@GraphElement
public abstract class VertexClass extends AbstractVertexFrame {

    /**
     * The label for this vertex
     */
    public static final String LABEL = "class";

    /**
     * The class types possible.
     *
     * A system class represents a class created from source file.
     *
     * A retrieved class represents a class that is outside the source files
     */
    public enum ClassType {
        SystemClass,
        RetrievedClass;
    }

    /**
     * This static function creates a new retrieved class from a given name
     *
     * @param framedGraph The graph to add the class vertex to
     * @param clazz The name the class vertex should have
     * @return The newly created class vertex
     */
    public static VertexClass createRetrievedClass(
            FramedGraph framedGraph, String clazz
    ) {

        // Create the vertex class with the class label
        VertexClass vertex = framedGraph.addFramedVertex(
                new DefaultClassInitializer<>(VertexClass.class), T.label, LABEL
        );

        vertex.setName(clazz);
        vertex.setClassType(ClassType.RetrievedClass);
        // Not implemented for retieved classes
        vertex.setClassModifier("none");

        return vertex;
    }

    /**
     * This static function creates a new system class from a CtTypr
     *
     * @param framedGraph The graph to add the class vertex to
     * @param clazz The class this class vertex is created for
     * @return The newly created class vertex
     */
    public static VertexClass createSystemClass(
            FramedGraph framedGraph, CtType clazz
    ) {

        String classModifier = "none";

        if (clazz.isAbstract()) {
            classModifier = "Abstract";
        }
        if (clazz.isInterface()) {
            classModifier = "Interface";
        }

        // Create the vertex class with the class label
        VertexClass vertex = framedGraph.addFramedVertex(
                new DefaultClassInitializer<>(VertexClass.class), T.label, LABEL
        );

        vertex.setName(clazz.getQualifiedName());
        vertex.setClassType(ClassType.SystemClass);
        vertex.setClassModifier(classModifier);

        return vertex;
    }

    /**
     * This static function gives a vertex class back from a qualified class
     * name or returns null when not found.
     *
     * @param framedGraph The graph to find the vertex class on
     * @param name The qualified name of the class
     * @return The vertex class, or null if not found
     */
    public static VertexClass getVertexClassByName(
            FramedGraph framedGraph, String name
    ) {

        // Find the class vertexes and then find the name
        return framedGraph.traverse(
                (g) -> g.V().hasLabel(LABEL).has("name", name)
        ).nextOrDefault(VertexClass.class, null);
    }

    /**
     * Returns the name of the vertex class
     *
     * @return The name property
     */
    @Property("name")
    public abstract String getName();

    /**
     * Sets the name property on the vertex
     *
     * @param name The name to set
     */
    @Property("name")
    public abstract void setName(String name);


    /**
     * The number of lines of code in the class.
     * @return the lines of code in the class as a long.
     */
    @Property("linesOfCode")
    public abstract long getLinesOfCode();

    /**
     * Set the number of lines of code in the source code of this class.
     * @param linesOfCode the lines of code.
     */
    @Property("linesOfCode")
    public abstract void setLinesOfCode(long linesOfCode);

    /**
     * This function returns the class type of the vertex class
     *
     * @return The class type
     */
    public ClassType getClassType() {
        return ClassType.valueOf(getProperty("ClassType", String.class));
    }

    /**
     * This function sets the class type of the vertex class
     *
     * @param classType The class type to set
     */
    public void setClassType(ClassType classType) {
        setProperty("ClassType", classType.name());
    }

    /**
     * Returns true is the property class type equals to ClassType.SystemClass
     *
     * @return True if it is a system class, false otherwise
     */
    public boolean isSystemClass() {
        return getClassType().equals(ClassType.SystemClass);
    }

    /**
     * Gets the class modifier property for this vertex class
     *
     * @return The class modifier
     */
    @Property("classModifier")
    public abstract String getClassModifier();

    /**
     * Sets the class modifier property on the vertex class
     *
     * @param classModifier The class modifier to set
     */
    @Property("classModifier")
    public abstract void setClassModifier(String classModifier);

    /**
     * This function removes all the edges from the vertex class
     */
    public void removeEdges() {
        removeDependsOn();
        removeImplementationOf();
        removeChildsOf();
    }

    /**
     * This function overrides the default remove, also deleting all edges
     * and belonging edges as well as the node.
     */
    @Override
    public void remove() {
        removeEdges();
        removeBelongsTo();
        super.remove();
    }

    /**
     * Returns the edge for belongs to. In a valid graph only one per vertex
     * should exists
     *
     * @return The belongsTo edge
     */
    @Incidence(label = "belongsTo")
    public abstract EdgeBelongsTo getBelongsTo();

    /**
     * Returns the VertexPackage the vertex class belongs to. In a valid graph
     * only one per vertex should exists
     *
     * @return The VertexPackage where the out of the belongsTo edge points to
     */
    @Adjacency(label = "belongsTo")
    public abstract VertexPackage getBelongsToPackage();

    /**
     * This sets the vertex package this vertex class belongs to.
     *
     * Only one per vertex class can exists. If a previous edge exists, that
     * edge is removed and a new one is set.
     *
     * This function also increases the total number of classes the package has.
     *
     * @param vertexPackage The vertex package this vertex class belongs to
     * @return The newly created edge
     */
    public EdgeBelongsTo setBelongsTo(VertexPackage vertexPackage) {
        // Get previous set edges for belongs to
        Iterator<Edge> edges = getElement().edges(Direction.OUT, "belongsTo");
        // Remove all found edges if any
        if (edges.hasNext()) {
            getBelongsToPackage().decrementNumOfClassesInPackage();
            edges.next().remove();
        }
        // This vertex class now belongs to the package, so increment the number
        // of classes
        vertexPackage.incrementNumOfClassesInPackage();

        return addFramedEdge("belongsTo", vertexPackage, EdgeBelongsTo.class);
    }

    /**
     * This function removes the belongsTo edge. This also decrements the total
     * number of the package this class belonged to.
     */
    public void removeBelongsTo() {
        VertexPackage belongsToPackage = getBelongsToPackage();
        if (belongsToPackage != null) {
            belongsToPackage.decrementNumOfClassesInPackage();
            getBelongsTo().remove();

            // If no more classes belong to the package, remove the package.
            if (belongsToPackage.getNumOfClassesInPackage() == 0) {
                belongsToPackage.remove();
            }
        }
    }

    /**
     * Gets a list of edges that this vertex class is afferent of.
     *
     * @return The list of afferent edges
     */
    @Incidence(label = "isAfferentOf")
    public abstract List<EdgeIsAfferentOf> getAfferentOfEdges();

    /**
     * Gets a list of packages that this vertex class is afferent of.
     *
     * @return The list of afferent vertex packages
     */
    @Adjacency(label = "isAfferentOf")
    public abstract List<VertexPackage> getAfferentOfPackages();

    /**
     * Adds an edge between a class and package that denotes that this vertex
     * class is afferent of the given package.
     *
     * This also adds a edge between this class belongsTo package and the given
     * package if this edge does not already exists.
     *
     * @param vertexPackage The vertex package this vertex class is afferent of
     * @return The newly created edge
     */
    public EdgeIsAfferentOf addAfferentOf(VertexPackage vertexPackage) {

        EdgeIsAfferentOf edgeIsAfferentOf = addFramedEdge(
                "isAfferentOf", vertexPackage, EdgeIsAfferentOf.class
        );

        // Check if we need to add a edge between packages.
        VertexPackage belongsToPackage = getBelongsToPackage();
        belongsToPackage.addAfferentOfPackage(vertexPackage);

        return edgeIsAfferentOf;
    }

    /**
     * Gets a list of edges that this vertex class is efferent of.
     *
     * @return The list of efferent edges
     */
    @Incidence(label = "isEfferentOf")
    public abstract List<EdgeIsEfferentOf> getEfferentOfEdges();

    /**
     * Gets a list of packages that this vertex class is efferent of.
     *
     * @return The list of efferent vertex packages
     */
    @Adjacency(label = "isEfferentOf")
    public abstract List<VertexPackage> getEfferentOfPackages();

    /**
     * Adds an edge between a class and package that denotes that this vertex
     * class is efferent of the given package.
     *
     * @param vertexPackage The vertex package this vertex class is efferent of
     * @return The newly created edge
     */
    public EdgeIsEfferentOf addEfferentOf(VertexPackage vertexPackage) {
        return addFramedEdge(
                "isEfferentOf", vertexPackage, EdgeIsEfferentOf.class
        );
    }

    /**
     * Gets a list of edges that this vertex class depends on.
     *
     * @return The list of dependsOn edges
     */
    @Incidence(label = "dependsOn")
    public abstract List<EdgeDependsOn> getDependOnEdges();

    /**
     * Gets a list of classes that this vertex class depends on.
     *
     * @return The list of classes.
     */
    @Adjacency(label = "dependsOn")
    public abstract List<VertexClass> getDependOnClasses();

    /**
     * This adds an edge to a class denoting that this class depends on the
     * given class.
     *
     * If the edge already exists the weight of the edge is increased instead.
     *
     * @param dependingClass The class this vertex class depends on.
     *
     * @return The newly created edge, or the already existing edge that has
     * been incremented.
     */
    public EdgeDependsOn addDependOnClass(VertexClass dependingClass) {

        if (dependingClass.getName().equals(getName())) {
            return null;
        }

        // Find if this class already depends on the given class
        Optional<EdgeDependsOn> dependency = getDependOnEdges().stream().filter(
                (edge) -> edge.getDependOn().equals(dependingClass)
        ).findFirst();

        EdgeDependsOn dependOnEdge;
        if (dependency.isEmpty()) {
            // Create an edge and set the weight to 1
            dependOnEdge = addFramedEdge(
                    "dependsOn", dependingClass, EdgeDependsOn.class
            );

            dependOnEdge.setWeight(1);
        } else {
            // Increment the weight.
            dependOnEdge = dependency.get();
            dependOnEdge.incrementWeight();
        }
        return dependOnEdge;
    }

    /**
     * Removes all depends on edges.
     */
    @Adjacency(label = "dependsOn")
    public abstract void removeDependsOn();

    /**
     * Gets a list of edges that this vertex class is a child of.
     *
     * @return The list of isChildOf edges
     */
    @Incidence(label = "isChildOf")
    public abstract List<EdgeIsChildOf> getChildsOfEdges();

    /**
     * Gets a list of classes that this vertex class is a child of.
     *
     * @return The list of classes.
     */
    @Adjacency(label = "isChildOf")
    public abstract List<VertexClass> getChildsOfClasses();

    /**
     * This adds a new edge between this class and the given class that denotes
     * that this class is a child of the given class.
     *
     * @param childOfClass The class this class is a child of
     * @return The newly created edge
     */
    public EdgeIsChildOf addChildOfClass(VertexClass childOfClass) {
        return addFramedEdge("isChildOf", childOfClass, EdgeIsChildOf.class);
    }

    /**
     * Removes all is child of edges.
     */
    @Adjacency(label = "isChildOf")
    public abstract void removeChildsOf();

    /**
     * Gets a list of edges that this vertex class is an implementation of.
     *
     * @return The list of isImplementationOf edges
     */
    @Incidence(label = "isImplementationOf")
    public abstract List<EdgeIsImplementationOf> getImplementationOfEdges();

    /**
     * Gets a list of classes that this vertex class is an implementation of.
     *
     * @return The list of classes.
     */
    @Adjacency(label = "isImplementationOf")
    public abstract List<VertexClass> getImplementationOfClasses();

    /**
     * This adds a new edge between this class and the given class that denotes
     * that this class is an implementation of the given class.
     *
     * @param implementationOfClass The class this class is an implementation of
     * @return The newly created edge
     */
    public EdgeIsImplementationOf addImplematationOfClass(
            VertexClass implementationOfClass
    ) {
        return addFramedEdge(
                "isImplementationOf",
                implementationOfClass,
                EdgeIsImplementationOf.class
        );
    }

    /**
     * Removes all is implementation of edges.
     */
    @Adjacency(label = "isImplementationOf")
    public abstract void removeImplementationOf();

}
