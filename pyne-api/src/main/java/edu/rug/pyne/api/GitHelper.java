package edu.rug.pyne.api;

import edu.rug.pyne.api.parser.Parser;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.WindowCacheConfig;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.util.FileUtils;

/**
 * This is a helper class for the parser that gives the ability to parse source
 * code files using a git repository.
 *
 * @author Patrick Beuks (s2288842) <code@beuks.net>
 */
public class GitHelper {

    // The graph key used to store the commit id on
    private static final String COMMIT_ID_VARIABLE = "CommitId";

    // The temporary location to store the cione in
    private final File cloneDir;
    // The git created after the clone
    private final Git git;

    /**
     * Creates a clone repository in a temporary location and gives access to
     * parse functions using commit ids
     *
     * @param repositoryToClone The URI to the repository to clone
     * @throws IOException Thrown when failed to create a temporary directory
     * @throws GitAPIException Thrown when failed to clone the repository.
     */
    public GitHelper(String repositoryToClone)
            throws IOException, GitAPIException {

        WindowCacheConfig config = new WindowCacheConfig();
        config.setPackedGitMMAP(false);
        config.install();

        cloneDir = Files.createTempDirectory("temp_git_clone_").toFile();
        git = Git.cloneRepository().setURI(repositoryToClone)
                .setDirectory(cloneDir).call();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            this.cleanUp();
        }));

    }

    /**
     * Returns the git created by the cloned repository.
     *
     * @return The git from the cloned repository.
     */
    public Git getGit() {
        return git;
    }

    /**
     * parses a given commit. If a commit id is set on the graph it will check
     * out the difference between that commit and the given one.
     *
     * @param parser The parser that is used to process the files
     * @param commitId The commit id to parse
     * @throws IOException Thrown if a loose object or pack file could not be
     * read. Can only occur when a diff is calculated.
     */
    public void parseCommit(Parser parser, String commitId) throws IOException {
        parser.setRootDirectory(cloneDir);
        Optional<String> graphCommit = parser.getGraph().variables()
                .<String>get(COMMIT_ID_VARIABLE);

        try {
            if (graphCommit.isEmpty()) {
                initGraph(parser, commitId);
            } else {
                diffGraph(parser, graphCommit.get(), commitId);
            }
        } catch (GitAPIException ex) {
            Logger.getLogger(GitHelper.class.getName())
                    .log(Level.SEVERE, null, ex);
        }
    }

    /**
     * This sets up the parser to process a commit. It first checks out the
     * commit and then processes it
     *
     * @param parser The parser that is used to process the files
     * @param commitId The commit id to parse
     * @throws GitAPIException Thrown if git failed to checkout a commit
     */
    private void initGraph(Parser parser, String commitId)
            throws GitAPIException {

        // Set git config before checkout. 
        // This is always needed or cleanup cannot occure
        WindowCacheConfig config = new WindowCacheConfig();
        config.setPackedGitMMAP(false);
        config.install();

        System.out.println("Checking out commit");
        git.checkout().setName(commitId).call();

        // Reset files if not already on null. 
        // This indicates a full parsing of classes.
        parser.setAddedFiles(null);
        parser.setModifiedFiles(null);
        parser.setRemovedFiles(null);

        System.out.println("Processing classes");
        parser.process();

        // Set commit id on graph
        parser.getGraph().variables().set(COMMIT_ID_VARIABLE, commitId);
    }

    /**
     * This first gets the difference between the commits, saves the files
     * changed. It then reinstates the old commit and does the remove process.
     * After this it checks out the new commit and does the normal process.
     *
     * @param parser The parser that is used to process the files
     * @param oldCommit The commit id of the old commit
     * @param newCommit The commit id of the new commit
     * @throws GitAPIException Thrown if git failed to checkout a commit or to
     * find the differences
     * @throws IOException Thrown if a loose object or pack file could not be
     * read.
     */
    private void diffGraph(Parser parser, String oldCommit, String newCommit)
            throws GitAPIException, IOException {

        Repository repository = git.getRepository();
        RevWalk revWalk = new RevWalk(repository);
        ObjectReader reader = repository.newObjectReader();
        File rootDir = repository.getWorkTree();

        // Get old commit tree
        ObjectId oldCommitObject = ObjectId.fromString(oldCommit);
        RevCommit revOldCommit = revWalk.parseCommit(oldCommitObject);
        CanonicalTreeParser canonicalTreeParserOld = new CanonicalTreeParser(
                null, reader, revOldCommit.getTree().getId()
        );

        // Get new commit tree
        ObjectId newCommitObject = ObjectId.fromString(newCommit);
        RevCommit revNewCommit = revWalk.parseCommit(newCommitObject);
        CanonicalTreeParser canonicalTreeParserNew = new CanonicalTreeParser(
                null, reader, revNewCommit.getTree().getId()
        );

        System.out.println("Finding diffrences");
        List<DiffEntry> diffEntries = git
                .diff()
                .setOldTree(canonicalTreeParserOld)
                .setNewTree(canonicalTreeParserNew)
                .call();

        // Init diff arrays
        List<File> addedFiles = new ArrayList<>();
        List<File> modifiedFiles = new ArrayList<>();
        List<File> removedFiles = new ArrayList<>();

        for (DiffEntry diffEntry : diffEntries) {
            switch (diffEntry.getChangeType()) {
                case ADD:
                case COPY:
                    addedFiles.add(new File(rootDir, diffEntry.getNewPath()));
                    break;
                case DELETE:
                    removedFiles.add(new File(rootDir, diffEntry.getOldPath()));
                    break;
                case MODIFY:
                    addedFiles.add(new File(rootDir, diffEntry.getNewPath()));
                    modifiedFiles
                            .add(new File(rootDir, diffEntry.getOldPath()));
                    break;
                case RENAME:
                    addedFiles.add(new File(rootDir, diffEntry.getNewPath()));
                    removedFiles
                            .add(new File(rootDir, diffEntry.getOldPath()));
                    break;
            }

        }

        // Setup parser
        parser.setAddedFiles(addedFiles);
        parser.setModifiedFiles(modifiedFiles);
        parser.setRemovedFiles(removedFiles);

        // Set git config before checkout. 
        // This is always needed or cleanup cannot occure
        WindowCacheConfig config = new WindowCacheConfig();
        config.setPackedGitMMAP(false);
        config.install();

        System.out.println("Checking out old commit");
        git.checkout().setName(oldCommit).call();

        System.out.println("Proccessing removed files");
        parser.processRemoved();

        // Set git config before checkout. 
        // This is always needed or cleanup cannot occure
        config = new WindowCacheConfig();
        config.setPackedGitMMAP(false);
        config.install();

        System.out.println("Checking out new commit");
        git.checkout().setName(newCommit).call();

        System.out.println("Processing classes");
        parser.process();

        // Set commit id on graph
        parser.getGraph().variables().set(COMMIT_ID_VARIABLE, newCommit);
    }

    /**
     * Closes the repository, tries to remove all temporary files and closes git
     */
    private void cleanUp() {

        git.getRepository().close();
        try {
            FileUtils.delete(cloneDir, FileUtils.RECURSIVE);
        } catch (IOException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        }
        git.close();
    }

}
