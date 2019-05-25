package edu.rug.pyne.parser.structureprocessor;

import com.syncleus.ferma.FramedGraph;
import edu.rug.pyne.structure.VertexClass;
import edu.rug.pyne.structure.VertexPackage;
import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtType;
import spoon.reflect.reference.CtTypeReference;

/**
 *
 * @author Patrick Beuks (s2288842) <code@beuks.net>
 */
public class ClassProcessor extends AbstractProcessor<CtClass<?>> {

    private final FramedGraph framedGraph;
    public static int TOTAL = 0;

    public ClassProcessor(FramedGraph framedGraph) {
        this.framedGraph = framedGraph;
    }

    @Override
    public void process(CtClass<?> clazz) {
        this.processClass(clazz);
    }

    public void processClass(CtType<?> clazz) {
        
        TOTAL++;
        VertexClass vertex = VertexClass.getVertexClassByName(framedGraph, clazz.getQualifiedName());
        if (vertex == null) {
            
            VertexClass vertexClass = VertexClass.createSystemClass(framedGraph, clazz);
            
            CtTypeReference cur = clazz.getReference();
            while(cur.getPackage() == null) {
                cur = cur.getDeclaringType();
            }
            VertexPackage packageVertex = VertexPackage.getVertexPackageByName(framedGraph, cur.getPackage().getQualifiedName());
            if (packageVertex == null) {
                packageVertex = VertexPackage.createVertexPackage(framedGraph, cur.getPackage());
            }
            vertexClass.setBelongsTo(packageVertex);
        }
        
    }

}
