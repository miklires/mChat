<div align="center">
  <h1>mChat</h1>
  <p>Paper-native chat channels with interactive messages and moderation controls.</p>

  <p>
    <a href="https://papermc.io/software/paper"><img alt="Paper" height="56" src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/supported/paper_vector.svg"></a>
    <a href="https://purpurmc.org"><img alt="Purpur" height="56" src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/supported/purpur_vector.svg"></a>
  </p>

  <p>
    <a href="https://github.com/miklires/mChat"><img alt="GitHub" src="https://tr7zw.github.io/uikit/social_buttons_icon/Github-Button-64.png"></a>
  </p>

  <p>
    <a href="https://bstats.org/plugin/bukkit/mChat/33354"><img alt="bStats" src="https://img.shields.io/badge/bStats-33354-2F9BE6?style=for-the-badge"></a>
    <img alt="Java 25" src="https://img.shields.io/badge/Java-25-5382A1?style=for-the-badge">
  </p>
</div>

## Development status

mChat is being rebuilt for its first public release. The current private development build includes local and global chat, private messages, interactive tags, mentions, LuckPerms prefixes, and configurable channel definitions. Moderation filters, ignore/socialspy, signed-message deletion, storage, and Folia verification are still in progress and are not advertised as released features.

## Requirements

- Java 25
- Paper or Purpur 26.2

## Build

```bash
./gradlew clean build
```

The development JAR is written to `build/libs/mChat-1.0.0.jar`.

## Telemetry and updates

mChat uses [bStats plugin ID 33354](https://bstats.org/plugin/bukkit/mChat/33354) for anonymous usage statistics. Disable it with `metrics.enabled: false`. Update checks are configured separately under `updates` and remain inactive until the Modrinth project exists.

Licensed under the MIT License.

