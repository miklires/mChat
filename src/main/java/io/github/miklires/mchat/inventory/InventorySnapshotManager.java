package io.github.miklires.mchat.inventory;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import io.github.miklires.mchat.MChat;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class InventorySnapshotManager {

    private final MChat plugin;
    private final Map<String, Snapshot> snapshots = new ConcurrentHashMap<>();
    private final Map<UUID, String> latestByPlayer = new ConcurrentHashMap<>();

    public InventorySnapshotManager(MChat plugin) {
        this.plugin = plugin;
    }

    public String createSnapshot(Player player) {
        ItemStack[] main = clone(player.getInventory().getStorageContents());
        ItemStack[] armor = clone(player.getInventory().getArmorContents());
        ItemStack offhand = player.getInventory().getItemInOffHand() != null
                ? player.getInventory().getItemInOffHand().clone() : null;

        String previous = latestByPlayer.get(player.getUniqueId());
        if (previous != null) snapshots.remove(previous);

        String id = randomId();
        snapshots.put(id, new Snapshot(player.getName(), main, armor, offhand, System.currentTimeMillis()));
        latestByPlayer.put(player.getUniqueId(), id);
        return id;
    }

    public Snapshot get(String id) {
        Snapshot s = snapshots.get(id);
        if (s == null) return null;
        long ttlMs = plugin.getConfigManager().getSnapshotTtlMinutes() * 60_000L;
        if (System.currentTimeMillis() - s.createdAt > ttlMs) {
            snapshots.remove(id);
            return null;
        }
        return s;
    }

    public Inventory buildGui(Snapshot s) {
        String title = plugin.getConfigManager().getSnapshotGuiTitle()
                .replace("<player>", s.ownerName);
        var component = net.kyori.adventure.text.minimessage.MiniMessage.miniMessage().deserialize(title);
        Inventory inv = Bukkit.createInventory(new SnapshotHolder(s), 54, component);

        for (int i = 0; i < 9 && i < s.mainContents.length; i++) {
            inv.setItem(45 + i, s.mainContents[i]);
        }
        for (int i = 9; i < 36 && i < s.mainContents.length; i++) {
            inv.setItem(i - 9, s.mainContents[i]);
        }

        if (s.armorContents != null && s.armorContents.length >= 4) {
            inv.setItem(36, s.armorContents[3]);
            inv.setItem(37, s.armorContents[2]);
            inv.setItem(38, s.armorContents[1]);
            inv.setItem(39, s.armorContents[0]);
        }
        if (s.offhand != null) inv.setItem(40, s.offhand);
        return inv;
    }

    public void purgeExpired() {
        long ttlMs = plugin.getConfigManager().getSnapshotTtlMinutes() * 60_000L;
        long now = System.currentTimeMillis();
        snapshots.entrySet().removeIf(e -> now - e.getValue().createdAt > ttlMs);
    }

    private ItemStack[] clone(ItemStack[] src) {
        ItemStack[] out = new ItemStack[src.length];
        for (int i = 0; i < src.length; i++) {
            out[i] = src[i] != null ? src[i].clone() : null;
        }
        return out;
    }

    private String randomId() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    public static class Snapshot {
        public final String ownerName;
        public final ItemStack[] mainContents;
        public final ItemStack[] armorContents;
        public final ItemStack offhand;
        public final long createdAt;

        public Snapshot(String ownerName, ItemStack[] mainContents, ItemStack[] armorContents,
                        ItemStack offhand, long createdAt) {
            this.ownerName = ownerName;
            this.mainContents = mainContents;
            this.armorContents = armorContents;
            this.offhand = offhand;
            this.createdAt = createdAt;
        }
    }
}
