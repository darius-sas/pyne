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
 * package is afferent of another package. 
 *
 * @author Patrick Beuks (s2288842) <code@beuks.net>
 */
@GraphElement
public abstract class EdgePackageIsAfferentOf extends AbstractEdgeFrame {

    /**
     * The package that the edge points to
     * 
     * @return The package is afferent
     */
    @InVertex
    public abstract VertexPackage getAfferentOf();

    /**
     * The package that the edge comes from
     * 
     * @return The package that the is afferent of another package
     */
    @OutVertex
    public abstract VertexPackage getAfferentBy();
    
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
