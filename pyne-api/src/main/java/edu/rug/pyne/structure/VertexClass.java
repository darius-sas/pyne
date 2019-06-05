package edu.rug.pyne.structure;

import com.syncleus.ferma.AbstractVertexFrame;
import com.syncleus.ferma.DefaultClassInitializer;
import com.syncleus.ferma.FramedGraph;
import com.syncleus.ferma.annotations.Adjacency;
import com.syncleus.ferma.annotations.GraphElement;
import com.syncleus.ferma.annotations.Incidence;
import com.syncleus.ferma.annotations.Property;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.T;
import spoon.reflect.declaration.CtType;

/**
 *
 *
 * @author Patrick Beuks (s2288842) <code@beuks.net>
 */
@GraphElement
public abstract class VertexClass extends AbstractVertexFrame {

    public static final String LABEL = "class";

    public enum ClassType {
        SystemClass,
        RetrievedClass;
    }

    public static VertexClass createRetrievedClass(FramedGraph framedGraph, String clazz) {

        VertexClass vertex = framedGraph.addFramedVertex(new DefaultClassInitializer<>(VertexClass.class), T.label, LABEL);
        vertex.setName(clazz);
        vertex.setClassType(ClassType.RetrievedClass);
        vertex.setClassModifier("none");

        return vertex;
    }

    public static VertexClass createSystemClass(FramedGraph framedGraph, CtType clazz) {

        String classModifier = "none";

        if (clazz.isAbstract()) {
            classModifier = "Abstract";
        }
        if (clazz.isInterface()) {
            classModifier = "Interface";
        }

        VertexClass vertex = framedGraph.addFramedVertex(new DefaultClassInitializer<>(VertexClass.class), T.label, LABEL);
        vertex.setName(clazz.getQualifiedName());
        vertex.setClassType(ClassType.SystemClass);
        vertex.setClassModifier(classModifier);

        return vertex;
    }

    public static VertexClass getVertexClassByName(FramedGraph framedGraph, String name) {
        return framedGraph.traverse(
                (g) -> g.V().hasLabel(LABEL).has("name", name)
        ).nextOrDefault(VertexClass.class, null);
    }

    @Property("name")
    public abstract String getName();

    @Property("name")
    public abstract void setName(String name);
    
    public void removeEdges() {
        
        removeAfferentsOf();
        removeEfferentsOf();
        removeDependsOn();
        removeImplementationOf();
        removeChildsOf();
        removeBelongsTo();
        
    }
    
    @Override
    public void remove() {
        this.removeEdges();
        super.remove();
    }

    public ClassType getClassType() {
        return ClassType.valueOf(getProperty("ClassType", String.class));
    }

    public void setClassType(ClassType classType) {
        setProperty("ClassType", classType.name());
    }

    public boolean isSystemClass() {
        return getClassType().equals(ClassType.SystemClass);
    }

    @Property("classModifier")
    public abstract String getClassModifier();

    @Property("classModifier")
    public abstract void setClassModifier(String classModifier);

    @Incidence(label = "belongsTo")
    public abstract EdgeBelongsTo getBelongsTo();

    @Adjacency(label = "belongsTo")
    public abstract VertexPackage getBelongsToPackage();

    public EdgeBelongsTo setBelongsTo(VertexPackage vertexPackage) {
        Iterator<Edge> edges = getElement().edges(Direction.OUT, "belongsTo");
        if (edges.hasNext()) {
            getBelongsToPackage().decrementNumOfClassesInPackage();
            edges.next().remove();
        }
        vertexPackage.incrementNumOfClassesInPackage();
        return addFramedEdge("belongsTo", vertexPackage, EdgeBelongsTo.class);
    }

    public void removeBelongsTo() {
        getBelongsToPackage().decrementNumOfClassesInPackage();
        if (getBelongsToPackage().getNumOfClassesInPackage() == 0) {
            getBelongsToPackage().remove();
        } else {
            getBelongsTo().remove();
        }
    }
    
    @Incidence(label = "isAfferentOf")
    public abstract List<EdgeIsAfferentOf> getAfferentOfEdges();

