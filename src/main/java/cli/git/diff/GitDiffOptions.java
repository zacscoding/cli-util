package cli.git.diff;

/**
 * @author zacconding
 * @Date 2018-12-29
 * @GitHub : https://github.com/zacscoding
 */
public class GitDiffOptions {

    private int depth;
    private String parentDir;

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth < 0 ? Integer.MAX_VALUE : depth;
    }

    public String getParentDir() {
        return parentDir;
    }

    public void setParentDir(String parentDir) {
        this.parentDir = parentDir;
    }
}
