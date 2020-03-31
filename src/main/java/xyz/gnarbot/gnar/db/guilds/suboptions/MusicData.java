package xyz.gnarbot.gnar.db.guilds.suboptions;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

@JsonIgnoreProperties("djRole")
public class MusicData {
    private Set<String> musicChannels;
    private int volume = 100;
    private boolean announce = true;
    private int maxQueueSize;
    private long maxSongLength;
    private long voteSkipCooldown;
    private long voteSkipDuration;
    private boolean isVotePlay;
    private long votePlayCooldown;
    private long votePlayDuration;

    @NotNull
    public final Set<String> getChannels() {
        if (musicChannels == null) musicChannels = new HashSet<>();
        return musicChannels;
    }

    public int getMaxQueueSize() {
        return maxQueueSize;
    }

    public void setMaxQueueSize(int maxQueueSize) {
        this.maxQueueSize = maxQueueSize;
    }

    public void setMaxSongLength(long maxSongLength) {
        this.maxSongLength = maxSongLength;
    }

    public long getMaxSongLength() {
        return maxSongLength;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int musicVolume) {
        this.volume = musicVolume;
    }

    public boolean getAnnounce() {
        return announce;
    }

    public void setAnnounce(boolean announce) {
        this.announce = announce;
    }

    public long getVoteSkipCooldown() {
        return voteSkipCooldown;
    }

    public void setVoteSkipCooldown(long voteSkipCooldown) {
        this.voteSkipCooldown = voteSkipCooldown;
    }

    public long getVoteSkipDuration() {
        return voteSkipDuration;
    }

    public void setVoteSkipDuration(long voteSkipDuration) {
        this.voteSkipDuration = voteSkipDuration;
    }

    public boolean isVotePlay() {
        return isVotePlay;
    }

    public void setVotePlay(boolean votePlay) {
        isVotePlay = votePlay;
    }

    public long getVotePlayCooldown() {
        return votePlayCooldown;
    }

    public void setVotePlayCooldown(long votePlayCooldown) {
        this.votePlayCooldown = votePlayCooldown;
    }

    public long getVotePlayDuration() {
        return votePlayDuration;
    }

    public void setVotePlayDuration(long votePlayDuration) {
        this.votePlayDuration = votePlayDuration;
    }
}
