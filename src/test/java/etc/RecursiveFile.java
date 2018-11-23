package etc;

import java.io.File;
import java.util.function.Consumer;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author zacconding
 * @Date 2018-11-23
 * @GitHub : https://github.com/zacscoding
 */
public class RecursiveFile {

    int maxDepth = 2;

    @Test
    public void temp() {
        String[] bools = {"false", "False"};
        for(String bool : bools) {
            System.out.println(Boolean.parseBoolean(bool));
        }
    }

    @Test
    @Ignore
    public void checkWithRecursive() {
        File rootDir = new File("C:\\git\\zaccoding");
        Consumer<File> consumer = file -> System.out.println("Will check : " + file.getAbsolutePath());
        for (File file : rootDir.listFiles()) {
            checkDir(file, 1, consumer);
        }
    }

    private void checkDir(File file, int depth, Consumer<File> consumer) {
        if (depth > maxDepth || !file.isDirectory()) {
            return;
        }

        for (int i = 0; i < depth; i++) {
            System.out.print(" ");
        }
        consumer.accept(file);

        for (File child : file.listFiles()) {
            checkDir(child, depth + 1, consumer);
        }
    }
}
