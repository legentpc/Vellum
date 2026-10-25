package dev.vellum.client.screen;

import dev.vellum.client.editor.VellumConfigEditor;
import dev.vellum.client.input.VellumMousePosition;
import dev.vellum.client.layout.VellumEditorLayout;
import dev.vellum.client.focus.VellumFocusManager;
import dev.vellum.client.render.MinecraftRenderContext;
import dev.vellum.client.render.VellumEditorRenderContext;
import dev.vellum.client.render.VellumRenderContext;
import dev.vellum.client.widget.NumericOption;
import dev.vellum.client.widget.OptionMenu;
import dev.vellum.client.widget.StringListOption;
import dev.vellum.client.widget.StringOption;
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

import dev.vellum.platform.VellumPlatform;
import dev.vellum.platform.api.VellumLogger;

public final class ConfigScreen
        extends VellumScreenComponent {
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
    private final VellumFocusManager focusManager;

    private static final VellumLogger LOGGER =
            VellumPlatform.logger(
                    "vellumconfig-client"
            );

    private final VellumEditorLayout layout;

    private EditBox listInputBox;
    private OptionMenu optionMenu;
    private ConfigScreenRenderer renderer;
    private VellumRenderContext renderContext;

    public static ConfigScreen create(
            VellumConfigEditor editor,
            Screen previousScreen
    ) {
        return new ConfigScreen(
                editor,
                previousScreen
        );
    }

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

        this.focusManager =
                new VellumFocusManager();

        this.layout =
                new VellumEditorLayout();
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
                layout.contentLeft() + 24,
                layout.contentTop() + 80,
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
                layout.navigationLeft(),
                layout.navigationTop(),
                layout.navigationRight(),
                layout.navigationBottom()
        );

        drawContentPanel();
    }

    private void updateLayout() {
        layout.update(
                width,
                height
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

        int left = layout.contentRight() - 150;
        int top = layout.contentTop() + 72;

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

    private void drawContentPanel() {
        VellumEditorRenderContext editorContext =
                new VellumEditorRenderContext(
                        renderContext,
                        editor,
                        layout.contentLeft(),
                        layout.contentTop(),
                        layout.contentRight(),
                        layout.contentBottom()
                );

        renderer.drawContentPanelHeader(
                editorContext
        );

        if (editor.selectedCategory() == null) {
            renderer.drawSaveButton(
                    editorContext
            );
            return;
        }

        int optionTop = layout.optionTop();
        focusManager.setOptionCount(
                editor.selectedCategory().options().size()
        );

        int optionIndex = 0;
        for (var option : editor.selectedCategory().options()) {
            drawOptionRow(
                    option,
                    optionTop,
                    optionIndex == focusManager.focusedIndex()
            );

            optionIndex++;
            optionTop += OPTION_ROW_HEIGHT;
        }

        renderer.drawSaveButton(
                editorContext
        );

        renderer.drawOptionMenu(
                renderContext,
                optionMenu,
                layout.contentRight(),
                layout.contentTop()
        );
        renderer.drawCloseWarning(
                renderContext,
                editor,
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
            ConfigOptionHandle<?> option,
            int rowTop,
            boolean focused
    ) {
        if (focused) {
            renderer.drawFocusBorder(
                    renderContext,
                    layout.contentLeft(),
                    rowTop,
                    layout.contentRight(),
                    rowTop + OPTION_ROW_HEIGHT
            );
        }

        renderer.drawOptionRowHeader(
                renderContext,
                option,
                layout.contentLeft(),
                layout.contentRight(),
                rowTop,
                OPTION_ROW_HEIGHT
        );

        if (isBooleanOption(option)) {
            renderer.drawBooleanControl(
                    renderContext,
                    readBoolean(option),
                    layout.contentRight(),
                    rowTop,
                    OPTION_CONTROL_WIDTH,
                    OPTION_CONTROL_HEIGHT
            );
        } else if (isStringListOption(option)) {
            drawListControls(
                    option,
                    rowTop
            );
        } else if (isNumericOption(option)) {
            NumericOption numeric =
                    new NumericOption(option);

            renderer.drawNumericControl(
                    renderContext,
                    numeric.value(),
                    layout.contentRight(),
                    rowTop,
                    OPTION_CONTROL_HEIGHT
            );
        } else if (isStringOption(option)) {
            StringOption string =
                    new StringOption(option);

            renderer.drawStringControl(
                    renderContext,
                    string.value(),
                    layout.contentRight(),
                    rowTop,
                    OPTION_CONTROL_HEIGHT
            );
        }

        renderer.drawMoreButton(
                renderContext,
                layout.contentRight(),
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
                layout.navigationLeft(),
                layout.navigationTop(),
                layout.navigationRight(),
                layout.navigationBottom(),
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

    private boolean isInsideContent(
            double mouseX,
            double mouseY
    ) {
        return inputHandler.isInsideContent(
                new VellumMousePosition(mouseX, mouseY),
                layout.contentLeft(),
                layout.contentTop(),
                layout.contentRight(),
                layout.contentBottom()
        );
    }

    private boolean handleContentClick(
            VellumMousePosition mouse,
            MouseButtonEvent click,
            boolean doubled
    ) {
        if (handleCloseWarningClick(mouse.x(), mouse.y())) {
            return true;
        }

        if (handleSaveClick(mouse)) {
            return true;
        }

        if (handleOptionMenuClick(mouse.x(), mouse.y())) {
            return true;
        }

        if (editor.selectedCategory() == null) {
            return super.mouseClicked(click, doubled);
        }

        int rowTop = layout.optionTop();
        int optionIndex = 0;

        for (var option : editor.selectedCategory().options()) {
            int rowBottom = rowTop + OPTION_ROW_HEIGHT;

            if (mouse.y() >= rowTop && mouse.y() < rowBottom) {
                focusManager.focusIndex(optionIndex);

                if (isBooleanOption(option)
                        && isInsideBooleanControl(mouse.x(), rowTop)) {
                    toggleBoolean(option);
                    return true;
                }

                if (handleNumericClick(
                        mouse,
                        option,
                        rowTop
                )) {
                    return true;
                }

                if (handleStringClick(
                        mouse,
                        option,
                        rowTop
                )) {
                    return true;
                }

                if (handleListClick(
                        mouse,
                        option,
                        rowTop
                )) {
                    return true;
                }

                if (handleMoreClick(
                        mouse,
                        option,
                        rowTop
                )) {
                    return true;
                }

                return super.mouseClicked(
                        click,
                        doubled
                );

            }

            rowTop += OPTION_ROW_HEIGHT;
            optionIndex++;
        }

        return super.mouseClicked(click, doubled);
    }

    private boolean isInsideSaveButton(
            double mouseX,
            double mouseY
    ) {
        return inputHandler.isInsideSaveButton(
                new VellumMousePosition(mouseX, mouseY),
                layout.contentRight(),
                layout.contentBottom(),
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
                layout.contentRight(),
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
                layout.contentRight(),
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
                layout.contentRight(),
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

    private boolean activateFocusedOption() {
        if (!focusManager.hasFocus()
                || editor.selectedCategory() == null) {
            return false;
        }

        var options = editor.selectedCategory().options();
        int index = focusManager.focusedIndex();
        if (index < 0 || index >= options.size()) {
            return false;
        }

        ConfigOptionHandle<?> option = options.get(index);
        if (isBooleanOption(option)) {
            toggleBoolean(option);
            return true;
        }

        return false;
    }

    @Override
    public boolean keyPressed(
            KeyEvent event
    ) {
        int keyCode = event.key();

        if (keyCode == 264) {
            focusManager.focusNext();
            return true;
        }

        if (keyCode == 265) {
            focusManager.focusPrevious();
            return true;
        }

        if ((keyCode == 257 || keyCode == 335 || keyCode == 32)
                && activateFocusedOption()) {
            return true;
        }

        if (keyCode == 256 && editor.editingStringOption() == null
                && !editor.listInputOpen()) {
            onClose();
            return true;
        }

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
        int left = layout.contentRight()
                - OPTION_CONTROL_WIDTH
                - 18;

        int top = rowTop + 3;

        return mouseX >= left
                && mouseX < left + OPTION_CONTROL_WIDTH
                && top >= layout.contentTop()
                && top + OPTION_CONTROL_HEIGHT <= layout.contentBottom();
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
                layout.contentRight(),
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
                layout.contentRight(),
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
                layout.contentRight(),
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
                layout.contentLeft(),
                layout.contentRight(),
                rowTop,
                LIST_ROW_TOP_OFFSET,
                LIST_ROW_HEIGHT,
                OPTION_CONTROL_HEIGHT
        );
    }

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
            int rowTop = layout.optionTop();

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
                        layout.contentLeft(),
                        layout.contentRight(),
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
    protected void afterScreenClose() {
        editor.close();
    }
}
