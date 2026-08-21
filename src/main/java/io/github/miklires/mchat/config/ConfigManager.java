package io.github.miklires.mchat.config;

import org.bukkit.configuration.file.FileConfiguration;
import io.github.miklires.mchat.MChat;
import io.github.miklires.mchat.chat.ChatChannel;

import java.util.LinkedHashMap;
import java.util.Map;

public class ConfigManager {

    private final MChat plugin;

    public ConfigManager(MChat plugin) {
        this.plugin = plugin;
    }

    public void reload() {
        plugin.reloadConfig();
    }

    private FileConfiguration cfg() {
        return plugin.getConfig();
    }

    public String getChatFormat() { return cfg().getString("channels.local.format"); }
    public String getGlobalFormat() { return cfg().getString("channels.global.format"); }
    public int getLocalRadius() { return Math.max(1, cfg().getInt("channels.local.radius", 50)); }
    public int getCooldownSeconds() { return Math.max(0, cfg().getInt("channels.local.cooldown-seconds", 2)); }
    public String getCooldownMessage() { return plugin.getMessageUtil().get("chat.cooldown"); }
    public boolean isBlockDuplicates() { return cfg().getBoolean("anti-spam.block-duplicates", true); }
    public String getDuplicateMessage() { return plugin.getMessageUtil().get("chat.duplicate"); }
    public String getFilteredMessage() { return plugin.getMessageUtil().get("chat.filtered"); }

    public Map<String, ChatChannel> getChannels() {
        Map<String, ChatChannel> channels = new LinkedHashMap<>();
        var root = cfg().getConfigurationSection("channels");
        if (root == null) return channels;
        for (String id : root.getKeys(false)) {
            var section = root.getConfigurationSection(id);
            if (section == null || !section.getBoolean("enabled", true)) continue;
            channels.put(id, new ChatChannel(id,
                    section.getString("format", "<player>: <message>"),
                    Math.max(0, section.getInt("radius", 0)),
                    section.getString("permission-read", ""),
                    section.getString("permission-write", ""),
                    Math.max(0, section.getInt("cooldown-seconds", 0)),
                    section.getString("prefix-char", ""),
                    section.getBoolean("log", true)));
        }
        return channels;
    }

    public String getPrivateFormatSender() { return cfg().getString("private.format-sender"); }
    public String getPrivateFormatTarget() { return cfg().getString("private.format-target"); }
    public String getSelfMessage() { return plugin.getMessageUtil().get("private.self-message"); }
    public String getTargetOffline() { return plugin.getMessageUtil().get("private.target-offline"); }
    public String getNoReplyTarget() { return plugin.getMessageUtil().get("private.no-reply-target"); }

    public boolean isMentionEnabled() { return cfg().getBoolean("mention.enabled", true); }
    public String getMentionSymbol() { return cfg().getString("mention.symbol", "@"); }
    public String getMentionHighlight() { return cfg().getString("mention.highlight"); }
    public String getMentionHighlightEnd() { return cfg().getString("mention.highlight-end"); }
    public String getMentionSound() { return cfg().getString("mention.sound", "BLOCK_NOTE_BLOCK_PLING"); }
    public float getMentionSoundVolume() { return (float) cfg().getDouble("mention.sound-volume", 0.5); }
    public float getMentionSoundPitch() { return (float) cfg().getDouble("mention.sound-pitch", 1.5); }

    public boolean isJoinEnabled() { return cfg().getBoolean("join.enabled", true); }
    public boolean isWaitForAuth() { return cfg().getBoolean("join.wait-for-auth", true); }
    public String getJoinMessage() { return cfg().getString("join.message"); }
    public boolean isQuitEnabled() { return cfg().getBoolean("quit.enabled", true); }
    public String getQuitMessage() { return cfg().getString("quit.message"); }

    public String getTagXp() { return cfg().getString("tags.xp"); }
    public String getTagXyz() { return cfg().getString("tags.xyz"); }
    public String getTagItemEmpty() { return cfg().getString("tags.item-empty"); }
    public String getTagItemFormat() { return cfg().getString("tags.item-format"); }
    public String getTagInventory() { return cfg().getString("tags.inventory"); }
    public String getTagOnline() { return cfg().getString("tags.online"); }
    public String getTagPing() { return cfg().getString("tags.ping"); }
    public String getTagTps() { return cfg().getString("tags.tps"); }
    public String getPingGoodColor() { return cfg().getString("tags.ping-good"); }
    public String getPingMidColor() { return cfg().getString("tags.ping-mid"); }
    public String getPingBadColor() { return cfg().getString("tags.ping-bad"); }
    public String getTpsGoodColor() { return cfg().getString("tags.tps-good"); }
    public String getTpsMidColor() { return cfg().getString("tags.tps-mid"); }
    public String getTpsBadColor() { return cfg().getString("tags.tps-bad"); }

    public int getSnapshotTtlMinutes() { return cfg().getInt("inventory-snapshot.ttl-minutes", 60); }
    public int getSnapshotTagCooldownSeconds() { return cfg().getInt("inventory-snapshot.tag-cooldown-seconds", 15); }
    public int getMaxSnapshotsPerPlayer() { return Math.max(1, cfg().getInt("inventory-snapshot.max-active-per-player", 3)); }
    public String getSnapshotGuiTitle() { return cfg().getString("inventory-snapshot.gui-title"); }
}
