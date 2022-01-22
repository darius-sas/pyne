package edu.rug.pyne.api.parser;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import spoon.reflect.factory.Factory;
import spoon.support.compiler.jdt.JDTBasedSpoonCompiler;
import spoon.support.compiler.jdt.JDTTreeBuilder;

public class ArcanJDTBasedSpoonCompiler extends JDTBasedSpoonCompiler {

    private final static Logger logger = LogManager.getLogger();

    public ArcanJDTBasedSpoonCompiler(Factory factory) {
        super(factory);
    }

    @Override
    protected void traverseUnitDeclaration(JDTTreeBuilder builder, CompilationUnitDeclaration unitDeclaration) {
        try {
            logger.trace("Spoon is parsing file {}", String.valueOf(unitDeclaration.getFileName()));
            super.traverseUnitDeclaration(builder, unitDeclaration);
        }catch (Exception | Error e){
            logger.warn("Failed to parse file {}: {}", String.valueOf(unitDeclaration.getFileName()), e.getMessage());
        }
    }
}
