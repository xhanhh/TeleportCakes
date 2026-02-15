package top.ilov.mcmods.tc;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Data;
import top.ilov.mcmods.tc.platform.Services;

import java.io.BufferedWriter;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import static top.ilov.mcmods.tc.TeleportCakesMod.LOGGER;

@Data
public final class CakeConfig {

    private static final String FILE_NAME = "teleportcakes-common.json";

    private static final boolean DEFAULT_ENABLE_THE_SOUND_OF_EATING_CAKES = true;
    private static final boolean DEFAULT_ENABLE_TOOLTIPS_FOR_DISPLAYING_ITEM = true;

    private static final Gson GSON = new GsonBuilder()
            .disableHtmlEscaping()
            .setPrettyPrinting()
            .create();

    private boolean enable_the_sound_of_eating_cakes = DEFAULT_ENABLE_THE_SOUND_OF_EATING_CAKES;
    private boolean enable_tooltips_for_displaying_item = DEFAULT_ENABLE_TOOLTIPS_FOR_DISPLAYING_ITEM;

    private static Path configPath() {
        return Services.PLATFORM.getConfigDir().resolve(FILE_NAME);
    }

    public static CakeConfig loadConfig() {

        CakeConfig defaults = new CakeConfig();

        Path path = configPath();
        try {
            Files.createDirectories(path.getParent());
        } catch (Exception e) {
            LOGGER.warn("Failed to create config directory for {}.", path, e);
        }

        if (Files.notExists(path)) {
            write(defaults);
            return defaults;
        }

        JsonObject json = null;
        try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            JsonElement parsed = JsonParser.parseReader(reader);
            if (parsed != null && parsed.isJsonObject()) {
                json = parsed.getAsJsonObject();
            }
        } catch (Exception e) {
            LOGGER.warn("Failed to read config {}, using defaults.", path, e);
        }

        if (json == null) {
            write(defaults);
            return defaults;
        }

        CakeConfig loaded = null;
        try {
            loaded = GSON.fromJson(json, CakeConfig.class);
        } catch (Exception e) {
            LOGGER.warn("Failed to parse config {}, using defaults.", path, e);
        }
        if (loaded == null) {
            loaded = defaults;
        }

        boolean changed = false;
        if (!json.has("enable_the_sound_of_eating_cakes")) {
            loaded.enable_the_sound_of_eating_cakes = DEFAULT_ENABLE_THE_SOUND_OF_EATING_CAKES;
            changed = true;
        }
        if (!json.has("enable_tooltips_for_displaying_item")) {
            loaded.enable_tooltips_for_displaying_item = DEFAULT_ENABLE_TOOLTIPS_FOR_DISPLAYING_ITEM;
            changed = true;
        }

        if (changed) {
            write(loaded);
        }

        return loaded;

    }

    public static void write(CakeConfig cakeConfig) {

        if (cakeConfig == null) return;

        Path path = configPath();
        try {
            Files.createDirectories(path.getParent());
        } catch (Exception e) {
            LOGGER.warn("Failed to create config directory for {}.", path, e);
        }

        Path tmp = path.resolveSibling(path.getFileName() + ".tmp");

        try (BufferedWriter writer = Files.newBufferedWriter(tmp, StandardCharsets.UTF_8)) {
            writer.write(GSON.toJson(cakeConfig));
        } catch (Exception e) {
            LOGGER.warn("Failed to write temp config {}.", tmp, e);
            return;
        }

        try {
            Files.move(tmp, path, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (AtomicMoveNotSupportedException e) {
            try {
                Files.move(tmp, path, StandardCopyOption.REPLACE_EXISTING);
            } catch (Exception moveFallbackException) {
                LOGGER.warn("Failed to move config {} into place (fallback).", path, moveFallbackException);
            }
        } catch (Exception e) {
            LOGGER.warn("Failed to move config {} into place.", path, e);
        }

    }

}
