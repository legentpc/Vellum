package dev.vellum.client.widget;

import dev.vellum.client.input.VellumMousePosition;
import dev.vellum.client.render.VellumRenderContext;

public interface VellumOptionWidget {
    void render(
            VellumRenderContext context,
            int contentRight,
            int rowTop
    );

    boolean contains(
            VellumMousePosition mouse,
            int contentRight,
            int rowTop
    );

    void activate();
}
