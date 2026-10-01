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
                .setTitle(Component.translatable("string.betterloadingscreen.modname"))
                .setSavingRunnable(ConfigManager::save);

        var general = builder.getOrCreateCategory(
                Component.translatable("string.betterloadingscreen.general")
        );

        general.addEntry(
                builder.entryBuilder()
                        .startBooleanToggle(
                                Component.translatable("config.betterloadingscreen.draw_background"),
                                config.draw_background
                        )
                        .setDefaultValue(true)
                        .setSaveConsumer(value -> config.draw_background = value)
                        .build()
        );
        general.addEntry(
                builder.entryBuilder()
                        .startBooleanToggle(
                                Component.translatable("config.betterloadingscreen.i18n_loading_text"),
                                config.i18n_loading_text
                        )
                        .setDefaultValue(false)
                        .setSaveConsumer(value -> config.i18n_loading_text = value)
                        .build()
        );

        return builder.build();
    }
}
