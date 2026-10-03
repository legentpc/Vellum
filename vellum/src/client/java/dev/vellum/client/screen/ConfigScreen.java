package dev.vellum.client.screen;

import dev.vellum.client.widget.NumericOption;
import dev.vellum.client.widget.OptionMenu;
import dev.vellum.client.widget.StringListOption;
import dev.vellum.client.widget.StringOption;
import dev.vellum.config.ConfigManager;
import dev.vellum.config.ConfigOptionHandle;

import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

import java.lang.reflect.ParameterizedType;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;

public final class ConfigScreen extends Screen {
    private static final int PANEL_MARGIN = 24;
    private static final int HEADER_HEIGHT = 32;
    private static final int NAVIGATION_WIDTH = 180;
    private static final int CATEGORY_ROW_HEIGHT = 20;
    private static final int CATEGORY_TEXT_PADDING = 8;
    private static final int CATEGORY_INDENT = 12;
    private static final int OPTION_ROW_HEIGHT = 150;
    private static final int LIST_ROW_HEIGHT = 18;
    private static final int LIST_ROW_TOP_OFFSET = 42;
    private static final int OPTION_CONTROL_WIDTH = 52;
    private static final int OPTION_CONTROL_HEIGHT = 20;
    private static final int SAVE_BUTTON_WIDTH = 64;
    private static final int SAVE_BUTTON_HEIGHT = 20;

    private final ConfigScreenState state;
    private final ConfigManager<?> configManager;
    private EditBox listInputBox;
    private OptionMenu optionMenu;

    private int navigationLeft;
    private int navigationTop;
    private int navigationRight;
    private int navigationBottom;

    private int contentLeft;
    private int contentTop;
    private int contentRight;
    private int contentBottom;

    public ConfigScreen(
            ConfigScreenState state,
            ConfigManager<?> configManager
    ) {
        super(Component.literal("Vellum Configuration"));
        this.state = Objects.requireNonNull(state, "state");
        this.configManager = Objects.requireNonNull(
                configManager,
                "configManager"
        );
    }

    public ConfigScreenState state() {
        return state;
    }

