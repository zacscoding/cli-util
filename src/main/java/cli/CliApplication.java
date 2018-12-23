package cli;

import cli.git.status.GitStatusCommand;
import java.util.Arrays;
import java.util.List;

/**
 * Cli application main class
 *
 * @author zacconding
 * @Date 2018-11-23
 * @GitHub : https://github.com/zacscoding
 */
public class CliApplication {

    private static List<Command> COMMANDS;

    static {
        COMMANDS = Arrays.asList(new GitStatusCommand());
    }

    public static void main(String[] args) {
        args = new String[]{
            "status"
            , "-p"
            , "C:\\git\\zaccoding"
            , "-d"
            , "1"
            , "-s"
            , "false"
        };
        if (args == null || args.length == 0) {
            printHelpMessage();
            return;
        }

        String commandArg = args[0];
        boolean existCommand = false;

        for (Command command : COMMANDS) {
            if (command.getType().equalsIgnoreCase(commandArg)) {
                command.process(Arrays.copyOfRange(args, 1, args.length));
                existCommand = true;
                break;
            }
        }

        if (!existCommand) {
            printHelpMessage();
        }
    }

    private static void printHelpMessage() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < COMMANDS.size(); i++) {
            if (i != 0 && i != COMMANDS.size()) {
                sb.append(" | ");
            }

            sb.append(COMMANDS.get(i).getType());
        }
        System.out.println("\nAvailable commands : [ " + sb.toString() + " ]\n");
    }
}
