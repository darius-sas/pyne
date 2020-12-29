package edu.rug.pyne.api.parser;

import spoon.Launcher;
import spoon.SpoonModelBuilder;
import spoon.reflect.factory.Factory;

public class ArcanSpoonLauncher extends Launcher {

    @Override
    public SpoonModelBuilder getCompilerInstance(Factory factory) {
        return new ArcanJDTBasedSpoonCompiler(factory);
    }

}
