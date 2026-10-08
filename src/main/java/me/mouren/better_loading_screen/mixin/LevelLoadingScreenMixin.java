package me.mouren.better_loading_screen.mixin;

import me.mouren.better_loading_screen.Main;
import me.mouren.better_loading_screen.models.animation.AnimationInfo;
import me.mouren.better_loading_screen.models.animation.Animations;
import me.mouren.better_loading_screen.models.animation.AnimationType;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.LevelLoadingScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelLoadingScreen.class)
public class LevelLoadingScreenMixin extends Screen {

    // =========================
    // 常量
    // =========================

    private static final int BAR_HEIGHT = 4;

    private static final float TEXT_SCALE = 3.0F;

    private static final int TEXT_COLOR = 0xFFFFFFFF;
    private static final int PROGRESS_BAR_COLOR = 0xFF00FF00;

    private static final Component DEFAULT_LOADING_TEXT = Component.literal("§lLOADING...");

    @Shadow
    private float smoothedProgress;

    protected LevelLoadingScreenMixin(Component title) {
        super(title);
    }

    @Inject(method = "extractRenderState", at = @At("HEAD"), cancellable = true)
    private void onExtractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        // 取消原版渲染
        ci.cancel();

        boolean hasProgress = smoothedProgress > 0.0F;

        int width = this.width;
        int height = this.height;

        int bottom = height;
        int top = bottom - BAR_HEIGHT;

        // 背景
        if (Main.config.draw_background)
            renderBackground(graphics, width, height);

        // 进度条
        if (hasProgress)
            renderProgressBar(graphics, width, top, bottom);

        // 当前动画
        AnimationInfo animation = getAnimation();

        // 文字布局
        int scaledTextHeight = (int) (9 * TEXT_SCALE);
        int textY = top - scaledTextHeight - 5;

        int scaledAnimationWidth = (int) (animation.width() * TEXT_SCALE);
        int animationX = width - scaledAnimationWidth - 6;

        // LOADING
        renderLoadingText(graphics, textY);

        // 动画
        renderAnimation(graphics, animation, animationX, textY);

        // 百分比
        if (hasProgress)
            renderProgressPercentage(graphics, animationX, textY);

        // Minecraft logo
        if (Main.config.draw_minecraft_logo)
            renderMinecraftLogo(graphics, width);
    }

    /**
     * 获取当前配置选择的动画。
     */
    private AnimationInfo getAnimation() {
        return switch (Main.config.animation_type) {
            case ANIMATION -> Animations.ANIMATION;
            case SPIN -> Animations.SPIN;
        };
    }

    /**
     * 绘制底部黑色渐变背景。
     */
    private void renderBackground(GuiGraphicsExtractor graphics, int width, int height) {
        int gradientTop = height / 3;

        graphics.fillGradient(
                0,
                gradientTop,
                width,
                height,
                0x00000000,
                0x80000000
        );
    }

    /**
     * 绘制进度条。
     */
    private void renderProgressBar(GuiGraphicsExtractor graphics, int width, int top, int bottom) {
        int progressBarRight = Mth.clamp(
                (int) (this.smoothedProgress * width),
                0,
                width
        );

        if (progressBarRight <= 0) {
            return;
        }

        graphics.fill(
                0,
                top,
                progressBarRight,
                bottom,
                PROGRESS_BAR_COLOR
        );
    }

    /**
     * 绘制加载动画。
     */
    private void renderAnimation(GuiGraphicsExtractor graphics, AnimationInfo animation, int animationX, int textY) {
        int frameTime = (int) (Main.config.animation_frame_interval * animation.frame_interval_scale());

        int currentFrame =
                (int) ((System.currentTimeMillis() / frameTime)
                        % animation.frame());

        int textureV = currentFrame * animation.height();

        int textureHeight = animation.height() * animation.frame();

        float v0 = (float) textureV / textureHeight;
        float v1 =
                (float) (textureV + animation.height())
                        / textureHeight;

        graphics.pose().pushMatrix();

        graphics.pose().translate(animationX, textY);
        graphics.pose().scale(TEXT_SCALE, TEXT_SCALE);

        graphics.blit(
                animation.resource(),
                0,
                0,
                animation.width(),
                animation.height(),
                0.0F,
                1.0F,
                v0,
                v1
        );

        graphics.pose().popMatrix();
    }

    /**
     * 绘制 LOADING 文本。
     */
    private void renderLoadingText(GuiGraphicsExtractor graphics, int textY) {
        var loadingText =
                Main.config.i18n_loading_text ?
                        Component.translatable("string.betterloadingscreen.loading")
                        : DEFAULT_LOADING_TEXT;

        graphics.pose().pushMatrix();
        graphics.pose().translate(6.0F, textY);
        graphics.pose().scale(TEXT_SCALE, TEXT_SCALE);

        graphics.text(
                this.font,
                loadingText,
                0,
                0,
                TEXT_COLOR,
                true
        );

        graphics.pose().popMatrix();
    }

    /**
     * 绘制百分比。
     */
    private void renderProgressPercentage(GuiGraphicsExtractor graphics, int animationX, int textY) {
        int progressPercent = Mth.floor(smoothedProgress * 100.0F);

        String percentText = progressPercent + "%";

        int textWidth = this.font.width(percentText);

        int percentX = animationX - textWidth - 6;
        int percentY = textY + 18;

        graphics.text(
                this.font,
                percentText,
                percentX,
                percentY,
                TEXT_COLOR,
                true
        );
    }

    /**
     * 绘制 Minecraft Logo。
     */
    private void renderMinecraftLogo(GuiGraphicsExtractor graphics, int width) {
        int logoWidth = 256;
        int logoHeight = 64;

        int logoX = (width - logoWidth) / 2;
        int logoY = 25;

        graphics.blit(
                Identifier.withDefaultNamespace("textures/gui/title/minecraft.png"),
                logoX,
                logoY,
                logoX + logoWidth,
                logoY + logoHeight,
                0.0F,
                1.0F,
                0.0F,
                1.0F
        );
    }
}