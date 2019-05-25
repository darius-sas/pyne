package edu.rug.pyne.parser.structureprocessor;

import com.syncleus.ferma.FramedGraph;
import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.CtInterface;

/**
 *
 * @author Patrick Beuks (s2288842) <code@beuks.net>
 */
public class InterfaceProcessor extends AbstractProcessor<CtInterface<?>>{

    private final ClassProcessor processor;

    public InterfaceProcessor(FramedGraph framedGraph) {
        processor = new ClassProcessor(framedGraph);
    }

    @Override
    public void process(CtInterface<?> clazz) {
        processor.processClass(clazz);
    }
    
}
