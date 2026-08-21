package io.github.miklires.mchat.command;

import io.github.miklires.mchat.MChat;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class IgnoreCommand implements CommandExecutor {
    private final MChat plugin;
    private final MiniMessage mm = MiniMessage.miniMessage();

    public IgnoreCommand(MChat plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) return true;
        if (args.length != 1) {
            player.sendMessage(mm.deserialize("<yellow>Usage: <white>/ignore <player>"));
            return true;
        }
        Player target = plugin.getPlayerDirectory().findExact(args[0]);
        if (target == null || target.equals(player)) {
            player.sendMessage(mm.deserialize(plugin.getConfigManager().getTargetOffline()));
            return true;
        }
        boolean ignored = plugin.getSocialManager().toggleIgnore(player.getUniqueId(), target.getUniqueId());
        player.sendMessage(mm.deserialize(ignored
                ? "<gray>You now ignore <white>" + target.getName() + "</white>."
                : "<gray>You no longer ignore <white>" + target.getName() + "</white>."));
        return true;
    }
}
