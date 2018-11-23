package git;

import java.io.File;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.errors.RepositoryNotFoundException;
import org.eclipse.jgit.lib.Repository;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author zacconding
 * @Date 2018-11-23
 * @GitHub : https://github.com/zacscoding
 */
public class CheckNotCommitDir {

    @Test
    @Ignore
    public void checkGitDir() {
        String[] dirs = {"C:\\git\\zaccoding\\git-util-cli", "C:\\git_prev"};

        for (String dir : dirs) {
            System.out.println("Try : " + dir);
            try (Git git = Git.open(new File(dir))) {
                System.out.println("is git dir");
            } catch (RepositoryNotFoundException e) {
                System.out.println("is not git dir");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    @Ignore
    public void displayStatus() {
        // https://github.com/centic9/jgit-cookbook/blob/master/src/main/java/org/dstadler/jgit/porcelain/ShowStatus.java
        String destDir = "C:\\git\\zaccoding\\git-util-cli";
        try (Git git = Git.open(new File(destDir)); Repository repository = git.getRepository()) {
            Status status = git.status().call();
            System.out.println("Added: " + status.getAdded()); // use
            System.out.println("Changed: " + status.getChanged());
            System.out.println("Conflicting: " + status.getConflicting());
            System.out.println("ConflictingStageState: " + status.getConflictingStageState());
            System.out.println("IgnoredNotInIndex: " + status.getIgnoredNotInIndex());
            System.out.println("Missing: " + status.getMissing());
            System.out.println("Modified: " + status.getModified());
            System.out.println("Removed: " + status.getRemoved());
            System.out.println("Untracked: " + status.getUntracked()); // use
            System.out.println("UntrackedFolders: " + status.getUntrackedFolders());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
