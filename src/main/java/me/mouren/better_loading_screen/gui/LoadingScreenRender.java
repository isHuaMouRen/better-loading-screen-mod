package me.mouren.better_loading_screen.gui;

import me.mouren.better_loading_screen.Main;
import me.mouren.better_loading_screen.models.ScreenSize;
import me.mouren.better_loading_screen.models.animation.AnimationInfo;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.LevelLoadingScreen;
import net.minecraft.client.multiplayer.LevelLoadTracker;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

public final class LoadingScreenRender {
    public LoadingScreenRender() {
    }

    public static final int PROGRESS_BAR_COLOR = 0xFF00FF00;
    public static final Component DEFAULT_LOADING_TEXT = Component.literal("§lLOADING...");


    /**
     * 绘制渐变背景
     *
     * @param height 背景的高度，从底部算起
     */
    public static void renderBackground(GuiGraphicsExtractor graphics, ScreenSize size, int height) {
        graphics.fillGradient(
                0,
                size.height - height,
                size.width,
                size.height,
                0x00000000,
                0x80000000
        );
    }

    /**
     * 绘制进度条
     *
     * @param progress 进度(0~1)
     */
    public static void renderProgressBar(GuiGraphicsExtractor graphics, ScreenSize size, float progress, int top) {
        int progressBarRight = Mth.clamp((int) (progress * size.width), 0, size.width);

        if (progressBarRight <= 0) return;

        graphics.fill(0, top, progressBarRight, size.height, PROGRESS_BAR_COLOR);
    }

    /**
     * 绘制加载动画。
     */
    public static void renderAnimation(GuiGraphicsExtractor graphics, ScreenSize size, AnimationInfo animation, int x, int y, float scale) {
        int frameTime = (int) (Main.config.animation_frame_interval * animation.frame_interval_scale());
        int currentFrame = (int) ((System.currentTimeMillis() / frameTime) % animation.frame());

        int textureV = currentFrame * animation.height();
        int textureHeight = animation.height() * animation.frame();

        float v0 = (float) textureV / textureHeight;
        float v1 = (float) (textureV + animation.height()) / textureHeight;

        graphics.pose().pushMatrix();

        graphics.pose().translate(x, y);
        graphics.pose().scale(scale, scale);

        graphics.blit(animation.resource(), 0, 0, animation.width(), animation.height(), 0.0F, 1.0F, v0, v1);

        graphics.pose().popMatrix();
    }

    /**
     * 绘制 LOADING 文本。
     */
    public static void renderLoadingText(GuiGraphicsExtractor graphics, ScreenSize size, Font font, float scale, int y) {
        var loadingText = Main.config.i18n_loading_text ? Component.translatable("string.betterloadingscreen.loading") : DEFAULT_LOADING_TEXT;

        graphics.pose().pushMatrix();
        graphics.pose().translate(6.0F, y);
        graphics.pose().scale(scale, scale);

        graphics.text(font, loadingText, 0, 0, 0xFFFFFFFF, true);

        graphics.pose().popMatrix();
    }

    /**
     * 绘制百分比。
     */
    public static void renderPercentText(GuiGraphicsExtractor graphics, ScreenSize size, float progress, Font font, int x, int y) {
        var percentText = String.format("%.2f%%", progress * 100.0F);

        int textWidth = font.width(percentText);

        int percentX = x - textWidth - 6;
        int percentY = y + 18;

        graphics.text(font, percentText, percentX, percentY, 0xFFFFFFFF, true);
    }

    /**
     * 绘制 Minecraft Logo。
     */
    public static void renderMinecraftLogo(GuiGraphicsExtractor graphics, ScreenSize size) {
        int logoWidth = 256;
        int logoHeight = 64;

        int logoX = (size.width - logoWidth) / 2;
        int logoY = 25;

        graphics.blit(Identifier.withDefaultNamespace("textures/gui/title/minecraft.png"), logoX, logoY, logoX + logoWidth, logoY + logoHeight, 0.0F, 1.0F, 0.0F, 1.0F);
    }

    /**
     * 绘制原版加载动画
     */
    public static void renderVanillaAnimation(GuiGraphicsExtractor graphics, LevelLoadTracker loadTracker, int y) {
        var statusView = loadTracker.statusView();

        if (statusView == null) return;

        int size = 2;
        int margin = 0;

        int diameter = statusView.radius() * 2 + 1;
        int totalWidth = diameter * (size + margin) - margin;

        int xCenter = 6 + totalWidth / 2;
        int yCenter = y - totalWidth / 2 - 5;

        LevelLoadingScreen.extractChunksForRendering(graphics, xCenter, yCenter, size, margin, statusView);
    }
}
