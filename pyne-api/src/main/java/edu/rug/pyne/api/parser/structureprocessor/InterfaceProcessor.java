package edu.rug.pyne.api.parser.structureprocessor;

import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.CtInterface;

/**
 * This a structure processor. It takes the source code interface and adds it as
 * a SystemClass vertex to the graph
 *
 * Since this is the same as processing a class, a class processor is given to
 * do the actual processing.
 *
 * This is because in java you cannot extend two different Objects.
 *
 * @author Patrick Beuks (s2288842) <code@beuks.net>
 */
public class InterfaceProcessor extends AbstractProcessor<CtInterface<?>> {

    // The class processor to do the processing
    private final ClassProcessor processor;

    /**
     * This interface processor implements a spoon processor to find source code
     * interfaces
     *
     * @param processor The class processor to use
     */
    public InterfaceProcessor(
            ClassProcessor processor
    ) {
        this.processor = processor;
    }

    /**
     * Processes a single source code interface
     * 
     * @param clazz The interface to process
     */
    @Override
    public void process(CtInterface<?> clazz) {
        processor.processClass(clazz);
    }

}
