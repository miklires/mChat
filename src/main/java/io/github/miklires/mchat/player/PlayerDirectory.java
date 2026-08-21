package io.github.miklires.mchat.player;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class PlayerDirectory implements Listener {

    private final Map<UUID, Entry> players = new ConcurrentHashMap<>();
    private final Map<String, UUID> names = new ConcurrentHashMap<>();

    public void track(Player player) {
        update(player, player.getLocation());
    }

    public void update(Player player, Location location) {
        Entry entry = new Entry(player, location.getWorld().getUID(),
                location.getX(), location.getY(), location.getZ());
        players.put(player.getUniqueId(), entry);
        names.put(player.getName().toLowerCase(Locale.ROOT), player.getUniqueId());
    }

    public Collection<Entry> entries() {
        return players.values();
    }

    public Entry get(UUID playerId) {
        return players.get(playerId);
    }

    public Player findExact(String name) {
        UUID playerId = names.get(name.toLowerCase(Locale.ROOT));
        Entry entry = playerId == null ? null : players.get(playerId);
        return entry == null ? null : entry.player();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        track(event.getPlayer());
    }

    @EventHandler(ignoreCancelled = true)
    public void onMove(PlayerMoveEvent event) {
        Location from = event.getFrom();
        Location to = event.getTo();
        if (from.getWorld() == to.getWorld()
                && from.getBlockX() == to.getBlockX()
                && from.getBlockY() == to.getBlockY()
                && from.getBlockZ() == to.getBlockZ()) {
            return;
        }
        update(event.getPlayer(), to);
    }

    @EventHandler(ignoreCancelled = true)
    public void onTeleport(PlayerTeleportEvent event) {
        update(event.getPlayer(), event.getTo());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        players.remove(player.getUniqueId());
        names.remove(player.getName().toLowerCase(Locale.ROOT), player.getUniqueId());
    }

    public record Entry(Player player, UUID worldId, double x, double y, double z) {
        public double distanceSquared(Entry other) {
            double dx = x - other.x;
            double dy = y - other.y;
            double dz = z - other.z;
            return dx * dx + dy * dy + dz * dz;
        }
    }
}
