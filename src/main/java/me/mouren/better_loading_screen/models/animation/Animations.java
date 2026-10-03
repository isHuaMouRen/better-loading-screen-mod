package me.mouren.better_loading_screen.models.animation;

import me.mouren.better_loading_screen.Main;
import net.minecraft.resources.Identifier;

public final class Animations {
    public static final AnimationInfo ANIMATION
            = new AnimationInfo(10, 10, 91, 1.0F, Identifier.fromNamespaceAndPath(Main.MOD_ID, "textures/gui/loading_animation.png"));
    public static final AnimationInfo SPIN
            = new AnimationInfo(7, 7, 10, 3.0F, Identifier.fromNamespaceAndPath(Main.MOD_ID, "textures/gui/loading_spin.png"));
}
