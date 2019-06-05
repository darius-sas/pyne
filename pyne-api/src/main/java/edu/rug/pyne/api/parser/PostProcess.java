package edu.rug.pyne.api.parser;

import com.syncleus.ferma.FramedGraph;

/**
 *
 * @author Patrick Beuks (s2288842) <code@beuks.net>
 */
public interface PostProcess {
    
    public abstract void postProcess(FramedGraph framedGraph);
    
}
