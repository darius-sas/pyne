package edu.rug.pyne.api.parser.removeprocessor;

import com.syncleus.ferma.FramedGraph;
import edu.rug.pyne.api.parser.PostProcess;
import java.util.List;
import org.apache.tinkerpop.gremlin.structure.Edge;

/**
 * This class is a post processor for the remove processor. It removes all
 * efferent and afferent edges as these are re-added on the post analysis step
 *
 * @author Patrick Beuks (s2288842) <code@beuks.net>
 */
public class ClassRemovePostProcess implements PostProcess {

    /**
     * Removes all efferent and afferent edges from the given framedGraph
     *
     * @param framedGraph The graph to remove all efferent and afferent edges
     * from
     */
    @Override
    public void postProcess(FramedGraph framedGraph) {
        List<Edge> efferentEdges = framedGraph.getRawTraversal().E()
                .hasLabel("isAfferentOf", "isEfferentOf", "packageIsAfferentOf")
                .toList();
        for (Edge efferentEdge : efferentEdges) {
            efferentEdge.remove();
        }
    }

}
