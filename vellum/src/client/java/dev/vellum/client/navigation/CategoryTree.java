package dev.vellum.client.navigation;

import dev.vellum.config.ConfigManager;
import dev.vellum.config.ConfigOptionHandle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class CategoryTree {
    private final CategoryNode root;

    public CategoryTree() {
        this.root = new CategoryNode("root", "Root", null);
        this.root.setExpanded(true);
    }

    public static CategoryTree fromConfigManager(
            ConfigManager<?> manager
    ) {
        Objects.requireNonNull(manager, "manager");

        CategoryTree tree = new CategoryTree();

        for (var entry : manager.optionsByCategory().entrySet()) {
            String categoryPath = entry.getKey();
            String categoryTitle = titleFromPath(categoryPath);

            CategoryNode category = tree.addPath(
                    categoryPath,
                    categoryTitle
            );

            for (var option : entry.getValue()) {
                category.addOption(option);
            }
        }

        return tree;
    }

    private static String titleFromPath(String path) {
        int separator = path.lastIndexOf('.');

        String title = separator >= 0
                ? path.substring(separator + 1)
                : path;

        if (title.isEmpty()) {
            return path;
        }

        return Character.toUpperCase(title.charAt(0))
                + title.substring(1);
    }

    public CategoryNode root() {
        return root;
    }

    public CategoryNode addPath(String path, String title) {
        Objects.requireNonNull(path, "path");
        Objects.requireNonNull(title, "title");

        String[] segments = path.split("\\.");
        CategoryNode current = root;

        for (int index = 0; index < segments.length; index++) {
            String segment = segments[index];

            if (segment.isBlank()) {
                throw new IllegalArgumentException(
                        "Category path contains an empty segment: " + path
                );
            }

            String segmentTitle = index == segments.length - 1
                    ? title
                    : segment;

            current = current.child(segment, segmentTitle);
        }

        return current;
    }

    public CategoryNode addOption(
            String categoryPath,
            String categoryTitle,
            ConfigOptionHandle<?> option
    ) {
        CategoryNode category = addPath(
                categoryPath,
                categoryTitle
        );

        category.addOption(option);
        return category;
    }

    public List<CategoryNode> visibleNodes() {
        List<CategoryNode> visible = new ArrayList<>();
        collectVisible(root, visible);
        return Collections.unmodifiableList(visible);
    }

    private void collectVisible(
            CategoryNode node,
            List<CategoryNode> output
    ) {
        if (node != root) {
            output.add(node);
        }

        if (!node.expanded()) {
            return;
        }

        for (CategoryNode child : node.children()) {
            collectVisible(child, output);
        }
    }
}
