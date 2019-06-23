package edu.rug.pyne.api.structure;

import com.syncleus.ferma.AbstractEdgeFrame;
import com.syncleus.ferma.annotations.GraphElement;
import com.syncleus.ferma.annotations.InVertex;
import com.syncleus.ferma.annotations.OutVertex;

/**
 * This is a structure for a TinkerPop edge.
 *
 * This edge is meant to represent relation in the dependency graph, where a
 * class is afferent of a package. 
 *
 * @author Patrick Beuks (s2288842) <code@beuks.net>
 */
@GraphElement
public abstract class EdgeIsAfferentOf extends AbstractEdgeFrame {

    /**
     * The class that the edge comes from
     * 
     * @return The class that is afferent of the pointed to package
     */
    @OutVertex
    public abstract VertexClass getVertexClass();

    /**
     * The package that the edge points to
     * 
     * @return The package that the class is afferent of
     */
    @InVertex
    public abstract VertexPackage getVertexPackage();
    
}
