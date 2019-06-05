package edu.rug.pyne.api;

import edu.rug.pyne.api.parser.Parser;
import java.io.File;
import java.io.IOException;
import java.net.URI;
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
 *
 * @author Patrick Beuks (s2288842) <code@beuks.net>
 */
public class GitHelper {

    private static final String COMMIT_ID_VARIABLE = "CommitId";
    
    private final File cloneDir;
    private final Git git;

    public GitHelper(URI repositoryToClone) throws IOException, GitAPIException {

        WindowCacheConfig config = new WindowCacheConfig();
        config.setPackedGitMMAP(false);
        config.install();

        cloneDir = Files.createTempDirectory("temp_git_clone_").toFile();
        git = Git.cloneRepository().setURI(repositoryToClone.getPath()).setDirectory(cloneDir).call();

    }

    public void parseCommit(Parser parser, String commitId) throws IOException {
        parser.setRootDirectory(cloneDir);
        Optional<String> graphCommit = parser.getGraph().variables().<String>get(COMMIT_ID_VARIABLE);
        try {
            if (graphCommit.isEmpty()) {
                initGraph(parser, commitId);
            } else {
                diffGraph(parser, graphCommit.get(), commitId);
            }
        } catch (GitAPIException ex) {
            Logger.getLogger(GitHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void initGraph(Parser parser, String commitId) throws GitAPIException {
        
        WindowCacheConfig config = new WindowCacheConfig();
        config.setPackedGitMMAP(false);
        config.install();
        
        git.checkout().setName(commitId).call();
        parser.setAddedFiles(null);
        parser.setRemovedFiles(null);
        parser.process();
        parser.getGraph().variables().set(COMMIT_ID_VARIABLE, commitId);
    }

    private void diffGraph(Parser parser, String oldCommit, String newCommit) throws GitAPIException, IOException {
        Repository repository = git.getRepository();

        ObjectId oldCommitObject = ObjectId.fromString(oldCommit);
        ObjectId newCommitObject = ObjectId.fromString(newCommit);

        RevWalk revWalk = new RevWalk(repository);
        RevCommit revOldCommit = revWalk.parseCommit(oldCommitObject);
        RevCommit revNewCommit = revWalk.parseCommit(newCommitObject);

        ObjectReader reader = repository.newObjectReader();
        CanonicalTreeParser canonicalTreeParserOld = new CanonicalTreeParser(null, reader, revOldCommit.getTree().getId());
        CanonicalTreeParser canonicalTreeParserNew = new CanonicalTreeParser(null, reader, revNewCommit.getTree().getId());

        List<DiffEntry> diffEntries = git.diff().setOldTree(canonicalTreeParserOld).setNewTree(canonicalTreeParserNew).call();
        List<File> addedFiles = new ArrayList<>();
        List<File> modifiedFiles = new ArrayList<>();
        List<File> removedFiles = new ArrayList<>();

        for (DiffEntry diffEntry : diffEntries) {
            switch (diffEntry.getChangeType()) {
                case ADD:
                case COPY:
                    addedFiles.add(new File(repository.getWorkTree(), diffEntry.getNewPath()));
                    break;
                case DELETE:
                    removedFiles.add(new File(repository.getWorkTree(), diffEntry.getOldPath()));
                    break;
                case MODIFY:
                    addedFiles.add(new File(repository.getWorkTree(), diffEntry.getNewPath()));
                    modifiedFiles.add(new File(repository.getWorkTree(), diffEntry.getOldPath()));
                    break;
                case RENAME:
                    addedFiles.add(new File(repository.getWorkTree(), diffEntry.getNewPath()));
                    removedFiles.add(new File(repository.getWorkTree(), diffEntry.getOldPath()));
                    break;
            }

        }

        parser.setAddedFiles(addedFiles);
        parser.setModifiedFiles(modifiedFiles);
        parser.setRemovedFiles(removedFiles);
        
        WindowCacheConfig config = new WindowCacheConfig();
        config.setPackedGitMMAP(false);
        config.install();
        
        git.checkout().setName(oldCommit).call();

        parser.processRemoved();
        
        config = new WindowCacheConfig();
        config.setPackedGitMMAP(false);
        config.install();
        
        git.checkout().setName(newCommit).call();

        parser.process();
        parser.getGraph().variables().set(COMMIT_ID_VARIABLE, newCommit);
    }

    public void cleanUp() {

        git.getRepository().close();
        try {
            FileUtils.delete(cloneDir, FileUtils.RECURSIVE);
        } catch (IOException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        }
        git.close();
    }

}
