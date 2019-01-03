package cli.git;

import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;
import java.util.function.Predicate;
import org.eclipse.jgit.api.Git;

/**
 * @author zacconding
 * @Date 2018-12-29
 * @GitHub : https://github.com/zacscoding
 */
public class GitHelper {

    /**
     * Check whether git repository or not
     */
    public static boolean isGitDirectory(File file) {
        if (file == null || file.isFile()) {
            return false;
        }

        try (Git git = Git.open(file)) {
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Explore directories with depth
     *
     * Consumer will consume this file if current depth is less than maximum depth and success to filter
     */
    public static void exploreFilesWithDepth(File file, Predicate<File> consumePredicate, Consumer<File> consumer,
        Predicate<File> terminatePredicate, final int maximumDepth, int currentDepth) {

        if (currentDepth > maximumDepth) {
            return;
        }

        if (consumePredicate == null || consumePredicate.test(file)) {
            consumer.accept(file);
        }

        if (file.isFile()) {
            return;
        }

        if (terminatePredicate != null && terminatePredicate.test(file)) {
            return;
        }

        for (File child : file.listFiles()) {
            exploreFilesWithDepth(child, consumePredicate, consumer, terminatePredicate
                , maximumDepth, currentDepth + 1
            );
        }
    }
}
