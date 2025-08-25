package top.lqsnow.lss.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import top.lqsnow.lss.LitematicaShulkerSupply;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * 负责把 Configs 里的值（malilib 的 ConfigBoolean）映射到我们自己的 JSON 文件。
 */
public final class ConfigManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private final Path file;

    /**
     * @param file 配置文件路径
     */
    public ConfigManager(Path file) {
        this.file = file;
    }

    /**
     * 从磁盘读取配置到内存。若文件不存在则写入默认配置。
     */
    public void load() throws IOException {
        if (!Files.exists(file)) {
            // 首次运行：按默认值写一个文件
            save();
            return;
        }
        try (Reader r = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            JsonObject obj = GSON.fromJson(r, JsonObject.class);
            if (obj == null) obj = new JsonObject();

            boolean enabled = obj.has("enabled") ? obj.get("enabled").getAsBoolean() : Configs.ENABLED.getBooleanValue();
            Configs.ENABLED.setBooleanValue(enabled);
        } catch (Exception e) {
            LitematicaShulkerSupply.LOGGER.warn("[{}] Config load failed, using defaults. ({})",
                    LitematicaShulkerSupply.MOD_ID, e.getMessage());
        }
    }

    /**
     * 将内存中的配置写回磁盘。
     */
    public void save() {
        try {
            Files.createDirectories(file.getParent());
            JsonObject obj = new JsonObject();
            obj.addProperty("enabled", Configs.ENABLED.getBooleanValue());
            try (Writer w = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
                GSON.toJson(obj, w);
            }
        } catch (Exception e) {
            LitematicaShulkerSupply.LOGGER.warn("[{}] Config save failed: {}", LitematicaShulkerSupply.MOD_ID, e.getMessage());
        }
    }
}
