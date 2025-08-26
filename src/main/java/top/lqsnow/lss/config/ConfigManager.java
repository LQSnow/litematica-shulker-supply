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
 * Manages loading and saving the mod's configuration. Values from
 * {@link Configs} (malilib's {@code ConfigBoolean}) are mapped to a simple
 * JSON file.
 */
public final class ConfigManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private final Path file;

    /**
     * @param file path to the configuration file
     */
    public ConfigManager(Path file) {
        this.file = file;
    }

    /**
     * Load configuration from disk into memory. Writes default configuration if
     * the file does not exist.
     */
    public void load() throws IOException {
        if (!Files.exists(file)) {
            // First run: write a file with default values
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
     * Save in-memory configuration back to disk.
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
