package io.github.miklires.mchat.chat;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import io.github.miklires.mchat.MChat;
import io.github.miklires.mchat.player.PlayerDirectory;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageRouter {

    private final MChat plugin;
    private final MiniMessage mm = MiniMessage.miniMessage();

    public MessageRouter(MChat plugin) {
        this.plugin = plugin;
    }

    public void routeChat(Player sender, String rawMessage) {
        ChatChannel channel = selectChannel(rawMessage);
        if (channel == null) return;
        String body = channel.prefix().isEmpty()
                ? rawMessage.trim() : rawMessage.substring(channel.prefix().length()).trim();
        if (body.isEmpty()) return;
        sendChat(sender, body, channel);
    }

    public void sendChat(Player sender, String body, boolean global) {
        ChatChannel channel = plugin.getConfigManager().getChannels().get(global ? "global" : "local");
        if (channel != null) sendChat(sender, body, channel);
    }

    public void sendChat(Player sender, String body, ChatChannel channel) {
        if (!channel.writePermission().isBlank() && !sender.hasPermission(channel.writePermission())) {
            sender.sendMessage(mm.deserialize(plugin.getMessageUtil().prefix()
                    + plugin.getMessageUtil().get("common.no-permission")));
            return;
        }
        ChatFilter.Result filtered = plugin.getChatFilter().check(body);
        if (filtered.blocked()) {
            sender.sendMessage(mm.deserialize(plugin.getConfigManager().getFilteredMessage()
                    .replace("<filter>", filtered.filter())));
            return;
        }
        body = filtered.message();
        String renderedBody = plugin.getTagRenderer().renderTags(sender, body);
        List<Player> mentioned = collectMentions(renderedBody);
        String highlighted = highlightedMessage(renderedBody, mentioned);

        String template = channel.format();

        String coloredName = plugin.getColorProvider().getColoredName(sender);

        String clickablePlayer = "<click:suggest_command:'/m " + sender.getName() + " '>"
                + "<hover:show_text:'Кликни чтобы написать в личку'>"
                + coloredName
                + "</hover></click>";

        String prefix = plugin.getPrefixProvider().getPrefix(sender);

        String composed = template
                .replace("<prefix>", prefix)
                .replace("<world>", sender.getWorld().getName())
                .replace("<player>", clickablePlayer)
                .replace("<message>", highlighted);

        Component finalMessage = mm.deserialize(composed);

        List<Player> recipients = recipientsFor(sender, channel);
        for (Player r : recipients) {
            r.sendMessage(finalMessage);
        }
        sender.sendMessage(finalMessage);

        playMentionSounds(mentioned);
    }

    private ChatChannel selectChannel(String message) {
        ChatChannel fallback = plugin.getConfigManager().getChannels().get("local");
        for (ChatChannel channel : plugin.getConfigManager().getChannels().values()) {
            if (!channel.prefix().isEmpty() && message.startsWith(channel.prefix())) return channel;
            if (fallback == null) fallback = channel;
        }
        return fallback;
    }

    private List<Player> recipientsFor(Player sender, ChatChannel channel) {
        List<Player> result = new ArrayList<>();
        PlayerDirectory.Entry source = plugin.getPlayerDirectory().get(sender.getUniqueId());
        if (source == null) return result;
        if (!channel.local()) {
            for (PlayerDirectory.Entry entry : plugin.getPlayerDirectory().entries()) {
                if (!entry.player().equals(sender) && canRead(entry.player(), sender, channel)) result.add(entry.player());
            }
            return result;
        }
        int radius = channel.radius();
        int radiusSq = radius * radius;
        for (PlayerDirectory.Entry entry : plugin.getPlayerDirectory().entries()) {
            if (entry.player().equals(sender)) continue;
            if (!entry.worldId().equals(source.worldId())) continue;
            if (canRead(entry.player(), sender, channel) && entry.distanceSquared(source) <= radiusSq) {
                result.add(entry.player());
            }
        }
        return result;
    }

    private boolean canRead(Player player, Player sender, ChatChannel channel) {
        if (plugin.getSocialManager().ignores(player.getUniqueId(), sender.getUniqueId())) return false;
        return channel.readPermission().isBlank() || player.hasPermission(channel.readPermission());
    }

    private List<Player> collectMentions(String body) {
        List<Player> result = new ArrayList<>();
        if (!plugin.getConfigManager().isMentionEnabled()) return result;
        String symbol = plugin.getConfigManager().getMentionSymbol();
        Pattern p = Pattern.compile(Pattern.quote(symbol) + "([a-zA-Z0-9_]{3,16})");
        Matcher m = p.matcher(body);
        while (m.find()) {
            Player target = plugin.getPlayerDirectory().findExact(m.group(1));
            if (target != null && !result.contains(target)) result.add(target);
        }
        return result;
    }

    private String highlightedMessage(String body, List<Player> mentioned) {
        if (mentioned.isEmpty()) return body;
        String highlight = plugin.getConfigManager().getMentionHighlight();
        String end = plugin.getConfigManager().getMentionHighlightEnd();
        String symbol = plugin.getConfigManager().getMentionSymbol();
        String result = body;
        for (Player p : mentioned) {
            String tag = symbol + p.getName();
            result = result.replace(tag, highlight + tag + end);
        }
        return result;
    }

    private void playMentionSounds(List<Player> mentioned) {
        if (mentioned.isEmpty()) return;
        Sound sound;
        try {
            sound = Sound.valueOf(plugin.getConfigManager().getMentionSound());
        } catch (IllegalArgumentException e) {
            return;
        }
        float volume = plugin.getConfigManager().getMentionSoundVolume();
        float pitch = plugin.getConfigManager().getMentionSoundPitch();
        for (Player p : mentioned) {
            p.playSound(p.getLocation(), sound, volume, pitch);
        }
    }
}
