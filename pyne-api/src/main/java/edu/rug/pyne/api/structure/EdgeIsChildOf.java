package edu.rug.pyne.api.structure;

import com.syncleus.ferma.AbstractEdgeFrame;
import com.syncleus.ferma.annotations.GraphElement;
import com.syncleus.ferma.annotations.InVertex;
import com.syncleus.ferma.annotations.OutVertex;

/**
 * This is a structure for a TinkerPop edge.
 *
 * This edge is meant to represent relation in the dependency graph, where a
 * class is a child of another class.
 *
 * @author Patrick Beuks (s2288842) <code@beuks.net>
 */
@GraphElement
public abstract class EdgeIsChildOf extends AbstractEdgeFrame {
    
    /**
     * The class that the edge points to
     * 
     * @return The class that is the child of the class
     */
    @InVertex
    public abstract VertexClass getChild();
    
    /**
     * The class that the edge comes from
     * 
     * @return The class that is the parent of the pointed to class
     */
    @OutVertex
    public abstract VertexClass getParent();
    
}