    @Override
    protected void init() {
        updateLayout();

        listInputBox = new EditBox(
                font,
                contentLeft + 24,
                contentTop + 80,
                220,
                20,
                Component.literal("New list item")
        );

        listInputBox.setVisible(false);
        addRenderableWidget(listInputBox);
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

    private void drawOptionMenu(
            GuiGraphicsExtractor graphics
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

    private boolean handleOptionMenuClick(
            double mouseX,
            double mouseY
    ) {
        if (optionMenu == null
                || !optionMenu.open()) {
            return false;
        }

        int left = contentRight - 150;
        int top = contentTop + 72;

        if (mouseX < left
                || mouseX >= left + 130
                || mouseY < top
                || mouseY >= top + 48) {
            optionMenu.hide();
            return true;
        }

        if (mouseY < top + 24) {
            optionMenu.perform(
                    OptionMenu.Action.RESET
            );

            state.markDirty();
            return true;
        }

        optionMenu.perform(
                OptionMenu.Action.CLOSE
        );

        return true;
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
            drawSaveButton(graphics);
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

        drawSaveButton(graphics);

        drawCloseWarning(graphics);
    }

    private boolean handleCloseWarningClick(
            double mouseX,
            double mouseY
    ) {
        if (!state.closeWarningOpen()) {
            return false;
        }

        int left = width / 2 - 140;
        int top = height / 2 - 55;

        if (mouseX < left
                || mouseX >= left + 280
                || mouseY < top
                || mouseY >= top + 110) {
            return true;
        }

        if (mouseY >= top + 32
                && mouseY < top + 56) {
            saveConfiguration();
            state.closeCloseWarning();
            super.onClose();
            return true;
        }

        if (mouseY >= top + 56
                && mouseY < top + 78) {
            state.markClean();
            state.closeCloseWarning();
            super.onClose();
            return true;
        }

        if (mouseY >= top + 78
                && mouseY < top + 104) {
            state.closeCloseWarning();
            return true;
        }

        return true;
    }

    private void drawSaveButton(
            GuiGraphicsExtractor graphics
    ) {
        int left = contentRight - SAVE_BUTTON_WIDTH - 12;
        int top = contentBottom - SAVE_BUTTON_HEIGHT - 12;

        graphics.fill(
                left,
                top,
                left + SAVE_BUTTON_WIDTH,
                top + SAVE_BUTTON_HEIGHT,
                state.dirty() ? 0xFF3E7A52 : 0xFF454A52
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
            drawListControls(graphics, option, rowTop);
        } else if (isNumericOption(option)) {
            drawNumericControl(graphics, option, rowTop);
        } else if (isStringOption(option)) {
            drawStringControl(graphics, option, rowTop);
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

    private void drawMoreButton(
            GuiGraphicsExtractor graphics,
            int rowTop
    ) {
        int left = contentRight - 84;
        int top = rowTop + 3;

        graphics.fill(
                left,
                top,
                left + 52,
                top + OPTION_CONTROL_HEIGHT,
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

    @Override
    public void onClose() {
        if (state.dirty()) {
            state.openCloseWarning();
            return;
        }

        super.onClose();
    }

    private void drawCloseWarning(
            GuiGraphicsExtractor graphics
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
        if (handleCloseWarningClick(mouseX, mouseY)) {
            return true;
        }

        if (isInsideSaveButton(mouseX, mouseY)) {
            saveConfiguration();
            return true;
        }

        if (handleOptionMenuClick(mouseX, mouseY)) {
            return true;
        }

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

                if (isNumericOption(option)) {
                    NumericOption numeric = new NumericOption(option);
                    if (isInsideNumericMinus(mouseX, mouseY, rowTop)) {
                        numeric.decrement();
                        state.markDirty();
                        return true;
                    }
                    if (isInsideNumericPlus(mouseX, mouseY, rowTop)) {
                        numeric.increment();
                        state.markDirty();
                        return true;
                    }
                }

                if (isStringOption(option)
                        && isInsideStringControl(mouseX, mouseY, rowTop)) {
                    beginStringEdit(option, rowTop);
                    return true;
                }

                if (isStringListOption(option)) {
                    StringListOption list = new StringListOption(option);
                    int listTop = rowTop + LIST_ROW_TOP_OFFSET;
                    int index = (int) ((mouseY - listTop) / LIST_ROW_HEIGHT);

                    if (index >= 0
                            && index < list.values().size()
                            && mouseX >= contentLeft + 18
                            && mouseX < contentRight - 150) {
                        list.select(index);
                        state.selectListItem(option, index);
                        state.beginListDrag(option, index);
                        return true;
                    }
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

    private boolean isInsideSaveButton(
            double mouseX,
            double mouseY
    ) {
        int left = contentRight - SAVE_BUTTON_WIDTH - 12;
        int top = contentBottom - SAVE_BUTTON_HEIGHT - 12;

        return mouseX >= left
                && mouseX < left + SAVE_BUTTON_WIDTH
                && mouseY >= top
                && mouseY < top + SAVE_BUTTON_HEIGHT;
    }

    private void saveConfiguration() {
        if (!state.dirty()) {
            return;
        }

        try {
            configManager.save();
            state.markClean();
        } catch (IOException exception) {
            throw new IllegalStateException(
                    "Unable to save Vellum configuration",
                    exception
            );
        }
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
        state.openListInput(option);

        listInputBox.setValue("");
        listInputBox.setVisible(true);
        setInitialFocus(listInputBox);
    }

    private void confirmListInput() {
        String value = listInputBox.getValue().trim();

        if (value.isEmpty()) {
            return;
        }

        ConfigOptionHandle<?> option =
                state.listInputOption();

        if (option == null) {
            return;
        }

        StringListOption list =
                new StringListOption(option);

        list.add(value);
        state.markDirty();

        state.closeListInput();
        listInputBox.setValue("");
        listInputBox.setVisible(false);
    }

    private void cancelListInput() {
        state.closeListInput();

        listInputBox.setValue("");
        listInputBox.setVisible(false);
    }

    @Override
    public boolean keyPressed(
            KeyEvent event
    ) {
        int keyCode = event.key();

        if (state.editingStringOption() != null) {
            if (keyCode == 257) {
                confirmStringEdit();
                return true;
            }

            if (keyCode == 256) {
                cancelStringEdit();
                return true;
            }
        }

        if (state.listInputOpen()) {
            if (keyCode == 257) {
                confirmListInput();
                return true;
            }

            if (keyCode == 256) {
                cancelListInput();
                return true;
            }
        }

        return super.keyPressed(event);
    }

    private void removeListValue(
            ConfigOptionHandle<?> option
    ) {
        if (state.selectedListOption() != option) {
            return;
        }

        StringListOption list = new StringListOption(option);
        list.select(state.selectedListIndex());
        list.removeSelected();

        state.clearListSelection();
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

    private boolean isNumericOption(ConfigOptionHandle<?> option) {
        Class<?> type = option.valueType();
        return type == byte.class || type == Byte.class
                || type == short.class || type == Short.class
                || type == int.class || type == Integer.class
                || type == long.class || type == Long.class
                || type == float.class || type == Float.class
                || type == double.class || type == Double.class;
    }

    private boolean isStringOption(ConfigOptionHandle<?> option) {
        return option.valueType() == String.class;
    }

    private void drawNumericControl(GuiGraphicsExtractor graphics, ConfigOptionHandle<?> option, int rowTop) {
        NumericOption numeric = new NumericOption(option);
        int top = rowTop + 3;
        int plusLeft = contentRight - 38;
        int minusLeft = plusLeft - 24;

        graphics.fill(minusLeft, top, minusLeft + 20, top + OPTION_CONTROL_HEIGHT, 0xFF454A52);
        graphics.text(font, Component.literal("-"), minusLeft + 7, top + 5, 0xFFFFFFFF, false);
        graphics.text(font, Component.literal(numeric.value().toString()), minusLeft - 42, top + 5, 0xFFFFFFFF, false);
        graphics.fill(plusLeft, top, plusLeft + 20, top + OPTION_CONTROL_HEIGHT, 0xFF3E7A52);
        graphics.text(font, Component.literal("+"), plusLeft + 7, top + 5, 0xFFFFFFFF, false);
    }

    private void drawStringControl(GuiGraphicsExtractor graphics, ConfigOptionHandle<?> option, int rowTop) {
        StringOption string = new StringOption(option);
        int left = contentRight - 210;
        int top = rowTop + 3;
        graphics.fill(left, top, contentRight - 18, top + OPTION_CONTROL_HEIGHT, 0xFF2A2E35);
        graphics.text(font, Component.literal(string.value()), left + 8, top + 5, 0xFFFFFFFF, false);
    }

    private boolean isInsideNumericMinus(double mouseX, double mouseY, int rowTop) {
        int left = contentRight - 62;
        return mouseX >= left && mouseX < left + 20
                && mouseY >= rowTop + 3
                && mouseY < rowTop + 3 + OPTION_CONTROL_HEIGHT;
    }

    private boolean isInsideNumericPlus(double mouseX, double mouseY, int rowTop) {
        int left = contentRight - 38;
        return mouseX >= left && mouseX < left + 20
                && mouseY >= rowTop + 3
                && mouseY < rowTop + 3 + OPTION_CONTROL_HEIGHT;
    }

    private boolean isInsideStringControl(double mouseX, double mouseY, int rowTop) {
        int left = contentRight - 210;
        return mouseX >= left && mouseX < contentRight - 18
                && mouseY >= rowTop + 3
                && mouseY < rowTop + 3 + OPTION_CONTROL_HEIGHT;
    }

    private void beginStringEdit(
            ConfigOptionHandle<?> option,
            int rowTop
    ) {
        StringOption string =
                new StringOption(option);

        state.beginStringEdit(option);

        listInputBox.setValue(string.value());
        listInputBox.setY(rowTop + 3);
        listInputBox.setVisible(true);
        setInitialFocus(listInputBox);
    }

    private void confirmStringEdit() {
        ConfigOptionHandle<?> option =
                state.editingStringOption();

        if (option == null) {
            return;
        }

        StringOption string =
                new StringOption(option);

        string.setValue(listInputBox.getValue());
        state.markDirty();
        state.endStringEdit();

        listInputBox.setValue("");
        listInputBox.setVisible(false);
    }

    private void cancelStringEdit() {
        state.endStringEdit();

        listInputBox.setValue("");
        listInputBox.setVisible(false);
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
        StringListOption list = new StringListOption(option);
        int listTop = rowTop + LIST_ROW_TOP_OFFSET;
        int index = 0;

        for (String value : list.values()) {
            int itemTop = listTop + index * LIST_ROW_HEIGHT;
            boolean selected = state.selectedListOption() == option
                    && state.selectedListIndex() == index;

            graphics.fill(
                    contentLeft + 18,
                    itemTop,
                    contentRight - 150,
                    itemTop + LIST_ROW_HEIGHT - 2,
                    selected ? 0xFF3E596F : 0xFF2A2E35
            );

            graphics.text(
                    font,
                    Component.literal(value),
                    contentLeft + 26,
                    itemTop + 4,
                    0xFFFFFFFF,
                    false
            );
            index++;
        }

        drawListButtons(graphics, rowTop);
    }

    private void drawListButtons(
            GuiGraphicsExtractor graphics,
            int rowTop
    ) {
        int buttonTop = rowTop + 3;
        int removeLeft = contentRight - 72;
        int addLeft = removeLeft - 60;

        graphics.fill(addLeft, buttonTop, addLeft + 52,
                buttonTop + OPTION_CONTROL_HEIGHT, 0xFF3E7A52);
        graphics.text(font, Component.literal("Add"), addLeft + 13,
                buttonTop + 6, 0xFFFFFFFF, false);

        graphics.fill(removeLeft, buttonTop, removeLeft + 64,
                buttonTop + OPTION_CONTROL_HEIGHT, 0xFF8A4141);
        graphics.text(font, Component.literal("Remove"), removeLeft + 7,
                buttonTop + 6, 0xFFFFFFFF, false);
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
    @Override
    public boolean mouseDragged(MouseButtonEvent event, double deltaX, double deltaY) {
        if (!state.draggingListItem()) return super.mouseDragged(event, deltaX, deltaY);
        return true;
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        if (!state.draggingListItem()) return super.mouseReleased(event);
        ConfigOptionHandle<?> option = state.draggingListOption();
        if (option != null && state.selectedCategory() != null) {
            int rowTop = contentTop + 42;
            for (var currentOption : state.selectedCategory().options()) {
                if (currentOption == option) {
                    int target = (int) ((event.y() - rowTop - LIST_ROW_TOP_OFFSET) / LIST_ROW_HEIGHT);
                    StringListOption list = new StringListOption(option);
                    if (target >= 0 && target < list.values().size()) {
                        list.move(state.draggingListIndex(), target);
                        state.selectListItem(option, target);
                        state.markDirty();
                    }
                    break;
                }
                rowTop += OPTION_ROW_HEIGHT;
            }
        }
        state.endListDrag();
        return true;
    }

}
