package dev.vellum.client.widget;

import dev.vellum.client.input.VellumMousePosition;
import dev.vellum.client.render.VellumRenderContext;

import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.Objects;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

public final class VellumListWidget
        implements VellumOptionWidget {
    private final Supplier<List<String>> values;
    private final IntSupplier selectedIndex;
    private final IntConsumer select;
    private final Runnable add;
    private final Runnable remove;

    public VellumListWidget(
            Supplier<List<String>> values,
            IntSupplier selectedIndex,
            IntConsumer select,
            Runnable add,
            Runnable remove
    ) {
        this.values =
                Objects.requireNonNull(
                        values,
                        "values"
                );

        this.selectedIndex =
                Objects.requireNonNull(
                        selectedIndex,
                        "selectedIndex"
                );

        this.select =
                Objects.requireNonNull(
                        select,
                        "select"
                );

        this.add =
                Objects.requireNonNull(
                        add,
                        "add"
                );

        this.remove =
                Objects.requireNonNull(
                        remove,
                        "remove"
                );
    }

    @Override
    public void render(
            VellumRenderContext context,
            int contentRight,
            int rowTop
    ) {
        int listTop = rowTop + 42;
        int index = 0;

        for (String value : values.get()) {
            int itemTop = listTop + index * 18;
            boolean selected =
                    selectedIndex.getAsInt() == index;

            context.fill(
                    18,
                    itemTop,
                    contentRight - 150,
                    itemTop + 16,
                    selected
                            ? 0xFF3E596F
                            : 0xFF2A2E35
            );

            context.text(
                    Component.literal(value),
                    26,
                    itemTop + 4,
                    0xFFFFFFFF
            );

            index++;
        }
    }

    @Override
    public boolean contains(
            VellumMousePosition mouse,
            int contentRight,
            int rowTop
    ) {
        int listTop = rowTop + 42;

        return mouse.x() >= 18
                && mouse.x() < contentRight - 150
                && mouse.y() >= listTop
                && mouse.y()
                < listTop + values.get().size() * 18;
    }

    @Override
    public void activate() {
        add.run();
    }

    public void selectAt(
            int index
    ) {
        select.accept(index);
    }

    public void removeSelected() {
        remove.run();
    }
}
