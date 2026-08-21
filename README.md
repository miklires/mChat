<div align="center">
  <h1>mChat</h1>
  <p>Paper-native chat channels with interactive messages and moderation controls.</p>

  <p>
    <a href="https://papermc.io/software/paper"><img alt="Paper" height="56" src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/supported/paper_vector.svg"></a>
    <a href="https://purpurmc.org"><img alt="Purpur" height="56" src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/supported/purpur_vector.svg"></a>
  </p>

  <p>
    <a href="https://github.com/miklires/mChat"><img alt="GitHub" src="https://tr7zw.github.io/uikit/social_buttons_icon/Github-Button-64.png"></a>
    <a href="https://modrinth.com/plugin/mchat"><img alt="Modrinth" src="https://tr7zw.github.io/uikit/social_buttons_icon/Modrinth-Button-64.png"></a>
  </p>

  <p>
    <a href="https://bstats.org/plugin/bukkit/mChat/33354"><img alt="bStats" src="https://img.shields.io/badge/bStats-33354-2F9BE6?style=for-the-badge"></a>
    <img alt="Java 25" src="https://img.shields.io/badge/Java-25-5382A1?style=for-the-badge">
  </p>
</div>

## Features

- Configuration-driven local, global, and staff channels
- Per-channel radius, format, prefix, cooldown, and read/write permissions
- Interactive `[item]`, `[inventory]`, `[xyz]`, `[xp]`, `[ping]`, `[tps]`, and `[online]` tags
- Private messages, replies, mentions, ignore lists, and staff social spy
- Caps, flood, advertising, Zalgo, and custom regex filters
- MiniMessage formatting guarded by permissions
- LuckPerms prefixes and optional PlaceholderAPI integration
- Bounded inventory snapshots with cooldown and expiry

## Requirements

- Java 25
- Paper or Purpur 26.2

## Build

```bash
./gradlew clean build
```

The JAR is written to `build/libs/mChat-1.0.0.jar`.

## Telemetry and updates

mChat uses [bStats plugin ID 33354](https://bstats.org/plugin/bukkit/mChat/33354) for anonymous usage statistics. Disable it with `metrics.enabled: false`.

Licensed under the MIT License.
