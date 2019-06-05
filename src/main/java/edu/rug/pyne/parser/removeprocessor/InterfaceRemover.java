package edu.rug.pyne.parser.removeprocessor;

import com.syncleus.ferma.FramedGraph;
import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.CtInterface;

/**
 *
 * @author Patrick Beuks (s2288842) <code@beuks.net>
 */
public class InterfaceRemover extends AbstractProcessor<CtInterface<?>> {

    private final ClassRemover remover;

    public InterfaceRemover(FramedGraph framedGraph, ClassRemover classRemover) {
        remover = classRemover;
    }

    @Override
    public void process(CtInterface<?> clazz) {
        remover.removeClass(clazz);
    }

}
