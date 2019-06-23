package edu.rug.pyne.api.parser.removeprocessor;

import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.CtInterface;

/**
 * This a remove processor. It takes the source code interface and it removes
 * it when needed.
 *
 * Since this is the same as processing a class, a class processor is given to
 * do the actual processing.
 *
 * This is because in java you cannot extend two different Objects.
 *
 * @author Patrick Beuks (s2288842) <code@beuks.net>
 */
public class InterfaceRemover extends AbstractProcessor<CtInterface<?>> {

    // The class processor to do the processing
    private final ClassRemover remover;

    /**
     * This interface processor implements a spoon processor to remove source
     * code interfaces when needed.
     *
     * @param classRemover The class processor to use
     */
    public InterfaceRemover(
            ClassRemover classRemover
    ) {
        remover = classRemover;
    }


    /**
     * Processes a single source code interface
     *
     * @param clazz The interface to process
     */
    @Override
    public void process(CtInterface<?> clazz) {
        remover.removeClass(clazz);
    }

}
