package dev.vellum.client.screen;

import dev.vellum.client.editor.VellumConfigEditor;
import dev.vellum.client.input.VellumCharacterEvent;
import dev.vellum.client.input.VellumKeyEvent;
import dev.vellum.client.input.VellumMouseEvent;
import dev.vellum.client.input.VellumMousePosition;
import dev.vellum.client.render.MinecraftRenderContext;
import dev.vellum.client.render.VellumRenderContext;
import dev.vellum.client.widget.NumericOption;
import dev.vellum.client.widget.OptionMenu;
import dev.vellum.client.widget.StringListOption;
import dev.vellum.client.widget.StringOption;
import dev.vellum.config.ConfigOptionHandle;

import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;

import dev.vellum.platform.VellumPlatform;
import dev.vellum.platform.api.VellumLogger;

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
    private final ConfigScreenInputHandler inputHandler;

    private static final VellumLogger LOGGER =
            VellumPlatform.logger(
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
    private VellumRenderContext renderContext;

    public ConfigScreen(
            VellumConfigEditor editor,
            Screen previousScreen
    ) {
        super(
                Component.literal(
                        "Vellum Configuration"
                ),
                previousScreen
        );

        this.editor = Objects.requireNonNull(
                editor,
                "editor"
        );

        this.inputHandler =
                new ConfigScreenInputHandler();
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

        renderContext =
                new MinecraftRenderContext(
                        graphics,
                        font,
                        width,
                        height
                );

        renderer.drawBackground(
                renderContext
        );

        renderer.drawHeader(
                renderContext,
                title,
                width
        );

        renderer.drawNavigationPanel(
                renderContext,
                editor,
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
            VellumMousePosition mouse
    ) {
        if (optionMenu == null
                || !optionMenu.open()) {
            return false;
        }

        int left = contentRight - 150;
        int top = contentTop + 72;

        if (mouse.x() < left
                || mouse.x() >= left + 130
                || mouse.y() < top
                || mouse.y() >= top + 48) {
            optionMenu.hide();
            return true;
        }

        if (mouse.y() < top + 24) {
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
                renderContext,
                editor,
                contentLeft,
                contentTop,
                contentRight,
                contentBottom
        );

        if (editor.selectedCategory() == null) {
            renderer.drawSaveButton(
                    renderContext,
                    editor,
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
                renderContext,
                editor,
                contentRight,
                contentBottom
        );

        renderer.drawOptionMenu(
                renderContext,
                optionMenu,
                contentRight,
                contentTop
        );
        renderer.drawCloseWarning(
                renderContext,
                editor,
                width,
                height
        );
    }

    private boolean handleCloseWarningClick(
            VellumMousePosition mouse
    ) {
        if (!editor.closeWarningOpen()) {
            return false;
        }

        int left = width / 2 - 140;
        int top = height / 2 - 55;

        if (mouse.x() < left
                || mouse.x() >= left + 280
                || mouse.y() < top
                || mouse.y() >= top + 110) {
            return true;
        }

        if (mouse.y() >= top + 32
                && mouse.y() < top + 56) {
            saveConfiguration();
            editor.closeCloseWarning();
            super.onClose();
            return true;
        }

        if (mouse.y() >= top + 56
                && mouse.y() < top + 78) {
            editor.markClean();
            editor.closeCloseWarning();
            super.onClose();
            return true;
        }

        if (mouse.y() >= top + 78
                && mouse.y() < top + 104) {
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
                renderContext,
                option,
                contentLeft,
                contentRight,
                rowTop,
                OPTION_ROW_HEIGHT
        );

        if (isBooleanOption(option)) {
            renderer.drawBooleanControl(
                    renderContext,
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
                    renderContext,
                    numeric.value(),
                    contentRight,
                    rowTop,
                    OPTION_CONTROL_HEIGHT
            );
        } else if (isStringOption(option)) {
            StringOption string =
                    new StringOption(option);

            renderer.drawStringControl(
                    renderContext,
                    string.value(),
                    contentRight,
                    rowTop,
                    OPTION_CONTROL_HEIGHT
            );
        }

        renderer.drawMoreButton(
                renderContext,
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
                new VellumMousePosition(
                        mouseX,
                        mouseY
                ),
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
                    new VellumMousePosition(
                            mouseX,
                            mouseY
                    ),
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
                new VellumMousePosition(mouseX, mouseY),
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
                new VellumMousePosition(mouseX, mouseY),
                contentLeft,
                contentTop,
                contentRight,
                contentBottom
        );
    }

    private boolean handleContentClick(
            VellumMousePosition mouse,
            MouseButtonEvent click,
            boolean doubled
    ) {
        if (handleCloseWarningClick(mouse)) {
            return true;
        }

        if (isInsideSaveButton(mouse.x(), mouse.y())) {
            saveConfiguration();
            return true;
        }

        if (handleOptionMenuClick(mouse)) {
            return true;
        }

        if (editor.selectedCategory() == null) {
            return super.mouseClicked(click, doubled);
        }

        int rowTop = contentTop + 42;

        for (var option : editor.selectedCategory().options()) {
            int rowBottom = rowTop + OPTION_ROW_HEIGHT;

            if (mouse.y() >= rowTop && mouse.y() < rowBottom) {
                if (isBooleanOption(option)
                        && isInsideBooleanControl(mouse.x(), rowTop)) {
                    toggleBoolean(option);
                    return true;
                }

                if (isNumericOption(option)) {
                    NumericOption numeric = new NumericOption(option);
                    if (isInsideNumericMinus(mouse.x(), mouse.y(), rowTop)) {
                        numeric.decrement();
                        editor.markDirty();
                        return true;
                    }
                    if (isInsideNumericPlus(mouse.x(), mouse.y(), rowTop)) {
                        numeric.increment();
                        editor.markDirty();
                        return true;
                    }
                }

                if (handleStringClick(
                        mouse,
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
                                    mouse,
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
                        && isInsideAddButton(mouse.x(), rowTop)) {
                    addListValue(option);
                    return true;
                }

                if (isStringListOption(option)
                        && isInsideRemoveButton(
                        mouse.x(),
                        rowTop
                )) {
                    removeListValue(option);
                    return true;
                }

                if (isInsideMoreButton(
                        mouse.x(),
                        mouse.y(),
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
                new VellumMousePosition(mouseX, mouseY),
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
                new VellumMousePosition(mouseX, mouseY),
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
                new VellumMousePosition(mouseX, 0),
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
                new VellumMousePosition(mouseX, 0),
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
        VellumKeyEvent keyEvent =
                new VellumKeyEvent(
                        event.key(),
                        event.scancode(),
                        event.modifiers(),
                        true
                );

        int keyCode = keyEvent.key();

        if (editor.editingStringOption() != null) {
            if (keyEvent.key() == 257) {
                confirmStringEdit();
                return true;
            }

            if (keyEvent.key() == 256) {
                cancelStringEdit();
                return true;
            }
        }

        if (editor.listInputOpen()) {
            if (keyEvent.key() == 257) {
                confirmListInput();
                return true;
            }

            if (keyEvent.key() == 256) {
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
                new VellumMousePosition(mouseX, mouseY),
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
                new VellumMousePosition(mouseX, mouseY),
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
                new VellumMousePosition(mouseX, mouseY),
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
                renderContext,
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
    public boolean mouseDragged(
            MouseButtonEvent event,
            double deltaX,
            double deltaY
    ) {
        VellumMouseEvent mouseEvent =
                new VellumMouseEvent(
                        new VellumMousePosition(
                                event.x(),
                                event.y()
                        ),
                        event.button(),
                        deltaX,
                        deltaY,
                        false
                );

        if (!editor.draggingListItem()) {
            return super.mouseDragged(
                    event,
                    mouseEvent.deltaX(),
                    mouseEvent.deltaY()
            );
        }

        return true;
    }

    @Override
    public boolean mouseReleased(
            MouseButtonEvent event
    ) {
        VellumMouseEvent mouseEvent =
                new VellumMouseEvent(
                        new VellumMousePosition(
                                event.x(),
                                event.y()
                        ),
                        event.button(),
                        0,
                        0,
                        false
                );

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
                                    (mouseEvent.position().y()
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
            VellumMousePosition mouse
    ) {
        if (!isInsideSaveButton(mouse.x(), mouse.y())) {
            return false;
        }

        saveConfiguration();
        return true;
    }

    private boolean handleMoreClick(
            VellumMousePosition mouse,
            ConfigOptionHandle<?> option,
            int rowTop
    ) {
        if (!isInsideMoreButton(
                mouse.x(),
                mouse.y(),
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
            VellumMousePosition mouse,
            ConfigOptionHandle<?> option,
            int rowTop
    ) {
        if (!isNumericOption(option)) {
            return false;
        }

        NumericOption numeric =
                new NumericOption(option);

        if (isInsideNumericMinus(
                mouse.x(),
                mouse.y(),
                rowTop
        )) {
            numeric.decrement();
            editor.markDirty();
            return true;
        }

        if (isInsideNumericPlus(
                mouse.x(),
                mouse.y(),
                rowTop
        )) {
            numeric.increment();
            editor.markDirty();
            return true;
        }

        return false;
    }

    private boolean handleListClick(
            VellumMousePosition mouse,
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
                        mouse,
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
                mouse.x(),
                rowTop
        )) {
            addListValue(option);
            return true;
        }

        if (isInsideRemoveButton(
                mouse.x(),
                rowTop
        )) {
            removeListValue(option);
            return true;
        }

        return false;
    }

    private boolean handleStringClick(
            VellumMousePosition mouse,
            ConfigOptionHandle<?> option,
            int rowTop
    ) {
        if (!isStringOption(option)) {
            return false;
        }

        if (!isInsideStringControl(
                mouse.x(),
                mouse.y(),
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
    public boolean keyReleased(
            KeyEvent event
    ) {
        VellumKeyEvent keyEvent =
                new VellumKeyEvent(
                        event.key(),
                        event.scancode(),
                        event.modifiers(),
                        false
                );

        return super.keyReleased(event);
    }

    private boolean forwardCharacterInput(
            VellumCharacterEvent event,
            CharacterEvent minecraftEvent
    ) {
        if (event.codePoint() <= 0) {
            return false;
        }

        return super.charTyped(
                minecraftEvent
        );
    }

    @Override
    public boolean charTyped(
            CharacterEvent event
    ) {
        VellumCharacterEvent characterEvent =
                new VellumCharacterEvent(
                        event.codepoint()
                );

        return forwardCharacterInput(
                characterEvent,
                event
        );
    }

    @Override
    protected void afterScreenClose() {
        editor.close();
    }
}
