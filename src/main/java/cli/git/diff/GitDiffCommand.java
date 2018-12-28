package cli.git.diff;

import cli.Command;
import cli.git.GitHelper;
import cli.util.SimpleLogger;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.errors.RepositoryNotFoundException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.transport.RemoteConfig;
import org.eclipse.jgit.transport.URIish;

/**
 * Compare sha local repository with remote repositories
 *
 * @author zacconding
 * @Date 2018-12-29
 * @GitHub : https://github.com/zacscoding
 */
public class GitDiffCommand extends Command {

    private Options argOptions;
    private GitDiffOptions gitDiffOptions;

    public GitDiffCommand() {
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

            parseGitDiffOptions(commandLine);

            File rootDir = new File(gitDiffOptions.getParentDir());

            if (!rootDir.exists()) {
                throw new FileNotFoundException("Not exist directory " + gitDiffOptions.getParentDir());
            }

            Predicate<File> consumePredicate = file -> file != null && file.isDirectory();
            Consumer<File> consumer = file -> compareLocalWithRemotes(file);
            Predicate<File> terminatePredicate = file -> GitHelper.isGitDirectory(file);

            GitHelper.exploreFilesWithDepth(rootDir, consumePredicate, consumer, terminatePredicate,
                gitDiffOptions.getDepth(), 0
            );
        } catch (ParseException e) {
            // ignore
        } catch (FileNotFoundException e) {
            SimpleLogger.error(e);
        } catch (Exception e) {
            SimpleLogger.error("Failed to process args " + Arrays.toString(args), e);
        }
    }

    @Override
    public String getType() {
        return "git-diff";
    }

    @Override
    public Options getOptions() {
        return argOptions;
    }

    /**
     * Check exist whether exist diffrent commit between local and remotes or not
     */
    private void compareLocalWithRemotes(File dir) {
        try (Git git = Git.open(dir)) {
            System.out.println("----------------------------------------------------------------------------");
            SimpleLogger.print(">> Check repository [ {} ]", dir.getName());

            // local branchs map
            Map<String, String> localRefsMap = git.branchList().call().stream().collect(
                Collectors.toMap(ref -> ref.getName(), ref -> ref.getObjectId().name())
            );

            // remote configs
            List<RemoteConfig> remoteConfigs = git.remoteList().call();
            if (remoteConfigs.isEmpty()) {
                System.out.println("> empty remotes");
                return;
            }

            boolean isSynchronized = true;

            for (RemoteConfig remoteConfig : remoteConfigs) {
                for (URIish url : remoteConfig.getURIs()) {
                    try {
                        Collection<Ref> refs = Git.lsRemoteRepository()
                            .setHeads(true)
                            .setTags(true)
                            .setRemote(url.toString())
                            .call();

                        int notExistBranchCount = 0;

                        for (Ref ref : refs) {
                            String sha = localRefsMap.get(ref.getName());

                            if (sha == null) {
                                notExistBranchCount++;
                                isSynchronized = false;
                                continue;
                            }

                            if (sha.equals(ref.getObjectId().name())) {
                                continue;
                            }

                            isSynchronized = false;
                            SimpleLogger.println("\n>>> diff ref {}. local : {} | remote {}-{} : {}"
                                , ref.getName(), sha, remoteConfig.getName(), url.toString(), ref.getObjectId().name());
                        }

                        // display not exist branches
                        if (notExistBranchCount > 0) {
                            SimpleLogger.println(
                                "\n>>> Not exist {} branches at local repository compared with {}"
                                , notExistBranchCount, url.toString()
                            );
                        }
                    } catch (TransportException e) {
                        isSynchronized = false;
                        SimpleLogger.println("\n>>> Cannot connect repository. url : " + url.toString());
                    }
                }
            }

            if (isSynchronized) {
                System.out.println(" > synchronized");
            }
        } catch (RepositoryNotFoundException e) {
            // ignore not git dir
            return;
        } catch (Exception e) {
            SimpleLogger.error("Failed to check git diff " + dir.getAbsolutePath(), e);
        }
    }

    private void parseGitDiffOptions(CommandLine commandLine) throws Exception {
        gitDiffOptions = new GitDiffOptions();

        for (Option option : commandLine.getOptions()) {
            switch (option.getOpt()) {
                case "p":
                    gitDiffOptions.setParentDir(option.getValue());
                    break;
                case "d":
                    gitDiffOptions.setDepth(Integer.parseInt(option.getValue()));
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

        final Option depthOption = Option.builder("d").required(false).hasArg(true)
            .longOpt("depth").desc("will check with depth. default value is 1").build();

        return new Options().addOption(helpOption)
            .addOption(parentPathOption)
            .addOption(depthOption);
    }
}