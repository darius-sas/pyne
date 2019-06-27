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
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.tinkerpop.gremlin.process.traversal.IO;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.filter.CommitTimeRevFilter;
import org.eclipse.jgit.revwalk.filter.RevFilter;

/**
 * This class implements a cli to be able to interact with the api
 *
 * @author Patrick Beuks (s2288842) <code@beuks.net>
 */
public class PyneCli {

    private static final Logger LOGGER
            = LogManager.getLogger(PyneCli.class);

    /**
     * Start the application
     *
     * @param args The arguments given in the command line
     * @throws IOException Can have multiple sources
     * @throws GitAPIException Thrown when the GitHelper could not be created or
     * the log on the git could not be called
     * @throws ParseException Called when the CLI parser cannot parse the args
     */
    public static void main(String[] args)
            throws IOException, GitAPIException, ParseException {

        // Collection of cli options
        Options options = new Options();

        // Add all options possible on the command line
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

        // Add the options to the collection
        options.addOption(help);
        options.addOption(outputDirectoryOption);
        options.addOption(startDateOption);
        options.addOption(endDateOption);
        options.addOption(periodOption);
        options.addOption(inputDirectoriesOption);

        // Create a new parser
        CommandLineParser cliParser = new DefaultParser();
        CommandLine cmd = cliParser.parse(options, args);

        // Check if help is an argument and display the help
        if (cmd.hasOption(help.getOpt())) {
            printHelp(options);
            return;
        }
        // Check if an URI to a repo is given
        if (cmd.getArgs().length != 1) {
            LOGGER.fatal("Please give one and only one URI for a repo");
            printHelp(options);
            return;
        }

        // Check if the UIR is valid
        URI repoURI;
        try {
            repoURI = new URI(cmd.getArgs()[0]);
        } catch (URISyntaxException ex) {
            LOGGER.fatal("Not a valid URI given for a repo.", ex);
            printHelp(options);
            return;
        }

        // Get the period, DAY by default
        int period;
        String periodOptionValue
                = cmd.getOptionValue(periodOption.getOpt(), "DAY");

        switch (periodOptionValue.toUpperCase()) {
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
                LOGGER.fatal("\""
                        + cmd.getOptionValue(periodOption.getOpt())
                        + "\" is not a valid period");
                printHelp(options);
                return;
        }

        // Get the start and end date
        SimpleDateFormat dateInstance = new SimpleDateFormat("yyyy-MM-dd");

        Date endDate;
        if (cmd.hasOption(endDateOption.getOpt())) {
            try {
                endDate = dateInstance
                        .parse(cmd.getOptionValue(endDateOption.getOpt()));
            } catch (java.text.ParseException ex) {
                LOGGER.fatal("Could not parse end date", ex);
                return;
            }
        } else {
            // Default current day
            endDate = new Date();
        }

        Date startDate;
        if (cmd.hasOption(startDateOption.getOpt())) {
            try {
                startDate = dateInstance
                        .parse(cmd.getOptionValue(startDateOption.getOpt()));
            } catch (java.text.ParseException ex) {
                LOGGER.fatal("Could not parse start date", ex);
                return;
            }
        } else {
            // Default 5x the period before the end date
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(endDate);
            calendar.add(period, -5);
            startDate = calendar.getTime();
        }

        // Get the output directory
        File outputDirectory;
        if (cmd.hasOption(outputDirectoryOption.getOpt())) {
            outputDirectory = (File) cmd
                    .getParsedOptionValue(outputDirectoryOption.getOpt());
            if (!outputDirectory.exists()) {
                outputDirectory.mkdirs();
            }
            if (!outputDirectory.isDirectory()) {
                LOGGER.error("Output directory is not a directory "
                        + "or cannot be read.");
                return;
            }
        } else {
            // Default the current working directory
            outputDirectory = FileSystems.getDefault().getPath(".").toFile();
        }

        // Get and set the input direcotries
        Graph graph = TinkerGraph.open();
        Parser parser = new Parser(graph);
        if (cmd.getOptionValues(inputDirectoriesOption.getOpt()) != null) {
            for (String inputDirectory : cmd
                    .getOptionValues(inputDirectoriesOption.getOpt())) {
                parser.addInputDirectory(inputDirectory);
            }
        }

        // Create a git helper and a filter between start and end date
        GitHelper gitHelper = new GitHelper(repoURI.toString());

        Git git = gitHelper.getGit();

        RevFilter revFilter = CommitTimeRevFilter.between(startDate, endDate);

        Iterable<RevCommit> revCommits
                = git.log().all().setRevFilter(revFilter).call();

        // Add all commits in a map that is sorted by there date
        Map<Date, String> commitMap = new TreeMap<>();
        for (RevCommit revCommit : revCommits) {
            commitMap.put(
                    new Date(((long) revCommit.getCommitTime()) * 1000),
                    revCommit.getName()
            );
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        Date checkDate = calendar.getTime();
        // Go over all commits
        for (Map.Entry<Date, String> commitEntry : commitMap.entrySet()) {
            Date commitDate = commitEntry.getKey();
            String commit = commitEntry.getValue();

            // Check if the period has expired and a new commit should be parsed
            if (!commitDate.after(checkDate)) {
                continue;
            }

            LOGGER.info("Parsing commit: "
                    + commitDate + " | " + commit);

            // Parse the commit
            gitHelper.parseCommit(parser, commit);

            // Build the name for the output file
            StringBuilder nameBuilder = new StringBuilder();
            nameBuilder.append(dateInstance.format(commitDate)).append("-");
            nameBuilder.append(commit).append(".graphml");

            File outputFile = new File(outputDirectory, nameBuilder.toString());

            // Output the generated graph
            graph.traversal().io(outputFile.getAbsolutePath())
                    .with(IO.writer, IO.graphml).write().iterate();

            LOGGER.info("Saved graph to: "
                    + outputFile.getAbsolutePath() + "\n\n");

            // Set the date to the next period
            calendar.setTime(commitDate);
            calendar.add(period, 1);
            checkDate = calendar.getTime();

        }

    }

    /**
     * Prints the help message
     *
     * @param options The options to display in the help message
     */
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
