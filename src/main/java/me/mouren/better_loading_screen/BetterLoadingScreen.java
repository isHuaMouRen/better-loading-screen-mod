package me.mouren.better_loading_screen;

import me.mouren.better_loading_screen.utils.ConfigManager;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

public class BetterLoadingScreen implements ModInitializer {
    public static final String MOD_ID = "betterloadingscreen";

    // This logger is used to write text to the console and the log file.
    // It is considered best practice to use your mod id as the logger's name.
    // That way, it's clear which mod wrote info, warnings, and errors.
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static JsonConfig config = null;

    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        LOGGER.info("=====Initializing Better loading screen...=====");

        ConfigManager.load();

        LOGGER.info("=====Better loading screen initialize conpleted!=====");
    }
}