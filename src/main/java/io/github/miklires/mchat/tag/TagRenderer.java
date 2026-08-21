package io.github.miklires.mchat.tag;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import io.github.miklires.mchat.MChat;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TagRenderer {

    private static final Pattern TAG = Pattern.compile("\\[(xp|xyz|item|inventory|online|ping|tps)]",
            Pattern.CASE_INSENSITIVE);

    private final MChat plugin;
    private final MiniMessage mm = MiniMessage.miniMessage();

    public TagRenderer(MChat plugin) {
        this.plugin = plugin;
    }

    public String renderTags(Player sender, String input) {
        Matcher m = TAG.matcher(input);
        StringBuilder out = new StringBuilder();
        while (m.find()) {
            String tag = m.group(1).toLowerCase();
            String replacement;
            if (!sender.hasPermission("mchat.tag." + tag)) {
                replacement = m.group(0);
            } else {
                replacement = switch (tag) {
                    case "xp" -> renderXp(sender);
                    case "xyz" -> renderXyz(sender);
                    case "item" -> renderItem(sender);
                    case "inventory" -> renderInventory(sender);
                    case "online" -> renderOnline();
                    case "ping" -> renderPing(sender);
                    case "tps" -> renderTps();
                    default -> m.group(0);
                };
            }
            m.appendReplacement(out, Matcher.quoteReplacement(replacement));
        }
        m.appendTail(out);
        return out.toString();
    }

    private String renderXp(Player p) {
        return plugin.getConfigManager().getTagXp()
                .replace("<level>", String.valueOf(p.getLevel()));
    }

    private String renderXyz(Player p) {
        return plugin.getConfigManager().getTagXyz()
                .replace("<world>", p.getWorld().getName())
                .replace("<x>", String.valueOf(p.getLocation().getBlockX()))
                .replace("<y>", String.valueOf(p.getLocation().getBlockY()))
                .replace("<z>", String.valueOf(p.getLocation().getBlockZ()));
    }

    private String renderItem(Player p) {
        ItemStack item = p.getInventory().getItemInMainHand();
        if (item == null || item.getType().isAir()) {
            return plugin.getConfigManager().getTagItemEmpty();
        }
        String displayName;
        if (item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
            displayName = item.getItemMeta().getDisplayName();
        } else {
            displayName = readableMaterial(item.getType().name());
        }
        if (item.getAmount() > 1) displayName = displayName + " ×" + item.getAmount();
        return plugin.getConfigManager().getTagItemFormat()
                .replace("<material>", item.getType().getKey().toString())
                .replace("<amount>", String.valueOf(item.getAmount()))
                .replace("<nbt>", "{}")
                .replace("<name>", escapeMiniMessage(displayName));
    }

    private String renderInventory(Player p) {
        String id = plugin.getInventorySnapshotManager().createSnapshot(p);
        return plugin.getConfigManager().getTagInventory()
                .replace("<id>", id);
    }

    private String renderOnline() {
        int online = Bukkit.getOnlinePlayers().size();
        int max = Bukkit.getMaxPlayers();
        return plugin.getConfigManager().getTagOnline()
                .replace("<online>", String.valueOf(online))
                .replace("<max>", String.valueOf(max));
    }

    private String renderPing(Player p) {
        int ping = p.getPing();
        String color;
        if (ping < 100) color = plugin.getConfigManager().getPingGoodColor();
        else if (ping < 250) color = plugin.getConfigManager().getPingMidColor();
        else color = plugin.getConfigManager().getPingBadColor();
        return plugin.getConfigManager().getTagPing()
                .replace("<color>", color)
                .replace("<ping>", String.valueOf(ping));
    }

    private String renderTps() {
        double tps = Bukkit.getTPS()[0];
        String color;
        if (tps >= 19.0) color = plugin.getConfigManager().getTpsGoodColor();
        else if (tps >= 15.0) color = plugin.getConfigManager().getTpsMidColor();
        else color = plugin.getConfigManager().getTpsBadColor();
        return plugin.getConfigManager().getTagTps()
                .replace("<color>", color)
                .replace("<tps>", String.format("%.1f", tps));
    }

    private String readableMaterial(String material) {
        String[] parts = material.toLowerCase().split("_");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < parts.length; i++) {
            if (i > 0) sb.append(' ');
            sb.append(Character.toUpperCase(parts[i].charAt(0))).append(parts[i].substring(1));
        }
        return sb.toString();
    }

    private String escapeMiniMessage(String s) {
        return s.replace("<", "\\<");
    }

    public Component parse(String miniMessage) {
        return mm.deserialize(miniMessage);
    }
}
