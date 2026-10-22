package dev.vellum.client.screen;

import dev.vellum.client.editor.VellumConfigEditor;
import dev.vellum.client.navigation.CategoryNode;
import dev.vellum.client.render.VellumRenderContext;
import dev.vellum.client.widget.OptionMenu;
import dev.vellum.config.ConfigOptionHandle;

import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.Objects;

public final class ConfigScreenRenderer {
    private static final int PANEL_MARGIN = 24;
    private static final int HEADER_HEIGHT = 32;
    private static final int SAVE_BUTTON_WIDTH = 64;
    private static final int SAVE_BUTTON_HEIGHT = 20;

    private final Font font;

    public ConfigScreenRenderer(Font font) {
        this.font = Objects.requireNonNull(
                font,
                "font"
        );
    }

    public void drawBackground(
            VellumRenderContext context
    ) {
        context.fill(
                0,
                0,
                context.width(),
                context.height(),
                0xFF101216
        );
    }

    public void drawHeader(
            VellumRenderContext context,
            Component title,
            int width
    ) {
        context.fill(
                PANEL_MARGIN,
                PANEL_MARGIN,
                width - PANEL_MARGIN,
                PANEL_MARGIN + HEADER_HEIGHT,
                0xFF1B1E24
        );

        context.text(
                title,
                PANEL_MARGIN + 10,
                PANEL_MARGIN + 10,
                0xFFFFFFFF
        );
    }

    public void drawNavigationPanel(
            VellumRenderContext context,
            VellumConfigEditor editor,
            int navigationLeft,
            int navigationTop,
            int navigationRight,
            int navigationBottom
    ) {
        context.fill(
                navigationLeft,
                navigationTop,
                navigationRight,
                navigationBottom,
                0xFF191C21
        );

        context.text(

                Component.literal("Categories"),
                navigationLeft + 10,
                navigationTop + 10,
                0xFFE6E8EB
        );

        int rowTop = navigationTop + 32;

        for (CategoryNode category
                : editor.categoryTree().visibleNodes()) {
            drawCategoryRow(
                    context,
                    editor,
                    category,
                    rowTop,
                    navigationLeft,
                    navigationRight
            );

            rowTop += 20;
        }
    }

    private void drawCategoryRow(
            VellumRenderContext context,
            VellumConfigEditor editor,
            CategoryNode category,
            int rowTop,
            int navigationLeft,
            int navigationRight
    ) {
        boolean selected =
                editor.selectedCategory() == category;

        if (selected) {
            context.fill(
                    navigationLeft + 4,
                    rowTop - 3,
                    navigationRight - 4,
                    rowTop + 17,
                    0xFF303640
            );
        }

        int left = navigationLeft
                + 8
                + category.depth() * 12;

        String indicator = category.hasChildren()
                ? category.expanded()
                ? "▾ "
                : "▸ "
                : "  ";

        context.text(

                Component.literal(
                        indicator + category.title()
                ),
                left,
                rowTop,
                selected
                        ? 0xFFFFFFFF
                        : 0xFFD0D4DA
        );
    }

    public void drawContentPanelHeader(
            VellumRenderContext context,
            VellumConfigEditor editor,
            int contentLeft,
            int contentTop,
            int contentRight,
            int contentBottom
    ) {
        context.fill(
                contentLeft,
                contentTop,
                contentRight,
                contentBottom,
                0xFF191C21
        );

        Component selectedTitle =
                editor.selectedCategory() == null
                        ? Component.literal("Select a category")
                        : Component.literal(
                        editor.selectedCategory().title()
                );

        context.text(

                selectedTitle,
                contentLeft + 12,
                contentTop + 12,
                0xFFFFFFFF
        );
    }
    public void drawSaveButton(
            VellumRenderContext context,
            VellumConfigEditor editor,
            int contentRight,
            int contentBottom
    ) {
        int left =
                contentRight
                        - SAVE_BUTTON_WIDTH
                        - 12;

        int top =
                contentBottom
                        - SAVE_BUTTON_HEIGHT
                        - 12;

        context.fill(
                left,
                top,
                left + SAVE_BUTTON_WIDTH,
                top + SAVE_BUTTON_HEIGHT,
                editor.dirty()
                        ? 0xFF3E7A52
                        : 0xFF454A52
        );

        context.text(

                Component.literal("Save"),
                left + 16,
                top + 6,
                0xFFFFFFFF
        );
    }

    public void drawOptionMenu(
            VellumRenderContext context,
            OptionMenu optionMenu,
            int contentRight,
            int contentTop
    ) {
        if (optionMenu == null
                || !optionMenu.open()) {
            return;
        }

        int left = contentRight - 150;
        int top = contentTop + 72;

        context.fill(
                left,
                top,
                left + 130,
                top + 48,
                0xFF30343B
        );

        context.text(

                Component.literal("Reset"),
                left + 10,
                top + 8,
                0xFFFFFFFF
        );

        context.text(

                Component.literal("Close"),
                left + 10,
                top + 28,
                0xFFFFFFFF
        );
    }

    public void drawCloseWarning(
            VellumRenderContext context,
            VellumConfigEditor editor,
            int width,
            int height
    ) {
        if (!editor.closeWarningOpen()) {
            return;
        }

        int left = width / 2 - 140;
        int top = height / 2 - 55;

        context.fill(
                0,
                0,
                width,
                height,
                0x99000000
        );

        context.fill(
                left,
                top,
                left + 280,
                top + 110,
                0xFF30343B
        );

        context.text(

                Component.literal("Unsaved changes"),
                left + 20,
                top + 16,
                0xFFFFFFFF
        );

        context.text(

                Component.literal("Save and close"),
                left + 20,
                top + 42,
                0xFFFFFFFF
        );

        context.text(

                Component.literal("Discard"),
                left + 20,
                top + 62,
                0xFFFFFFFF
        );

        context.text(

                Component.literal("Cancel"),
                left + 20,
                top + 82,
                0xFFFFFFFF
        );
    }

