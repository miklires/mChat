package io.github.miklires.mchat.chat;

import org.bukkit.entity.Player;
import io.github.miklires.mchat.MChat;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class AntiSpamManager {

    private final MChat plugin;
    private final Map<UUID, Long> lastMessageAt = new ConcurrentHashMap<>();
    private final Map<UUID, String> lastMessageText = new ConcurrentHashMap<>();

    public AntiSpamManager(MChat plugin) {
        this.plugin = plugin;
    }

    public CheckResult check(Player player, String message) {
        if (player.hasPermission("mchat.bypass.cooldown")) {
            lastMessageText.put(player.getUniqueId(), message);
            lastMessageAt.put(player.getUniqueId(), System.currentTimeMillis());
            return CheckResult.ok();
        }
        long now = System.currentTimeMillis();
        long cooldownMs = plugin.getConfigManager().getCooldownSeconds() * 1000L;
        Long last = lastMessageAt.get(player.getUniqueId());
        if (last != null && now - last < cooldownMs) {
            long left = (cooldownMs - (now - last) + 999) / 1000;
            return CheckResult.cooldown((int) left);
        }
        if (plugin.getConfigManager().isBlockDuplicates()) {
            String prev = lastMessageText.get(player.getUniqueId());
            if (prev != null && prev.equalsIgnoreCase(message)) {
                return CheckResult.duplicate();
            }
        }
        lastMessageAt.put(player.getUniqueId(), now);
        lastMessageText.put(player.getUniqueId(), message);
        return CheckResult.ok();
    }

    public void clear(UUID uuid) {
        lastMessageAt.remove(uuid);
        lastMessageText.remove(uuid);
    }

    public record CheckResult(Status status, int cooldownLeft) {
        public static CheckResult ok() { return new CheckResult(Status.OK, 0); }
        public static CheckResult cooldown(int sec) { return new CheckResult(Status.COOLDOWN, sec); }
        public static CheckResult duplicate() { return new CheckResult(Status.DUPLICATE, 0); }
    }

    public enum Status { OK, COOLDOWN, DUPLICATE }
}
