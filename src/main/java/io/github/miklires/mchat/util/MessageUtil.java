package io.github.miklires.mchat.util;

import org.bukkit.configuration.file.YamlConfiguration;
import io.github.miklires.mchat.MChat;

import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class MessageUtil {

    private final MChat plugin;
    private YamlConfiguration messages;
    private File file;

    public MessageUtil(MChat plugin) {
        this.plugin = plugin;
        reload();
    }

    public void reload() {
        file = new File(plugin.getDataFolder(), "messages.yml");
        if (!file.exists()) {
            plugin.saveResource("messages.yml", false);
        }
        messages = YamlConfiguration.loadConfiguration(file);
        var defStream = plugin.getResource("messages.yml");
        if (defStream != null) {
            messages.setDefaults(YamlConfiguration.loadConfiguration(
                    new InputStreamReader(defStream, StandardCharsets.UTF_8)));
        }
    }

    public String get(String key) {
        String s = messages.getString(key);
        return s != null ? s : key;
    }

    public String prefix() {
        return get("prefix");
    }
}
