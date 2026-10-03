package me.mouren.better_loading_screen.models.animation;

import net.minecraft.resources.Identifier;

public record AnimationInfo(int width, int height, int frame, float frame_interval_scale, Identifier resource) {
}
