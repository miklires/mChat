package io.github.miklires.mchat.command;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import io.github.miklires.mchat.MChat;

import java.util.Optional;

public class ReplyCommand implements CommandExecutor {

    private final MChat plugin;

    public ReplyCommand(MChat plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) return true;
        if (!player.hasPermission("mchat.command.reply")) {
            player.sendMessage(MiniMessage.miniMessage().deserialize("<red>Нет прав."));
            return true;
        }
        if (args.length < 1) {
            player.sendMessage(MiniMessage.miniMessage().deserialize("<red>Использование: /r <сообщение>"));
            return true;
        }
        Optional<Player> target = plugin.getPrivateMessageManager().getReplyTarget(player);
        if (target.isEmpty()) {
            player.sendMessage(MiniMessage.miniMessage().deserialize(
                    plugin.getConfigManager().getNoReplyTarget()));
            return true;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < args.length; i++) {
            if (i > 0) sb.append(' ');
            sb.append(args[i]);
        }
        plugin.getPrivateMessageManager().deliver(player, target.get(), sb.toString());
        return true;
    }
}
