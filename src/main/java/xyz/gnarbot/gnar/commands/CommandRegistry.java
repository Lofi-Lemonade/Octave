package xyz.gnarbot.gnar.commands;

import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;
import xyz.gnarbot.gnar.Bot;
import xyz.gnarbot.gnar.commands.admin.*;
import xyz.gnarbot.gnar.commands.general.*;
import xyz.gnarbot.gnar.commands.music.*;
import xyz.gnarbot.gnar.commands.music.dj.MoveCommand;
import xyz.gnarbot.gnar.commands.music.dj.StopCommand;
import xyz.gnarbot.gnar.commands.music.search.DiscordFMCommand;
import xyz.gnarbot.gnar.commands.music.search.PlayCommand;
import xyz.gnarbot.gnar.commands.music.search.SoundcloudCommand;
import xyz.gnarbot.gnar.commands.music.search.YoutubeCommand;
import xyz.gnarbot.gnar.commands.polls.PollCommand;
import xyz.gnarbot.gnar.commands.settings.SettingsDelegateCommand;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * A registry storing CommandExecutor entries for the bot.
 */
public class CommandRegistry {

    /**
     * The mapped registry of invoking key to the classes.
     */
    private final Map<String, CommandExecutor> commandEntryMap = new LinkedHashMap<>();

    private final TIntSet idSet = new TIntHashSet();

    public CommandRegistry(Bot bot) {
        register(new HelpCommand());
        register(new InviteBotCommand());
        register(new PingCommand());
        register(new SupportCommand());
        register(new VoteCommand());
        register(new UptimeCommand());
        register(new WhoIsCommand());
        register(new GuildInfoCommand());
        register(new BotInfoCommand());
        register(new DonateCommand());
        register(new RedeemCommand());
        register(new IAmCommand());
        register(new QuoteCommand());
        //End General Commands

        //Mod Commands
        register(new SettingsDelegateCommand());
        //End Mod Commands

        //Poll Commands
        register(new PollCommand());
        //End Poll Commands

        // Administrator commands
        register(new ShutdownCommand());
        register(new RestartShardsCommand());
        register(new EvalCommand());
        register(new UpdateBotAvatarCommand());
        //register(new UpdatePatreonTokenCommand());
        register(new ShardInfoCommand());
        register(new ThrowError());
        register(new PremiumKeyCommand());
        register(new SudoCommand());

        //MUSIC COMMAND
        if (bot.getConfiguration().getMusicEnabled()) {
            register(new PlayCommand());
            register(new PauseCommand());
            register(new StopCommand());
            register(new SkipCommand());
            register(new RemoveCommand());
            register(new MoveCommand());
            register(new ShuffleCommand());
            register(new NowPlayingCommand());
            register(new QueueCommand());
            register(new RestartCommand());
            register(new RepeatCommand());
            register(new VoteSkipCommand());
            register(new VolumeCommand());
            register(new JumpCommand());
            register(new BassBoostedCommand());
            register(new DiscordFMCommand());
            register(new CleanupCommand());
            register(new LyricsCommand());
            register(new FiltersCommand());
        } else {
            register(new DisabledPlayCommand());
        }

        register(new YoutubeCommand());
        register(new SoundcloudCommand());
    }

    public Map<String, CommandExecutor> getCommandMap() {
        return commandEntryMap;
    }

    private void register(CommandExecutor cmd) {
        Class<? extends CommandExecutor> cls = cmd.getClass();
        if (!cls.isAnnotationPresent(Command.class)) {
            throw new IllegalStateException("@Command annotation not found for class: " + cls.getName());
        }

        if (idSet.contains(cmd.getBotInfo().id())) {
            throw new IllegalStateException("@Command duplicate ID for class: " + cls.getName());
        } else {
            idSet.add(cmd.getBotInfo().id());
        }

        for (String alias : cmd.getInfo().aliases()) {
            registerCommand(alias, cmd);
        }
    }

    /**
     * Register the CommandExecutor instance into the registry.
     *
     * @param label Invoking key.
     * @param cmd   Command entry.
     */
    private void registerCommand(String label, CommandExecutor cmd) {
        if (commandEntryMap.containsKey(label.toLowerCase())) {
            throw new IllegalStateException("Command alias already registered: " + label);
        }
        commandEntryMap.put(label.toLowerCase(), cmd);
    }

    /**
     * Unregisters a CommandExecutor.
     *
     * @param label Invoking key.
     */
    public void unregisterCommand(String label) {
        commandEntryMap.remove(label);
    }

    /**
     * Returns the command registry.
     *
     * @return The command registry.
     */
    public Set<CommandExecutor> getEntries() {
        return new LinkedHashSet<>(commandEntryMap.values());
    }

    public CommandExecutor getCommand(String label) {
        return commandEntryMap.get(label);
    }
}
