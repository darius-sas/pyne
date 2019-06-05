package edu.rug.pyne.api.parser.analysisprocessor;

import com.syncleus.ferma.FramedGraph;
import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.CtInterface;

/**
 *
 * @author Patrick Beuks (s2288842) <code@beuks.net>
 */
public class InterfaceAnalysis extends AbstractProcessor<CtInterface<?>> {

    private final ClassAnalysis analyser;

    public InterfaceAnalysis(FramedGraph framedGraph, ClassAnalysis analyser) {
        this.analyser = analyser;
    }

    @Override
    public void process(CtInterface<?> clazz) {
        analyser.processClass(clazz);
    }
    
}
