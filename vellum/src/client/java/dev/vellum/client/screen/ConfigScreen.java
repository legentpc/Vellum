package dev.vellum.client.screen;

import dev.vellum.client.widget.StringListOption;
import dev.vellum.config.ConfigOptionHandle;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

public final class ConfigScreen extends Screen {
    private static final int PANEL_MARGIN = 24;
    private static final int HEADER_HEIGHT = 32;
    private static final int NAVIGATION_WIDTH = 180;
    private static final int CATEGORY_ROW_HEIGHT = 20;
    private static final int CATEGORY_TEXT_PADDING = 8;
    private static final int CATEGORY_INDENT = 12;
    private static final int OPTION_ROW_HEIGHT = 38;
    private static final int OPTION_CONTROL_WIDTH = 52;
    private static final int OPTION_CONTROL_HEIGHT = 20;

    private final ConfigScreenState state;

    private int navigationLeft;
    private int navigationTop;
    private int navigationRight;
    private int navigationBottom;

    private int contentLeft;
    private int contentTop;
    private int contentRight;
    private int contentBottom;

    public ConfigScreen(ConfigScreenState state) {
        super(Component.literal("Vellum Configuration"));
        this.state = state;
    }

    public ConfigScreenState state() {
        return state;
    }

    @Override
    protected void init() {
        updateLayout();
    }

    @Override
    public void extractRenderState(
            GuiGraphicsExtractor graphics,
            int mouseX,
            int mouseY,
            float partialTick
    ) {
        updateLayout();

        drawBackground(graphics);
        drawHeader(graphics);
        drawNavigationPanel(graphics);
        drawContentPanel(graphics);
    }

    private void updateLayout() {
        int left = PANEL_MARGIN;
        int top = PANEL_MARGIN + HEADER_HEIGHT;
        int right = width - PANEL_MARGIN;
        int bottom = height - PANEL_MARGIN;

        navigationLeft = left;
        navigationTop = top;
        navigationRight = left + NAVIGATION_WIDTH;
        navigationBottom = bottom;

        contentLeft = navigationRight + 12;
        contentTop = top;
        contentRight = right;
        contentBottom = bottom;
    }

    private void drawBackground(GuiGraphicsExtractor graphics) {
        graphics.fill(
                0,
                0,
                width,
                height,
                0xFF101216
        );
    }

