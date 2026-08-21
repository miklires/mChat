package io.github.miklires.mchat.join;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import io.github.miklires.mchat.MChat;

public class JoinQuitListener implements Listener {

    private final MChat plugin;
    private final MiniMessage mm = MiniMessage.miniMessage();

    public JoinQuitListener(MChat plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        event.joinMessage(null);
        if (!plugin.getConfigManager().isJoinEnabled()) return;
        if (plugin.getConfigManager().isWaitForAuth() && plugin.isMAuthHooked()) {
            return;
        }
        broadcastJoin(event.getPlayer());
    }

    public void broadcastJoin(Player player) {
        String template = plugin.getConfigManager().getJoinMessage()
                .replace("<player>", player.getName());
        Bukkit.broadcast(mm.deserialize(template));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        plugin.getAntiSpamManager().clear(event.getPlayer().getUniqueId());
        if (!plugin.getConfigManager().isQuitEnabled()) return;
        event.quitMessage(null);
        String template = plugin.getConfigManager().getQuitMessage()
                .replace("<player>", event.getPlayer().getName());
        Bukkit.broadcast(mm.deserialize(template));
    }
}
