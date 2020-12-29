package edu.rug.pyne.api.parser;

import spoon.compiler.Environment;
import spoon.reflect.CtModel;
import spoon.reflect.factory.CoreFactory;
import spoon.reflect.factory.FactoryImpl;

public class ArcanSpoonFactory extends FactoryImpl {
        private CtModel model;

    public ArcanSpoonFactory(CoreFactory coreFactory, Environment environment) {
        super(coreFactory, environment);
        // neeed to override getModule() to get a module that has a modified
        // CtModule.getUnnamedModule()
        this.model = new ArcanCtModelImp(this);
    }

}
