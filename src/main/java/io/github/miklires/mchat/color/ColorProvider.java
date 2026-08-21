package io.github.miklires.mchat.color;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import io.github.miklires.mchat.MChat;

import java.lang.reflect.Method;

public class ColorProvider {

    private final MChat plugin;
    private boolean hooked = false;
    private Method setPlaceholdersMethod;

    public ColorProvider(MChat plugin) {
        this.plugin = plugin;
    }

    public boolean tryHook() {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") == null) return false;
        if (Bukkit.getPluginManager().getPlugin("mColor") == null) return false;
        try {
            Class<?> papi = Class.forName("me.clip.placeholderapi.PlaceholderAPI");
            setPlaceholdersMethod = papi.getMethod("setPlaceholders", org.bukkit.OfflinePlayer.class, String.class);
            hooked = true;
            plugin.getLogger().info("mColor подключён — цвета никнеймов активны.");
            return true;
        } catch (Exception e) {
            plugin.getLogger().info("mColor найден, но интеграция не удалась: " + e.getMessage());
            return false;
        }
    }

    public String getColoredName(Player player) {
        if (!hooked) return player.getName();
        try {
            Object result = setPlaceholdersMethod.invoke(null, player, "%mcolor_name_mm%");
            if (result instanceof String s) {
                if (s.equals("%mcolor_name_mm%") || s.isBlank()) return player.getName();
                return s;
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Ошибка получения цвета: " + e.getMessage());
        }
        return player.getName();
    }

    public boolean isHooked() {
        return hooked;
    }
}
