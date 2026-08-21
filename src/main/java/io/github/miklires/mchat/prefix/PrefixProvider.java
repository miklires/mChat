package io.github.miklires.mchat.prefix;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.cacheddata.CachedMetaData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import io.github.miklires.mchat.MChat;

public class PrefixProvider {

    private final MChat plugin;
    private LuckPerms luckPerms;
    private boolean available;

    public PrefixProvider(MChat plugin) {
        this.plugin = plugin;
        hook();
    }

    private void hook() {
        if (Bukkit.getPluginManager().getPlugin("LuckPerms") == null) {
            available = false;
            plugin.getLogger().info("LuckPerms не найден — префиксы в чате отключены.");
            return;
        }
        try {
            luckPerms = LuckPermsProvider.get();
            available = true;
            plugin.getLogger().info("LuckPerms подключён — префиксы активны.");
        } catch (IllegalStateException e) {
            available = false;
            plugin.getLogger().warning("LuckPerms API недоступен: " + e.getMessage());
        }
    }

    public String getPrefix(Player player) {
        if (!available) return "";
        try {
            var user = luckPerms.getUserManager().getUser(player.getUniqueId());
            if (user == null) return "";
            CachedMetaData meta = user.getCachedData().getMetaData();
            String prefix = meta.getPrefix();
            if (prefix == null || prefix.isEmpty()) return "";
            return convertLegacy(prefix);
        } catch (Exception e) {
            return "";
        }
    }

    private String convertLegacy(String input) {
        return input
                .replace("&0", "<black>").replace("&1", "<dark_blue>")
                .replace("&2", "<dark_green>").replace("&3", "<dark_aqua>")
                .replace("&4", "<dark_red>").replace("&5", "<dark_purple>")
                .replace("&6", "<gold>").replace("&7", "<gray>")
                .replace("&8", "<dark_gray>").replace("&9", "<blue>")
                .replace("&a", "<green>").replace("&b", "<aqua>")
                .replace("&c", "<red>").replace("&d", "<light_purple>")
                .replace("&e", "<yellow>").replace("&f", "<white>")
                .replace("&l", "<bold>").replace("&o", "<italic>")
                .replace("&n", "<underlined>").replace("&m", "<strikethrough>")
                .replace("&k", "<obfuscated>").replace("&r", "<reset>");
    }
}
