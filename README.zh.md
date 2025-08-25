# Litematica Shulker Supply

Litematica Shulker Supply 是一个 Fabric 模组，为 Litematica 及其打印机添加“潜影盒供应”功能：当需要某种物品时，将其从潜影盒中整格取出并放入快捷栏。

## 特性

- 客户端和服务端握手，确保双方均安装模组。
- 支持在打印模式下自动从潜影盒补充所需物品。
- 可配置开关以快速启用或禁用该功能。

## 依赖

本模组需要在客户端和服务端同时安装以下依赖：

- [Fabric API](https://modrinth.com/mod/fabric-api) ≥ 0.132.0
- [malilib](https://modrinth.com/mod/malilib) ≥ 0.25.4
- [Litematica](https://modrinth.com/mod/litematica) ≥ 0.23.3
- 可选：[Litematica Printer](https://modrinth.com/mod/litematica-printer) ≥ 3.2.1-sakura.8
- Minecraft 1.21.8 与 Fabric Loader ≥ 0.17.2
- Java 21

## 构建

```bash
./gradlew build
```

首次运行可能需要联网下载依赖。

## 配置

配置文件会在首次运行后生成于 `config/litematica-shulker-supply.json`，可在其中启用或关闭潜影盒供应功能。

## 许可

本项目基于 MIT License 发布。
