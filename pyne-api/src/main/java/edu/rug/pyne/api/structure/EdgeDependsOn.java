package edu.rug.pyne.api.structure;

import com.syncleus.ferma.AbstractEdgeFrame;
import com.syncleus.ferma.annotations.GraphElement;
import com.syncleus.ferma.annotations.InVertex;
import com.syncleus.ferma.annotations.OutVertex;
import com.syncleus.ferma.annotations.Property;

/**
 *
 * @author Patrick Beuks (s2288842) <code@beuks.net>
 */
@GraphElement
public abstract class EdgeDependsOn extends AbstractEdgeFrame {
    
    @InVertex
    public abstract VertexClass getDependOn();
    
    @OutVertex
    public abstract VertexClass getDependend();
    
    @Property("Weight")
    public abstract int getWeight();
    
    @Property("Weight")
    public abstract void setWeight(int weight);
    
    public void incrementWeight() {
        setWeight(getWeight() + 1);
    }
    
}
