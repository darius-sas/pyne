package edu.rug.pyne.api.parser.analysisprocessor;

import com.syncleus.ferma.FramedGraph;
import edu.rug.pyne.api.parser.Parser;
import edu.rug.pyne.api.parser.structureprocessor.ClassProcessor;
import edu.rug.pyne.api.structure.VertexClass;
import edu.rug.pyne.api.structure.VertexPackage;
import java.io.File;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import spoon.processing.AbstractProcessor;
import spoon.reflect.code.BinaryOperatorKind;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtConstructorCall;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtType;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.filter.TypeFilter;

/**
 * This a analysis processor. It takes the source code class and analyzes the
 * dependencies it has.
 *
 * @author Patrick Beuks (s2288842) <code@beuks.net>
 */
public class ClassAnalysis extends AbstractProcessor<CtClass<?>> {

    // The index of the class being processed.
    public static int CUR = 0;

    // The graph with the vertex classes
    private final FramedGraph framedGraph;

    // The parser containing additional information
    private final Parser parser;

    /**
     * A consumer for annotations, used to get the type and add the declaration
     * of it
     */
    private class AnnotationConsumer
            implements Consumer<CtAnnotation<? extends Annotation>> {

        private final List<CtType> dependences;

        /**
         * A consumer for annotations, used to get the type and add the
         * declaration of it
         *
         * @param dependences The list to add the found type to
         */
        public AnnotationConsumer(List<CtType> dependences) {
            this.dependences = dependences;
        }

        @Override
        public void accept(CtAnnotation<? extends Annotation> annotation) {
            dependences.add(annotation.getAnnotationType().getDeclaration());
        }

    }

    /**
     * A consumer for elements, used to get the type and add the declaration of
     * it
     */
    private class ExecutatbleConsumer implements Consumer<CtElement> {

        private final List<CtType> dependences;

        /**
         * A consumer for elements, used to get the type and add the declaration
         * of it
         *
         * @param dependences The list to add the found type to
         */
        public ExecutatbleConsumer(List<CtType> dependences) {
            this.dependences = dependences;
        }

        @Override
        public void accept(CtElement element) {
            if (!(element instanceof CtExecutableReference<?>)) {
                return;
            }
            CtExecutableReference executable = (CtExecutableReference) element;
            if (executable.getType() != null) {
                dependences.add(executable.getType().getTypeDeclaration());
            }
        }
    }

    /**
     * This class processor implements a spoon processor to analyze source code
     * classes
     *
     * @param parser The parser to use
     * @param framedGraph The graph with the vertex classes
     */
    public ClassAnalysis(Parser parser, FramedGraph framedGraph) {
        this.framedGraph = framedGraph;
        this.parser = parser;
    }

    /**
     * Processes a single source code class
     *
     * @param clazz The class to process
     */
    @Override
    public void process(CtClass<?> clazz) {
        this.processClass(clazz);
    }

    /**
     * Processes a single source code class or interface
     *
     * @param clazz The class or interface to process
     */
    public void processClass(CtType<?> clazz) {

        VertexClass vertex = VertexClass
                .getVertexClassByName(framedGraph, clazz.getQualifiedName());

        if (vertex == null) {
            return;
        }

        // Check if added files is set, and if so only analyze those classes
        if (parser.getAddedFiles() != null) {
            File file = clazz.getPosition().getFile();
            if (!parser.getAddedFiles().contains(file)) {
                return;
            }
        }

        // Most consoles restart the line by using \r.
        // This makes it so that you do not get long lists of steps
        System.out.print("\rClass " + ++CUR + " of " + ClassProcessor.TOTAL);
        if (ClassProcessor.TOTAL == CUR) {
            System.out.print("\n");
        }

        processClassDependencies(clazz, vertex);
        processClassReferences(clazz, vertex);

    }

    /**
     * Checks if the given class has a superclass or implements interfaces and
     * if so adds the corresponding edges to the vertex.
     *
     * @param clazz The class being processed
     * @param vertexClass The corresponding vertex
     */
    private void processClassDependencies(
            CtType clazz, VertexClass vertexClass
    ) {

        if (clazz.getSuperclass() != null) {
            VertexClass superClass
                    = getOrCreateVertexClass(clazz.getSuperclass());
            vertexClass.addChildOfClass(superClass);
        }

        for (CtTypeReference<?> superInterface : clazz.getSuperInterfaces()) {
            VertexClass superInterfaceClass
                    = getOrCreateVertexClass(superInterface);
            vertexClass.addImplematationOfClass(superInterfaceClass);
        }

    }

