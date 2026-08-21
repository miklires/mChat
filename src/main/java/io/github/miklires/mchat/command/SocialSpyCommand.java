package io.github.miklires.mchat.command;

import io.github.miklires.mchat.MChat;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class SocialSpyCommand implements CommandExecutor {
    private final MChat plugin;
    public SocialSpyCommand(MChat plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player) || !player.hasPermission("mchat.socialspy")) return true;
        boolean enabled = plugin.getSocialManager().toggleSpy(player.getUniqueId());
        player.sendMessage(MiniMessage.miniMessage().deserialize("<gray>Social spy: "
                + (enabled ? "<green>enabled" : "<red>disabled")));
        return true;
    }
}
