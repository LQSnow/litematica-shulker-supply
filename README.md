# Litematica Shulker Supply

> Chinese version: **[README.zh.md](README.zh.md)**

A tiny Fabric mod that teaches **Litematica** (and optionally **Litematica Printer**) to auto‑restock from **Shulker Boxes** in your **player inventory**. When an item is missing, it is automatically taken from the Shulker Box and placed in the hotbar.

---

## Requirements

### Client:

- **Fabric API** ≥ 0.132.0
- **malilib** ≥ 0.25.4
- **Litematica** ≥ 0.23.3
- *(Optional)* **Litematica Printer** ≥ 3.2.1-sakura.8
- **Minecraft** 1.21.8 & **Fabric Loader** ≥ 0.17.2
- **Java 21**

### Server:
- **Fabric API** ≥ 0.132.0
- **Minecraft** 1.21.8 & **Fabric Loader** ≥ 0.17.2
- **Java 21**
---

## Installation

1. Install on **both client and server**.
2. Drop the mod JAR into each side’s `mods` folder.

## Usage

1. Launch the game and open **Litematica → Configuration menu → Generic**.
   - Enable **`shulkerSupply`**
   - **Disable** Litematica’s **`pickBlockShulkers`**
     > If `pickBlockShulkers` remains **enabled**, the mod defers to Litematica’s built‑in behavior and does nothing.
2. Put shulker boxes **in your inventory** (not placed in the world).
3. Build or print as usual — when an item is missing, the mod swaps from the first matching shulker slot into a valid hotbar slot and selects it.

---
