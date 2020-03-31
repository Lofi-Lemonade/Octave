package xyz.gnarbot.gnar.commands.media;

import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.apache.http.client.utils.URIBuilder;
import org.json.JSONObject;
import xyz.gnarbot.gnar.commands.*;
import xyz.gnarbot.gnar.utils.HttpUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;

@Command(
        aliases = {
                "action", "awoo", "bang", "blush", "clagwimoth", "cry", "cuddle", "dance", "hug",
                "insult", "jojo", "kiss", "lewd", "lick", "megumin", "neko", "nom", "owo", "pat",
                "poke", "pout", "rem", "shrug", "slap", "sleepy", "smile", "teehee", "smug", "stare",
                "thumbsup", "triggered", "wag", "waifu_insult", "wasted", "sumfuk", "dab", "tickle",
                "highfive", "banghead", "bite", "discord_memes", "nani", "initial_d", "delet_this",
                "poi", "thinking", "greet", "punch", "handholding"
        },
        description = "Unleash your unrelenting yet suppressed emotions.",
        usage = "(action)"
)
@BotInfo(
        id = 78,
        category = Category.MEDIA
)
public class ActionCommand extends CommandExecutor {
    private static String getImage(String type, String token) {
        String url;
        try {
            url = new URIBuilder("https://api.weeb.sh/images/random").addParameter("type", type).toString();
        } catch (URISyntaxException e) {
            return null;
        }

        Request request = new Request.Builder()
                .url(url)
                .header("Authorization", "Wolke " + token)
                .header("Accept", "application/json")
                .build();

        try (Response r = HttpUtils.CLIENT.newCall(request).execute()) {
            ResponseBody body = r.body();
            if (body == null) {
                return null;
            }

            return new JSONObject(body.string()).getString("url");
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public void execute(Context context, String label, String[] args) {
        String token = context.getBot().getCredentials().getWeebSh();

        if (token == null) {
            context.send().error("Weeb.Sh services are down.").queue();
            return;
        }

        String action;

        if (!label.equals("action")) {
            action = label;
        } else if (args.length > 0) {
            action = args[0];
        } else {
            context.send().issue("Try one of these actions: `_action " + Arrays.toString(getInfo().aliases()) + "`").queue();
            return;
        }

        if (!Arrays.asList(getInfo().aliases()).contains(action)) {
            context.send().issue("This isn't one of the available actions.").queue();
            return;
        }

        context.send().embed().setImage(getImage(action, token)).action().queue();
    }
}
