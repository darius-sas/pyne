package edu.rug.pyne.parser;

import com.syncleus.ferma.DelegatingFramedGraph;
import com.syncleus.ferma.FramedGraph;
import edu.rug.pyne.parser.analysisprocessor.ClassAnalysis;
import edu.rug.pyne.parser.analysisprocessor.InterfaceAnalysis;
import edu.rug.pyne.parser.structureprocessor.ClassProcessor;
import edu.rug.pyne.parser.structureprocessor.InterfaceProcessor;
import java.util.ArrayList;
import java.util.List;
import org.apache.tinkerpop.gremlin.structure.Graph;
import spoon.Launcher;
import spoon.SpoonModelBuilder;
import spoon.processing.Processor;
import spoon.reflect.declaration.CtElement;

/**
 *
 * @author Patrick Beuks (s2288842) <code@beuks.net>
 */
public class Parser {

    private final FramedGraph framedGraph;
    private final List<String> inputList = new ArrayList<>();
    private final List<Processor<? extends CtElement>> structureProcessors = new ArrayList<>();
    private final List<Processor<? extends CtElement>> analysisProcessors = new ArrayList<>();
    private final Launcher launcher = new Launcher();

    public Parser(Graph graph, String project) {
        this(new DelegatingFramedGraph<Graph>(graph, true, true));
    }

    public Parser(FramedGraph framedGraph) {
        this.framedGraph = framedGraph;

    }

    public boolean addStructureProcessor(Processor<? extends CtElement> processor) {
        return this.structureProcessors.add(processor);
    }

    public void addDefaultStructureProcessors() {
        structureProcessors.add(new ClassProcessor(framedGraph));
        structureProcessors.add(new InterfaceProcessor(framedGraph));
    }

    public boolean addAnalysisProcessor(Processor<? extends CtElement> processor) {
        return this.analysisProcessors.add(processor);
    }

    public void addDefaultAnalysisProcessors() {
        analysisProcessors.add(new ClassAnalysis(framedGraph));
        analysisProcessors.add(new InterfaceAnalysis(framedGraph));
    }

    public boolean addInputDirectory(String path) {
        return inputList.add(path);
    }

    public void process() {
        for (String string : inputList) {
            launcher.addInputResource(string);
        }
        launcher.buildModel();
        launcher.getModel();
        SpoonModelBuilder modelBuilder = launcher.getModelBuilder();
        launcher.process();
        modelBuilder.process(structureProcessors);
        launcher.process();
        modelBuilder.process(analysisProcessors);

    }

}
