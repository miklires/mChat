package io.github.miklires.mchat.chat;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import io.github.miklires.mchat.MChat;

public class ChatListener implements Listener {

    private final MChat plugin;
    private final MiniMessage mm = MiniMessage.miniMessage();

    public ChatListener(MChat plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onChat(AsyncChatEvent event) {
        var player = event.getPlayer();
        String raw = PlainTextComponentSerializer.plainText().serialize(event.message());

        AntiSpamManager.CheckResult check = plugin.getAntiSpamManager().check(player, raw);
        if (check.status() == AntiSpamManager.Status.COOLDOWN) {
            event.setCancelled(true);
            player.sendMessage(mm.deserialize(
                    plugin.getConfigManager().getCooldownMessage()
                            .replace("<seconds>", String.valueOf(check.cooldownLeft()))));
            return;
        }
        if (check.status() == AntiSpamManager.Status.DUPLICATE) {
            event.setCancelled(true);
            player.sendMessage(mm.deserialize(plugin.getConfigManager().getDuplicateMessage()));
            return;
        }

        event.setCancelled(true);

        plugin.getServer().getScheduler().runTask(plugin, () ->
                plugin.getMessageRouter().routeChat(player, raw));
    }
}
