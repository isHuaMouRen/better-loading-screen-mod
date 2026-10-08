package me.mouren.better_loading_screen.gui;

import me.mouren.better_loading_screen.Main;
import me.mouren.better_loading_screen.models.JsonConfig;
import me.mouren.better_loading_screen.models.animation.AnimationType;
import me.mouren.better_loading_screen.utils.ConfigManager;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ConfigScreen {
    public static Screen create(Screen parent) {
        JsonConfig config = Main.config;

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
                                Component.translatable("config.betterloadingscreen.draw_minecraft_logo"),
                                config.draw_minecraft_logo
                        )
                        .setTooltip(Component.translatable("config.betterloadingscreen.draw_minecraft_logo.tooltip"))
                        .setDefaultValue(false)
                        .setSaveConsumer(value -> config.draw_minecraft_logo = value)
                        .build()
        );
        general.addEntry(
                builder.entryBuilder()
                        .startBooleanToggle(
                                Component.translatable("config.betterloadingscreen.draw_vanilla_chunks"),
                                config.draw_vanilla_chunks
                        )
                        .setTooltip(Component.translatable("config.betterloadingscreen.draw_vanilla_chunks.tooltip"))
                        .setDefaultValue(false)
                        .setSaveConsumer(value -> config.draw_vanilla_chunks = value)
                        .build()
        );
        general.addEntry(
                builder.entryBuilder()
                        .startBooleanToggle(
                                Component.translatable("config.betterloadingscreen.draw_background"),
                                config.draw_background
                        )
                        .setTooltip(Component.translatable("config.betterloadingscreen.draw_background.tooltip"))
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
                        .setTooltip(Component.translatable("config.betterloadingscreen.i18n_loading_text.tooltip"))
                        .setDefaultValue(false)
                        .setSaveConsumer(value -> config.i18n_loading_text = value)
                        .build()
        );
        general.addEntry(
                builder.entryBuilder()
                        .startIntField(
                                Component.translatable("config.betterloadingscreen.animation_frame_interval"),
                                config.animation_frame_interval
                        )
                        .setTooltip(Component.translatable("config.betterloadingscreen.animation_frame_interval.tooltip"))
                        .setDefaultValue(40)
                        .setSaveConsumer(value -> config.animation_frame_interval = value)
                        .build()
        );
        general.addEntry(
                builder.entryBuilder()
                        .startEnumSelector(
                                Component.translatable("config.betterloadingscreen.animation_type"),
                                AnimationType.class,
                                config.animation_type
                        )
                        .setTooltip(Component.translatable("config.betterloadingscreen.animation_type.tooltip"))
                        .setDefaultValue(AnimationType.ANIMATION)
                        .setSaveConsumer(value -> config.animation_type = value)
                        .build()
        );

        return builder.build();
    }
}
