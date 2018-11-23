package cli.git.status;

import cli.Command;
import cli.util.SimpleLogger;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.function.Consumer;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.errors.RepositoryNotFoundException;

/**
 * Check git status from parent dir
 *
 * @author zacconding
 * @Date 2018-11-23
 * @GitHub : https://github.com/zacscoding
 */
public class GitStatusCommand extends Command {

    private Options argOptions;
    private GitStatusOptions gitStatusOptions;

    public GitStatusCommand() {
        this.argOptions = generateOptions();
    }

    @Override
    public void process(String[] args) {
        try {
            CommandLine commandLine = generateCommandLine(argOptions, args);
            Option[] options = commandLine.getOptions();

            if (options.length == 0 || options.length == 1 && options[0].getOpt().equalsIgnoreCase("h")) {
                printHelp();
                return;
            }

            gitStatusOptions = parseGitStatusOptions(commandLine);

            File rootDir = new File(gitStatusOptions.getParentDir());
            if (!rootDir.exists()) {
                throw new FileNotFoundException();
            }
            Consumer<File> consumer = file -> checkGitStatus(file);
            for (File file : rootDir.listFiles()) {
                checkDirectoryWithDepth(file, 1, consumer);
            }
        } catch (ParseException e) {
            // ignore
        } catch (FileNotFoundException e) {
            SimpleLogger.error("Not exist directory : " + gitStatusOptions.getParentDir());
        } catch (Exception e) {
            SimpleLogger.error("Failed to process args " + Arrays.toString(args), e);
        }
    }

    @Override
    public String getType() {
        return "status";
    }

    @Override
    public Options getOptions() {
        return argOptions;
    }

    /**
     * Check added or untracked files if git repository
     */
    private void checkGitStatus(File dir) {
        if (dir == null || !dir.isDirectory()) {
            return;
        }

        try (Git git = Git.open(dir)) {
            Status status = git.status().call();
            boolean isEmptyCheck = status.getAdded().isEmpty() && status.getUntracked().isEmpty();
            System.out.println("----------------------------------------------------------------------------");
            SimpleLogger.println(">> Check [ {} ]", dir.getName());
            if (isEmptyCheck) {
                SimpleLogger.println("> not exist stage files", dir.getName());
            } else {
                SimpleLogger.println("> remain state & un tracked file");
                if (gitStatusOptions.isDisplayAll()) {
                    SimpleLogger.println("Added : {}\nUntracked : {}", dir.getName(), status.getAdded(), status.getUntracked());
                }
            }
        } catch (RepositoryNotFoundException e) {
            // ignore not git dir
            return;
        } catch (Exception e) {
            SimpleLogger.error("Failed to check git status " + dir.getAbsolutePath(), e);
        }
    }

    /**
     * Check directory with depth.
     * Consumer will consume this file if current depth is less than maximum depth and is directory
     */
    private void checkDirectoryWithDepth(File file, int currentDepth, Consumer<File> consumer) {
        if (!file.isDirectory() || currentDepth > gitStatusOptions.getDepth()) {
            return;
        }

        consumer.accept(file);
        for (File child : file.listFiles()) {
            checkDirectoryWithDepth(child, currentDepth + 1, consumer);
        }
    }

    /**
     * Parse GitStatusOptions
     */
    private GitStatusOptions parseGitStatusOptions(CommandLine commandLine) throws Exception {
        GitStatusOptions gitStatusOptions = new GitStatusOptions();
        for (Option option : commandLine.getOptions()) {
            switch (option.getOpt()) {
                case "p":
                    gitStatusOptions.setParentDir(option.getValue());
                    break;
                case "d":
                    gitStatusOptions.setDepth(Integer.parseInt(option.getValue()));
                    break;
                case "s" :
                    gitStatusOptions.setDisplayAll(Boolean.parseBoolean(option.getValue()));
                    break;
                default:
                    SimpleLogger.error("Invalid param " + option.getOpt());
            }
        }

        if (gitStatusOptions.getDepth() == 0) {
            gitStatusOptions.setDepth(1);
        } else if (gitStatusOptions.getDepth() < 0) {
            gitStatusOptions.setDepth(Integer.MAX_VALUE);
        }

        return gitStatusOptions;
    }

    /**
     * Generate cli options
     */
    private Options generateOptions() {
        final Option helpOption = Option.builder("h").required(false).hasArg(false).longOpt("help").desc("Display help messages").build();
        final Option parentPathOption = Option.builder("p").required(false).hasArg(true).longOpt("path").desc("trace from this path").build();
        final Option showFilesOption = Option.builder("s").required(false).hasArg(true).longOpt("show")
                                             .desc("show added & untracked files. default false").build();
        final Option depthOption = Option.builder("d").required(false).hasArg(true).longOpt("depth").desc("Will check with depth. default value is 1")
                                         .build();

        return new Options().addOption(helpOption)
                            .addOption(parentPathOption)
                            .addOption(showFilesOption)
                            .addOption(depthOption);
    }
}
