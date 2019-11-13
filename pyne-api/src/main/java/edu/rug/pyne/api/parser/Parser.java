package edu.rug.pyne.api.parser;

import com.syncleus.ferma.DelegatingFramedGraph;
import com.syncleus.ferma.FramedGraph;
import edu.rug.pyne.api.GitHelper;
import edu.rug.pyne.api.parser.analysisprocessor.ClassAnalysis;
import edu.rug.pyne.api.parser.analysisprocessor.ClassPostProcess;
import edu.rug.pyne.api.parser.analysisprocessor.InterfaceAnalysis;
import edu.rug.pyne.api.parser.removeprocessor.ClassRemovePostProcess;
import edu.rug.pyne.api.parser.removeprocessor.ClassRemover;
import edu.rug.pyne.api.parser.removeprocessor.InterfaceRemover;
import edu.rug.pyne.api.parser.structureprocessor.ClassProcessor;
import edu.rug.pyne.api.parser.structureprocessor.InterfaceProcessor;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.io.File;
import java.util.stream.Collectors;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.tinkerpop.gremlin.structure.Graph;
import spoon.Launcher;
import spoon.SpoonModelBuilder;
import spoon.processing.Processor;
import spoon.reflect.declaration.CtElement;

/**
 * This class is for parsing source code. It is used to contain the processors
 * as well as the files that have been modified.
 *
 * @author Patrick Beuks (s2288842) <code@beuks.net>
 */
public class Parser {

    // The framed graph the parsing occures on
    private final FramedGraph framedGraph;

    private static final Logger LOGGER
            = LogManager.getLogger(Parser.class);

    // A list of input paths that contain the source code files
    private final List<String> inputList = new ArrayList<>();

    // The root directory of the project. Does not need to contain the source 
    // code files themself 
    private File rootDirectory;

    // The processors to use
    private final List<Processor<? extends CtElement>> structureProcessors
            = new ArrayList<>();
    private final List<Processor<? extends CtElement>> analysisProcessors
            = new ArrayList<>();
    private final List<PostProcess> analysisPostProcessors
            = new ArrayList<>();
    private final List<Processor<? extends CtElement>> removeProcessors
            = new ArrayList<>();
    private final List<PostProcess> removePostProcessors
            = new ArrayList<>();

    // The files to work on, null if all files
    private List<File> addedFiles;
    private List<File> modifiedFiles;
    private List<File> removedFiles;

    /**
     * Creates a parser that contains all relevant information to be able to
     * process java source code file
     *
     * @param graph The graph to apply the parsing on.
     */
    public Parser(Graph graph) {
        this(new DelegatingFramedGraph<Graph>(graph, true, true));
    }

    /**
     * Creates a parser that contains all relevant information to be able to
     * process java source code file
     *
     * @param framedGraph The graph to apply the parsing on
     */
    public Parser(FramedGraph framedGraph) {
        this.framedGraph = framedGraph;
    }

    /**
     * Gets the given or created FramedGraph.
     *
     * @return The framed graph
     */
    public FramedGraph getFramedGraph() {
        return framedGraph;
    }

    /**
     * Gets the given graph or implied graph
     *
     * @return The graph
     */
    public Graph getGraph() {
        return framedGraph.getRawTraversal().getGraph();
    }

    /**
     * Adds a processor that handels removed classes.
     *
     * @param processor The processor to add
     * @return true if this collection changed as a result of the call.
     */
    public boolean addRemoveProcessor(
            Processor<? extends CtElement> processor
    ) {
        return this.removeProcessors.add(processor);
    }

    /**
     * Adds the remove processors that by default come with this library.
     */
    public void addDefaultRemoveProcessors() {
        ClassRemover classRemover = new ClassRemover(this, framedGraph);
        InterfaceRemover interfaceRemover = new InterfaceRemover(classRemover);

        addRemoveProcessor(classRemover);
        addRemoveProcessor(interfaceRemover);
    }

    /**
     * Adds a processor that handels added or modified classes to set up the
     * structure.
     *
     * @param processor The processor to add
     * @return true if this collection changed as a result of the call.
     */
    public boolean addStructureProcessor(
            Processor<? extends CtElement> processor
    ) {
        return this.structureProcessors.add(processor);
    }

