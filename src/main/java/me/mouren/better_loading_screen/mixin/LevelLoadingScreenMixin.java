package me.mouren.better_loading_screen.mixin;

import me.mouren.better_loading_screen.BetterLoadingScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.LevelLoadingScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.progress.StoringChunkProgressListener;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelLoadingScreen.class)
public class LevelLoadingScreenMixin extends Screen {

    // 1.21.6不支持
    /*@Shadow
    private LevelLoadTracker loadTracker;
    @Shadow
    private float smoothedProgress;*/
    @Final
    @Shadow
    private StoringChunkProgressListener progressListener;


    protected LevelLoadingScreenMixin(Component title) {
        super(title);
    }


    @Redirect(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screens/LevelLoadingScreen;renderChunks(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/server/level/progress/StoringChunkProgressListener;IIII)V"
            )
    )
    private void cancelRenderChunks(
            GuiGraphics guiGraphics,
            StoringChunkProgressListener listener,
            int i,
            int j,
            int k,
            int l
    ) {
        //什么也不做，取消矩阵进度
    }

    @Redirect(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiGraphics;drawCenteredString(Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;III)V"
            )
    )
    private void cancelDrawString(
            GuiGraphics guiGraphics,
            net.minecraft.client.gui.Font font,
            Component component,
            int x,
            int y,
            int color
    ) {
        // 什么都不做，取消信息显示
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void onRender(GuiGraphics guiGraphics, int i, int j, float f, CallbackInfo ci) {
        // 拦截原版渲染
        //ci.cancel(); 1.21.5cancel后不渲染全景图

        int nowPercent = this.progressListener.getProgress();

        // 基础布局
        int barHeight = 4;
        int left = 0;
        int right = this.width;
        int bottom = this.height;
        int top = bottom - barHeight;

        // 渐变背景
        int gradientHeight = 100;
        int gradientTop = this.height - gradientHeight;

        guiGraphics.fillGradient(0, gradientTop, right, bottom, 0x00000000, 0x80000000);

        // 进度条绘制
        if (nowPercent != 0) {
            int progressBarRight = (int) (((float) nowPercent / 100F) * this.width);
            if (progressBarRight > 0) {
                guiGraphics.fill(left, top, progressBarRight, bottom, 0xFF00FF00);
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
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate((float) animX, (float) textY, 0.0F);
        guiGraphics.pose().scale(scale, scale, 1.0F);

        int totalFrames = 91;
        int currentFrame = (int) ((System.currentTimeMillis() / 40) % totalFrames);
        int textureV = currentFrame * animSize;

        net.minecraft.resources.ResourceLocation animationTextureIdentifier = net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(BetterLoadingScreen.MOD_ID, "textures/gui/loading_animation.png");

        guiGraphics.blit(
                animationTextureIdentifier,
                0, 0,
                0,
                textureV,
                animSize,
                animSize,
                animSize,
                910
        );

        guiGraphics.pose().popPose();


        // ==========================================
        // 左侧 LOADING
        // ==========================================
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(6.0F, (float) textY, 0.0F);
        guiGraphics.pose().scale(scale, scale, 1.0F);
        guiGraphics.drawString(this.font, "§lLOADING...", 0, 0, 0xFFFFFFFF, true);
        guiGraphics.pose().popPose();


        // ==========================================
        // 百分比文字
        // ==========================================
        if (nowPercent != 0) {
            String percentString = nowPercent + "%";

            int rawTextWidth = this.font.width(percentString);
            int percentX = animX - rawTextWidth - 6;
            int normalTextY = textY + 18;

            guiGraphics.drawString(this.font, percentString, percentX, normalTextY, 0xFFFFFFFF, true);
        }
    }
}