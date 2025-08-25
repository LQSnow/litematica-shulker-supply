# Litematica Shulker Supply

Litematica Shulker Supply is a Fabric mod that adds a "shulker box full-slot supply" feature for Litematica and compatible printers. When an item is needed, the mod takes it from a shulker box and swaps it into the player's hotbar.

## Features

- Client and server handshake to ensure both sides have the mod installed.
- Automatically restock required items from shulker boxes while printing.
- Toggleable setting to quickly enable or disable the feature.

## Dependencies

The following dependencies must be installed on both client and server:

- [Fabric API](https://modrinth.com/mod/fabric-api) ≥ 0.132.0
- [malilib](https://modrinth.com/mod/malilib) ≥ 0.25.4
- [Litematica](https://modrinth.com/mod/litematica) ≥ 0.23.3
- Optional: [Litematica Printer](https://modrinth.com/mod/litematica-printer) ≥ 3.2.1-sakura.8
- Minecraft 1.21.8 and Fabric Loader ≥ 0.17.2
- Java 21

## Build

```bash
./gradlew build
```

The first run may download dependencies.

## Configuration

A config file is generated at `config/litematica-shulker-supply.json` after first launch. Use it to enable or disable the shulker supply feature.

## License

MIT License.

- [中文说明 / Chinese README](README.zh.md)
