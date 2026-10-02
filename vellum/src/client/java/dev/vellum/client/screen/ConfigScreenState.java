package dev.vellum.client.screen;

import dev.vellum.client.navigation.CategoryNode;
import dev.vellum.client.navigation.CategoryTree;

import java.util.Objects;

public final class ConfigScreenState {
    private final CategoryTree categoryTree;
    private CategoryNode selectedCategory;
    private boolean dirty;

    public ConfigScreenState(CategoryTree categoryTree) {
        this.categoryTree = Objects.requireNonNull(
                categoryTree,
                "categoryTree"
        );
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

    public boolean dirty() {
        return dirty;
    }

    public void markDirty() {
        dirty = true;
    }

    public void markClean() {
        dirty = false;
    }
}
