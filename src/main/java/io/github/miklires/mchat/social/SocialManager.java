package io.github.miklires.mchat.social;

import io.github.miklires.mchat.MChat;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class SocialManager {
    private final MChat plugin;
    private final File file;
    private final Map<UUID, Set<UUID>> ignored = new ConcurrentHashMap<>();
    private final Set<UUID> spies = ConcurrentHashMap.newKeySet();

    public SocialManager(MChat plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "ignored.yml");
        load();
    }

    public boolean toggleIgnore(UUID owner, UUID target) {
        Set<UUID> entries = ignored.computeIfAbsent(owner, ignoredOwner -> ConcurrentHashMap.newKeySet());
        boolean nowIgnored = entries.add(target);
        if (!nowIgnored) entries.remove(target);
        save();
        return nowIgnored;
    }

    public boolean ignores(UUID owner, UUID sender) {
        return ignored.getOrDefault(owner, Set.of()).contains(sender);
    }

    public Set<UUID> ignoredBy(UUID owner) {
        return Set.copyOf(ignored.getOrDefault(owner, Set.of()));
    }

    public boolean toggleSpy(UUID playerId) {
        return spies.add(playerId) || !spies.remove(playerId);
    }

    public boolean isSpy(UUID playerId) { return spies.contains(playerId); }

    public Set<UUID> spies() { return Set.copyOf(spies); }

    private void load() {
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
        for (String ownerText : yaml.getKeys(false)) {
            try {
                UUID owner = UUID.fromString(ownerText);
                Set<UUID> entries = ConcurrentHashMap.newKeySet();
                for (String targetText : yaml.getStringList(ownerText)) entries.add(UUID.fromString(targetText));
                ignored.put(owner, entries);
            } catch (IllegalArgumentException exception) {
                plugin.getLogger().warning("Skipped invalid UUID in ignored.yml: " + ownerText);
            }
        }
    }

    private void save() {
        YamlConfiguration yaml = new YamlConfiguration();
        ignored.forEach((owner, entries) -> yaml.set(owner.toString(),
                entries.stream().map(UUID::toString).sorted().toList()));
        try {
            yaml.save(file);
        } catch (IOException exception) {
            plugin.getLogger().severe("Could not save ignored.yml: " + exception.getMessage());
        }
    }
}
