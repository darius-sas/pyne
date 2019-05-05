package edu.rug.pyne.structure;

import com.syncleus.ferma.AbstractVertexFrame;
import com.syncleus.ferma.annotations.Adjacency;
import com.syncleus.ferma.annotations.GraphElement;
import com.syncleus.ferma.annotations.Incidence;
import com.syncleus.ferma.annotations.Property;
import java.util.Iterator;
import java.util.List;
import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Edge;

/**
 *
 * @author Patrick Beuks (s2288842) <code@beuks.net>
 */
@GraphElement
public abstract class VertexMethod extends AbstractVertexFrame {

    @Property("name")
    public abstract String getName();

    @Property("name")
    public abstract void setName(String name);
    
    @Property("MethodType")
    public abstract String getMethodType();
    
    @Property("MethodType")
    public abstract void setMethodType(String methodType);
    
    @Property("isStatic")
    public abstract boolean isStatic();
    
    @Property("isStatic")
    public abstract void setIsStatic(boolean isStatic);
    
    @Property("returnType")
    public abstract String getReturnType();
    
    @Property("returnType")
    public abstract void setReturnType(String returnType);
    
    @Property("argumentTypes")
    public abstract List<String> getArgumentTypes();
    
    @Property("argumentTypes")
    public abstract void setArgumentTypes(List<String> argumentTypes);
    
    @Property("accessModifier")
    public abstract String getAccessModifier();
    
    @Property("accessModifier")
    public abstract void setAccessModifier(String accessModifier);
    
    @Property("containerClassName")
    public abstract String getContainerClassName();
    
    @Property("containerClassName")
    public abstract void setContainerClassName(String containerClassName);

    @Incidence(label = "containedIn", direction = Direction.OUT)
    public abstract EdgeContainedIn getContainedIn();
    
    @Adjacency(label = "containedIn", direction = Direction.OUT)
    public abstract VertexClass getContainedInClass();
    
    public EdgeContainedIn setContainedIn(VertexClass vertexClass) {
        Iterator<Edge> edges = getElement().edges(Direction.OUT, "containedIn");
        if (edges.hasNext()) {
            edges.next().remove();
        }
        return addFramedEdge("containedIn", vertexClass, EdgeContainedIn.class);
    }
    
}
