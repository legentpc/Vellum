package dev.vellum.client.screen;

import dev.vellum.client.navigation.CategoryNode;
import dev.vellum.client.screen.ConfigScreenState;
import dev.vellum.client.widget.OptionMenu;
import dev.vellum.config.ConfigOptionHandle;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
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
            GuiGraphicsExtractor graphics,
            int width,
            int height
    ) {
        graphics.fill(
                0,
                0,
                width,
                height,
                0xFF101216
        );
    }

    public void drawHeader(
            GuiGraphicsExtractor graphics,
            Component title,
            int width
    ) {
        graphics.fill(
                PANEL_MARGIN,
                PANEL_MARGIN,
                width - PANEL_MARGIN,
                PANEL_MARGIN + HEADER_HEIGHT,
                0xFF1B1E24
        );

        graphics.text(
                font,
                title,
                PANEL_MARGIN + 10,
                PANEL_MARGIN + 10,
                0xFFFFFFFF,
                false
        );
    }

    public void drawNavigationPanel(
            GuiGraphicsExtractor graphics,
            ConfigScreenState state,
            int navigationLeft,
            int navigationTop,
            int navigationRight,
            int navigationBottom
    ) {
        graphics.fill(
                navigationLeft,
                navigationTop,
                navigationRight,
                navigationBottom,
                0xFF191C21
        );

        graphics.text(
                font,
                Component.literal("Categories"),
                navigationLeft + 10,
                navigationTop + 10,
                0xFFE6E8EB,
                false
        );

        int rowTop = navigationTop + 32;

        for (CategoryNode category
                : state.categoryTree().visibleNodes()) {
            drawCategoryRow(
                    graphics,
                    state,
                    category,
                    rowTop,
                    navigationLeft,
                    navigationRight
            );

            rowTop += 20;
        }
    }

    private void drawCategoryRow(
            GuiGraphicsExtractor graphics,
            ConfigScreenState state,
            CategoryNode category,
            int rowTop,
            int navigationLeft,
            int navigationRight
    ) {
        boolean selected =
                state.selectedCategory() == category;

        if (selected) {
            graphics.fill(
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

        graphics.text(
                font,
                Component.literal(
                        indicator + category.title()
                ),
                left,
                rowTop,
                selected
                        ? 0xFFFFFFFF
                        : 0xFFD0D4DA,
                false
        );
    }

    public void drawContentPanelHeader(
            GuiGraphicsExtractor graphics,
            ConfigScreenState state,
            int contentLeft,
            int contentTop,
            int contentRight,
            int contentBottom
    ) {
        graphics.fill(
                contentLeft,
                contentTop,
                contentRight,
                contentBottom,
                0xFF191C21
        );

        Component selectedTitle =
                state.selectedCategory() == null
                        ? Component.literal("Select a category")
                        : Component.literal(
                        state.selectedCategory().title()
                );

        graphics.text(
                font,
                selectedTitle,
                contentLeft + 12,
                contentTop + 12,
                0xFFFFFFFF,
                false
        );
    }
    public void drawSaveButton(
            GuiGraphicsExtractor graphics,
            ConfigScreenState state,
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

        graphics.fill(
                left,
                top,
                left + SAVE_BUTTON_WIDTH,
                top + SAVE_BUTTON_HEIGHT,
                state.dirty()
                        ? 0xFF3E7A52
                        : 0xFF454A52
        );

        graphics.text(
                font,
                Component.literal("Save"),
                left + 16,
                top + 6,
                0xFFFFFFFF,
                false
        );
    }

    public void drawOptionMenu(
            GuiGraphicsExtractor graphics,
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

        graphics.fill(
                left,
                top,
                left + 130,
                top + 48,
                0xFF30343B
        );

        graphics.text(
                font,
                Component.literal("Reset"),
                left + 10,
                top + 8,
                0xFFFFFFFF,
                false
        );

        graphics.text(
                font,
                Component.literal("Close"),
                left + 10,
                top + 28,
                0xFFFFFFFF,
                false
        );
    }

    public void drawCloseWarning(
            GuiGraphicsExtractor graphics,
            ConfigScreenState state,
            int width,
            int height
    ) {
        if (!state.closeWarningOpen()) {
            return;
        }

        int left = width / 2 - 140;
        int top = height / 2 - 55;

        graphics.fill(
                0,
                0,
                width,
                height,
                0x99000000
        );

        graphics.fill(
                left,
                top,
                left + 280,
                top + 110,
                0xFF30343B
        );

        graphics.text(
                font,
                Component.literal("Unsaved changes"),
                left + 20,
                top + 16,
                0xFFFFFFFF,
                false
        );

        graphics.text(
                font,
                Component.literal("Save and close"),
                left + 20,
                top + 42,
                0xFFFFFFFF,
                false
        );

        graphics.text(
                font,
                Component.literal("Discard"),
                left + 20,
                top + 62,
                0xFFFFFFFF,
                false
        );

        graphics.text(
                font,
                Component.literal("Cancel"),
                left + 20,
                top + 82,
                0xFFFFFFFF,
                false
        );
    }

    public void drawOptionRowHeader(
            GuiGraphicsExtractor graphics,
            ConfigOptionHandle<?> option,
            int contentLeft,
            int contentRight,
            int rowTop,
            int optionRowHeight
    ) {
        graphics.fill(
                contentLeft + 8,
                rowTop - 4,
                contentRight - 8,
                rowTop + optionRowHeight - 4,
                0xFF22262D
        );

        graphics.text(
                font,
                Component.literal(option.name()),
                contentLeft + 18,
                rowTop + 2,
                0xFFFFFFFF,
                false
        );

        if (!option.description().isBlank()) {
            graphics.text(
                    font,
                    Component.literal(option.description()),
                    contentLeft + 18,
                    rowTop + 16,
                    0xFFB8BEC8,
                    false
            );
        }
    }

    public void drawMoreButton(
            GuiGraphicsExtractor graphics,
            int contentRight,
            int rowTop,
            int optionControlHeight
    ) {
        int left = contentRight - 84;
        int top = rowTop + 3;

        graphics.fill(
                left,
                top,
                left + 52,
                top + optionControlHeight,
                0xFF454A52
        );

        graphics.text(
                font,
                Component.literal("More"),
                left + 8,
                top + 6,
                0xFFFFFFFF,
                false
        );
    }

    public void drawBooleanControl(
            GuiGraphicsExtractor graphics,
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

        graphics.fill(
                left,
                top,
                left + optionControlWidth,
                top + optionControlHeight,
                enabled
                        ? 0xFF3E7A52
                        : 0xFF454A52
        );

        graphics.text(
                font,
                Component.literal(
                        enabled
                                ? "ON"
                                : "OFF"
                ),
                left + 13,
                top + 6,
                0xFFFFFFFF,
                false
        );
    }

    public void drawNumericControl(
            GuiGraphicsExtractor graphics,
            Object value,
            int contentRight,
            int rowTop,
            int optionControlHeight
    ) {
        int top = rowTop + 3;
        int plusLeft = contentRight - 38;
        int minusLeft = plusLeft - 24;

        graphics.fill(
                minusLeft,
                top,
                minusLeft + 20,
                top + optionControlHeight,
                0xFF454A52
        );

        graphics.text(
                font,
                Component.literal("-"),
                minusLeft + 7,
                top + 5,
                0xFFFFFFFF,
                false
        );

        graphics.text(
                font,
                Component.literal(String.valueOf(value)),
                minusLeft - 42,
                top + 5,
                0xFFFFFFFF,
                false
        );

        graphics.fill(
                plusLeft,
                top,
                plusLeft + 20,
                top + optionControlHeight,
                0xFF3E7A52
        );

        graphics.text(
                font,
                Component.literal("+"),
                plusLeft + 7,
                top + 5,
                0xFFFFFFFF,
                false
        );
    }

    public void drawStringControl(
            GuiGraphicsExtractor graphics,
            String value,
            int contentRight,
            int rowTop,
            int optionControlHeight
    ) {
        int left = contentRight - 210;
        int top = rowTop + 3;

        graphics.fill(
                left,
                top,
                contentRight - 18,
                top + optionControlHeight,
                0xFF2A2E35
        );

        graphics.text(
                font,
                Component.literal(value),
                left + 8,
                top + 5,
                0xFFFFFFFF,
                false
        );
    }

    public void drawListControls(
            GuiGraphicsExtractor graphics,
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
                    graphics,
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
                graphics,
                contentRight,
                rowTop,
                optionControlHeight
        );
    }

    private void drawListItem(
            GuiGraphicsExtractor graphics,
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

        graphics.fill(
                contentLeft + 18,
                itemTop,
                contentRight - 150,
                itemTop + listRowHeight - 2,
                selected
                        ? 0xFF3E596F
                        : 0xFF2A2E35
        );

        graphics.text(
                font,
                Component.literal(value),
                contentLeft + 26,
                itemTop + 4,
                0xFFFFFFFF,
                false
        );
    }

    private void drawListButtons(
            GuiGraphicsExtractor graphics,
            int contentRight,
            int rowTop,
            int optionControlHeight
    ) {
        int buttonTop = rowTop + 3;
        int removeLeft = contentRight - 72;
        int addLeft = removeLeft - 60;

        graphics.fill(
                addLeft,
                buttonTop,
                addLeft + 52,
                buttonTop + optionControlHeight,
                0xFF3E7A52
        );

        graphics.text(
                font,
                Component.literal("Add"),
                addLeft + 13,
                buttonTop + 6,
                0xFFFFFFFF,
                false
        );

        graphics.fill(
                removeLeft,
                buttonTop,
                removeLeft + 64,
                buttonTop + optionControlHeight,
                0xFF8A4141
        );

        graphics.text(
                font,
                Component.literal("Remove"),
                removeLeft + 7,
                buttonTop + 6,
                0xFFFFFFFF,
                false
        );
    }
}