    /**
     * Goes over all class references for the given class and adds the
     * corresponding edges.
     *
     * @param clazz The class being processed
     * @param vertexClass The corresponding vertex
     */
    private void processClassReferences(CtType clazz, VertexClass vertexClass) {

        for (CtType referencedClass : getClassReferences(clazz)) {
            if (referencedClass == null) {
                continue;
            }

            VertexClass referencedClassVertex
                    = getOrCreateVertexClass(referencedClass.getReference());

            vertexClass.addDependOnClass(referencedClassVertex);

        }

    }

    /**
     * Finds all dependencies the given class has.
     *
     * @param clazz The class being processed
     */
    private List<CtType> getClassReferences(CtType clazz) {
        List<CtType> references = new ArrayList<>();

        // Sets up the consumers that will add the references.
        AnnotationConsumer annotationConsumer
                = new AnnotationConsumer(references);
        ExecutatbleConsumer executatbleConsumer
                = new ExecutatbleConsumer(references);

        // Get all methods and loop over them
        for (CtMethod<?> ctMethod : (Set<CtMethod<?>>) clazz.getMethods()) {

            // Get binaryOperators used in the method, so we can check if they 
            // are instanceof elements and add the dependency if so.
            List<CtBinaryOperator<?>> BinaryElements = ctMethod
                    .getElements(new TypeFilter<>(CtBinaryOperator.class));

            for (CtBinaryOperator<?> element : BinaryElements) {
                if (element.getKind().equals(BinaryOperatorKind.INSTANCEOF)) {
                    references.add(element.getRightHandOperand().getType()
                            .getTypeDeclaration());
                }
            }

            // Add all references for annotations this method uses
            ctMethod.getAnnotations().forEach(annotationConsumer);
            for (CtParameter<?> parameter : ctMethod.getParameters()) {
                parameter.getAnnotations().forEach(annotationConsumer);
            }

            // Get the body if the method has one
            CtBlock<?> body = ctMethod.getBody();
            if (body == null) {
                continue;
            }

            // Get all constructors in the method
            List<CtConstructorCall<?>> constructorElements = body
                    .getElements(new TypeFilter<>(CtConstructorCall.class));

            // Get all invocations in the method
            List<CtInvocation<?>> invocationElements = body
                    .getElements(new TypeFilter<>(CtInvocation.class));

            // Add all references from the constructors
            constructorElements.forEach((constructorCall) -> {
                constructorCall.getDirectChildren()
                        .forEach(executatbleConsumer);
            });

            // Add all references from the invocations.
            invocationElements.forEach((statement) -> {
                statement.getDirectChildren().forEach(executatbleConsumer);
            });

        }

        // Get all annotations the class uses and add them
        clazz.getAnnotations().forEach(annotationConsumer);
        for (CtField<?> field : (List<CtField<?>>) clazz.getFields()) {
            field.getAnnotations().forEach(annotationConsumer);
        }

        return references;
    }

    /**
     * Gets the vertex class by the reference. If it does not exists a new
     * vertex class, with SystemType set to RetrievedClass, is created and
     * returned.
     *
     * @param clazz The class to find in the graph
     * @return The found vertex, or a newly created one if it does not exists
     */
    private VertexClass getOrCreateVertexClass(CtTypeReference clazz) {
        // Find the vertex class by name
        VertexClass vertexClass = VertexClass
                .getVertexClassByName(framedGraph, clazz.getQualifiedName());

        // If found we are done and it can be returned
        if (vertexClass != null) {
            return vertexClass;
        }

        // A new vertex class is created.
        vertexClass = VertexClass
                .createRetrievedClass(framedGraph, clazz.getQualifiedName());

        // An inner class does not have a package. So we need to go outside
        // until we find the partent class that does have a package.
        CtTypeReference cur = clazz;
        while (!cur.isPrimitive() && cur.getPackage() == null) {
            cur = cur.getDeclaringType();
        }
        
        VertexPackage packageVertex;
        // If the type is a primative (like int or byte) it does not have a
        // package, So we set it to "(default package)"
        if (cur.isPrimitive()) {
            packageVertex = VertexPackage
                    .getVertexPackageByName(framedGraph, "(default package)");
            if (packageVertex == null) {
                packageVertex = VertexPackage.createRetrievedPackage(
                        framedGraph, "(default package)"
                );
            }
        } else {
            // Get or create the package by name.
            packageVertex = VertexPackage.getVertexPackageByName(
                    framedGraph, cur.getPackage().getQualifiedName()
            );
            if (packageVertex == null) {
                packageVertex = VertexPackage.createVertexPackage(
                        framedGraph, cur.getPackage()
                );
            }
        }

        // Set the belongsTo edge.
        vertexClass.setBelongsTo(packageVertex);

        return vertexClass;
    }

}
