# Litematica Shulker Supply

Litematica Shulker Supply 是一个 Fabric 模组，为 Litematica 及其打印机添加“潜影盒供应”功能：当需要某种物品时，自动将其从潜影盒中取出并放入快捷栏。

## 依赖

本模组需要在客户端和服务端同时安装以下依赖：

- [Fabric API](https://modrinth.com/mod/fabric-api) ≥ 0.132.0
- [malilib](https://modrinth.com/mod/malilib) ≥ 0.25.4
- [Litematica](https://modrinth.com/mod/litematica) ≥ 0.23.3
- 可选：[Litematica Printer](https://modrinth.com/mod/litematica-printer) ≥ 3.2.1-sakura.8
- Minecraft 1.21.8 与 Fabric Loader ≥ 0.17.2
- Java 21

## 安装

1. 客户端与服务端**均需**安装本模组。
2. 将 JAR 文件放入各自的 `mods` 文件夹。

## 使用

1. 启动游戏并打开 **Litematica → 配置菜单 → 通用**。
   - 启用 **`shulkerSupply`**（界面中显示为 **“启用潜影盒自动补货”**）
   - 关闭 **`pickBlockShulkers`**（界面中显示为 **“选取有此方块的潜影盒”**）  
     > 若未关闭 `pickBlockShulkers`，模组会让位于 Litematica 的原生行为。
2. 将潜影盒放在**物品栏**中（而非放置在地面上）。
3. 像平常一样建造/打印，缺少物品时模组会从匹配的潜影盒整格交换到快捷栏。