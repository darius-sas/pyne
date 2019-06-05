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
public abstract class EdgeIsChildOf extends AbstractEdgeFrame {
    
    @OutVertex
    public abstract VertexClass getChild();
    
    @InVertex
    public abstract VertexClass getParent();
    
}
