package io.github.miklires.mchat;

import org.bukkit.plugin.java.JavaPlugin;
import org.bstats.bukkit.Metrics;
import io.github.miklires.mchat.chat.AntiSpamManager;
import io.github.miklires.mchat.chat.ChatListener;
import io.github.miklires.mchat.chat.MessageRouter;
import io.github.miklires.mchat.command.GlobalCommand;
import io.github.miklires.mchat.command.LocalCommand;
import io.github.miklires.mchat.command.MsgCommand;
import io.github.miklires.mchat.command.MChatAdminCommand;
import io.github.miklires.mchat.command.ReplyCommand;
import io.github.miklires.mchat.config.ConfigManager;
import io.github.miklires.mchat.inventory.InventorySnapshotManager;
import io.github.miklires.mchat.inventory.InventoryViewCommand;
import io.github.miklires.mchat.join.JoinQuitListener;
import io.github.miklires.mchat.privatechat.PrivateMessageManager;
import io.github.miklires.mchat.tag.TagRenderer;

public class MChat extends JavaPlugin {

    private ConfigManager configManager;
    private io.github.miklires.mchat.util.MessageUtil messageUtil;
    private AntiSpamManager antiSpamManager;
    private InventorySnapshotManager inventorySnapshotManager;
    private TagRenderer tagRenderer;
    private MessageRouter messageRouter;
    private PrivateMessageManager privateMessageManager;
    private io.github.miklires.mchat.prefix.PrefixProvider prefixProvider;
    private JoinQuitListener joinQuitListener;
    private io.github.miklires.mchat.join.MAuthHook mAuthHook;
    private io.github.miklires.mchat.color.ColorProvider colorProvider;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        saveResource("messages.yml", false);
        messageUtil = new io.github.miklires.mchat.util.MessageUtil(this);
        configManager = new ConfigManager(this);
        antiSpamManager = new AntiSpamManager(this);
        inventorySnapshotManager = new InventorySnapshotManager(this);
        tagRenderer = new TagRenderer(this);
        prefixProvider = new io.github.miklires.mchat.prefix.PrefixProvider(this);
        messageRouter = new MessageRouter(this);
        privateMessageManager = new PrivateMessageManager(this);

        joinQuitListener = new JoinQuitListener(this);
        getServer().getPluginManager().registerEvents(new ChatListener(this), this);
        getServer().getPluginManager().registerEvents(joinQuitListener, this);
        getServer().getPluginManager().registerEvents(
                new io.github.miklires.mchat.inventory.SnapshotProtectionListener(), this);

        mAuthHook = new io.github.miklires.mchat.join.MAuthHook(this);
        mAuthHook.tryHook();

        colorProvider = new io.github.miklires.mchat.color.ColorProvider(this);
        colorProvider.tryHook();

        if (getConfig().getBoolean("metrics.enabled", true)) {
            int bstatsId = Math.max(0, getConfig().getInt("metrics.bstats-id", 33354));
            if (bstatsId > 0) new Metrics(this, bstatsId);
        }

        getCommand("msg").setExecutor(new MsgCommand(this));
        getCommand("reply").setExecutor(new ReplyCommand(this));
        getCommand("global").setExecutor(new GlobalCommand(this));
        getCommand("local").setExecutor(new LocalCommand(this));
        getCommand("mchat").setExecutor(new MChatAdminCommand(this));

        getServer().getCommandMap().register("mchat", new InventoryViewCommand(this));

        getServer().getScheduler().runTaskTimer(this,
                () -> inventorySnapshotManager.purgeExpired(),
                20L * 60L, 20L * 60L);

        getLogger().info("mChat enabled.");
    }

    @Override
    public void onDisable() {
        getLogger().info("mChat disabled.");
    }

    public ConfigManager getConfigManager() { return configManager; }
    public AntiSpamManager getAntiSpamManager() { return antiSpamManager; }
    public InventorySnapshotManager getInventorySnapshotManager() { return inventorySnapshotManager; }
    public TagRenderer getTagRenderer() { return tagRenderer; }
    public MessageRouter getMessageRouter() { return messageRouter; }
    public PrivateMessageManager getPrivateMessageManager() { return privateMessageManager; }
    public io.github.miklires.mchat.prefix.PrefixProvider getPrefixProvider() { return prefixProvider; }
    public io.github.miklires.mchat.util.MessageUtil getMessageUtil() { return messageUtil; }
    public JoinQuitListener getJoinQuitListener() { return joinQuitListener; }
    public boolean isMAuthHooked() { return mAuthHook != null && mAuthHook.isHooked(); }
    public io.github.miklires.mchat.color.ColorProvider getColorProvider() { return colorProvider; }
}
