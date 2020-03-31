package xyz.gnarbot.gnar.commands.music.search

import net.dv8tion.jda.api.EmbedBuilder
import xyz.gnarbot.gnar.commands.*
import xyz.gnarbot.gnar.music.MusicLimitException
import xyz.gnarbot.gnar.music.MusicManager
import xyz.gnarbot.gnar.music.TrackContext
import xyz.gnarbot.gnar.utils.desc
import xyz.gnarbot.gnar.utils.getDisplayValue
import java.util.concurrent.TimeUnit

@Command(
        aliases = ["play", "p"],
        usage = "[url|YT search]",
        description = "Plays music in a voice channel"
)
@BotInfo(
        id = 62,
        scope = Scope.VOICE,
        category = Category.MUSIC
)
class PlayCommand : CommandExecutor() {
    override fun execute(context: Context, label: String, args: Array<String>) {
        val botChannel = context.selfMember.voiceState?.channel
        val userChannel = context.voiceChannel

        if (botChannel != null && botChannel != userChannel) {
            context.send().issue("The bot is already playing music in another channel.").queue()
            return
        }
        val manager = context.bot.players.getExisting(context.guild)

        if (args.isEmpty()) {
            if (manager == null) {
                context.send().issue("There's no music player in this guild.\n" +
                        "\uD83C\uDFB6` ${config.prefix}play (song/url)` to start playing some music!").queue()
                return
            }

            when {
                manager.player.isPaused -> {
                    manager.player.isPaused = false
                    context.send().embed("Play Music") {
                        desc { "Music is no longer paused." }
                    }.action().queue()
                }
                manager.player.playingTrack != null -> {
                    context.send().error("Music is already playing. Are you trying to queue a track? Try adding a search term with this command!").queue()
                }
                manager.scheduler.queue.isEmpty() -> {
                    context.send().embed("Empty Queue") {
                        desc { "There is no music queued right now. Add some songs with `${config.prefix}play -song|url`." }
                    }.action().queue()
                }
            }
            return
        }

        if(context.data.music.isVotePlay) {
            startPlayVote(context, manager, args, false, "")
            return
        }

        play(context, args, false, "")
    }

    companion object {
        fun play(context: Context, args: Array<String>, isSearchResult: Boolean, uri: String) {
            val config = context.bot.configuration
            val manager = try {
                context.bot.players.get(context.guild)
            } catch (e: MusicLimitException) {
                e.sendToContext(context)
                return
            }

            if ("https://" in args[0] || "http://" in args[0]) {
                val link = args[0].removePrefix("<").removeSuffix(">")

                manager.loadAndPlay(
                        context,
                        link,
                        TrackContext(
                                context.member.user.idLong,
                                context.textChannel.idLong
                        ), "You can search and pick results using ${config.prefix}youtube or ${config.prefix}soundcloud while in a channel.")
            } else if (isSearchResult) { //As in, it comes from SoundcloudCommand or YoutubeCommand
                manager.loadAndPlay(
                        context,
                        uri,
                        TrackContext(
                                context.member.user.idLong,
                                context.textChannel.idLong
                        )
                )
            } else {
                if (!context.bot.configuration.searchEnabled) {
                    context.send().issue("Search is currently disabled. Try direct links instead.").queue()
                    return
                }

                val query = args.joinToString(" ").trim()
                manager.loadAndPlay(
                        context,
                        "ytsearch:$query",
                        TrackContext(
                                context.member.user.idLong,
                                context.textChannel.idLong
                        ), "You can search and pick results using ${config.prefix}youtube or ${config.prefix}soundcloud while in a channel.")
            }
        }

        fun startPlayVote(context: Context, manager: MusicManager?, args: Array<String>, isSearchResult: Boolean, uri: String) {
            if (manager!!.isVotingToPlay) {
                context.send().issue("There is already a vote going on!").queue()
                return
            }

            val voteSkipCooldown = if(context.data.music.votePlayCooldown <= 0) {
                context.bot.configuration.votePlayCooldown.toMillis()
            } else {
                context.data.music.votePlayCooldown
            }

            if (System.currentTimeMillis() - manager.lastPlayVoteTime < voteSkipCooldown) {
                context.send().issue("You must wait $voteSkipCooldown before starting a new vote.").queue()
                return
            }

            val voteSkipDuration = if(context.data.music.votePlayDuration == 0L) {
                context.data.music.votePlayDuration
            } else {
                context.bot.configuration.votePlayDuration.toMillis()
            }

            val voteSkipDurationText = if(context.data.music.votePlayDuration == 0L) {
                context.bot.configuration.votePlayDurationText
            } else {
                getDisplayValue(context.data.music.votePlayDuration)
            }

            manager.lastPlayVoteTime = System.currentTimeMillis()
            manager.isVotingToPlay = true
            val halfPeople = (context.selfMember.voiceState!!.channel!!.members.count() / 2 + 1)

            context.send().embed("Play Vote") {
                desc {
                    buildString {
                        append(context.message.author.asMention)
                        append(" has voted to **play** this track!")
                        append(" React with :thumbsup: or :thumbsdown:\n")
                        append("Whichever has the most votes in $voteSkipDurationText will win! This requires at least half of the people in the VC to agree!")
                    }
                }
            }.action().queue {
                it.addReaction("👍").queue()
                it.addReaction("👎").queue()

                it.editMessage(EmbedBuilder(it.embeds[0]).apply {
                    desc { "Voting has ended! Check the newer messages for results." }
                    clearFields()
                }.build()).queueAfter(voteSkipDuration, TimeUnit.MILLISECONDS) {
                    var skip = 0
                    var stay = 0

                    it.reactions.forEach {
                        if (it.reactionEmote.name == "👍") skip = it.count - 1
                        if (it.reactionEmote.name == "👎") stay = it.count - 1
                    }

                    context.send().embed("Vote Play") {
                        desc {
                            buildString {
                                if (skip > halfPeople) {
                                    appendln("The vote has passed! The song will be queued!")
                                    play(context, args, isSearchResult, uri)
                                } else {
                                    appendln("The vote has failed! The song will not be queued.")
                                }
                            }
                        }
                        field("Results") {
                            "__$skip Play Votes__ — __$stay No Play Votes__"
                        }
                    }.action().queue()
                    manager.isVotingToPlay = false
                }
            }
        }
    }
}
