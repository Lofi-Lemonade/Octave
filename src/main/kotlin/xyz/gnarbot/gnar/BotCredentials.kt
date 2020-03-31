package xyz.gnarbot.gnar

import ninja.leaping.configurate.hocon.HoconConfigurationLoader
import xyz.gnarbot.gnar.utils.get
import java.io.File

class BotCredentials(file: File) {
    private val loader = HoconConfigurationLoader.builder().setFile(file).build()

    private val config = loader.load()

    /* Discord */
    val token = config["token"].string.takeIf { !it.isNullOrBlank() }
            ?: error("Bot token can't be null or blank.")

    val totalShards = config["sharding", "total"].int.takeIf { it > 0 }
            ?: error("Shard count total needs to be > 0")
    val shardStart = config["sharding", "start"].int.takeIf { it >= 0 }
            ?: error("Shard start needs to be >= 0")
    val shardEnd = config["sharding", "end"].int.takeIf { it in shardStart..totalShards }
            ?: error("Shard end needs to be <= sharding.total")

    val webHookURL: String? = config["webhook url"].string

    /* Bot Lists */
    val carbonitex: String? = config["server counts", "carbonitex"].string
    val topGg: String? = config["server counts", "discordbots"].string
    val botsGg: String? = config["server counts", "botsgg"].string
    val botsForDiscord: String? = config["server counts", "bfd"].string
    val botsOnDiscord: String? = config["server counts", "bod"].string

    /* External APIs */
    val cat: String? = config["cat"].string
    val imgFlip: String? = config["imgflip"].string
    val mashape: String? = config["mashape"].string
    val weebSh: String? = config["weebsh"].string
    val riotAPIKey: String? = config["riot", "apiKey"].string
    val patreonToken: String? = config["patreon", "token"].string

    /* MyAnimeList */
    val malUsername: String? = config["mal credentials", "username"].string
    val malPassword: String? = config["mal credentials", "password"].string

    /* Audio */
    val spotifyClientId: String? = config["spotify", "clientid"].string
    val spotifyClientSecret: String? = config["spotify", "clientsecret"].string
    val discordFM: String? = config["discordfm"].string

    /* Database */
    val databaseURL: String? = config["db", "url"].string
    val databaseUsername: String? = config["db", "username"].string
    val databasePassword: String? = config["db", "password"].string
}