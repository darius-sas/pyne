package edu.rug.pyne;

import com.syncleus.ferma.DelegatingFramedGraph;
import com.syncleus.ferma.FramedGraph;
import edu.rug.pyne.parser.Parser;
import edu.rug.pyne.structure.VertexClass;
import edu.rug.pyne.structure.VertexMethod;
import edu.rug.pyne.structure.VertexPackage;
import java.util.List;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;

/**
 *
 * @author Patrick
 */
public class Pyne {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        FramedGraph framedGraph = new DelegatingFramedGraph(TinkerGraph.open(), true, true);
        Parser parser = new Parser(framedGraph);
        
        parser.addInputDirectory("D:\\@netbeansProjects\\antlr3-antlr-3.5\\tool\\src\\main\\java");
        parser.addInputDirectory("D:\\@netbeansProjects\\antlr3-antlr-3.5\\runtime\\Java");

        parser.addDefaultStructureProcessors();
        parser.addDefaultAnalysisProcessors();

        parser.process();

        List<? extends VertexPackage> packages = framedGraph.traverse((g) -> g.V().hasLabel("package")).toList(VertexPackage.class);
        List<? extends VertexClass> classes = framedGraph.traverse((g) -> g.V().hasLabel("class")).toList(VertexClass.class);
        List<? extends VertexMethod> methods = framedGraph.traverse((g) -> g.V().hasLabel("method")).toList(VertexMethod.class);

        
        System.out.println("***Graph created from compiled file*** - graph:" + framedGraph.getRawTraversal().getGraph());
        
        
        
        //System.out.println("Packages: ");
        for (VertexPackage aPackage : packages) {
            System.out.println(aPackage);
        }
        //System.out.println("Classes: ");
        for (VertexClass aClass : classes) {
            //System.out.println(aClass.getName());
            //System.out.println(aClass);
            //System.out.println(aClass.getAfferentOfEdges().size());
            //System.out.println(aClass.getEfferentOfEdges().size());
            //System.out.println(aClass.getDependOnEdges().size());
            //System.out.println(aClass.getDependingEdges().size());
            //System.out.println(aClass.getBelongsToPackage().getName());
        }
        //System.out.println("Methods: ");
        for (VertexMethod aMethod : methods) {
            //System.out.println(aMethod);
        }
        
    }
    
}
