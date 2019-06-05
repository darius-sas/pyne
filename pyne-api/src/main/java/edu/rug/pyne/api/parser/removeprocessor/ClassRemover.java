package edu.rug.pyne.api.parser.removeprocessor;

import com.syncleus.ferma.FramedGraph;
import edu.rug.pyne.api.parser.Parser;
import edu.rug.pyne.api.structure.VertexClass;
import java.io.File;
import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtType;

/**
 *
 * @author Patrick Beuks (s2288842) <code@beuks.net>
 */
public class ClassRemover extends AbstractProcessor<CtClass<?>> {

    private final FramedGraph framedGraph;
    private final Parser parser;

    public ClassRemover(Parser parser, FramedGraph framedGraph) {
        this.framedGraph = framedGraph;
        this.parser = parser;
    }

    @Override
    public void process(CtClass<?> clazz) {
        this.removeClass(clazz);
    }

    public void removeClass(CtType<?> clazz) {

        if (parser.getRemovedFiles() == null || parser.getModifiedFiles() == null) {
            throw new IllegalStateException("Expected files to be removed");
        }
        File curFile = clazz.getPosition().getFile();
        if (!parser.getRemovedFiles().contains(curFile) && !parser.getModifiedFiles().contains(curFile)) {
            return;
        }
        
        VertexClass vertex = VertexClass.getVertexClassByName(framedGraph, clazz.getQualifiedName());
        if (vertex == null) {
            return;
        }
        
        if (parser.getRemovedFiles().contains(curFile)) {
            vertex.remove();
        } else if (parser.getModifiedFiles().contains(curFile)) {
            vertex.removeEdges();
        }
        
        
    }

}
