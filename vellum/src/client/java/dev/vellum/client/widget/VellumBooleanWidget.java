package dev.vellum.client.widget;

import dev.vellum.client.input.VellumMousePosition;
import dev.vellum.client.render.VellumRenderContext;

import java.util.Objects;
import java.util.function.BooleanSupplier;

public final class VellumBooleanWidget
        implements VellumOptionWidget {
    private final BooleanSupplier valueReader;
    private final Runnable toggler;

    public VellumBooleanWidget(
            BooleanSupplier valueReader,
            Runnable toggler
    ) {
        this.valueReader =
                Objects.requireNonNull(
                        valueReader,
                        "valueReader"
                );

        this.toggler =
                Objects.requireNonNull(
                        toggler,
                        "toggler"
                );
    }

    @Override
    public void render(
            VellumRenderContext context,
            int contentRight,
            int rowTop
    ) {
        int width = 52;
        int height = 20;
        int left = contentRight - width - 18;
        int top = rowTop + 3;
        boolean enabled = valueReader.getAsBoolean();

        context.fill(
                left,
                top,
                left + width,
                top + height,
                enabled
                        ? 0xFF3E7A52
                        : 0xFF454A52
        );

        context.text(
                net.minecraft.network.chat.Component.literal(
                        enabled ? "ON" : "OFF"
                ),
                left + 13,
                top + 6,
                0xFFFFFFFF
        );
    }

    @Override
    public boolean contains(
            VellumMousePosition mouse,
            int contentRight,
            int rowTop
    ) {
        int left = contentRight - 52 - 18;
        int top = rowTop + 3;

        return mouse.x() >= left
                && mouse.x() < left + 52
                && mouse.y() >= top
                && mouse.y() < top + 20;
    }

    @Override
    public void activate() {
        toggler.run();
    }
}
