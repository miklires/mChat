package io.github.miklires.mchat.command;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import io.github.miklires.mchat.MChat;

public class GlobalCommand implements CommandExecutor {

    private final MChat plugin;

    public GlobalCommand(MChat plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) return true;
        if (!player.hasPermission("mchat.command.global")) {
            player.sendMessage(MiniMessage.miniMessage().deserialize("<red>Нет прав."));
            return true;
        }
        if (args.length < 1) {
            player.sendMessage(MiniMessage.miniMessage().deserialize("<red>Использование: /g <сообщение>"));
            return true;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < args.length; i++) {
            if (i > 0) sb.append(' ');
            sb.append(args[i]);
        }
        plugin.getMessageRouter().sendChat(player, sb.toString(), true);
        return true;
    }
}
