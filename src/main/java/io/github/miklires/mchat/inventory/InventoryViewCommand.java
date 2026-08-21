package io.github.miklires.mchat.inventory;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import io.github.miklires.mchat.MChat;

public class InventoryViewCommand implements CommandExecutor {

    private final MChat plugin;

    public InventoryViewCommand(MChat plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) return true;
        if (args.length < 1) {
            player.sendMessage(MiniMessage.miniMessage().deserialize("<red>Использование: /mchat-inv <id>"));
            return true;
        }
        InventorySnapshotManager.Snapshot s = plugin.getInventorySnapshotManager().get(args[0]);
        if (s == null) {
            player.sendMessage(MiniMessage.miniMessage().deserialize("<red>Снапшот не найден или истёк."));
            return true;
        }
        player.openInventory(plugin.getInventorySnapshotManager().buildGui(s));
        return true;
    }
}