    @Adjacency(label = "isAfferentOf")
    public abstract List<VertexPackage> getAfferentOfPackages();

    public EdgeIsAfferentOf addAfferentOf(VertexPackage vertexPackage) {
        
        EdgeIsAfferentOf edgeIsAfferentOf = addFramedEdge("isAfferentOf", vertexPackage, EdgeIsAfferentOf.class);

        VertexPackage belongsToPackage = getBelongsToPackage();
        if (!belongsToPackage.getAfferentOfPackages().contains(vertexPackage)) {
            belongsToPackage.addAfferentOfPackage(vertexPackage);
        }

        return edgeIsAfferentOf;
    }

    public void removeAfferentsOf() {
        VertexPackage belongsToPackage = getBelongsToPackage();
        List<VertexClass> otherBelongs = belongsToPackage.getBelongsToClasses();

        List<VertexPackage> otherAfferents = new ArrayList<>();

        for (VertexClass otherBelong : otherBelongs) {
            if (otherBelong.equals(this)) {
                continue;
            }
            for (VertexPackage afferentOfPackage : otherBelong.getAfferentOfPackages()) {
                if (!otherAfferents.contains(afferentOfPackage)) {
                    otherAfferents.add(afferentOfPackage);
                }
            }
        }

        for (EdgeIsAfferentOf afferentOfEdge : getAfferentOfEdges()) {
            if (!otherAfferents.contains(afferentOfEdge.getVertexPackage())) {
                belongsToPackage.removePackageIsAfferentOf(afferentOfEdge.getVertexPackage());
            }
            afferentOfEdge.remove();
        }
    }

    @Incidence(label = "isEfferentOf")
    public abstract List<EdgeIsEfferentOf> getEfferentOfEdges();

    @Adjacency(label = "isEfferentOf")
    public abstract List<VertexPackage> getEfferentOfPackages();

    public EdgeIsEfferentOf addEfferentOf(VertexPackage vertexPackage) {
        return addFramedEdge("isEfferentOf", vertexPackage, EdgeIsEfferentOf.class);
    }

    @Adjacency(label = "isEfferentOf")
    public abstract void removeEfferentsOf();

    @Incidence(label = "dependsOn")
    public abstract List<EdgeDependsOn> getDependOnEdges();

    @Adjacency(label = "dependsOn")
    public abstract List<VertexClass> getDependOnClasses();

    public EdgeDependsOn addDependOnClass(VertexClass dependingClass) {
        return addFramedEdge("dependsOn", dependingClass, EdgeDependsOn.class);
    }

    @Adjacency(label = "dependsOn")
    public abstract void removeDependsOn();
    
    @Adjacency(label = "dependsOn", direction = Direction.IN)
    public abstract List<VertexClass> getDependByClasses();

    @Incidence(label = "isChildOf")
    public abstract List<EdgeIsChildOf> getChildsOfEdges();

    @Adjacency(label = "isChildOf")
    public abstract List<VertexClass> getChildsOfClasses();

    public EdgeIsChildOf addChildOfClass(VertexClass childOfClass) {
        return addFramedEdge("isChildOf", childOfClass, EdgeIsChildOf.class);
    }

    @Adjacency(label = "isChildOf")
    public abstract void removeChildsOf();

    @Adjacency(label = "isChildOf", direction = Direction.IN)
    public abstract List<VertexClass> getChildsByClasses();

    @Incidence(label = "isImplementationOf")
    public abstract List<EdgeIsImplementationOf> getImplementationOfEdges();

    @Adjacency(label = "isImplementationOf")
    public abstract List<VertexClass> getImplementationOfClasses();

    public EdgeIsImplementationOf addImplematationOfClass(VertexClass implementationOfClass) {
        return addFramedEdge("isImplementationOf", implementationOfClass, EdgeIsImplementationOf.class);
    }

    @Adjacency(label = "isImplementationOf")
    public abstract void removeImplementationOf();

    @Adjacency(label = "isImplementationOf", direction = Direction.IN)
    public abstract List<VertexClass> getImplementationByClasses();

    
}
