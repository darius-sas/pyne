package edu.rug.pyne.api.parser.analysisprocessor;

import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.CtInterface;

/**
 * This a analysis processor. It takes the source code interface and it analyses
 * it.
 *
 * Since this is the same as processing a class, a class processor is given to
 * do the actual processing.
 *
 * This is because in java you cannot extend two different Objects.
 *
 * @author Patrick Beuks (s2288842) <code@beuks.net>
 */
public class InterfaceAnalysis extends AbstractProcessor<CtInterface<?>> {

    // The class processor to do the processing
    private final ClassAnalysis analyser;

    /**
     * This interface processor implements a spoon processor to analyze source
     * code interfaces
     *
     * @param analyser The class processor to use
     */
    public InterfaceAnalysis(ClassAnalysis analyser) {
        this.analyser = analyser;
    }

    /**
     * Processes a single source code interface
     *
     * @param clazz The interface to process
     */
    @Override
    public void process(CtInterface<?> clazz) {
        analyser.processClass(clazz);
    }

}
