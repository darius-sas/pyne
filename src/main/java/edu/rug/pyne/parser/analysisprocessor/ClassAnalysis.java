package edu.rug.pyne.parser.analysisprocessor;

import com.syncleus.ferma.FramedGraph;
import edu.rug.pyne.parser.structureprocessor.ClassProcessor;
import edu.rug.pyne.structure.EdgeDependsOn;
import edu.rug.pyne.structure.VertexClass;
import edu.rug.pyne.structure.VertexPackage;
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
    
    private static int cur = 0;

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

    private final FramedGraph framedGraph;

    public ClassAnalysis(FramedGraph framedGraph) {
        this.framedGraph = framedGraph;
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
        System.out.println("Class " + ++cur + " of " + ClassProcessor.TOTAL);
        processClassDependencies(clazz, vertex);
        processClassReferences(clazz, vertex);

    }

    private void processClassDependencies(CtType clazz, VertexClass vertexClass) {
        if (clazz.getSuperclass() != null) {
            VertexClass superClass = getOrCreateVertexClass(clazz.getSuperclass());
            vertexClass.addChildOfClass(superClass);
            createCoupling(vertexClass, superClass);
        }

        for (CtTypeReference<?> superInterface : clazz.getSuperInterfaces()) {
            VertexClass superInterfaceClass = getOrCreateVertexClass(superInterface);
            vertexClass.addImplematationOfClass(superInterfaceClass);
            createCoupling(vertexClass, superInterfaceClass);
        }
    }

    private void processClassReferences(CtType clazz, VertexClass vertexClass) {

        for (CtType referencedclass : getClassReferences(clazz)) {
            if (referencedclass == null) {
                continue;
            }

            VertexClass referencedclassVertex = getOrCreateVertexClass(referencedclass.getReference());

            if (!referencedclass.getQualifiedName().equals(vertexClass.getName())) {

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

            createCoupling(vertexClass, referencedclassVertex);

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

    private void createCoupling(VertexClass vertexClass, VertexClass coupleVertex) {

        if (vertexClass.getBelongsToPackage().equals(coupleVertex.getBelongsToPackage())) {
            return;
        }

        if (!vertexClass.getAfferentOfPackages().contains(coupleVertex.getBelongsToPackage())) {
            vertexClass.addAfferentOf(coupleVertex.getBelongsToPackage());
        }

        if (!coupleVertex.getEfferentOfPackages().contains(vertexClass.getBelongsToPackage())) {
            coupleVertex.addEfferentOf(vertexClass.getBelongsToPackage());
        }

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
