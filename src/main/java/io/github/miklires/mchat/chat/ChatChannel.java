package io.github.miklires.mchat.chat;

public record ChatChannel(String id, String format, int radius, String readPermission,
                          String writePermission, int cooldownSeconds, String prefix, boolean log) {
    public boolean local() { return radius > 0; }
}
