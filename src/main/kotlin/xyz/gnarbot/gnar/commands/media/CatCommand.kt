package xyz.gnarbot.gnar.commands.media

import okhttp3.Call
import okhttp3.Callback
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import xyz.gnarbot.gnar.commands.*
import xyz.gnarbot.gnar.utils.HttpUtils
import java.io.IOException

@Command(aliases = ["cats", "cat"], description = "Grab random cats for you.")
@BotInfo(id = 24, category = Category.MEDIA)

class CatCommand : CommandExecutor() {
    override fun execute(context: Context, label: String, args: Array<String>) {
        val request = Request.Builder()
                .url("http://aws.random.cat/meow")
                .addHeader("X-API-Key", context.bot.credentials.cat!!)
                .build()

        HttpUtils.CLIENT.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                call.cancel()
                context.send().error("Error grabbing cat pics, blame the API")
            }

            override fun onResponse(call: Call, response: Response) {
                try {
                    val obj = JSONObject(response.body()!!.string())
                    val catUrl = obj.getString("file")
                    context.send().embed()
                            .setImage(catUrl)
                            .action().queue()
                } catch (e: NullPointerException) {
                    context.send().error("A developer dun messed up, pls report this to Melmsie#0001")
                }
            }
        })

    }
}