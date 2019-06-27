package edu.rug.pyne.demo;

import edu.rug.pyne.api.GitHelper;
import edu.rug.pyne.api.parser.Parser;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.tinkerpop.gremlin.process.traversal.IO;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.eclipse.jgit.api.errors.GitAPIException;

/**
 *
 * @author Patrick Beuks (s2288842) <code@beuks.net>
 */
public class DemoGit implements Runnable {

    /**
     * The GitHelper to use for parsing
     */
    private final GitHelper gitHelper;

    /**
     * Directory to put the graphs in
     */
    private final File outputDirectory;
    
    /**
     * Creates a demo showing off how git works
     * 
     * @param repo The repo to clone from
     * @param outputDirectory The location to put output graphs in
     * @throws IOException
     * @throws GitAPIException 
     */
    public DemoGit(String repo, File outputDirectory) 
            throws IOException, GitAPIException {
        gitHelper = new GitHelper(repo);
        this.outputDirectory = outputDirectory;
    }

    /**
     * Parse the commits and output graphs
     */
    @Override
    public void run() {

        long startTime = System.currentTimeMillis();

        // Create the graph
        Graph graph = TinkerGraph.open();
        Parser parser = new Parser(graph);

        try {
            for (int i = 0; i < PyneDemo.COMMIT_LIST.length; i++) {
                // Get the commit
                String commitDate = PyneDemo.COMMIT_DATE_LIST[i];
                String commit = PyneDemo.COMMIT_LIST[i];

                // Parse the commit
                gitHelper.parseCommit(parser, commit);

                // Build the name for the output file
                StringBuilder nameBuilder = new StringBuilder();
                nameBuilder.append(commitDate).append("-");
                nameBuilder.append(commit).append(".graphml");

                File outputFile
                        = new File(outputDirectory, nameBuilder.toString());

                // Output the generated graph
                graph.traversal().io(outputFile.getAbsolutePath())
                        .with(IO.writer, IO.graphml).write().iterate();
                
                // Send completion status
                PyneDemo.taskComplete(PyneDemo.Task.GIT, i);

            }
        } catch (IOException ex) {
            Logger.getLogger(DemoGit.class.getName())
                    .log(Level.SEVERE, null, ex);
        }
        
        int diffTime = (int) ((System.currentTimeMillis() - startTime) / 1000);
        
        System.out.println("Time to completion: " + (diffTime / 60) 
                + "min " + (diffTime % 60) + "sec");
    }

}
