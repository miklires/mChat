# Changelog

## 1.0.0 - 2026-08-21

### Added

- Configuration-driven local, global, and staff channels.
- Private messages, replies, mentions, interactive tags, ignore lists, and social spy.
- Caps, flood, advertising, Zalgo, duplicate, cooldown, and custom regex filters.
- Paper Commands API registration and bounded inventory snapshots.
- LuckPerms prefixes, optional PlaceholderAPI support, and bStats telemetry.

### Changed

- Migrated the project to `io.github.miklires.mchat`, Java 25, and Paper 26.2.
- Reworked local recipient lookup to avoid scanning Bukkit's online-player collection per message.
