package edu.rug.pyne.api.parser.structureprocessor;

import com.syncleus.ferma.FramedGraph;
import edu.rug.pyne.api.parser.Parser;
import edu.rug.pyne.api.structure.VertexClass;
import edu.rug.pyne.api.structure.VertexPackage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import spoon.SpoonException;
import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtType;
import spoon.reflect.reference.CtTypeReference;

import java.util.regex.Pattern;

/**
 * This a structure processor. It takes the source code class and adds it as a
 * SystemClass vertex to the graph
 *
 * @author Patrick Beuks (s2288842) <code@beuks.net>
 */
public class ClassProcessor extends AbstractProcessor<CtClass<?>> {

    private final static Logger LOGGER = LogManager.getLogger(ClassProcessor.class.getName());
    
    // The graph to add the vertexes to
    private final FramedGraph framedGraph;

    // The parser containing additional information
    private final Parser parser;

    /**
     * This class processor implements a spoon processor to find source code
     * classes
     * 
     * @param parser The parser to use
     * @param framedGraph The graph to add the classes to
     */
    public ClassProcessor(Parser parser, FramedGraph framedGraph) {
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
        try {
            // Check if it only needs to parse added files.
            // If null process all given classes
            if (parser.getAddedFiles() != null) {
                // Check if the current class is in an added file.
                // If not we do not have to process it.
                if (!parser.getAddedFiles().contains(
                        clazz.getPosition().getFile()
                )) {
                    return;
                }
            }

            // Try to get the vertex by name
            VertexClass vertex = VertexClass.getVertexClassByName(
                    framedGraph, clazz.getQualifiedName()
            );

            // Check if the class exists, if not create it.
            if (vertex == null) {
                vertex = VertexClass.createSystemClass(framedGraph, clazz);
            }

            // Check if the vertex has a belonging package, if not add it.
            if (vertex.getBelongsToPackage() == null) {

                CtTypeReference cur = clazz.getReference();
                while (cur.getPackage() == null) {
                    cur = cur.getDeclaringType();
                }
                // Try to get the package by name
                VertexPackage packageVertex = VertexPackage.getVertexPackageByName(
                        framedGraph, cur.getPackage().getQualifiedName()
                );

                // Check if the package exists, if not create it.
                if (packageVertex == null) {
                    packageVertex = VertexPackage.createVertexPackage(
                            framedGraph, cur.getPackage()
                    );
                }

                vertex.setBelongsTo(packageVertex);
                vertex.setLinesOfCode(countLOC(clazz));
            }
        }catch (Exception e){
            LOGGER.error("Spoon error while analysing class " + clazz.getQualifiedName() + ": " + e.getMessage());
        }

    }

    private Pattern linePattern = Pattern.compile("[^\\s*].*[\\n\\r]+");
    private long countLOC(CtType<?> clazz){
        var linesOfCode = 0;
        try {
            var sourceCode = clazz.toString();

            var matcher = linePattern.matcher(sourceCode);

            while (matcher.find())
                linesOfCode++;
        }catch (SpoonException e){
            LOGGER.warn("Spoon could not fetch class " + clazz.getQualifiedName() + ", 0 LOC assigned.");
        }
        return linesOfCode;
    }

}
