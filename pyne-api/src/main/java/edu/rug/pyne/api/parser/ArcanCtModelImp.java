package edu.rug.pyne.api.parser;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import spoon.reflect.CtModelImpl;
import spoon.reflect.declaration.CtModule;
import spoon.reflect.factory.Factory;
import spoon.reflect.factory.ModuleFactory;
import spoon.reflect.visitor.CtVisitor;

class ArcanCtModelImp extends CtModelImpl {

    private final static Logger logger = LogManager.getLogger();

    public ArcanCtModelImp(Factory f) {
        super(f);
    }

    @Override
    public CtModule getUnnamedModule() {
        return new ModuleFactory.CtUnnamedModule() {
            @Override
            public void accept(CtVisitor visitor) {
                try {
                    super.accept(visitor);
                } catch (Exception | Error e) {
                    logger.warn("Error while visiting module: {}", e.getMessage());
                }
            }
        };
    }
}
