# Litematica Shulker Supply

Litematica Shulker Supply 是一个 Fabric 模组，为 Litematica 或其打印机添加“潜影盒整格供应”功能：
当需要某种物品时，将其从潜影盒中整格取出并放入快捷栏。

## 特性

- 客户端和服务端握手，确保双方均安装模组。
- 支持在打印模式下自动从潜影盒补充所需物品。
- 可配置开关以快速启用或禁用该功能。

## 构建

本项目使用 Gradle 构建：

```bash
./gradlew build
```

若首次运行可能需要联网下载依赖。

## 配置

配置文件会在首次运行后生成于 `config/litematica-shulker-supply.json`，
可在其中启用或关闭潜影盒供应功能。

## 许可

本项目基于 MIT License 发布。

