package edu.rug.pyne.api.structure;

import com.syncleus.ferma.AbstractEdgeFrame;
import com.syncleus.ferma.annotations.GraphElement;
import com.syncleus.ferma.annotations.InVertex;
import com.syncleus.ferma.annotations.OutVertex;

/**
 * This is a structure for a TinkerPop edge.
 *
 * This edge is meant to represent relation in the dependency graph, where a
 * class implements another class. 
 *
 * @author Patrick Beuks (s2288842) <code@beuks.net>
 */
@GraphElement
public abstract class EdgeIsImplementationOf extends AbstractEdgeFrame {
    
    /**
     * The class that the edge points to
     * 
     * @return The class that is implemented by another class
     */
    @InVertex
    public abstract VertexClass getImplementedOf();
    
    /**
     * The class that the edge comes from
     * 
     * @return The class that implements the pointed to package
     */
    @OutVertex
    public abstract VertexClass getImplementedBy();
    
}
