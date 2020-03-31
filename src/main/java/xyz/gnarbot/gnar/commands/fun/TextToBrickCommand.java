package xyz.gnarbot.gnar.commands.fun;

import org.apache.commons.lang3.StringUtils;
import xyz.gnarbot.gnar.commands.*;

@Command(
        aliases = "ttb",
        usage = "(words...)",
        description = "Text to bricks fun."
)
@BotInfo(
        id = 10,
        category = Category.FUN
)
public class TextToBrickCommand extends CommandExecutor {
    @Override
    public void execute(Context context, String label, String[] args) {
        if (args.length == 0) {
            context.getBot().getCommandDispatcher().sendHelp(context, getInfo());
            return;
        }

        char[] array = StringUtils.join(args, " ").toUpperCase().toCharArray();

        if (array.length > 100) {
            context.send().issue("Alright, that's way too long.").queue();
            return;
        }

        StringBuilder sb = new StringBuilder();

        for (char c : array) {
            if (Character.isLetter(c)) {
                sb.appendCodePoint(0x1F1E6 + c - 'A');
            } else if (Character.isDigit(c) || c == '#' || c == '*') {
                sb.append(c).append('\u20E3');
            } else {
                sb.append(c);
            }
            sb.append(' ');
        }

        if (sb.length() > 2000) {
            context.send().issue("Too many characters.").queue();
            return;
        }

        context.send().text(sb.toString()).queue();
    }
}
