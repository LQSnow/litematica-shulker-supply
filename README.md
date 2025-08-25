# Litematica Shulker Supply

> 中文版请见 **[README.zh.md](README.zh.md)**

A tiny Fabric mod that teaches **Litematica** (and optionally **Litematica Printer**) to auto-restock from **Shulker Boxes** in your **player inventory**: when a required item is missing, it swaps a full slot from the shulker directly into your hotbar.

---

## Features

- 🔗 **Client–Server handshake** – works only when both sides have the mod (single-player counts as both).
- 📦 **Auto restock from shulkers** – picks the first matching stack inside your carried shulker boxes and swaps the whole slot into a hotbar slot.
- 🖨️ **Printer aware** (optional) – before each print action, it ensures the right item is in hand, preventing wrong block placements during rapid switching.
- ⚙️ **Toggleable** – one switch to enable/disable.

> The mod **never** puts a shulker *into* a shulker. It also respects Litematica’s “pickable slots / avoid tools” rules.

---

## Requirements

Install on **both client and server**:

- **Fabric API** ≥ 0.132.0
- **malilib** ≥ 0.25.4
- **Litematica** ≥ 0.23.3
- *(Optional)* **Litematica Printer** ≥ 3.2.1-sakura.8
- **Minecraft** 1.21.8 & **Fabric Loader** ≥ 0.17.2
- **Java 21**

---

## Installation & Usage

1) **Install on both sides** (client & server).
2) Launch the game and open **Litematica → Config → Generic**:
    - Enable **`shulkerSupply`**  
      *(shown in Chinese UI as **“启用潜影盒自动补货”**)*
    - **Disable** Litematica’s **`pickBlockShulkers`**  
      *(shown in Chinese UI as **“选取有此方块的潜影盒”**)*
      > If `pickBlockShulkers` is **ON**, the mod intentionally **defers** to Litematica’s native behavior and does nothing.
3) Put shulker boxes **in your inventory** (not placed in the world).
4) Build/print as usual — when an item is missing, it swaps from the first matching shulker slot into a valid hotbar slot and selects it.

---

## How it works (short)

- Scans **your player inventory’s shulker boxes** (not chests, not placed shulkers).
- Finds the **first** slot containing the **required item**.
- Performs a **whole-slot swap**: *(shulker slot) ⇄ (chosen hotbar slot)*.
- Chooses a hotbar slot using Litematica’s own rules (empty pickable slot first, otherwise its target pick slot), and **skips** hotbar slots that contain a shulker.
- For Printer: validates **right before interaction** that you still hold the correct item; if not, it restocks again and cancels the action rather than placing a wrong block.

---

## Build

```bash
./gradlew build
