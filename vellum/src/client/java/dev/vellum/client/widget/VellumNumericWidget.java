package dev.vellum.client.widget;

import dev.vellum.client.input.VellumMousePosition;
import dev.vellum.client.render.VellumRenderContext;

import net.minecraft.network.chat.Component;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class VellumNumericWidget
        implements VellumOptionWidget {
    private final Supplier<Object> valueReader;
    private final Runnable decrement;
    private final Runnable increment;

    public VellumNumericWidget(
            Supplier<Object> valueReader,
            Runnable decrement,
            Runnable increment
    ) {
        this.valueReader =
                Objects.requireNonNull(
                        valueReader,
                        "valueReader"
                );

        this.decrement =
                Objects.requireNonNull(
                        decrement,
                        "decrement"
                );

        this.increment =
                Objects.requireNonNull(
                        increment,
                        "increment"
                );
    }

    @Override
    public void render(
            VellumRenderContext context,
            int contentRight,
            int rowTop
    ) {
        int top = rowTop + 3;
        int plusLeft = contentRight - 38;
        int minusLeft = plusLeft - 24;

        context.fill(
                minusLeft,
                top,
                minusLeft + 20,
                top + 20,
                0xFF454A52
        );

        context.text(
                Component.literal("-"),
                minusLeft + 7,
                top + 5,
                0xFFFFFFFF
        );

        context.text(
                Component.literal(
                        String.valueOf(
                                valueReader.get()
                        )
                ),
                minusLeft - 42,
                top + 5,
                0xFFFFFFFF
        );

        context.fill(
                plusLeft,
                top,
                plusLeft + 20,
                top + 20,
                0xFF3E7A52
        );

        context.text(
                Component.literal("+"),
                plusLeft + 7,
                top + 5,
                0xFFFFFFFF
        );
    }

    @Override
    public boolean contains(
            VellumMousePosition mouse,
            int contentRight,
            int rowTop
    ) {
        int top = rowTop + 3;

        return mouse.y() >= top
                && mouse.y() < top + 20
                && mouse.x() >= contentRight - 62
                && mouse.x() < contentRight - 18;
    }

    @Override
    public void activate() {
        increment.run();
    }

    public boolean isMinus(
            VellumMousePosition mouse,
            int contentRight,
            int rowTop
    ) {
        int top = rowTop + 3;
        int left = contentRight - 62;

        return mouse.x() >= left
                && mouse.x() < left + 20
                && mouse.y() >= top
                && mouse.y() < top + 20;
    }

    public void decrementValue() {
        decrement.run();
    }
}
