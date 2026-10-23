package dev.vellum.client.editor;

import com.google.gson.Gson;

import dev.vellum.client.navigation.CategoryNode;
import dev.vellum.client.navigation.CategoryTree;
import dev.vellum.client.screen.ConfigScreenState;
import dev.vellum.client.test.VellumFakeConfig;
import dev.vellum.config.ConfigManager;
import dev.vellum.config.ConfigOptionHandle;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;

public final class VellumConfigEditor {
    private final ConfigScreenState state;
    private final ConfigManager<?> configManager;
    private final Gson gson;

    public VellumConfigEditor(
            ConfigScreenState state,
            ConfigManager<?> configManager
    ) {
        this.state = Objects.requireNonNull(
                state,
                "state"
        );

        this.configManager = Objects.requireNonNull(
                configManager,
                "configManager"
        );

        this.gson = new Gson();
    }

    public void close() {
        state.endListDrag();
        state.clearListSelection();
        state.endStringEdit();
        state.closeListInput();
        state.closeCloseWarning();
    }

    public static VellumConfigEditor create(
            ConfigManager<?> configManager
    ) {
        CategoryTree tree =
                CategoryTree.fromConfigManager(
                        configManager
                );

        return new VellumConfigEditor(
                new ConfigScreenState(
                        tree
                ),
                configManager
        );
    }

    public static VellumConfigEditor createForTesting() {
        VellumFakeConfig config =
                new VellumFakeConfig();

        ConfigManager<VellumFakeConfig> configManager =
                new ConfigManager<>(
                        config,
                        Path.of(
                                "build",
                                "vellum-fake-config.json"
                        )
                );

        return create(
                configManager
        );
    }

    public ConfigManager<?> configManager() {
        return configManager;
    }

    public boolean dirty() {
        return state.dirty();
    }

    public void markDirty() {
        state.markDirty();
    }

    public void markClean() {
        state.markClean();
    }

    public void save() throws IOException {
        if (!state.dirty()) {
            return;
        }

        configManager.save();
        state.markClean();
    }

    public void reset(
            ConfigOptionHandle<?> option
    ) {
        Objects.requireNonNull(
                option,
                "option"
        );

        option.resetToDefault(gson);
        state.markDirty();
    }

    public CategoryNode selectedCategory() {
        return state.selectedCategory();
    }

    public CategoryTree categoryTree() {
        return state.categoryTree();
    }

    public void select(
            CategoryNode category
    ) {
        state.select(category);
    }

    public void toggle(
            CategoryNode category
    ) {
        state.toggle(category);
    }

    public void selectListItem(
            ConfigOptionHandle<?> option,
            int index
    ) {
        state.selectListItem(
                option,
                index
        );
    }

    public void beginListDrag(
            ConfigOptionHandle<?> option,
            int index
    ) {
        state.beginListDrag(
                option,
                index
        );
    }

    public void beginStringEdit(
            ConfigOptionHandle<?> option
    ) {
        state.beginStringEdit(option);
    }

    public void endStringEdit() {
        state.endStringEdit();
    }

    public void openListInput(
            ConfigOptionHandle<?> option
    ) {
        state.openListInput(option);
    }

    public void closeListInput() {
        state.closeListInput();
    }

    public void endListDrag() {
        state.endListDrag();
    }

    public void openCloseWarning() {
        state.openCloseWarning();
    }

    public void closeCloseWarning() {
        state.closeCloseWarning();
    }

    public boolean draggingListItem() {
        return state.draggingListItem();
    }

    public ConfigOptionHandle<?> draggingListOption() {
        return state.draggingListOption();
    }

    public int draggingListIndex() {
        return state.draggingListIndex();
    }

    public ConfigOptionHandle<?> selectedListOption() {
        return state.selectedListOption();
    }

    public int selectedListIndex() {
        return state.selectedListIndex();
    }

    public ConfigOptionHandle<?> editingStringOption() {
        return state.editingStringOption();
    }

    public boolean listInputOpen() {
        return state.listInputOpen();
    }

    public ConfigOptionHandle<?> listInputOption() {
        return state.listInputOption();
    }

    public boolean closeWarningOpen() {
        return state.closeWarningOpen();
    }

    public void clearListSelection() {
        state.clearListSelection();
    }
}
