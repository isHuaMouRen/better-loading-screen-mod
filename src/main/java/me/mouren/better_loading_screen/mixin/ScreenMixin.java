package me.mouren.better_loading_screen.mixin;

import me.mouren.better_loading_screen.Main;
import me.mouren.better_loading_screen.gui.LoadingScreenRender;
import me.mouren.better_loading_screen.models.ScreenSize;
import me.mouren.better_loading_screen.models.animation.Animations;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.GenericMessageScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Screen.class)
public abstract class ScreenMixin {
    @Inject(method = "extractRenderState", at = @At("HEAD"), cancellable = true)
    private void onExtractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        Screen screen = (Screen) (Object) this;
        if (!(screen instanceof GenericMessageScreen generic))
            return;
        if (!generic.getTitle().equals(Component.translatable("menu.savingLevel")))
            return;

        var screenSize = new ScreenSize(screen.width, screen.height);


        var animation = Animations.ANIMATION;
        LoadingScreenRender.renderAnimation(
                graphics,
                screenSize,
                Animations.ANIMATION,
                screen.width / 2 - animation.width() * 5 / 2,
                screen.height / 2 - animation.height() * 5 / 2,
                5
        );
        var savingText = Component.literal("§l").append(Component.translatable("menu.savingLevel"));
        LoadingScreenRender.renderText(
                graphics,
                screenSize,
                screen.getFont(),
                savingText,
                1,
                screen.width / 2 - screen.getFont().width(savingText) / 2,
                screen.height / 2 + animation.height() / 2 + 30
        );


        ci.cancel();
    }
}