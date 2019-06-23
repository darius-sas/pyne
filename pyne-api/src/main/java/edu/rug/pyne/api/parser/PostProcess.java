package edu.rug.pyne.api.parser;

import com.syncleus.ferma.FramedGraph;

/**
 * This post processor is needed to do operations after a processor is finished
 * and all information of the graph is available.
 * 
 * @author Patrick Beuks (s2288842) <code@beuks.net>
 */
public interface PostProcess {
    
    /**
     * The post process that need to occur.
     * 
     * @param framedGraph The graph the post processing should occur on.
     */
    public abstract void postProcess(FramedGraph framedGraph);
    
}
