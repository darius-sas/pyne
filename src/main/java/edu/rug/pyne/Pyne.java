package edu.rug.pyne;

import com.syncleus.ferma.DelegatingFramedGraph;
import com.syncleus.ferma.FramedGraph;
import edu.rug.pyne.parser.Parser;
import edu.rug.pyne.structure.VertexClass;
import edu.rug.pyne.structure.VertexMethod;
import edu.rug.pyne.structure.VertexPackage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.eclipse.jgit.api.errors.GitAPIException;

/**
 *
 * @author Patrick Beuks (s2288842) <code@beuks.net>
 */
public class Pyne {

    public static void main(String[] args) {
        new Pyne();
    }

    public Pyne() {
        runParser();
    }

    private void runParser() {
        GitHelper gitHelper = null;
        try {
            FramedGraph framedGraph = new DelegatingFramedGraph(TinkerGraph.open(), true, true);
            Parser parser = new Parser(framedGraph);
            
            parser.addInputDirectory("XPStorage\\src\\main\\java");
            
            parser.addDefaultStructureProcessors();
            parser.addDefaultRemoveProcessors();
            parser.addDefaultAnalysisProcessors();
            parser.addDefaultAnalysisPostProcessors();
            parser.addDefaultRemovePostProcessors();
            
            gitHelper = new GitHelper(new File("D:\\@netbeansProjects\\XPStorage").toURI());
            gitHelper.parseCommit(parser, "759c76a2a99497a74c09c89c07d16cd46f4860ed");
            
            
            List<String> edgeLabels = new ArrayList<>();
            
            edgeLabels.add("belongsTo");
            edgeLabels.add("isAfferentOf");
            edgeLabels.add("isEfferentOf");
            edgeLabels.add("dependsOn");
            edgeLabels.add("isChildOf");
            edgeLabels.add("isImplementationOf");
            
            Map<String, Long> old = new HashMap<>();
            List<String> oldClasses = framedGraph.traverse((g) -> g.V().hasLabel(VertexClass.LABEL)).toList(VertexClass.class).stream().map((t) -> t.getName()).collect(Collectors.toList());
            
            for (String edgeLabel : edgeLabels) {
                old.put(edgeLabel, framedGraph.getRawTraversal().V().hasLabel("class").outE(edgeLabel).count().next());
            }
            
            
            System.out.println("***Graph created from compiled file*** - graph:" + framedGraph.getRawTraversal().getGraph());
            
            gitHelper.parseCommit(parser, "6611b9fe676b8e4f9627156fba86caab42d211d7");
            
            List<? extends VertexPackage> packages = framedGraph.traverse((g) -> g.V().hasLabel("package")).toList(VertexPackage.class);
            List<? extends VertexClass> classes = framedGraph.traverse((g) -> g.V().hasLabel("class")).toList(VertexClass.class);
            List<? extends VertexMethod> methods = framedGraph.traverse((g) -> g.V().hasLabel("method")).toList(VertexMethod.class);
            
            System.out.println("***Graph created from compiled file*** - graph:" + framedGraph.getRawTraversal().getGraph());
            gitHelper.parseCommit(parser, "759c76a2a99497a74c09c89c07d16cd46f4860ed");
            System.out.println("***Graph created from compiled file*** - graph:" + framedGraph.getRawTraversal().getGraph());
            
            Map<String, Long> newM = new HashMap<>();
            List<String> newClasses = framedGraph.traverse((g) -> g.V().hasLabel(VertexClass.LABEL)).toList(VertexClass.class).stream().map((t) -> t.getName()).collect(Collectors.toList());
            
            for (String edgeLabel : edgeLabels) {
                newM.put(edgeLabel, framedGraph.getRawTraversal().V().hasLabel("class").outE(edgeLabel).count().next());
            }
            
            for (Map.Entry<String, Long> entry : old.entrySet()) {
                String key = entry.getKey();
                Long value = entry.getValue();
                Long newValue = newM.get(key);
                System.out.println(key + ": " + value + "|" + newValue);
            }
            newClasses.removeAll(oldClasses);
            for (String string : newClasses) {
                System.out.println("NEW: " + string);
                VertexClass v = VertexClass.getVertexClassByName(framedGraph, string);
                System.out.println(v);
                System.out.println(v.getAfferentOfEdges().size() + ":" + v.getEfferentOfEdges().size() + ":" + v.getChildsOfEdges().size() + ":" + v.getDependOnEdges().size() + ":" + v.getImplementationOfEdges().size());
            }
            
            //System.out.println("Packages: ");
            for (VertexPackage aPackage : packages) {
                //System.out.println(aPackage);
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
        } catch (IOException | GitAPIException ex) {
            Logger.getLogger(Pyne.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (gitHelper != null) {
                gitHelper.cleanUp();
            }
        }
    }

}
