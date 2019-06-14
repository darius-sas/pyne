package edu.rug.pyne.cli;

import edu.rug.pyne.api.GitHelper;
import edu.rug.pyne.api.parser.Parser;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystems;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PatternOptionBuilder;
import org.apache.tinkerpop.gremlin.process.traversal.IO;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.filter.CommitTimeRevFilter;
import org.eclipse.jgit.revwalk.filter.RevFilter;

/**
 *
 * @author Patrick Beuks (s2288842) <code@beuks.net>
 */
public class PyneCli {
    
    public static void main(String[] args) throws IOException, GitAPIException, ParseException {
        Options options = new Options();
        
        Option help = Option.builder("h").longOpt("help")
                .desc("Prints this help message").build();
        
        Option outputDirectoryOption = Option.builder("o")
                .longOpt("output-directory")
                .desc("Sets the output directory for the graphml files."
                        + " Defautls to the current working directory.")
                .hasArg().argName("file")
                .type(PatternOptionBuilder.FILE_VALUE).build();
        
        Option startDateOption = Option.builder("s").longOpt("start-date").
                desc("Sets the start date to parse from. Format: yyyy-MM-dd."
                        + " Defaults to 5 times the period from the end date.")
                .hasArg().argName("date").build();
        
        Option endDateOption = Option.builder("e").longOpt("end-date").
                desc("Sets the end date to parse to. Format: yyyy-MM-dd."
                        + " Defaults to the current date.")
                .hasArg().argName("date").build();
        
        Option periodOption = Option.builder("p").longOpt("period")
                .desc("Sets the period interval between commits to check."
                        + " Possible periods are: DAY, WEEK, MONTH, YEAR."
                        + " Defaults to DAY")
                .hasArg().argName("period")
                .type(PatternOptionBuilder.STRING_VALUE).build();
        
        Option inputDirectoriesOption = Option.builder("i")
                .longOpt("input-directories")
                .desc("A list of input directories relative to the root of the"
                        + " repo. If not given it will check"
                        + " \"" + File.separator + "src" + File.separator
                        + "main" + File.separator 
                        + "java\" if it exists, otherwise it uses"
                        + " the root of the repo for source files.")
                .hasArgs().argName("paths").build();
        
        options.addOption(help);
        options.addOption(outputDirectoryOption);
        options.addOption(startDateOption);
        options.addOption(endDateOption);
        options.addOption(periodOption);
        options.addOption(inputDirectoriesOption);
        
        CommandLineParser cliParser = new DefaultParser();
        CommandLine cmd = cliParser.parse(options, args);
        
        if (cmd.hasOption(help.getOpt())) {
            printHelp(options);
            return;
        }
        if (cmd.getArgs().length != 1) {
            System.out.println("Please give one and only one URI for a repo");
            printHelp(options);
            return;
        }
        
        URI repoURI;
        try {
            repoURI = new URI(cmd.getArgs()[0]);
        } catch (URISyntaxException ex) {
            System.out.println("Not a valid URI given for a repo.");
            printHelp(options);
            return;
        }
        
        int period;
        switch (cmd.getOptionValue(periodOption.getOpt(), "DAY").toUpperCase()) {
            case "DAY":
                period = Calendar.DAY_OF_YEAR;
                break;
            case "WEEK":
                period = Calendar.WEEK_OF_YEAR;
                break;
            case "MONTH":
                period = Calendar.MONTH;
                break;
            case "YEAR":
                period = Calendar.YEAR;
                break;
            default:
                System.out.println("\"" 
                        + cmd.getOptionValue(periodOption.getOpt()) 
                        + "\" is not a valid period");
                printHelp(options);
                return;
        }
        
        SimpleDateFormat dateInstance = new SimpleDateFormat("yyyy-MM-dd");
        
        Date endDate;
        if (cmd.hasOption(endDateOption.getOpt())) {
            try {
                endDate = dateInstance.parse(cmd.getOptionValue(endDateOption.getOpt()));
            } catch (java.text.ParseException ex) {
                System.out.println("Could not parse end date.");
                return;
            }
        } else {
            endDate = new Date();
        }
        
        Date startDate;
        if (cmd.hasOption(startDateOption.getOpt())) {
            try {
                startDate = dateInstance.parse(cmd.getOptionValue(startDateOption.getOpt()));
            } catch (java.text.ParseException ex) {
                System.out.println("Could not parse start date");
                return;
            }
        } else {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(endDate);
            calendar.add(period, -5);
            startDate = calendar.getTime();
        }
        
        File outputDirectory;
        if (cmd.hasOption(outputDirectoryOption.getOpt())) {
            outputDirectory = (File) cmd.getParsedOptionValue(outputDirectoryOption.getOpt());
            if (!outputDirectory.exists()) {
                outputDirectory.mkdirs();
            }
            if (!outputDirectory.isDirectory()) {
                System.out.println("Output directory is not a directory or cannot be read.");
                return;
            }
        } else {
            outputDirectory = FileSystems.getDefault().getPath(".").toFile();
        }

        Graph graph = TinkerGraph.open();
        Parser parser = new Parser(graph);
        if (cmd.getOptionValues(inputDirectoriesOption.getOpt()) != null) {
            for (String inputDirectory : cmd.getOptionValues(inputDirectoriesOption.getOpt())) {
                parser.addInputDirectory(inputDirectory);
            }
        }
        
        GitHelper gitHelper = new GitHelper(repoURI.toString());
        
        Git git = gitHelper.getGit();
        
        RevFilter revFilter = CommitTimeRevFilter.between(startDate, endDate);
        
        
        Iterable<RevCommit> revCommits 
                = git.log().all().setRevFilter(revFilter).call();
        
        Map<Date, String> commitMap = new TreeMap<>();
        for (RevCommit revCommit : revCommits) {
            commitMap.put(new Date(((long) revCommit.getCommitTime()) * 1000), revCommit.getName());
        }
        
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        Date checkDate = calendar.getTime();
        for (Map.Entry<Date, String> commitEntry : commitMap.entrySet()) {
            Date commitDate = commitEntry.getKey();
            String commit = commitEntry.getValue();
            
            if (!commitDate.after(checkDate)) {
                continue;
            }

            System.out.println("\n\nParsing commit: "
                    + commitDate + " | " + commit);


            StringBuilder nameBuilder = new StringBuilder();
            nameBuilder.append(dateInstance.format(commitDate)).append("-");
            nameBuilder.append(commit).append(".graphml");

            gitHelper.parseCommit(parser, commit);
            graph.traversal().io(
                    new File(outputDirectory, nameBuilder.toString())
                            .getAbsolutePath()
            ).with(IO.writer, IO.graphml).write().iterate();

            calendar.setTime(commitDate);
            calendar.add(period, 1);
            checkDate = calendar.getTime();
            
            
        }

    }
    
    private static void printHelp(Options options) {
        
        HelpFormatter helpFormatter = new HelpFormatter();
        helpFormatter.printHelp(
                "\tPyneCli <URI> [OPTIONS...]\tPyneCli --help" 
                        + System.lineSeparator() + System.lineSeparator(),
                "Where <URI> is the URI of a repository."
                        + System.lineSeparator()
                + "For a local repository prefix with \"file://\""
                        + System.lineSeparator() + System.lineSeparator(),
                options, "");
    }

}
