package io.github.miklires.mchat.command;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import io.github.miklires.mchat.MChat;

public class MsgCommand implements CommandExecutor {

    private final MChat plugin;

    public MsgCommand(MChat plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) return true;
        if (!player.hasPermission("mchat.command.msg")) {
            player.sendMessage(MiniMessage.miniMessage().deserialize("<red>Нет прав."));
            return true;
        }
        if (args.length < 2) {
            player.sendMessage(MiniMessage.miniMessage().deserialize("<red>Использование: /m <ник> <сообщение>"));
            return true;
        }
        String target = args[0];
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            if (i > 1) sb.append(' ');
            sb.append(args[i]);
        }
        plugin.getPrivateMessageManager().send(player, target, sb.toString());
        return true;
    }
}
