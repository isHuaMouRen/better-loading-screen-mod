package me.mouren.better_loading_screen.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.mouren.better_loading_screen.BetterLoadingScreen;
import me.mouren.better_loading_screen.JsonConfig;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class ConfigManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path ConfigPath = FabricLoader.getInstance().getConfigDir().resolve("betterloadingscreen.json");

    private ConfigManager() {
    }

    public static void load() {
        try {
            if (Files.exists(ConfigPath)) {
                String json = Files.readString(ConfigPath);
                BetterLoadingScreen.config = GSON.fromJson(json, JsonConfig.class);
                return;
            }
        } catch (Exception e) {
            e.fillInStackTrace();
        }

        BetterLoadingScreen.config = new JsonConfig();
        save();
    }

    public static void save() {
        try {
            Files.createDirectories(ConfigPath.getParent());

            String json = GSON.toJson(BetterLoadingScreen.config);

            Files.writeString(ConfigPath, json);
        } catch (IOException e) {
            e.fillInStackTrace();
        }
    }
}
