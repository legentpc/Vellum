package dev.vellum.client.screen;

import dev.vellum.client.editor.VellumConfigEditor;
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

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ConfigScreen
        extends VellumScreenComponent {
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

    private final VellumConfigEditor editor;
    private final ConfigManager<?> configManager;
    private final ConfigScreenInputHandler inputHandler;

    private static final Logger LOGGER =
            LoggerFactory.getLogger(
                    "vellumconfig-client"
            );

    private int navigationLeft;
    private int navigationTop;
    private int navigationRight;
    private int navigationBottom;

    private int contentLeft;
    private int contentTop;
    private int contentRight;
    private int contentBottom;

    private EditBox listInputBox;
    private OptionMenu optionMenu;
    private ConfigScreenRenderer renderer;

    public ConfigScreen(
            ConfigScreenState state,
            ConfigManager<?> configManager
    ) {
        super(Component.literal("Vellum Configuration"));

        ConfigScreenState checkedState =
                Objects.requireNonNull(
                        state,
                        "state"
                );

        this.configManager = Objects.requireNonNull(
                configManager,
                "configManager"
        );

        this.editor =
                new VellumConfigEditor(
                        checkedState,
                        this.configManager
                );

        this.inputHandler =
                new ConfigScreenInputHandler();
    }

    public ConfigScreenState state() {
        return editor.state();
    }

    @Override
    protected void init() {
        LOGGER.info(
                "ConfigScreen.init() called. Size: {}x{}",
                width,
                height
        );

        updateLayout();
        renderer = new ConfigScreenRenderer(font);

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
        LOGGER.info(
                "ConfigScreen.extractRenderState() called."
        );

        updateLayout();

        renderer.drawBackground(
                graphics,
                width,
                height
        );

        renderer.drawHeader(
                graphics,
                title,
                width
        );

        renderer.drawNavigationPanel(
                graphics,
                editor.state(),
                navigationLeft,
                navigationTop,
                navigationRight,
                navigationBottom
        );

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
            editor.reset(
                    optionMenu.option()
            );

            optionMenu.hide();
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
        renderer.drawContentPanelHeader(
                graphics,
                editor.state(),
                contentLeft,
                contentTop,
                contentRight,
                contentBottom
        );

        if (editor.selectedCategory() == null) {
            renderer.drawSaveButton(
                    graphics,
                    editor.state(),
                    contentRight,
                    contentBottom
            );
            return;
        }

        int optionTop = contentTop + 42;

        for (var option : editor.selectedCategory().options()) {
            drawOptionRow(
                    graphics,
                    option,
                    optionTop
            );

            optionTop += OPTION_ROW_HEIGHT;
        }

        renderer.drawSaveButton(
                graphics,
                editor.state(),
                contentRight,
                contentBottom
        );

        renderer.drawOptionMenu(
                graphics,
                optionMenu,
                contentRight,
                contentTop
        );
        renderer.drawCloseWarning(
                graphics,
                editor.state(),
                width,
                height
        );
    }

    private boolean handleCloseWarningClick(
            double mouseX,
            double mouseY
    ) {
        if (!editor.closeWarningOpen()) {
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
            editor.closeCloseWarning();
            super.onClose();
            return true;
        }

        if (mouseY >= top + 56
                && mouseY < top + 78) {
            editor.markClean();
            editor.closeCloseWarning();
            super.onClose();
            return true;
        }

        if (mouseY >= top + 78
                && mouseY < top + 104) {
            editor.closeCloseWarning();
            return true;
        }

        return true;
    }

    private void drawOptionRow(
            GuiGraphicsExtractor graphics,
            ConfigOptionHandle<?> option,
            int rowTop
    ) {
        renderer.drawOptionRowHeader(
                graphics,
                option,
                contentLeft,
                contentRight,
                rowTop,
                OPTION_ROW_HEIGHT
        );

        if (isBooleanOption(option)) {
            renderer.drawBooleanControl(
                    graphics,
                    readBoolean(option),
                    contentRight,
                    rowTop,
                    OPTION_CONTROL_WIDTH,
                    OPTION_CONTROL_HEIGHT
            );
        } else if (isStringListOption(option)) {
            drawListControls(
                    graphics,
                    option,
                    rowTop
            );
        } else if (isNumericOption(option)) {
            NumericOption numeric =
                    new NumericOption(option);

            renderer.drawNumericControl(
                    graphics,
                    numeric.value(),
                    contentRight,
                    rowTop,
                    OPTION_CONTROL_HEIGHT
            );
        } else if (isStringOption(option)) {
            StringOption string =
                    new StringOption(option);

            renderer.drawStringControl(
                    graphics,
                    string.value(),
                    contentRight,
                    rowTop,
                    OPTION_CONTROL_HEIGHT
            );
        }

        renderer.drawMoreButton(
                graphics,
                contentRight,
                rowTop,
                OPTION_CONTROL_HEIGHT
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

        if (inputHandler.handleNavigationClick(
                mouseX,
                mouseY,
                editor,
                navigationLeft,
                navigationTop,
                navigationRight,
                navigationBottom,
                CATEGORY_ROW_HEIGHT,
                CATEGORY_TEXT_PADDING,
                CATEGORY_INDENT
        )) {
            return true;
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

    @Override
    public void onClose() {
        if (editor.dirty()) {
            editor.openCloseWarning();
            return;
        }

        super.onClose();
    }

    private boolean isInsideNavigation(
            double mouseX,
            double mouseY
    ) {
        return inputHandler.isInsideNavigation(
                mouseX,
                mouseY,
                navigationLeft,
                navigationTop,
                navigationRight,
                navigationBottom
        );
    }

    private boolean isInsideContent(
            double mouseX,
            double mouseY
    ) {
        return inputHandler.isInsideContent(
                mouseX,
                mouseY,
                contentLeft,
                contentTop,
                contentRight,
                contentBottom
        );
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

        if (editor.selectedCategory() == null) {
            return super.mouseClicked(click, doubled);
        }

        int rowTop = contentTop + 42;

        for (var option : editor.selectedCategory().options()) {
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
                        editor.markDirty();
                        return true;
                    }
                    if (isInsideNumericPlus(mouseX, mouseY, rowTop)) {
                        numeric.increment();
                        editor.markDirty();
                        return true;
                    }
                }

                if (handleStringClick(
                        mouseX,
                        mouseY,
                        option,
                        rowTop
                )) {
                    return true;
                }

                if (isStringListOption(option)) {
                    StringListOption list =
                            new StringListOption(option);

                    int listTop =
                            rowTop + LIST_ROW_TOP_OFFSET;

                    int index =
                            inputHandler.listItemIndex(
                                    mouseX,
                                    mouseY,
                                    contentLeft,
                                    contentRight,
                                    listTop,
                                    LIST_ROW_HEIGHT,
                                    list.values().size()
                            );

                    if (index >= 0) {
                        editor.selectListItem(
                                option,
                                index
                        );

                        editor.beginListDrag(
                                option,
                                index
                        );

                        return true;
                    }
                }

                if (isStringListOption(option)
                        && isInsideAddButton(mouseX, rowTop)) {
                    addListValue(option);
                    return true;
                }

                if (isStringListOption(option)
                        && isInsideRemoveButton(
                        mouseX,
                        rowTop
                )) {
                    removeListValue(option);
                    return true;
                }

                if (isInsideMoreButton(
                        mouseX,
                        mouseY,
                        rowTop
                )) {
                    optionMenu = new OptionMenu(option);
                    optionMenu.show();
                    return true;
                }

                return super.mouseClicked(
                        click,
                        doubled
                );

            }

            rowTop += OPTION_ROW_HEIGHT;
        }

        return super.mouseClicked(click, doubled);
    }

    private boolean isInsideSaveButton(
            double mouseX,
            double mouseY
    ) {
        return inputHandler.isInsideSaveButton(
                mouseX,
                mouseY,
                contentRight,
                contentBottom,
                SAVE_BUTTON_WIDTH,
                SAVE_BUTTON_HEIGHT
        );
    }

    private boolean isInsideMoreButton(
            double mouseX,
            double mouseY,
            int rowTop
    ) {
        return inputHandler.isInsideMoreButton(
                mouseX,
                mouseY,
                contentRight,
                rowTop,
                OPTION_CONTROL_HEIGHT
        );
    }

    private void saveConfiguration() {
        try {
            editor.save();
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
        return inputHandler.isInsideAddButton(
                mouseX,
                contentRight,
                rowTop,
                OPTION_ROW_HEIGHT,
                OPTION_CONTROL_HEIGHT
        );
    }

    private boolean isInsideRemoveButton(
            double mouseX,
            int rowTop
    ) {
        return inputHandler.isInsideRemoveButton(
                mouseX,
                contentRight,
                rowTop,
                OPTION_ROW_HEIGHT,
                OPTION_CONTROL_HEIGHT
        );
    }

    private void addListValue(
            ConfigOptionHandle<?> option
    ) {
        editor.openListInput(option);

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
                editor.listInputOption();

        if (option == null) {
            return;
        }

        StringListOption list =
                new StringListOption(option);

        list.add(value);
        editor.markDirty();

        editor.closeListInput();
        listInputBox.setValue("");
        listInputBox.setVisible(false);
    }

    private void cancelListInput() {
        editor.closeListInput();

        listInputBox.setValue("");
        listInputBox.setVisible(false);
    }

    @Override
    public boolean keyPressed(
            KeyEvent event
    ) {
        int keyCode = event.key();

        if (editor.editingStringOption() != null) {
            if (keyCode == 257) {
                confirmStringEdit();
                return true;
            }

            if (keyCode == 256) {
                cancelStringEdit();
                return true;
            }
        }

        if (editor.listInputOpen()) {
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
        if (editor.selectedListOption() != option) {
            return;
        }

        StringListOption list =
                new StringListOption(option);

        list.remove(
                editor.selectedListIndex()
        );

        editor.clearListSelection();
        editor.markDirty();
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

    private boolean isInsideNumericMinus(
            double mouseX,
            double mouseY,
            int rowTop
    ) {
        return inputHandler.isInsideNumericMinus(
                mouseX,
                mouseY,
                contentRight,
                rowTop,
                OPTION_CONTROL_HEIGHT
        );
    }

    private boolean isInsideNumericPlus(
            double mouseX,
            double mouseY,
            int rowTop
    ) {
        return inputHandler.isInsideNumericPlus(
                mouseX,
                mouseY,
                contentRight,
                rowTop,
                OPTION_CONTROL_HEIGHT
        );
    }

    private boolean isInsideStringControl(
            double mouseX,
            double mouseY,
            int rowTop
    ) {
        return inputHandler.isInsideStringControl(
                mouseX,
                mouseY,
                contentRight,
                rowTop,
                OPTION_CONTROL_HEIGHT
        );
    }

    private void beginStringEdit(
            ConfigOptionHandle<?> option,
            int rowTop
    ) {
        StringOption string =
                new StringOption(option);

        editor.beginStringEdit(option);

        listInputBox.setValue(string.value());
        listInputBox.setY(rowTop + 3);
        listInputBox.setVisible(true);
        setInitialFocus(listInputBox);
    }

    private void confirmStringEdit() {
        ConfigOptionHandle<?> option =
                editor.editingStringOption();

        if (option == null) {
            return;
        }

        StringOption string =
                new StringOption(option);

        string.setValue(listInputBox.getValue());
        editor.markDirty();
        editor.endStringEdit();

        listInputBox.setValue("");
        listInputBox.setVisible(false);
    }

    private void cancelStringEdit() {
        editor.endStringEdit();

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

    private void drawListControls(
            GuiGraphicsExtractor graphics,
            ConfigOptionHandle<?> option,
            int rowTop
    ) {
        StringListOption list =
                new StringListOption(option);

        renderer.drawListControls(
                graphics,
                list.values(),
                editor.selectedListOption() == option
                        ? editor.selectedListIndex()
                        : -1,
                editor.selectedListOption() == option,
                contentLeft,
                contentRight,
                rowTop,
                LIST_ROW_TOP_OFFSET,
                LIST_ROW_HEIGHT,
                OPTION_CONTROL_HEIGHT
        );
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
        editor.markDirty();
    }
    @Override
    public boolean mouseDragged(MouseButtonEvent event, double deltaX, double deltaY) {
        if (!editor.draggingListItem()) return super.mouseDragged(event, deltaX, deltaY);
        return true;
    }

    @Override
    public boolean mouseReleased(
            MouseButtonEvent event
    ) {
        if (!editor.draggingListItem()) {
            return super.mouseReleased(event);
        }

        ConfigOptionHandle<?> option =
                editor.draggingListOption();

        if (option != null
                && editor.selectedCategory() != null) {
            int rowTop = contentTop + 42;

            for (var currentOption
                    : editor.selectedCategory().options()) {
                if (currentOption == option) {
                    int target =
                            (int) (
                                    (event.y()
                                            - rowTop
                                            - LIST_ROW_TOP_OFFSET)
                                            / LIST_ROW_HEIGHT
                            );

                    StringListOption list =
                            new StringListOption(option);

                    if (target >= 0
                            && target < list.values().size()) {
                        list.move(
                                editor.draggingListIndex(),
                                target
                        );

                        editor.selectListItem(
                                option,
                                target
                        );

                        editor.markDirty();
                    }

                    break;
                }

                rowTop += OPTION_ROW_HEIGHT;
            }
        }

        editor.endListDrag();
        return true;
    }

    private boolean handleSaveClick(
            double mouseX,
            double mouseY
    ) {
        if (!isInsideSaveButton(mouseX, mouseY)) {
            return false;
        }

        saveConfiguration();
        return true;
    }

    private boolean handleMoreClick(
            double mouseX,
            double mouseY,
            ConfigOptionHandle<?> option,
            int rowTop
    ) {
        if (!isInsideMoreButton(
                mouseX,
                mouseY,
                rowTop
        )) {
            return false;
        }

        optionMenu =
                new OptionMenu(option);

        optionMenu.show();
        return true;
    }

    private boolean handleNumericClick(
            double mouseX,
            double mouseY,
            ConfigOptionHandle<?> option,
            int rowTop
    ) {
        if (!isNumericOption(option)) {
            return false;
        }

        NumericOption numeric =
                new NumericOption(option);

        if (isInsideNumericMinus(
                mouseX,
                mouseY,
                rowTop
        )) {
            numeric.decrement();
            editor.markDirty();
            return true;
        }

        if (isInsideNumericPlus(
                mouseX,
                mouseY,
                rowTop
        )) {
            numeric.increment();
            editor.markDirty();
            return true;
        }

        return false;
    }

    private boolean handleListClick(
            double mouseX,
            double mouseY,
            ConfigOptionHandle<?> option,
            int rowTop
    ) {
        if (!isStringListOption(option)) {
            return false;
        }

        StringListOption list =
                new StringListOption(option);

        int listTop =
                rowTop + LIST_ROW_TOP_OFFSET;

        int index =
                inputHandler.listItemIndex(
                        mouseX,
                        mouseY,
                        contentLeft,
                        contentRight,
                        listTop,
                        LIST_ROW_HEIGHT,
                        list.values().size()
                );

        if (index >= 0) {
            editor.selectListItem(
                    option,
                    index
            );

            editor.beginListDrag(
                    option,
                    index
            );

            return true;
        }

        if (isInsideAddButton(
                mouseX,
                rowTop
        )) {
            addListValue(option);
            return true;
        }

        if (isInsideRemoveButton(
                mouseX,
                rowTop
        )) {
            removeListValue(option);
            return true;
        }

        return false;
    }

    private boolean handleStringClick(
            double mouseX,
            double mouseY,
            ConfigOptionHandle<?> option,
            int rowTop
    ) {
        if (!isStringOption(option)) {
            return false;
        }

        if (!isInsideStringControl(
                mouseX,
                mouseY,
                rowTop
        )) {
            return false;
        }

        beginStringEdit(
                option,
                rowTop
        );

        return true;
    }

    @Override
    protected void beforeScreenClose() {
        if (editor.dirty()) {
            editor.openCloseWarning();
        }
    }
}
