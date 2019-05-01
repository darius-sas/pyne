package edu.rug.pyne.structure;

import com.syncleus.ferma.AbstractEdgeFrame;
import com.syncleus.ferma.annotations.GraphElement;
import com.syncleus.ferma.annotations.InVertex;
import com.syncleus.ferma.annotations.OutVertex;

/**
 *
 * @author Patrick Beuks (s2288842) <code@beuks.net>
 */
@GraphElement
public abstract class EdgeContainedIn extends AbstractEdgeFrame {

    @OutVertex
    public abstract VertexMethod getVertexMethod();
    
    @InVertex
    public abstract VertexClass getVertexClass();

}
