package edu.rug.pyne.api.parser.analysisprocessor;

import com.syncleus.ferma.FramedGraph;
import edu.rug.pyne.api.parser.Parser;
import edu.rug.pyne.api.parser.structureprocessor.ClassProcessor;
import edu.rug.pyne.api.structure.EdgeDependsOn;
import edu.rug.pyne.api.structure.VertexClass;
import edu.rug.pyne.api.structure.VertexPackage;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import spoon.processing.AbstractProcessor;
import spoon.reflect.code.BinaryOperatorKind;
import spoon.reflect.code.CtBinaryOperator;
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
 *
 * @author Patrick Beuks (s2288842) <code@beuks.net>
 */
public class ClassAnalysis extends AbstractProcessor<CtClass<?>> {
    
    public static int CUR = 0;
    private final FramedGraph framedGraph;
    private final Parser parser;

    private class AnnotationConsumer implements Consumer<CtAnnotation<? extends Annotation>> {

        private final List<CtType> dependences;

        public AnnotationConsumer(List<CtType> dependences) {
            this.dependences = dependences;
        }

        @Override
        public void accept(CtAnnotation<? extends Annotation> annotation) {
            dependences.add(annotation.getAnnotationType().getDeclaration());
        }

    }

    private class ExecutatbleConsumer implements Consumer<CtElement> {

        private final List<CtType> dependences;

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


    public ClassAnalysis(Parser parser, FramedGraph framedGraph) {
        this.framedGraph = framedGraph;
        this.parser = parser;
    }

    @Override
    public void process(CtClass<?> element) {
        this.processClass(element);
    }

    public void processClass(CtType<?> clazz) {

        
        VertexClass vertex = VertexClass.getVertexClassByName(framedGraph, clazz.getQualifiedName());
        if (vertex == null) {
            return;
        }
        
        if (parser.getAddedFiles() != null) {
            if (!parser.getAddedFiles().contains(clazz.getPosition().getFile())) {
                return;
            }
        }
        
        clazz.getPosition().getFile();
        System.out.println("Class " + ++CUR + " of " + ClassProcessor.TOTAL);
        processClassDependencies(clazz, vertex);
        processClassReferences(clazz, vertex);

    }

    private void processClassDependencies(CtType clazz, VertexClass vertexClass) {
        if (clazz.getSuperclass() != null) {
            VertexClass superClass = getOrCreateVertexClass(clazz.getSuperclass());
            vertexClass.addChildOfClass(superClass);
        }

        for (CtTypeReference<?> superInterface : clazz.getSuperInterfaces()) {
            VertexClass superInterfaceClass = getOrCreateVertexClass(superInterface);
            vertexClass.addImplematationOfClass(superInterfaceClass);
        }
    }

    private void processClassReferences(CtType clazz, VertexClass vertexClass) {

        for (CtType referencedclass : getClassReferences(clazz)) {
            if (referencedclass == null) {
                continue;
            }

            VertexClass referencedclassVertex = getOrCreateVertexClass(referencedclass.getReference());

            if (referencedclass.getQualifiedName().equals(vertexClass.getName())) {
                continue;
            }

            Optional<EdgeDependsOn> dependency = vertexClass.getDependOnEdges().stream().filter(
                    (edge) -> edge.getDependOn().equals(referencedclassVertex)
            ).findFirst();

            if (dependency.isEmpty()) {
                EdgeDependsOn dependOnEdge = vertexClass.addDependOnClass(referencedclassVertex);
                dependOnEdge.setWeight(1);
            } else {
                dependency.get().incrementWeight();
            }
            
        }
        
    }

    private List<CtType> getClassReferences(CtType clazz) {
        List<CtType> references = new ArrayList<>();
        AnnotationConsumer annotationConsumer = new AnnotationConsumer(references);
        ExecutatbleConsumer executatbleConsumer = new ExecutatbleConsumer(references);

        for (CtMethod<?> ctMethod : (Set<CtMethod<?>>) clazz.getMethods()) {

            for (CtBinaryOperator<?> element : ctMethod.getElements(new TypeFilter<CtBinaryOperator<?>>(CtBinaryOperator.class))) {
                if (element.getKind().equals(BinaryOperatorKind.INSTANCEOF)) {
                    references.add(element.getRightHandOperand().getType().getTypeDeclaration());
                }
            }

            ctMethod.getAnnotations().forEach(annotationConsumer);
            for (CtParameter<?> parameter : ctMethod.getParameters()) {
                parameter.getAnnotations().forEach(annotationConsumer);
            }

            if (ctMethod.getBody() != null) {

                for (CtConstructorCall<?> constructorCall : ctMethod.getBody().getElements(new TypeFilter<CtConstructorCall<?>>(CtConstructorCall.class))) {
                    constructorCall.getDirectChildren().forEach(executatbleConsumer);
                }

                for (CtInvocation<?> statement : ctMethod.getBody().getElements(new TypeFilter<CtInvocation<?>>(CtInvocation.class))) {
                    statement.getDirectChildren().forEach(executatbleConsumer);
                }
            }

        }

        clazz.getAnnotations().forEach(annotationConsumer);
        for (CtField<?> field : (List<CtField<?>>) clazz.getFields()) {
            field.getAnnotations().forEach(annotationConsumer);
        }

        return references;
    }

    private VertexClass getOrCreateVertexClass(CtTypeReference clazz) {
        VertexClass vertexClass = VertexClass.getVertexClassByName(framedGraph, clazz.getQualifiedName());

        if (vertexClass != null) {
            return vertexClass;
        }

        vertexClass = VertexClass.createRetrievedClass(framedGraph, clazz.getQualifiedName());

        CtTypeReference cur = clazz;
        while (!cur.isPrimitive() && cur.getPackage() == null) {
            cur = cur.getDeclaringType();
        }
        VertexPackage packageVertex;

        if (cur.isPrimitive()) {
            packageVertex = VertexPackage.getVertexPackageByName(framedGraph, "(default package)");
            if (packageVertex == null) {
                packageVertex = VertexPackage.createRetrievedPackage(framedGraph, "(default package)");
            }
        } else {
            packageVertex = VertexPackage.getVertexPackageByName(framedGraph, cur.getPackage().getQualifiedName());
            if (packageVertex == null) {
                packageVertex = VertexPackage.createVertexPackage(framedGraph, cur.getPackage());
            }
        }

        vertexClass.setBelongsTo(packageVertex);

        return vertexClass;
    }

}
