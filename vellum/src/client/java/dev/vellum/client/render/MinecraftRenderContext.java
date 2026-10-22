package dev.vellum.client.render;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;

import java.util.Objects;

public final class MinecraftRenderContext
        implements VellumRenderContext {
    private final GuiGraphicsExtractor graphics;
    private final Font font;
    private final int width;
    private final int height;

    public MinecraftRenderContext(
            GuiGraphicsExtractor graphics,
            Font font,
            int width,
            int height
    ) {
        this.graphics =
                Objects.requireNonNull(
                        graphics,
                        "graphics"
                );

        this.font =
                Objects.requireNonNull(
                        font,
                        "font"
                );

        this.width = width;
        this.height = height;
    }

    @Override
    public void fill(
            int left,
            int top,
            int right,
            int bottom,
            int color
    ) {
        graphics.fill(
                left,
                top,
                right,
                bottom,
                color
        );
    }

    @Override
    public void text(
            Component text,
            int x,
            int y,
            int color
    ) {
        graphics.text(
                font,
                text,
                x,
                y,
                color,
                false
        );
    }

    @Override
    public int width() {
        return width;
    }

    @Override
    public int height() {
        return height;
    }
}