    public void drawOptionRowHeader(
            VellumRenderContext context,
            ConfigOptionHandle<?> option,
            int contentLeft,
            int contentRight,
            int rowTop,
            int optionRowHeight
    ) {
        context.fill(
                contentLeft + 8,
                rowTop - 4,
                contentRight - 8,
                rowTop + optionRowHeight - 4,
                0xFF22262D
        );

        context.text(

                Component.literal(option.name()),
                contentLeft + 18,
                rowTop + 2,
                0xFFFFFFFF
        );

        if (!option.description().isBlank()) {
            context.text(
                    Component.literal(option.description()),
                    contentLeft + 18,
                    rowTop + 16,
                    0xFFB8BEC8
            );
        }
    }

    public void drawMoreButton(
            VellumRenderContext context,
            int contentRight,
            int rowTop,
            int optionControlHeight
    ) {
        int left = contentRight - 84;
        int top = rowTop + 3;

        context.fill(
                left,
                top,
                left + 52,
                top + optionControlHeight,
                0xFF454A52
        );

        context.text(

                Component.literal("More"),
                left + 8,
                top + 6,
                0xFFFFFFFF
        );
    }

    public void drawBooleanControl(
            VellumRenderContext context,
            boolean enabled,
            int contentRight,
            int rowTop,
            int optionControlWidth,
            int optionControlHeight
    ) {
        int left = contentRight
                - optionControlWidth
                - 18;

        int top = rowTop + 3;

        context.fill(
                left,
                top,
                left + optionControlWidth,
                top + optionControlHeight,
                enabled
                        ? 0xFF3E7A52
                        : 0xFF454A52
        );

        context.text(

                Component.literal(
                        enabled
                                ? "ON"
                                : "OFF"
                ),
                left + 13,
                top + 6,
                0xFFFFFFFF
        );
    }

    public void drawNumericControl(
            VellumRenderContext context,
            Object value,
            int contentRight,
            int rowTop,
            int optionControlHeight
    ) {
        int top = rowTop + 3;
        int plusLeft = contentRight - 38;
        int minusLeft = plusLeft - 24;

        context.fill(
                minusLeft,
                top,
                minusLeft + 20,
                top + optionControlHeight,
                0xFF454A52
        );

        context.text(

                Component.literal("-"),
                minusLeft + 7,
                top + 5,
                0xFFFFFFFF
        );

        context.text(

                Component.literal(String.valueOf(value)),
                minusLeft - 42,
                top + 5,
                0xFFFFFFFF
        );

        context.fill(
                plusLeft,
                top,
                plusLeft + 20,
                top + optionControlHeight,
                0xFF3E7A52
        );

        context.text(

                Component.literal("+"),
                plusLeft + 7,
                top + 5,
                0xFFFFFFFF
        );
    }

    public void drawStringControl(
            VellumRenderContext context,
            String value,
            int contentRight,
            int rowTop,
            int optionControlHeight
    ) {
        int left = contentRight - 210;
        int top = rowTop + 3;

        context.fill(
                left,
                top,
                contentRight - 18,
                top + optionControlHeight,
                0xFF2A2E35
        );

        context.text(

                Component.literal(value),
                left + 8,
                top + 5,
                0xFFFFFFFF
        );
    }

    public void drawListControls(
            VellumRenderContext context,
            List<String> values,
            int selectedIndex,
            boolean selectedOption,
            int contentLeft,
            int contentRight,
            int rowTop,
            int listRowTopOffset,
            int listRowHeight,
            int optionControlHeight
    ) {
        int listTop = rowTop + listRowTopOffset;

        for (int index = 0; index < values.size(); index++) {
            drawListItem(
                    context,
                    values.get(index),
                    index,
                    selectedOption && selectedIndex == index,
                    contentLeft,
                    contentRight,
                    listTop,
                    listRowHeight
            );
        }

        drawListButtons(
                context,
                contentRight,
                rowTop,
                optionControlHeight
        );
    }

    private void drawListItem(
            VellumRenderContext context,
            String value,
            int index,
            boolean selected,
            int contentLeft,
            int contentRight,
            int listTop,
            int listRowHeight
    ) {
        int itemTop =
                listTop + index * listRowHeight;

        context.fill(
                contentLeft + 18,
                itemTop,
                contentRight - 150,
                itemTop + listRowHeight - 2,
                selected
                        ? 0xFF3E596F
                        : 0xFF2A2E35
        );

        context.text(

                Component.literal(value),
                contentLeft + 26,
                itemTop + 4,
                0xFFFFFFFF
        );
    }

    private void drawListButtons(
            VellumRenderContext context,
            int contentRight,
            int rowTop,
            int optionControlHeight
    ) {
        int buttonTop = rowTop + 3;
        int removeLeft = contentRight - 72;
        int addLeft = removeLeft - 60;

        context.fill(
                addLeft,
                buttonTop,
                addLeft + 52,
                buttonTop + optionControlHeight,
                0xFF3E7A52
        );

        context.text(

                Component.literal("Add"),
                addLeft + 13,
                buttonTop + 6,
                0xFFFFFFFF
        );

        context.fill(
                removeLeft,
                buttonTop,
                removeLeft + 64,
                buttonTop + optionControlHeight,
                0xFF8A4141
        );

        context.text(

                Component.literal("Remove"),
                removeLeft + 7,
                buttonTop + 6,
                0xFFFFFFFF
        );
    }
}
