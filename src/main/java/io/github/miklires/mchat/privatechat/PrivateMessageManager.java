package io.github.miklires.mchat.privatechat;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import io.github.miklires.mchat.MChat;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PrivateMessageManager {

    private final MChat plugin;
    private final MiniMessage mm = MiniMessage.miniMessage();
    private final Map<UUID, UUID> lastChat = new ConcurrentHashMap<>();

    public PrivateMessageManager(MChat plugin) {
        this.plugin = plugin;
    }

    public boolean send(Player sender, String targetName, String message) {
        if (sender.getName().equalsIgnoreCase(targetName)) {
            sender.sendMessage(mm.deserialize(plugin.getConfigManager().getSelfMessage()));
            return false;
        }
        Player target = Bukkit.getPlayerExact(targetName);
        if (target == null) {
            sender.sendMessage(mm.deserialize(plugin.getConfigManager().getTargetOffline()));
            return false;
        }
        deliver(sender, target, message);
        return true;
    }

    public Optional<Player> getReplyTarget(Player sender) {
        UUID lastUuid = lastChat.get(sender.getUniqueId());
        if (lastUuid == null) return Optional.empty();
        Player p = Bukkit.getPlayer(lastUuid);
        return Optional.ofNullable(p);
    }

    public void deliver(Player sender, Player target, String message) {
        String rendered = plugin.getTagRenderer().renderTags(sender, message);

        String senderLine = plugin.getConfigManager().getPrivateFormatSender()
                .replace("<target>", target.getName())
                .replace("<message>", rendered);
        String targetLine = plugin.getConfigManager().getPrivateFormatTarget()
                .replace("<sender>", sender.getName())
                .replace("<message>", rendered);

        sender.sendMessage(mm.deserialize(senderLine));
        target.sendMessage(mm.deserialize(targetLine));

        lastChat.put(sender.getUniqueId(), target.getUniqueId());
        lastChat.put(target.getUniqueId(), sender.getUniqueId());
    }
}
