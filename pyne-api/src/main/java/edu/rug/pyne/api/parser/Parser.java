package edu.rug.pyne.api.parser;

import com.syncleus.ferma.DelegatingFramedGraph;
import com.syncleus.ferma.FramedGraph;
import edu.rug.pyne.api.parser.analysisprocessor.ClassAnalysis;
import edu.rug.pyne.api.parser.analysisprocessor.ClassPostProcess;
import edu.rug.pyne.api.parser.analysisprocessor.InterfaceAnalysis;
import edu.rug.pyne.api.parser.removeprocessor.ClassRemovePostProcess;
import edu.rug.pyne.api.parser.removeprocessor.ClassRemover;
import edu.rug.pyne.api.parser.removeprocessor.InterfaceRemover;
import edu.rug.pyne.api.parser.structureprocessor.ClassProcessor;
import edu.rug.pyne.api.parser.structureprocessor.InterfaceProcessor;
import edu.rug.pyne.api.structure.EdgeIsAfferentOf;
import edu.rug.pyne.api.structure.EdgeIsEfferentOf;
import edu.rug.pyne.api.structure.VertexClass;
import edu.rug.pyne.api.structure.VertexPackage;
import java.util.ArrayList;
import java.util.List;
import java.io.File;
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
    private File rootDirectory;
    private final List<Processor<? extends CtElement>> structureProcessors = new ArrayList<>();
    private final List<Processor<? extends CtElement>> analysisProcessors = new ArrayList<>();
    private final List<PostProcess> analysisPostProcessors = new ArrayList<>();
    private final List<Processor<? extends CtElement>> removeProcessors = new ArrayList<>();
    private final List<PostProcess> removePostProcessors = new ArrayList<>();
    private List<File> addedFiles;
    private List<File> modifiedFiles;
    private List<File> removedFiles;

    public Parser(Graph graph) {
        this(new DelegatingFramedGraph<Graph>(graph, true, true));
    }

    public Parser(FramedGraph framedGraph) {
        this.framedGraph = framedGraph;
    }

    public boolean addRemoveProcessor(Processor<? extends CtElement> processor) {
        return this.removeProcessors.add(processor);
    }

    public FramedGraph getFramedGraph() {
        return framedGraph;
    }
    
    public Graph getGraph() {
        return framedGraph.getRawTraversal().getGraph();
    }

    public void addDefaultRemoveProcessors() {
        ClassRemover classRemover = new ClassRemover(this, framedGraph);
        InterfaceRemover interfaceRemover = new InterfaceRemover(framedGraph, classRemover);

        addRemoveProcessor(classRemover);
        addRemoveProcessor(interfaceRemover);
    }

    public boolean addStructureProcessor(Processor<? extends CtElement> processor) {
        return this.structureProcessors.add(processor);
    }

    public void addDefaultStructureProcessors() {
        ClassProcessor classProcessor = new ClassProcessor(this, framedGraph);
        InterfaceProcessor interfaceProcessor = new InterfaceProcessor(framedGraph, classProcessor);

        addStructureProcessor(classProcessor);
        addStructureProcessor(interfaceProcessor);
    }

    public boolean addAnalysisProcessor(Processor<? extends CtElement> processor) {
        return this.analysisProcessors.add(processor);
    }

    public void addDefaultAnalysisProcessors() {
        ClassAnalysis classAnalysis = new ClassAnalysis(this, framedGraph);
        InterfaceAnalysis interfaceAnalysis = new InterfaceAnalysis(framedGraph, classAnalysis);

        addAnalysisProcessor(classAnalysis);
        addAnalysisProcessor(interfaceAnalysis);
    }

    public boolean addAnalysisPostProcessor(PostProcess processor) {
        return this.analysisPostProcessors.add(processor);
    }

    public void addDefaultAnalysisPostProcessors() {
        addAnalysisPostProcessor(new ClassPostProcess());
    }

    public boolean addRemovePostProcessor(PostProcess processor) {
        return this.removePostProcessors.add(processor);
    }

    public void addDefaultRemovePostProcessors() {
        addRemovePostProcessor(new ClassRemovePostProcess());
    }

    public boolean addInputDirectory(String path) {
        return inputList.add(path);
    }

    public void setRootDirectory(File rootDirectory) {
        this.rootDirectory = rootDirectory;
    }
    
    public void setAddedFiles(List<File> addedFiles) {
        this.addedFiles = addedFiles;
    }

    public List<File> getAddedFiles() {
        return addedFiles;
    }
    
    public void setRemovedFiles(List<File> removedFiles) {
        this.removedFiles = removedFiles;
    }

    public List<File> getRemovedFiles() {
        return removedFiles;
    }
    
    public void setModifiedFiles(List<File> modifiedFiles) {
        this.modifiedFiles = modifiedFiles;
    }

    public List<File> getModifiedFiles() {
        return modifiedFiles;
    }
    
    public void processRemoved() {
        if (removedFiles == null) {
            return;
        }
        Launcher launcher = new Launcher();
        
        for (String string : inputList) {
            launcher.addInputResource(new File(rootDirectory, string).getAbsolutePath());
        }
        launcher.buildModel();
        launcher.getModel();
        SpoonModelBuilder modelBuilder = launcher.getModelBuilder();
        
        launcher.process();
        modelBuilder.process(removeProcessors);
        
        for (PostProcess removePostProcessor : removePostProcessors) {
            removePostProcessor.postProcess(framedGraph);
        }
        
    }

    public void process() {
        Launcher launcher = new Launcher();
        for (String string : inputList) {
            launcher.addInputResource(new File(rootDirectory, string).getAbsolutePath());
        }
        launcher.buildModel();
        launcher.getModel();
        SpoonModelBuilder modelBuilder = launcher.getModelBuilder();
        
        ClassProcessor.TOTAL = 0;
        
        launcher.process();
        modelBuilder.process(structureProcessors);
        
        ClassAnalysis.CUR = 0;
        launcher.process();
        modelBuilder.process(analysisProcessors);
        
        for (PostProcess analysisPostProcessor : analysisPostProcessors) {
            analysisPostProcessor.postProcess(framedGraph);
        }
        
    }

}