    /**
     * Adds the structure processors that by default come with this library.
     */
    public void addDefaultStructureProcessors() {
        ClassProcessor classProcessor = new ClassProcessor(this, framedGraph);
        InterfaceProcessor interfaceProcessor
                = new InterfaceProcessor(classProcessor);

        addStructureProcessor(classProcessor);
        addStructureProcessor(interfaceProcessor);
    }

    /**
     * Adds a processor that handels added or modified classes to analyze them
     *
     * @param processor The processor to add
     * @return true if this collection changed as a result of the call.
     */
    public boolean addAnalysisProcessor(
            Processor<? extends CtElement> processor
    ) {
        return this.analysisProcessors.add(processor);
    }

    /**
     * Adds the analysis processors that by default come with this library.
     */
    public void addDefaultAnalysisProcessors() {
        ClassAnalysis classAnalysis = new ClassAnalysis(this, framedGraph);
        InterfaceAnalysis interfaceAnalysis
                = new InterfaceAnalysis(classAnalysis);

        addAnalysisProcessor(classAnalysis);
        addAnalysisProcessor(interfaceAnalysis);
    }

    /**
     * Adds a post processor for the analysis step.
     *
     * @param processor The processor to add
     * @return true if this collection changed as a result of the call.
     */
    public boolean addAnalysisPostProcessor(PostProcess processor) {
        return this.analysisPostProcessors.add(processor);
    }

    /**
     * Adds the analysis post processors that by default come with this library.
     */
    public void addDefaultAnalysisPostProcessors() {
        addAnalysisPostProcessor(new ClassPostProcess());
    }

    /**
     * Adds a post processor for the remove step.
     *
     * @param processor The processor to add
     * @return true if this collection changed as a result of the call.
     */
    public boolean addRemovePostProcessor(PostProcess processor) {
        return this.removePostProcessors.add(processor);
    }

    /**
     * Adds the remove post processors that by default come with this library.
     */
    public void addDefaultRemovePostProcessors() {
        addRemovePostProcessor(new ClassRemovePostProcess());
    }

    /**
     * Adds an path to an input directory with source files
     *
     * @param path The path to add
     * @return true if this collection changed as a result of the call.
     */
    public boolean addInputDirectory(String path) {
        return inputList.add(path);
    }

    /**
     * Sets the root directory to be used by the parser
     *
     * @param rootDirectory The root directory to use
     */
    public void setRootDirectory(File rootDirectory) {
        this.rootDirectory = rootDirectory;
    }

    /**
     * Sets the added files. Only classes inside should be parsed. This behavior
     * is done inside the processors themselves, so actual behavior might
     * differ.
     *
     * Set to null to parse all the files.
     *
     * @param addedFiles The files to parse.
     */
    public void setAddedFiles(List<File> addedFiles) {
        this.addedFiles = addedFiles;
    }

    /**
     * Gets the list of added files
     *
     * @return The added files
     */
    public List<File> getAddedFiles() {
        return addedFiles;
    }

    /**
     * Sets the removed files. Only classes inside should be remove. This
     * behavior is done inside the processors themselves, so actual behavior
     * might differ.
     *
     * Set to null to not do the remove step.
     *
     * @param removedFiles The files to remove.
     */
    public void setRemovedFiles(List<File> removedFiles) {
        this.removedFiles = removedFiles;
    }

    /**
     * Gets the list of removed files
     *
     * @return The removed files
     */
    public List<File> getRemovedFiles() {
        return removedFiles;
    }

    /**
     * Sets the modified files. Only classes inside should be parsed. This
     * behavior is done inside the processors themselves, so actual behavior
     * might differ.
     *
     * @param modifiedFiles The files that have been modified.
     */
    public void setModifiedFiles(List<File> modifiedFiles) {
        this.modifiedFiles = modifiedFiles;
    }

    /**
     * Gets the list of modified files
     *
     * @return The modified files
     */
    public List<File> getModifiedFiles() {
        return modifiedFiles;
    }

    private Launcher getLauncher(){
        var launcher = new Launcher();
        launcher.getEnvironment().setIgnoreDuplicateDeclarations(true);
        launcher.getEnvironment().setCommentEnabled(false);
        launcher.getEnvironment().setNoClasspath(true);
        launcher.getEnvironment().setComplianceLevel(9);
        findSourceDirectories().forEach(f -> {
            launcher.addInputResource(f.getAbsolutePath());
            LOGGER.info("Added directory to input resource: " + f.getAbsolutePath());
        });
        return launcher;
    }

