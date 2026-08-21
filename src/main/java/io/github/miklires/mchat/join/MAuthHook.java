package io.github.miklires.mchat.join;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.plugin.EventExecutor;
import io.github.miklires.mchat.MChat;

public class MAuthHook {

    private final MChat plugin;
    private boolean hooked = false;

    public MAuthHook(MChat plugin) {
        this.plugin = plugin;
    }

    public boolean tryHook() {
        if (Bukkit.getPluginManager().getPlugin("mAuth") == null) return false;
        try {
            Class<?> eventClass = Class.forName("io.github.miklires.mauth.api.PlayerAuthenticatedEvent");
            EventExecutor executor = (listener, event) -> {
                try {
                    Player p = (Player) event.getClass().getMethod("getPlayer").invoke(event);
                    if (p != null) plugin.getJoinQuitListener().broadcastJoin(p);
                } catch (Exception e) {
                    plugin.getLogger().warning("Ошибка обработки PlayerAuthenticatedEvent: " + e.getMessage());
                }
            };
            @SuppressWarnings("unchecked")
            Class<? extends Event> evt = (Class<? extends Event>) eventClass;
            Bukkit.getPluginManager().registerEvent(evt, new org.bukkit.event.Listener() {},
                    EventPriority.MONITOR, executor, plugin, true);
            hooked = true;
            plugin.getLogger().info("mAuth подключён — join-сообщения будут показаны после авторизации.");
            return true;
        } catch (ClassNotFoundException e) {
            plugin.getLogger().info("mAuth найден, но его API недоступен — join-сообщения как обычно.");
            return false;
        } catch (Exception e) {
            plugin.getLogger().warning("Ошибка подключения к mAuth API: " + e.getMessage());
            return false;
        }
    }

    public boolean isHooked() {
        return hooked;
    }
}
