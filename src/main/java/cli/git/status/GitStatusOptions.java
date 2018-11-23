package cli.git.status;

/**
 * @author zacconding
 * @Date 2018-11-23
 * @GitHub : https://github.com/zacscoding
 */
public class GitStatusOptions {

    private int depth;
    private String parentDir;
    private boolean displayAll;

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public String getParentDir() {
        return parentDir;
    }

    public void setParentDir(String parentDir) {
        this.parentDir = parentDir;
    }

    public boolean isDisplayAll() {
        return displayAll;
    }

    public void setDisplayAll(boolean displayAll) {
        this.displayAll = displayAll;
    }
}