    /**
     * Processes the removed files. If removedFiles is set to null then this not
     * execute anything.
     *
     * After the remove process the post remove processors are used.
     *
     * If no remove processors or remove post processors have been defined the
     * default ones will be used.
     */
    public void processRemoved() {
        if (removedFiles == null) {
            return;
        }
        if (removeProcessors.isEmpty()) {
            addDefaultRemoveProcessors();
        }
        if (removePostProcessors.isEmpty()) {
            addDefaultRemovePostProcessors();
        }

        Launcher launcher = getLauncher();
        launcher.buildModel();
        launcher.getModel();
        SpoonModelBuilder modelBuilder = launcher.getModelBuilder();

        launcher.process();
        modelBuilder.process(removeProcessors);

        for (PostProcess removePostProcessor : removePostProcessors) {
            removePostProcessor.postProcess(framedGraph);
        }

    }

    /**
     * Processes the files. It first executes the structure step and then the
     * analysis step.
     *
     * After the analysis process step the post analysis processors are used.
     *
     * If no structure processors, analysis processors or analysis post
     * processors have been defined the default ones will be used.
     */
    public void process() {
        if (structureProcessors.isEmpty()) {
            addDefaultStructureProcessors();
        }
        if (analysisProcessors.isEmpty()) {
            addDefaultAnalysisProcessors();
        }
        if (analysisPostProcessors.isEmpty()) {
            addDefaultAnalysisPostProcessors();
        }

        Launcher launcher = getLauncher();

        launcher.buildModel();
        launcher.getModel();
        SpoonModelBuilder modelBuilder = launcher.getModelBuilder();

        launcher.process();
        modelBuilder.process(structureProcessors);

        launcher.process();
        modelBuilder.process(analysisProcessors);

        for (PostProcess analysisPostProcessor : analysisPostProcessors) {
            analysisPostProcessor.postProcess(framedGraph);
        }

    }

    public Set<File> findSourceDirectories() {
        var propsFile = Paths.get(rootDirectory.getAbsolutePath(), "sources.properties");
        if (propsFile.toFile().exists()){
            LOGGER.info("Using sources.properties file to read input sources.");
            var srcDirs = getFromPropertiesFile(propsFile.toFile());
            if (srcDirs.isEmpty()){
                LOGGER.warn("Could not find any directory from source.properties file. Falling back to recursive src dir.");
            }else {
                return srcDirs;
            }
        }
        var searchStartDir = rootDirectory.toPath();
        Set<File> sourceDirs = new HashSet<>();
        var testKeyword = File.separator + "test" + File.separator;
        var exampleKeyword = "example";
        try(var stream = Files.walk(searchStartDir)){
            Set<Path> srcPaths = stream.map(Path::toFile)
                    .filter(File::isDirectory)
                    .filter(f -> f.toPath().endsWith("src/main") || f.toPath().endsWith("src/java") )
                    .filter(f -> !f.getAbsolutePath().toLowerCase().contains(testKeyword))
                    .filter(f -> !f.getAbsolutePath().toLowerCase().contains(exampleKeyword))
                    .map(File::toPath)
                    .collect(Collectors.toSet());
            for (var p1 : srcPaths){
                var addToSource = true;
                for (var p2 : srcPaths){
                    if(p1 != p2 && p1.startsWith(p2)){
                        addToSource = false;
                    }
                }
                if (addToSource) {
                    sourceDirs.add(p1.toFile());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return sourceDirs;
    }

    public Set<File> getFromPropertiesFile(File propsFile){
        Properties props = new Properties();
        try(var fis = new FileInputStream(propsFile)){
            props.load(fis);
        }catch (IOException e){
            LOGGER.error("Could not read from sources properties file " + propsFile.getAbsolutePath());
        }
        String include = props.getProperty("sources.include", "src");
        String exclude = props.getProperty("sources.exclude", "test");

        var includeList = List.of(include.split(File.pathSeparator));
        var excludeList = List.of(exclude.split(File.pathSeparator));

        var srcDirs = new HashSet<File>();

        for (var inputDir : includeList){
            var file = Paths.get(rootDirectory.getAbsolutePath(), inputDir).toFile();
            if (file.exists() && file.isDirectory()){
                srcDirs.add(file);
            }
        }
        return srcDirs;
    }

}
