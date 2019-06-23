package edu.rug.pyne.api.parser.removeprocessor;

import com.syncleus.ferma.FramedGraph;
import edu.rug.pyne.api.parser.Parser;
import edu.rug.pyne.api.structure.VertexClass;
import java.io.File;
import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtType;

/**
 * This a remove processor. It takes the source code class and checks if it
 * needs to be removed and removes it
 *
 * @author Patrick Beuks (s2288842) <code@beuks.net>
 */
public class ClassRemover extends AbstractProcessor<CtClass<?>> {

    // The graph to remove the vertexes from
    private final FramedGraph framedGraph;

    // The parser containing additional information
    private final Parser parser;

    /**
     * This class processor implements a spoon processor to find source code
     * classes
     *
     * @param parser The parser to use
     * @param framedGraph The graph to remove the classes from
     */
    public ClassRemover(Parser parser, FramedGraph framedGraph) {
        this.framedGraph = framedGraph;
        this.parser = parser;
    }

    /**
     * Processes a single source code class
     *
     * @param clazz The class to process
     */
    @Override
    public void process(CtClass<?> clazz) {
        this.removeClass(clazz);
    }

    /**
     * Processes a single source code class or interface
     *
     * @param clazz The class or interface to process
     */
    public void removeClass(CtType<?> clazz) {

        // Input validation
        if (parser.getRemovedFiles() == null
                || parser.getModifiedFiles() == null) {
            throw new IllegalStateException("Expected files to be removed");
        }
        
        // Check if it needs to be removed or modified
        File curFile = clazz.getPosition().getFile();
        if (!parser.getRemovedFiles().contains(curFile)
                && !parser.getModifiedFiles().contains(curFile)) {
            return;
        }

        // Get the vertex to remove
        VertexClass vertex = VertexClass
                .getVertexClassByName(framedGraph, clazz.getQualifiedName());

        if (vertex == null) {
            return;
        }

        // If it is in a removed files it needs to be complete removed
        // Otherwise only the edges need to be removed
        if (parser.getRemovedFiles().contains(curFile)) {
            vertex.remove();
        } else if (parser.getModifiedFiles().contains(curFile)) {
            vertex.removeEdges();
        }

    }

}
