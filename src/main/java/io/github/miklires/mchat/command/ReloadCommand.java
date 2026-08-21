package io.github.miklires.mchat.command;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import io.github.miklires.mchat.MChat;

public class ReloadCommand implements CommandExecutor {

    private final MChat plugin;

    public ReloadCommand(MChat plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("mchat.command.reload")) {
            sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>Нет прав."));
            return true;
        }
        plugin.getConfigManager().reload();
        sender.sendMessage(MiniMessage.miniMessage().deserialize("<green>Конфиг mChat перезагружен."));
        return true;
    }
}
