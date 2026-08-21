package io.github.miklires.mchat.chat;

import io.github.miklires.mchat.MChat;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Locale;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public final class ChatFilter {
    private static final Pattern URL = Pattern.compile("(?i)(?:https?://|www\\.|[a-z0-9-]+\\.(?:com|net|org|ru|su|gg|io|dev)\\b)");
    private static final Pattern IP = Pattern.compile("\\b(?:\\d{1,3}\\.){3}\\d{1,3}(?::\\d{1,5})?\\b");
    private final MChat plugin;

    public ChatFilter(MChat plugin) { this.plugin = plugin; }

    public Result check(String input) {
        ConfigurationSection section = plugin.getConfig().getConfigurationSection("moderation");
        if (section == null || !section.getBoolean("enabled", true)) return Result.allowed(input);
        int combining = (int) input.codePoints().filter(ChatFilter::isCombiningMark).count();
        if (combining > section.getInt("zalgo.max-combining-marks", 6)) return apply(input, "zalgo", section);
        int repeatLimit = Math.max(2, section.getInt("flood.max-repeated-characters", 6));
        if (Pattern.compile("(.)\\1{" + repeatLimit + ",}", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE)
                .matcher(input).find()) return apply(input, "flood", section);
        int letters = 0;
        int uppercase = 0;
        for (int codePoint : input.codePoints().toArray()) {
            if (Character.isLetter(codePoint)) {
                letters++;
                if (Character.isUpperCase(codePoint)) uppercase++;
            }
        }
        if (letters >= section.getInt("caps.min-letters", 8)
                && uppercase / (double) letters > section.getDouble("caps.max-ratio", 0.7D)) {
            return apply(input, "caps", section);
        }
        if (section.getBoolean("advertising.enabled", true)
                && (URL.matcher(input).find() || IP.matcher(input).find())) return apply(input, "advertising", section);
        for (String expression : section.getStringList("regex.patterns")) {
            try {
                if (Pattern.compile(expression, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE).matcher(input).find()) {
                    return apply(input, "regex", section);
                }
            } catch (PatternSyntaxException exception) {
                plugin.getLogger().warning("Invalid moderation regex: " + expression);
            }
        }
        return Result.allowed(input);
    }

    private Result apply(String input, String filter, ConfigurationSection section) {
        String action = section.getString(filter + ".action", "block").toLowerCase(Locale.ROOT);
        if (action.equals("replace")) return new Result(false, section.getString(filter + ".replacement", "***"), filter);
        return new Result(true, input, filter);
    }

    private static boolean isCombiningMark(int codePoint) {
        int type = Character.getType(codePoint);
        return type == Character.NON_SPACING_MARK || type == Character.COMBINING_SPACING_MARK
                || type == Character.ENCLOSING_MARK;
    }

    public record Result(boolean blocked, String message, String filter) {
        public static Result allowed(String message) { return new Result(false, message, ""); }
    }
}
