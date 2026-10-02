package me.mouren.better_loading_screen.mixin;

import me.mouren.better_loading_screen.BetterLoadingScreen;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.LevelLoadingScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.LevelLoadTracker;
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

    private static final int GRADIENT_HEIGHT = 100;

    private static final int ANIMATION_SIZE = 10;
    private static final int ANIMATION_FRAMES = 91;
    private static final int ANIMATION_FRAME_TIME = 40;
    private static final int ANIMATION_TEXTURE_HEIGHT = ANIMATION_SIZE * ANIMATION_FRAMES;

    private static final float TEXT_SCALE = 3.0F;

    private static final int TEXT_COLOR = 0xFFFFFFFF;
    private static final int PROGRESS_BAR_COLOR = 0xFF00FF00;

    private static final Identifier ANIMATION_TEXTURE = Identifier.fromNamespaceAndPath(BetterLoadingScreen.MOD_ID, "textures/gui/loading_animation.png");

    private static final Component DEFAULT_LOADING_TEXT = Component.literal("§lLOADING...");

    @Shadow
    private LevelLoadTracker loadTracker;

    @Shadow
    private float smoothedProgress;

    protected LevelLoadingScreenMixin(Component title) {
        super(title);
    }

    @Inject(method = "extractRenderState", at = @At("HEAD"), cancellable = true)
    private void onExtractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        // 取消原版渲染
        ci.cancel();

        int width = this.width;
        int height = this.height;

        int bottom = height;
        int top = bottom - BAR_HEIGHT;

        // 背景
        renderBackground(graphics, width, height);

        // 进度条
        var hasProgress = this.loadTracker != null && this.loadTracker.hasProgress();
        if (hasProgress)
            renderProgressBar(graphics, width, top, bottom);

        // 文字布局
        int scaledTextHeight = (int) (9 * TEXT_SCALE);
        int textY = top - scaledTextHeight - 5;

        int scaledAnimationSize = (int) (ANIMATION_SIZE * TEXT_SCALE);
        int animationX = width - scaledAnimationSize - 6;

        // LOADING
        renderLoadingText(graphics, textY);

        // 动画
        renderAnimation(graphics, animationX, textY);

        // 百分比
        if (hasProgress)
            renderProgressPercentage(graphics, animationX, textY);

    }

    /**
     * 绘制底部黑色渐变背景。
     */
    private void renderBackground(GuiGraphicsExtractor graphics, int width, int height) {
        if (!BetterLoadingScreen.config.draw_background)
            return;

        int gradientTop = height - GRADIENT_HEIGHT;

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
    private void renderAnimation(GuiGraphicsExtractor graphics, int animationX, int textY) {
        int currentFrame = (int) ((System.currentTimeMillis() / ANIMATION_FRAME_TIME) % ANIMATION_FRAMES);
        int textureV = currentFrame * ANIMATION_SIZE;

        float v0 = (float) textureV / ANIMATION_TEXTURE_HEIGHT;
        float v1 = (float) (textureV + ANIMATION_SIZE) / ANIMATION_TEXTURE_HEIGHT;

        graphics.pose().pushMatrix();
        graphics.pose().translate(animationX, textY);
        graphics.pose().scale(TEXT_SCALE, TEXT_SCALE);

        graphics.blit(
                ANIMATION_TEXTURE,
                0,
                0,
                ANIMATION_SIZE,
                ANIMATION_SIZE,
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
        var loadingText = BetterLoadingScreen.config.i18n_loading_text ? Component.translatable("string.betterloadingscreen.loading") : DEFAULT_LOADING_TEXT;

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
        int progressPercent = Mth.floor(this.loadTracker.serverProgress() * 100.0F);

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
}