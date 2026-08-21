package io.github.miklires.mchat.inventory;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public class SnapshotHolder implements InventoryHolder {

    private final InventorySnapshotManager.Snapshot snapshot;

    public SnapshotHolder(InventorySnapshotManager.Snapshot snapshot) {
        this.snapshot = snapshot;
    }

    public InventorySnapshotManager.Snapshot getSnapshot() {
        return snapshot;
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        throw new UnsupportedOperationException("Snapshot inventory is bound externally.");
    }
}
