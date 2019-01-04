import static org.fusesource.jansi.Ansi.ansi;

import cli.util.ConsoleColors;
import org.fusesource.jansi.Ansi.Color;

/**
 * @author zacconding
 * @Date 2018-12-30
 * @GitHub : https://github.com/zacscoding
 */
public class Temp {

    public static void main(String[] args) {
        System.out.println(
            ansi().eraseScreen().fg(Color.RED).a("Its red..").fg(Color.GREEN).a("New world..").reset()
        );

        //System.out.println(ansi);
    }

}
