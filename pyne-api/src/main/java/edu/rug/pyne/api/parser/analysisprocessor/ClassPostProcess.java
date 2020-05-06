package edu.rug.pyne.api.parser.analysisprocessor;

import com.syncleus.ferma.FramedGraph;
import edu.rug.pyne.api.parser.PostProcess;
import edu.rug.pyne.api.structure.VertexClass;
import java.util.List;

import edu.rug.pyne.api.structure.VertexPackage;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;

/**
 * This class is a post processor for the analysis processor. It removes orphan
 * nodes and adds afferent and efferent edges.
 *
 * @author Patrick Beuks (s2288842) <code@beuks.net>
 */
public class ClassPostProcess implements PostProcess {

    private static final Logger LOGGER
            = LogManager.getLogger(ClassPostProcess.class);

    /**
     * Processes the graph after the analysis step
     * 
     * @param framedGraph The graph to do the processing on
     */
    @Override
    public void postProcess(FramedGraph framedGraph) {

        LOGGER.info("Post processing classes");
        LOGGER.info("Removing orphan nodes");
        // Get nodes with no edges going out or in
        framedGraph.traverse((g) -> {
            return g.V().hasLabel(VertexClass.LABEL).where(
                    __.both("dependsOn", "isChildOf", "isImplementationOf")
                            .count().is(0)
            );
        }).toList(VertexClass.class)
                .forEach((orphanNode) -> orphanNode.remove());

        LOGGER.info("Processing afferent edges");
        String SystemClassLabel = VertexClass.ClassType.SystemClass.name();

        // Get all system classes
        List<? extends VertexClass> systemClasses = framedGraph.traverse(
                (g) -> {
                    return g.V().hasLabel(VertexClass.LABEL)
                            .has("ClassType", SystemClassLabel);
                }
        ).toList(VertexClass.class);

        // Loop over the system classes
        for (VertexClass systemClass : systemClasses) {

            // Get all classes that this class points to
            List<? extends VertexClass> outVertexes = framedGraph.traverse(
                    (g) -> {
                        return g.V(systemClass.getElement().id())
                                .out(
                                        "dependsOn",
                                        "isChildOf",
                                        "isImplementationOf"
                                );
                    }
            ).toList(VertexClass.class);

            // For each of the classes add afferent and efferent edges where
            // needed.
            for (VertexClass outVertex : outVertexes) {
                // Do not add the edge if it belongs to its blongs to package
                VertexPackage classBelongsToPackage = systemClass.getBelongsToPackage();
                VertexPackage outVertexBelongsToPackage = outVertex.getBelongsToPackage();
                if (classBelongsToPackage == null || classBelongsToPackage.equals(outVertexBelongsToPackage)) {
                    continue;
                }

                if (outVertexBelongsToPackage != null && !systemClass.getAfferentOfPackages()
                        .contains(outVertexBelongsToPackage)) {
                    systemClass.addAfferentOf(outVertexBelongsToPackage);
                }

                if (!outVertex.getEfferentOfPackages()
                        .contains(classBelongsToPackage)) {
                    outVertex.addEfferentOf(classBelongsToPackage);
                }
            }

        }
    }

}
