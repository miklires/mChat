package io.github.miklires.mchat.command;

import io.github.miklires.mchat.MChat;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Collectors;

public final class IgnoreListCommand implements CommandExecutor {
    private final MChat plugin;
    private final MiniMessage mm = MiniMessage.miniMessage();

    public IgnoreListCommand(MChat plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) return true;
        String names = plugin.getSocialManager().ignoredBy(player.getUniqueId()).stream()
                .map(Bukkit::getOfflinePlayer)
                .map(target -> target.getName() == null ? target.getUniqueId().toString() : target.getName())
                .sorted()
                .collect(Collectors.joining(", "));
        player.sendMessage(mm.deserialize(names.isEmpty()
                ? "<gray>Your ignore list is empty."
                : "<gray>Ignored: <white>" + names + "</white>"));
        return true;
    }
}
