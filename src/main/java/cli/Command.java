package cli;

import cli.util.SimpleLogger;
import java.util.Arrays;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * Abstract command class
 *
 * @author zacconding
 * @Date 2018-11-23
 * @GitHub : https://github.com/zacscoding
 */
public abstract class Command {

    public abstract void process(String[] args);

    public abstract String getType();

    public abstract Options getOptions();

    protected CommandLine generateCommandLine(final Options options, final String[] commandLineArguments)
        throws ParseException {
        final CommandLineParser cmdLineParser = new DefaultParser();
        CommandLine commandLine = null;

        try {
            commandLine = cmdLineParser.parse(options, commandLineArguments);
        } catch (ParseException parseException) {
            SimpleLogger.error("ERROR: Unable to parse command-line arguments "
                + Arrays.toString(commandLineArguments) + " : " + parseException.getMessage()
            );

            printHelp();
            throw parseException;
        }

        return commandLine;
    }

    protected void printHelp() {
        Options options = getOptions();
        final HelpFormatter formatter = new HelpFormatter();
        final String syntax = "java -jar cli-util " + getType();
        final String usageHeader = "\nExample of Using cli-util :)\n";
        final String usageFooter = "\n";
        System.out.println();
        formatter.printHelp(syntax, usageHeader, options, usageFooter);
        System.out.println("\n");
    }

    protected boolean isDisplayHelpMessage(CommandLine commandLines) {
        if(commandLines == null) {
            return true;
        }

        return commandLines.hasOption("help");
    }
}
