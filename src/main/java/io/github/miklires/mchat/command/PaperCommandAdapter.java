package io.github.miklires.mchat.command;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.command.CommandExecutor;
import org.jetbrains.annotations.NotNull;

public final class PaperCommandAdapter implements BasicCommand {

    private final String label;
    private final CommandExecutor executor;

    public PaperCommandAdapter(String label, CommandExecutor executor) {
        this.label = label;
        this.executor = executor;
    }

    @Override
    public void execute(@NotNull CommandSourceStack source, @NotNull String[] args) {
        executor.onCommand(source.getSender(), null, label, args);
    }
}
