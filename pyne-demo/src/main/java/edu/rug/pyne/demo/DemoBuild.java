package edu.rug.pyne.demo;

import edu.rug.pyne.api.GitHelper;
import edu.rug.pyne.api.parser.Parser;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
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
public class DemoBuild implements Runnable {

    private final String repo;

    /**
     * Directory to put the graphs in
     */
    private final File outputDirectory;

    /**
     * Creates a demo showing off how building works
     *
     * @param repo The repo to clone from
     * @param outputDirectory The location to put output graphs in
     */
    public DemoBuild(String repo, File outputDirectory) {
        this.repo = repo;
        this.outputDirectory = outputDirectory;
    }

    /**
     * Parse the commits and output graphs
     */
    @Override
    public void run() {

        long startTime = System.currentTimeMillis();

        try {
            for (int i = 0; i < PyneDemo.COMMIT_LIST.length; i++) {
                // Create the GitHelper for the current iteration 
                GitHelper gitHelper = new GitHelper(repo);

                // Create the graph
                Graph graph = TinkerGraph.open();
                Parser parser = new Parser(graph);

                // Get the commit
                String commitDate = PyneDemo.COMMIT_DATE_LIST[i];
                String commit = PyneDemo.COMMIT_LIST[i];

                // Parse the commit
                gitHelper.parseCommit(parser, commit);

                // Simulate build
                Process process = Runtime.getRuntime().exec(new String[]{
                    "C:\\Program Files\\apache-maven-3.5.3\\bin\\mvn.cmd",
                    "install"
                }, null, gitHelper.getDir());
                BufferedReader input = new BufferedReader(
                        new InputStreamReader(process.getInputStream())
                );
                while (input.readLine() != null) {
                }
                input.close();

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
                PyneDemo.taskComplete(PyneDemo.Task.BUILD, i);
                
                // Clean directories up
                gitHelper.cleanUp();

                // It takes a long time to do this and 500+ commits is not
                // going to happen
                if (i == 20) {
                    break;
                }

            }
        } catch (IOException | GitAPIException ex) {
            Logger.getLogger(DemoBuild.class.getName())
                    .log(Level.SEVERE, null, ex);
        }

        int diffTime = (int) ((System.currentTimeMillis() - startTime) / 1000);

        System.out.println("Time to completion: " + (diffTime / 60)
                + "min " + (diffTime % 60) + "sec");

    }

}
