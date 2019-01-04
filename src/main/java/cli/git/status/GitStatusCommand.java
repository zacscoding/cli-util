package cli.git.status;

import static org.fusesource.jansi.Ansi.Color.CYAN;
import static org.fusesource.jansi.Ansi.Color.DEFAULT;
import static org.fusesource.jansi.Ansi.Color.GREEN;
import static org.fusesource.jansi.Ansi.Color.YELLOW;
import static org.fusesource.jansi.Ansi.ansi;

import cli.Command;
import cli.git.GitHelper;
import cli.util.SimpleLogger;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.Predicate;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.errors.RepositoryNotFoundException;
import org.fusesource.jansi.Ansi.Color;
import org.fusesource.jansi.AnsiConsole;

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

            if (isDisplayHelpMessage(commandLine)) {
                printHelp();
                return;
            }

            parseGitStatusOptions(commandLine);

            File rootDir = new File(gitStatusOptions.getParentDir());

            if (!rootDir.exists()) {
                throw new FileNotFoundException();
            }

            Predicate<File> consumePredicate = file -> file != null && file.isDirectory();
            Consumer<File> consumer = file -> checkGitStatus(file);
            Predicate<File> terminatePredicate = file -> GitHelper.isGitDirectory(file);

            GitHelper.exploreFilesWithDepth(rootDir, consumePredicate, consumer, terminatePredicate,
                gitStatusOptions.getDepth(), 0);

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
        return "git-status";
    }

    @Override
    public Options getOptions() {
        return argOptions;
    }

    /**
     * Check added or untracked files if git repository
     */
    private void checkGitStatus(File dir) {
        try (Git git = Git.open(dir)) {
            Status status = git.status().call();
            boolean isEmptyCheck = status.getAdded().isEmpty() && status.getUntracked().isEmpty();
            System.out.println("----------------------------------------------------------------------------");

            AnsiConsole.systemInstall();
            System.out.print(
                ansi().a(">> Check status. repository [ ")
                    .fg(GREEN)
                    .a(dir.getName())
                    .fg(DEFAULT)
                    .a(" ] ")
                    .reset()
            );

            if (isEmptyCheck) {
                System.out.println(
                    ansi().a("> ").fg(CYAN).a("empty stage files").reset()
                );
            } else {
                System.out.println(
                    ansi().a("> ")
                    .fg(YELLOW)
                    .a("remain state & untracked file.")
                    .fg(DEFAULT)
                    .a("added : " + status.getAdded().size())
                    .a(" | untracked : " + status.getUntracked().size())
                );

                if (gitStatusOptions.isDisplayAll()) {
                    SimpleLogger.println("Added : {}\nUntracked : {}"
                        , dir.getName(), status.getAdded(), status.getUntracked()
                    );
                }
            }
        } catch (RepositoryNotFoundException e) {
            // ignore not git dir
            return;
        } catch (Exception e) {
            SimpleLogger.println("[ERROR] Failed to check git status. {} | {}"
                , dir.getAbsolutePath(), e.getMessage());
        }
    }

    /**
     * Parse GitStatusOptions
     */
    private void parseGitStatusOptions(CommandLine commandLine) throws Exception {
        gitStatusOptions = new GitStatusOptions();

        for (Option option : commandLine.getOptions()) {
            switch (option.getOpt()) {
                case "p":
                    gitStatusOptions.setParentDir(option.getValue());
                    break;
                case "d":
                    gitStatusOptions.setDepth(Integer.parseInt(option.getValue()));
                    break;
                case "s":
                    gitStatusOptions.setDisplayAll(Boolean.parseBoolean(option.getValue()));
                    break;
                default:
                    SimpleLogger.error("Invalid param " + option.getOpt());
            }
        }
    }

    /**
     * Generate cli options
     */
    private Options generateOptions() {
        final Option helpOption = Option.builder("h").required(false).hasArg(false)
            .longOpt("help").desc("display help messages").build();

        final Option parentPathOption = Option.builder("p").required(false).hasArg(true)
            .longOpt("path").desc("trace from this path").build();

        final Option showFilesOption = Option.builder("s").required(false).hasArg(true)
            .longOpt("show").desc("show added & untracked files. default false").build();

        final Option depthOption = Option.builder("d").required(false).hasArg(true)
            .longOpt("depth").desc("will check with depth. default value is 1").build();

        return new Options().addOption(helpOption)
            .addOption(parentPathOption)
            .addOption(showFilesOption)
            .addOption(depthOption);
    }
}
