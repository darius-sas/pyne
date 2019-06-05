package edu.rug.pyne.api.structure;

import com.syncleus.ferma.AbstractEdgeFrame;
import com.syncleus.ferma.annotations.GraphElement;
import com.syncleus.ferma.annotations.InVertex;
import com.syncleus.ferma.annotations.OutVertex;

/**
 *
 * @author Patrick Beuks (s2288842) <code@beuks.net>
 */
@GraphElement
public abstract class EdgeIsImplementationOf extends AbstractEdgeFrame {
    
    @InVertex
    public abstract VertexClass getImplementedOf();
    
    @OutVertex
    public abstract VertexClass getImplementedBy();
    
}
