package edu.rug.pyne.api.structure;

import com.syncleus.ferma.AbstractEdgeFrame;
import com.syncleus.ferma.annotations.GraphElement;
import com.syncleus.ferma.annotations.InVertex;
import com.syncleus.ferma.annotations.OutVertex;
import com.syncleus.ferma.annotations.Property;

/**
 * This is a structure for a TinkerPop edge.
 *
 * This edge is meant to represent relation in the dependency graph, where a
 * class depends on another class.. 
 *
 * @author Patrick Beuks (s2288842) <code@beuks.net>
 */
@GraphElement
public abstract class EdgeDependsOn extends AbstractEdgeFrame {
    
    /**
     * The class that the edge points to
     * 
     * @return The class that is depended on by another class
     */
    @InVertex
    public abstract VertexClass getDependOn();
    
    /**
     * The class that the edge comes from
     * 
     * @return The class that depends on another class
     */
    @OutVertex
    public abstract VertexClass getDependend();
    
    /**
     * The weight of this edge
     * 
     * @return The weight
     */
    @Property("Weight")
    public abstract int getWeight();
    
    /**
     * Sets the weight of this edge
     * 
     * @param weight The weight to set
     */
    @Property("Weight")
    public abstract void setWeight(int weight);
    
    /**
     * Increments the weight by one.
     */
    public void incrementWeight() {
        setWeight(getWeight() + 1);
    }
    
}
