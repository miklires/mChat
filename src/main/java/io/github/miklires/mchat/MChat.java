package io.github.miklires.mchat;

import org.bukkit.plugin.java.JavaPlugin;
import org.bstats.bukkit.Metrics;
import io.github.miklires.mchat.chat.AntiSpamManager;
import io.github.miklires.mchat.chat.ChatListener;
import io.github.miklires.mchat.chat.MessageRouter;
import io.github.miklires.mchat.chat.ChatFilter;
import io.github.miklires.mchat.command.GlobalCommand;
import io.github.miklires.mchat.command.LocalCommand;
import io.github.miklires.mchat.command.MsgCommand;
import io.github.miklires.mchat.command.MChatAdminCommand;
import io.github.miklires.mchat.command.ReplyCommand;
import io.github.miklires.mchat.command.PaperCommandAdapter;
import io.github.miklires.mchat.config.ConfigManager;
import io.github.miklires.mchat.inventory.InventorySnapshotManager;
import io.github.miklires.mchat.inventory.InventoryViewCommand;
import io.github.miklires.mchat.join.JoinQuitListener;
import io.github.miklires.mchat.privatechat.PrivateMessageManager;
import io.github.miklires.mchat.tag.TagRenderer;
import io.github.miklires.mchat.player.PlayerDirectory;
import java.util.List;

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
    private PlayerDirectory playerDirectory;
    private ChatFilter chatFilter;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        saveResource("messages.yml", false);
        messageUtil = new io.github.miklires.mchat.util.MessageUtil(this);
        configManager = new ConfigManager(this);
        antiSpamManager = new AntiSpamManager(this);
        chatFilter = new ChatFilter(this);
        inventorySnapshotManager = new InventorySnapshotManager(this);
        tagRenderer = new TagRenderer(this);
        prefixProvider = new io.github.miklires.mchat.prefix.PrefixProvider(this);
        messageRouter = new MessageRouter(this);
        privateMessageManager = new PrivateMessageManager(this);
        playerDirectory = new PlayerDirectory();
        getServer().getOnlinePlayers().forEach(playerDirectory::track);

        joinQuitListener = new JoinQuitListener(this);
        getServer().getPluginManager().registerEvents(new ChatListener(this), this);
        getServer().getPluginManager().registerEvents(joinQuitListener, this);
        getServer().getPluginManager().registerEvents(playerDirectory, this);
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

        registerCommand("msg", "Send a private message", List.of("m", "tell", "w", "whisper"),
                new PaperCommandAdapter("msg", new MsgCommand(this)));
        registerCommand("reply", "Reply to the last private message", List.of("r"),
                new PaperCommandAdapter("reply", new ReplyCommand(this)));
        registerCommand("global", "Send a message to global chat", List.of("g"),
                new PaperCommandAdapter("global", new GlobalCommand(this)));
        registerCommand("local", "Send a message to local chat", List.of("l"),
                new PaperCommandAdapter("local", new LocalCommand(this)));
        registerCommand("mchat", "Manage mChat", List.of(),
                new PaperCommandAdapter("mchat", new MChatAdminCommand(this)));
        registerCommand("mchat-inv", "View an inventory snapshot", List.of(),
                new PaperCommandAdapter("mchat-inv", new InventoryViewCommand(this)));

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
    public PlayerDirectory getPlayerDirectory() { return playerDirectory; }
    public ChatFilter getChatFilter() { return chatFilter; }
}
