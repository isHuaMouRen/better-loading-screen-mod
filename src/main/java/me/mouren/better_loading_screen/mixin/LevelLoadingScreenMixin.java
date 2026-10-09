package me.mouren.better_loading_screen.mixin;

import me.mouren.better_loading_screen.Main;
import me.mouren.better_loading_screen.gui.LoadingScreenRender;
import me.mouren.better_loading_screen.models.ScreenSize;
import me.mouren.better_loading_screen.models.animation.AnimationInfo;
import me.mouren.better_loading_screen.models.animation.Animations;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.LevelLoadingScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.LevelLoadTracker;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelLoadingScreen.class)
public class LevelLoadingScreenMixin extends Screen {
    protected LevelLoadingScreenMixin(Component title) {
        super(title);
    }

    @Shadow
    private float smoothedProgress;
    @Shadow
    private LevelLoadTracker loadTracker;

    @Inject(method = "extractRenderState", at = @At("HEAD"), cancellable = true)
    private void onExtractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        // 取消原版渲染
        ci.cancel();

        //布局
        var screenSize = new ScreenSize(this.width, this.height);
        boolean hasProgress = smoothedProgress > 0.0F;

        final int barHeight = 4;
        final float textScale = 3.0F;
        int bottom = height;
        int top = bottom - barHeight;

        AnimationInfo animation = getAnimation();

        int scaledTextHeight = (int) (9 * textScale);
        int textY = top - scaledTextHeight - 5;

        int scaledAnimationWidth = (int) (animation.width() * textScale);
        int animationX = width - scaledAnimationWidth - 6;

        // 背景
        LoadingScreenRender.renderBackground(graphics, screenSize, height * 2 / 3);

        //LOADING
        var loadingText = Main.config.i18n_loading_text ? Component.translatable("string.betterloadingscreen.loading") : LoadingScreenRender.DEFAULT_LOADING_TEXT;
        LoadingScreenRender.renderText(graphics, screenSize, this.font, loadingText, textScale, textY);

        // 动画
        LoadingScreenRender.renderAnimation(
                graphics,
                screenSize,
                animation,
                animationX,
                textY,
                textScale
        );

        // 进度条&百分比
        if (hasProgress) {
            LoadingScreenRender.renderProgressBar(graphics, screenSize, this.smoothedProgress, top);
            LoadingScreenRender.renderPercentText(graphics, screenSize, smoothedProgress, this.font, animationX, textY);
        }

        // Minecraft logo
        if (Main.config.draw_minecraft_logo)
            LoadingScreenRender.renderMinecraftLogo(graphics, screenSize);

        // Vanilla loading animation
        if (Main.config.draw_vanilla_chunks)
            LoadingScreenRender.renderVanillaAnimation(graphics, loadTracker, textY);
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
}