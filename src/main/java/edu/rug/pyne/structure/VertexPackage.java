package edu.rug.pyne.structure;

import com.syncleus.ferma.AbstractVertexFrame;
import com.syncleus.ferma.annotations.Adjacency;
import com.syncleus.ferma.annotations.GraphElement;
import com.syncleus.ferma.annotations.Incidence;
import com.syncleus.ferma.annotations.Property;
import java.util.List;
import org.apache.tinkerpop.gremlin.structure.Direction;

/**
 *
 * @author Patrick Beuks (s2288842) <code@beuks.net>
 */
@GraphElement
public abstract class VertexPackage extends AbstractVertexFrame {
    
    @Property("name")
    public abstract String getName();
    
    @Property("name")
    public abstract void setName(String name);
    
    @Incidence(label = "belongsTo", direction = Direction.IN)
    public abstract List<EdgeBelongsTo> getBelongingEdges();
    
    @Adjacency(label = "belongsTo", direction = Direction.IN)
    public abstract List<VertexClass> getBelongingClasses();
    
    public void addBelongingClass(VertexClass vertexClass) {
        vertexClass.setBelongsTo(this);
    }
    
}
