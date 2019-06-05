package edu.rug.pyne.parser.removeprocessor;

import com.syncleus.ferma.FramedGraph;
import edu.rug.pyne.parser.PostProcess;
import java.util.List;
import org.apache.tinkerpop.gremlin.structure.Edge;

/**
 *
 * @author Patrick Beuks (s2288842) <code@beuks.net>
 */
public class ClassRemovePostProcess implements PostProcess {

    @Override
    public void postProcess(FramedGraph framedGraph) {
        List<Edge> efferentEdges = framedGraph.getRawTraversal().E().hasLabel("isAfferentOf", "isEfferentOf", "packageIsAfferentOf").toList();
        for (Edge efferentEdge : efferentEdges) {
            efferentEdge.remove();
        }
    }
    
}
