package me.mouren.better_loading_screen.gui;

import me.mouren.better_loading_screen.BetterLoadingScreen;
import me.mouren.better_loading_screen.JsonConfig;
import me.mouren.better_loading_screen.utils.ConfigManager;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ConfigScreen {
    public static Screen create(Screen parent) {
        JsonConfig config = BetterLoadingScreen.config;

        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Component.literal("Better loading screen"))
                .setSavingRunnable(ConfigManager::save);

        var general = builder.getOrCreateCategory(
                Component.literal("General")
        );

        general.addEntry(
                builder.entryBuilder()
                        .startBooleanToggle(
                                Component.literal("Draw background"),
                                config.draw_background
                        )
                        .setDefaultValue(true)
                        .setSaveConsumer(value -> config.draw_background = value)
                        .build()
        );

        return builder.build();
    }
}
