package edu.rug.pyne.structure;

import com.syncleus.ferma.AbstractVertexFrame;
import com.syncleus.ferma.DefaultClassInitializer;
import com.syncleus.ferma.FramedGraph;
import com.syncleus.ferma.annotations.Adjacency;
import com.syncleus.ferma.annotations.GraphElement;
import com.syncleus.ferma.annotations.Incidence;
import com.syncleus.ferma.annotations.Property;
import java.util.List;
import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.T;
import spoon.reflect.reference.CtPackageReference;

/**
 *
 * @author Patrick Beuks (s2288842) <code@beuks.net>
 */
@GraphElement
public abstract class VertexPackage extends AbstractVertexFrame {

    public static final String LABEL = "package";

    public static VertexPackage createRetrievedPackage(FramedGraph framedGraph, String PackageName) {

        VertexPackage vertex = framedGraph.addFramedVertex(new DefaultClassInitializer<>(VertexPackage.class), T.label, LABEL);
        vertex.setName(PackageName);
        vertex.setPackageType("RetrievedPackage");
        vertex.setNumTotalDep(0);
        vertex.setNumOfClassesInPackage(0);

        return vertex;
    }

    public static VertexPackage createVertexPackage(FramedGraph framedGraph, CtPackageReference ctPackage) {

        String packageType = ctPackage.isImplicit()? "RetrievedPackage" : "SystemPackage";

        VertexPackage vertex = framedGraph.addFramedVertex(new DefaultClassInitializer<>(VertexPackage.class), T.label, LABEL);
        vertex.setName(ctPackage.getQualifiedName());
        vertex.setPackageType(packageType);
        vertex.setNumTotalDep(0);
        vertex.setNumOfClassesInPackage(0);

        return vertex;
    }

    public static VertexPackage getVertexPackageByName(FramedGraph framedGraph, String name) {
        return framedGraph.traverse(
                (g) -> g.V().hasLabel(LABEL).has("name", name)
        ).nextOrDefault(VertexPackage.class, null);
    }

    @Property("name")
    public abstract String getName();

    @Property("name")
    public abstract void setName(String name);

    @Property("PackageType")
    public abstract String getPackageType();

    @Property("PackageType")
    public abstract void setPackageType(String packageType);

    @Property("numTotalDep")
    public abstract int getNumTotalDep();

    @Property("numTotalDep")
    public abstract void setNumTotalDep(int numTotalDep);

    public void incrementNumTotalDep() {
        setNumTotalDep(getNumTotalDep() + 1);
    }

    public void decrementNumOfTotalDep() {
        int numdep = getNumTotalDep();
        if (numdep <= 0) {
            throw new IllegalStateException("Cannot have a negative number of total depedancies");
        }
        setNumTotalDep(numdep - 1);
    }

    @Property("numOfClassesInPackage")
    public abstract int getNumOfClassesInPackage();

    @Property("numOfClassesInPackage")
    public abstract void setNumOfClassesInPackage(int numOfClassesInPackage);

    public void incrementNumOfClassesInPackage() {
        setNumOfClassesInPackage(getNumOfClassesInPackage() + 1);
    }

    public void decrementNumOfClassesInPackage() {
        int numcls = getNumOfClassesInPackage();
        if (numcls <= 0) {
            throw new IllegalStateException("Cannot have a negative number of classes");
        }
        setNumOfClassesInPackage(numcls - 1);
    }

    @Incidence(label = "packageIsAfferentOf")
    public abstract List<EdgePackageIsAfferentOf> getAfferentOfEdges();

    @Adjacency(label = "packageIsAfferentOf")
    public abstract List<VertexPackage> getAfferentOfPackages();

    public EdgePackageIsAfferentOf addAfferentOfPackage(VertexPackage afferentOfPackage) {
        incrementNumTotalDep();
        return addFramedEdge("packageIsAfferentOf", afferentOfPackage, EdgePackageIsAfferentOf.class);
    }
    
    public void removePackageIsAfferentOf(VertexPackage afferentOfPackage) {
        for (EdgePackageIsAfferentOf afferentOfEdge : getAfferentOfEdges()) {
            if (afferentOfEdge.getAfferentOf().equals(afferentOfPackage)) {
                afferentOfEdge.remove();
                decrementNumOfTotalDep();
                return;
            }
        }
    }
    
    @Adjacency(label = "belongsTo", direction = Direction.IN)
    public abstract List<VertexClass> getBelongsToClasses();
    
    @Adjacency(label = "isEfferentOf", direction = Direction.IN)
    public abstract List<VertexClass> getEffertentOfClasses();
    
    @Incidence(label = "isEfferentOf", direction = Direction.IN)
    public abstract List<EdgeIsEfferentOf> getEffertentOfEdges();

}
