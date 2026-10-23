package dev.vellum.client.widget;

import dev.vellum.client.input.VellumMousePosition;
import dev.vellum.client.render.VellumRenderContext;

import net.minecraft.network.chat.Component;

import java.util.Objects;
import java.util.function.Supplier;

public final class VellumStringWidget
        implements VellumOptionWidget {
    private final Supplier<String> valueReader;
    private final Runnable beginEdit;

    public VellumStringWidget(
            Supplier<String> valueReader,
            Runnable beginEdit
    ) {
        this.valueReader =
                Objects.requireNonNull(
                        valueReader,
                        "valueReader"
                );

        this.beginEdit =
                Objects.requireNonNull(
                        beginEdit,
                        "beginEdit"
                );
    }

    @Override
    public void render(
            VellumRenderContext context,
            int contentRight,
            int rowTop
    ) {
        int left = contentRight - 210;
        int top = rowTop + 3;

        context.fill(
                left,
                top,
                contentRight - 18,
                top + 20,
                0xFF2A2E35
        );

        context.text(
                Component.literal(
                        valueReader.get()
                ),
                left + 8,
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
        int left = contentRight - 210;
        int top = rowTop + 3;

        return mouse.x() >= left
                && mouse.x() < contentRight - 18
                && mouse.y() >= top
                && mouse.y() < top + 20;
    }

    @Override
    public void activate() {
        beginEdit.run();
    }
}
