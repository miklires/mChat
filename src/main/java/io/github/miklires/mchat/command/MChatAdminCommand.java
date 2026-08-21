package io.github.miklires.mchat.command;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import io.github.miklires.mchat.MChat;

public class MChatAdminCommand implements CommandExecutor {

    private final MChat plugin;
    private final MiniMessage mm = MiniMessage.miniMessage();

    public MChatAdminCommand(MChat plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        String prefix = plugin.getMessageUtil().prefix();

        if (args.length == 0) {
            sender.sendMessage(mm.deserialize(prefix
                    + "<gray>Версия: <white>" + plugin.getDescription().getVersion()));
            sender.sendMessage(mm.deserialize(prefix
                    + "<yellow>/mchat reload</yellow> <gray>— перезагрузить конфиги"));
            return true;
        }

        if (!sender.hasPermission("mchat.admin")) {
            sender.sendMessage(mm.deserialize(prefix + plugin.getMessageUtil().get("common.no-permission")));
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            plugin.reloadConfig();
            plugin.getConfigManager().reload();
            plugin.getMessageUtil().reload();
            sender.sendMessage(mm.deserialize(prefix + plugin.getMessageUtil().get("common.reloaded")));
            return true;
        }

        sender.sendMessage(mm.deserialize(prefix + plugin.getMessageUtil().get("common.usage-reload")));
        return true;
    }
}
