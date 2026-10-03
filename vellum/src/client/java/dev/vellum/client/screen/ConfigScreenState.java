package dev.vellum.client.screen;

import dev.vellum.client.navigation.CategoryNode;
import dev.vellum.client.navigation.CategoryTree;
import dev.vellum.config.ConfigOptionHandle;

import java.util.Objects;

public final class ConfigScreenState {
    private final CategoryTree categoryTree;
    private CategoryNode selectedCategory;
    private boolean dirty;
    private ConfigOptionHandle<?> selectedListOption;
    private int selectedListIndex = -1;
    private boolean listInputOpen;
    private ConfigOptionHandle<?> listInputOption;
    private String listInputText = "";
    private boolean draggingListItem;
    private ConfigOptionHandle<?> draggingListOption;
    private int draggingListIndex = -1;
    private ConfigOptionHandle<?> editingStringOption;

    public ConfigScreenState(CategoryTree categoryTree) {
        this.categoryTree = Objects.requireNonNull(
                categoryTree,
                "categoryTree"
        );

        if (!categoryTree.visibleNodes().isEmpty()) {
            selectedCategory = categoryTree.visibleNodes().get(0);
        }
    }

    public CategoryTree categoryTree() {
        return categoryTree;
    }

    public CategoryNode selectedCategory() {
        return selectedCategory;
    }

    public void select(CategoryNode category) {
        Objects.requireNonNull(category, "category");
        selectedCategory = category;
    }

    public void toggle(CategoryNode category) {
        Objects.requireNonNull(category, "category");
        category.setExpanded(!category.expanded());
    }

    public ConfigOptionHandle<?> selectedListOption() {
        return selectedListOption;
    }

    public int selectedListIndex() {
        return selectedListIndex;
    }

    public void selectListItem(ConfigOptionHandle<?> option, int index) {
        selectedListOption = Objects.requireNonNull(option, "option");
        selectedListIndex = index;
    }

    public void clearListSelection() {
        selectedListOption = null;
        selectedListIndex = -1;
    }

    public boolean draggingListItem() { return draggingListItem; }
    public ConfigOptionHandle<?> draggingListOption() { return draggingListOption; }
    public int draggingListIndex() { return draggingListIndex; }
    public void beginListDrag(ConfigOptionHandle<?> option, int index) { draggingListOption = Objects.requireNonNull(option, "option"); draggingListIndex = index; draggingListItem = true; }
    public void endListDrag() { draggingListOption = null; draggingListIndex = -1; draggingListItem = false; }

    public boolean dirty() {
        return dirty;
    }

    public void markDirty() {
        dirty = true;
    }

    public boolean listInputOpen() {
        return listInputOpen;
    }

    public ConfigOptionHandle<?> listInputOption() {
        return listInputOption;
    }

    public String listInputText() {
        return listInputText;
    }

    public ConfigOptionHandle<?> editingStringOption() {
        return editingStringOption;
    }

    public void beginStringEdit(
            ConfigOptionHandle<?> option
    ) {
        editingStringOption = Objects.requireNonNull(
                option,
                "option"
        );
    }

    public void endStringEdit() {
        editingStringOption = null;
    }

    public void openListInput(
            ConfigOptionHandle<?> option
    ) {
        listInputOption = Objects.requireNonNull(
                option,
                "option"
        );

        listInputText = "";
        listInputOpen = true;
    }

    public void updateListInput(String text) {
        listInputText = Objects.requireNonNull(
                text,
                "text"
        );
    }

    public void closeListInput() {
        listInputOption = null;
        listInputText = "";
        listInputOpen = false;
    }

    public void markClean() {
        dirty = false;
    }
}