    private void drawHeader(GuiGraphicsExtractor graphics) {
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

    private void drawNavigationPanel(
            GuiGraphicsExtractor graphics
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

        for (var category : state.categoryTree().visibleNodes()) {
            drawCategoryRow(graphics, category, rowTop);
            rowTop += CATEGORY_ROW_HEIGHT;
        }
    }

    private void drawContentPanel(
            GuiGraphicsExtractor graphics
    ) {
        graphics.fill(
                contentLeft,
                contentTop,
                contentRight,
                contentBottom,
                0xFF191C21
        );

        Component selectedTitle = state.selectedCategory() == null
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

        if (state.selectedCategory() == null) {
            return;
        }

        int optionTop = contentTop + 42;

        for (var option : state.selectedCategory().options()) {
            drawOptionRow(
                    graphics,
                    option,
                    optionTop
            );

            optionTop += OPTION_ROW_HEIGHT;
        }
    }

    private void drawOptionRow(
            GuiGraphicsExtractor graphics,
            ConfigOptionHandle<?> option,
            int rowTop
    ) {
        graphics.fill(
                contentLeft + 8,
                rowTop - 4,
                contentRight - 8,
                rowTop + OPTION_ROW_HEIGHT - 4,
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

        if (isBooleanOption(option)) {
            drawBooleanControl(
                    graphics,
                    option,
                    rowTop
            );
        } else if (isStringListOption(option)) {
            drawListControls(
                    graphics,
                    option,
                    rowTop
            );
        }
    }

    private void drawCategoryRow(
            GuiGraphicsExtractor graphics,
            dev.vellum.client.navigation.CategoryNode category,
            int rowTop
    ) {
        boolean selected =
                state.selectedCategory() == category;

        if (selected) {
            graphics.fill(
                    navigationLeft + 4,
                    rowTop - 3,
                    navigationRight - 4,
                    rowTop + CATEGORY_ROW_HEIGHT - 3,
                    0xFF303640
            );
        }

        int left = navigationLeft
                + CATEGORY_TEXT_PADDING
                + category.depth() * CATEGORY_INDENT;

        String indicator = category.hasChildren()
                ? category.expanded() ? "▾ " : "▸ "
                : "  ";

        graphics.text(
                font,
                Component.literal(indicator + category.title()),
                left,
                rowTop,
                selected ? 0xFFFFFFFF : 0xFFD0D4DA,
                false
        );
    }

    @Override
    public boolean mouseClicked(
            MouseButtonEvent click,
            boolean doubled
    ) {
        double mouseX = click.x();
        double mouseY = click.y();

        if (click.button() != 0) {
            return super.mouseClicked(click, doubled);
        }

        if (isInsideNavigation(mouseX, mouseY)) {
            return handleNavigationClick(
                    mouseX,
                    mouseY,
                    click,
                    doubled
            );
        }

        if (isInsideContent(mouseX, mouseY)) {
            return handleContentClick(
                    mouseX,
                    mouseY,
                    click,
                    doubled
            );
        }

        return super.mouseClicked(click, doubled);
    }

    private boolean handleNavigationClick(
            double mouseX,
            double mouseY,
            MouseButtonEvent click,
            boolean doubled
    ) {
        int rowTop = navigationTop + 32;

        for (var category : state.categoryTree().visibleNodes()) {
            int rowBottom = rowTop + CATEGORY_ROW_HEIGHT;

            if (mouseY >= rowTop && mouseY < rowBottom) {
                int categoryLeft = navigationLeft
                        + CATEGORY_TEXT_PADDING
                        + category.depth() * CATEGORY_INDENT;

                boolean clickedIndicator =
                        category.hasChildren()
                                && mouseX >= categoryLeft
                                && mouseX < categoryLeft + 16;

                if (clickedIndicator) {
                    state.toggle(category);
                    return true;
                }

                state.select(category);
                return true;
            }

            rowTop += CATEGORY_ROW_HEIGHT;
        }

        return super.mouseClicked(click, doubled);
    }

    private boolean isInsideNavigation(
            double mouseX,
            double mouseY
    ) {
        return mouseX >= navigationLeft
                && mouseX < navigationRight
                && mouseY >= navigationTop
                && mouseY < navigationBottom;
    }

    private boolean isInsideContent(
            double mouseX,
            double mouseY
    ) {
        return mouseX >= contentLeft
                && mouseX < contentRight
                && mouseY >= contentTop
                && mouseY < contentBottom;
    }

    private boolean handleContentClick(
            double mouseX,
            double mouseY,
            MouseButtonEvent click,
            boolean doubled
    ) {
        if (state.selectedCategory() == null) {
            return super.mouseClicked(click, doubled);
        }

        int rowTop = contentTop + 42;

        for (var option : state.selectedCategory().options()) {
            int rowBottom = rowTop + OPTION_ROW_HEIGHT;

            if (mouseY >= rowTop && mouseY < rowBottom) {
                if (isBooleanOption(option)
                        && isInsideBooleanControl(mouseX, rowTop)) {
                    toggleBoolean(option);
                    return true;
                }

                if (isStringListOption(option)
                        && isInsideAddButton(mouseX, rowTop)) {
                    addListValue(option);
                    return true;
                }

                if (isStringListOption(option)
                        && isInsideRemoveButton(mouseX, rowTop)) {
                    removeListValue(option);
                    return true;
                }

                return true;
            }

            rowTop += OPTION_ROW_HEIGHT;
        }

        return super.mouseClicked(click, doubled);
    }

    private boolean isInsideAddButton(
            double mouseX,
            int rowTop
    ) {
        int removeLeft = contentRight - 72;
        int addLeft = removeLeft - 60;
        int buttonTop = rowTop + 3;

        return mouseX >= addLeft
                && mouseX < addLeft + 52
                && buttonTop <= rowTop + OPTION_ROW_HEIGHT
                && rowTop <= buttonTop + OPTION_CONTROL_HEIGHT;
    }

    private boolean isInsideRemoveButton(
            double mouseX,
            int rowTop
    ) {
        int removeLeft = contentRight - 72;
        int buttonTop = rowTop + 3;

        return mouseX >= removeLeft
                && mouseX < removeLeft + 64
                && buttonTop <= rowTop + OPTION_ROW_HEIGHT
                && rowTop <= buttonTop + OPTION_CONTROL_HEIGHT;
    }

    private void addListValue(
            ConfigOptionHandle<?> option
    ) {
        StringListOption list =
                new StringListOption(option);

        list.add("New Entry");
        state.markDirty();
    }

    private void removeListValue(
            ConfigOptionHandle<?> option
    ) {
        StringListOption list =
                new StringListOption(option);

        list.removeSelected();
        state.markDirty();
    }

    private boolean isInsideBooleanControl(
            double mouseX,
            int rowTop
    ) {
        int left = contentRight
                - OPTION_CONTROL_WIDTH
                - 18;

        int top = rowTop + 3;

        return mouseX >= left
                && mouseX < left + OPTION_CONTROL_WIDTH
                && top >= contentTop
                && top + OPTION_CONTROL_HEIGHT <= contentBottom;
    }

    private boolean isStringListOption(
            ConfigOptionHandle<?> option
    ) {
        if (!List.class.isAssignableFrom(option.valueType())) {
            return false;
        }

        Type genericType = option.genericType();

        if (!(genericType instanceof ParameterizedType parameterizedType)) {
            return false;
        }

        Type[] arguments = parameterizedType.getActualTypeArguments();

        return arguments.length == 1
                && arguments[0] == String.class;
    }

    private boolean isBooleanOption(
            ConfigOptionHandle<?> option
    ) {
        Class<?> type = option.valueType();

        return type == boolean.class
                || type == Boolean.class;
    }

    private void drawBooleanControl(
            GuiGraphicsExtractor graphics,
            ConfigOptionHandle<?> option,
            int rowTop
    ) {
        boolean enabled = readBoolean(option);

        int left = contentRight
                - OPTION_CONTROL_WIDTH
                - 18;

        int top = rowTop + 3;

        graphics.fill(
                left,
                top,
                left + OPTION_CONTROL_WIDTH,
                top + OPTION_CONTROL_HEIGHT,
                enabled
                        ? 0xFF3E7A52
                        : 0xFF454A52
        );

        graphics.text(
                font,
                Component.literal(enabled ? "ON" : "OFF"),
                left + 13,
                top + 6,
                0xFFFFFFFF,
                false
        );
    }

    private void drawListControls(
            GuiGraphicsExtractor graphics,
            ConfigOptionHandle<?> option,
            int rowTop
    ) {
        int buttonTop = rowTop + 3;
        int removeLeft = contentRight - 72;
        int addLeft = removeLeft - 60;

        graphics.fill(
                addLeft,
                buttonTop,
                addLeft + 52,
                buttonTop + OPTION_CONTROL_HEIGHT,
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
                buttonTop + OPTION_CONTROL_HEIGHT,
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

    @SuppressWarnings("unchecked")
    private boolean readBoolean(
            ConfigOptionHandle<?> option
    ) {
        return Boolean.TRUE.equals(option.get());
    }

    @SuppressWarnings("unchecked")
    private void toggleBoolean(
            ConfigOptionHandle<?> option
    ) {
        ConfigOptionHandle<Boolean> booleanOption =
                (ConfigOptionHandle<Boolean>) option;

        booleanOption.set(!readBoolean(option));
        state.markDirty();
    }
}
