package me.mouren.better_loading_screen.mixin;

import me.mouren.better_loading_screen.BetterLoadingScreen;
import net.minecraft.client.gui.GuiGraphics;
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

    @Shadow
    private LevelLoadTracker loadTracker;
    @Shadow
    private float smoothedProgress;

    protected LevelLoadingScreenMixin(Component title) {
        super(title);
    }

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void onRender(GuiGraphics graphics, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        // 拦截原版渲染
        ci.cancel();

        // 基础布局
        int barHeight = 4;
        int left = 0;
        int right = this.width;
        int bottom = this.height;
        int top = bottom - barHeight;

        // 渐变背景
        int gradientHeight = 100;
        int gradientTop = this.height - gradientHeight;

        graphics.fillGradient(0, gradientTop, right, bottom, 0x00000000, 0x80000000);

        // 进度条绘制
        if (this.loadTracker != null && this.loadTracker.hasProgress()) {
            int progressBarRight = (int) (this.smoothedProgress * (float) this.width);
            if (progressBarRight > 0) {
                graphics.fill(left, top, progressBarRight, bottom, 0xFF00FF00);
            }
        }

        // 缩放尺寸定义
        float scale = 3F;
        int scaledTextHeight = (int) (9 * scale);
        int textY = top - scaledTextHeight - 5;

        int animSize = 10;
        int scaledAnimSize = (int) (animSize * scale);
        int animX = this.width - scaledAnimSize - 6;

        // ==========================================
        // 绘制动态贴图
        // ==========================================
        graphics.pose().pushMatrix();
        graphics.pose().translate((float) animX, (float) textY);
        graphics.pose().scale(scale, scale);

        int totalFrames = 91;
        int currentFrame = (int) ((System.currentTimeMillis() / 40) % totalFrames);
        int textureV = currentFrame * animSize;

        net.minecraft.resources.Identifier animationTextureIdentifier = net.minecraft.resources.Identifier.fromNamespaceAndPath(BetterLoadingScreen.MOD_ID, "textures/gui/loading_animation.png");

        float u0 = 0.0F;
        float u1 = 1.0F;
        float v0 = (float) textureV / 910.0F;
        float v1 = (float) (textureV + animSize) / 910.0F;

        graphics.blit(
                animationTextureIdentifier,
                0, 0,
                animSize, animSize,
                u0, u1,
                v0, v1
        );

        graphics.pose().popMatrix();


        // ==========================================
        // 左侧 LOADING
        // ==========================================
        graphics.pose().pushMatrix();
        graphics.pose().translate(6.0F, (float) textY);
        graphics.pose().scale(scale, scale);
        graphics.drawString(this.font, "§lLOADING...", 0, 0, 0xFFFFFFFF, true);
        graphics.pose().popMatrix();


        // ==========================================
        // 百分比文字
        // ==========================================
        if (this.loadTracker != null && this.loadTracker.hasProgress()) {
            int progressPercent = net.minecraft.util.Mth.floor(this.loadTracker.serverProgress() * 100.0F);
            String percentString = progressPercent + "%";

            int rawTextWidth = this.font.width(percentString);
            int percentX = animX - rawTextWidth - 6;
            int normalTextY = textY + 18;

            graphics.drawString(this.font, percentString, percentX, normalTextY, 0xFFFFFFFF, true);
        }
    }
}