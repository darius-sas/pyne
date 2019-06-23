package edu.rug.pyne.api.structure;

import com.syncleus.ferma.AbstractVertexFrame;
import com.syncleus.ferma.DefaultClassInitializer;
import com.syncleus.ferma.FramedGraph;
import com.syncleus.ferma.annotations.Adjacency;
import com.syncleus.ferma.annotations.GraphElement;
import com.syncleus.ferma.annotations.Incidence;
import com.syncleus.ferma.annotations.Property;
import java.util.List;
import org.apache.tinkerpop.gremlin.structure.T;
import spoon.reflect.reference.CtPackageReference;

/**
 * This is a structure for a TinkerPop vertex.
 *
 * This vertex is meant to represent a package in the dependency graph.
 *
 * @author Patrick Beuks (s2288842) <code@beuks.net>
 */
@GraphElement
public abstract class VertexPackage extends AbstractVertexFrame {

    /**
     * The label for this vertex
     */
    public static final String LABEL = "package";

    /**
     * Creates a new vertex package with the given name with its package type
     * set to A retrieved package, denoting a package that comes from outside
     * the source files.
     *
     * @param framedGraph The graph to add the vertex to
     * @param PackageName The name for this package
     * @return The newly created vertex
     */
    public static VertexPackage createRetrievedPackage(
            FramedGraph framedGraph, String PackageName
    ) {

        // Creates a new VertexPackage with the vertex label
        VertexPackage vertex = framedGraph.addFramedVertex(
                new DefaultClassInitializer<>(VertexPackage.class),
                T.label,
                LABEL
        );

        vertex.setName(PackageName);
        vertex.setPackageType("RetrievedPackage");
        vertex.setNumTotalDep(0);
        vertex.setNumOfClassesInPackage(0);

        return vertex;
    }

    /**
     * This creates a new vertex on the graph denoting a package.
     *
     * @param framedGraph The graph to add the vertex to
     * @param ctPackage The reference to the package
     * @return The newly created vertex.
     */
    public static VertexPackage createVertexPackage(
            FramedGraph framedGraph, CtPackageReference ctPackage
    ) {

        // Check if this package comes from within the source files
        String packageType = ctPackage.isImplicit()
                ? "RetrievedPackage" : "SystemPackage";

        VertexPackage vertex = framedGraph.addFramedVertex(
                new DefaultClassInitializer<>(VertexPackage.class),
                T.label,
                LABEL
        );

        vertex.setName(ctPackage.getQualifiedName());
        vertex.setPackageType(packageType);
        vertex.setNumTotalDep(0);
        vertex.setNumOfClassesInPackage(0);

        return vertex;
    }

    /**
     * This static function gives a vertex package back from a qualified package
     * name or returns null when not found.
     *
     * @param framedGraph The graph to find the vertex package on
     * @param name The qualified name of the package
     * @return The vertex package, or null if not found
     */
    public static VertexPackage getVertexPackageByName(
            FramedGraph framedGraph, String name
    ) {

        // Find the package vertexes and then find the name
        return framedGraph.traverse(
                (g) -> g.V().hasLabel(LABEL).has("name", name)
        ).nextOrDefault(VertexPackage.class, null);
    }

    /**
     * Returns the name of the vertex package
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
     * Gets the PackageType property of this vertex
     *
     * @return The package type
     */
    @Property("PackageType")
    public abstract String getPackageType();

    /**
     * Sets the PackageType property on this vertex.
     *
     * @param packageType The package type
     */
    @Property("PackageType")
    public abstract void setPackageType(String packageType);

    /**
     * Gets the number of total dependencies this package has.
     *
     * @return The number of dependencies
     */
    @Property("numTotalDep")
    public abstract int getNumTotalDep();

    /**
     * Sets the number of total dependencies this package has.
     *
     * @param numTotalDep The total number of dependencies to set
     */
    @Property("numTotalDep")
    public abstract void setNumTotalDep(int numTotalDep);

    /**
     * Increment the total number of dependencies by one
     */
    public void incrementNumTotalDep() {
        setNumTotalDep(getNumTotalDep() + 1);
    }

    /**
     * Decrement the total number of dependencies by one.
     *
     * @throws IllegalStateException Throws a state exception when the total
     * number of dependencies becomes negative.
     */
    public void decrementNumOfTotalDep() {
        int numdep = getNumTotalDep();
        if (numdep <= 0) {
            throw new IllegalStateException(
                    "Cannot have a negative number of total dependencies"
            );
        }
        setNumTotalDep(numdep - 1);
    }

    /**
     * Gets the number of classes this package has.
     *
     * @return The number of classes
     */
    @Property("numOfClassesInPackage")
    public abstract int getNumOfClassesInPackage();

    /**
     * Sets the number of classes this package has.
     *
     * @param numOfClassesInPackage The total number of classes to set
     */
    @Property("numOfClassesInPackage")
    public abstract void setNumOfClassesInPackage(int numOfClassesInPackage);

    /**
     * Increment the total number of classes by one
     */
    public void incrementNumOfClassesInPackage() {
        setNumOfClassesInPackage(getNumOfClassesInPackage() + 1);
    }

    /**
     * Decrement the total number of dependencies by one.
     *
     * @throws IllegalStateException Throws a state exception when the total
     * number of classes becomes negative.
     */
    public void decrementNumOfClassesInPackage() {
        int numcls = getNumOfClassesInPackage();
        if (numcls <= 0) {
            throw new IllegalStateException(
                    "Cannot have a negative number of classes"
            );
        }
        setNumOfClassesInPackage(numcls - 1);
    }

    /**
     * This function overrides the default remove, also deleting all edges as
     * well as the node.
     */
    @Override
    public void remove() {
        removePackageIsAfferentOfEdges();
        super.remove();
    }

    /**
     * Gets a list of edges that this vertex package is afferent of.
     *
     * @return The list of afferent edges
     */
    @Incidence(label = "packageIsAfferentOf")
    public abstract List<EdgePackageIsAfferentOf> getAfferentOfEdges();

    /**
     * Gets a list of packages that this vertex package is afferent of.
     *
     * @return The list of afferent vertex packages
     */
    @Adjacency(label = "packageIsAfferentOf")
    public abstract List<VertexPackage> getAfferentOfPackages();

    /**
     * Adds an edge between this package and package that denotes that this
     * vertex package is afferent of the given package.
     *
     * @param afferentOfPackage The vertex package this vertex package is
     * afferent of
     * @return The newly created edge
     */
    public EdgePackageIsAfferentOf addAfferentOfPackage(
            VertexPackage afferentOfPackage
    ) {

        incrementNumTotalDep();
        return addFramedEdge(
                "packageIsAfferentOf",
                afferentOfPackage,
                EdgePackageIsAfferentOf.class
        );
    }

    /**
     * Removes all package is afferent of edges.
     */
    @Adjacency(label = "packageIsAfferentOf")
    public abstract void removePackageIsAfferentOfEdges();

}
