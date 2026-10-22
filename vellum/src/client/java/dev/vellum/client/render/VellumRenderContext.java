package dev.vellum.client.render;

import net.minecraft.network.chat.Component;

public interface VellumRenderContext {
    void fill(
            int left,
            int top,
            int right,
            int bottom,
            int color
    );

    void text(
            Component text,
            int x,
            int y,
            int color
    );

    int width();

    int height();
}
