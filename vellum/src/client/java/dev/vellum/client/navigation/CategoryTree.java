package dev.vellum.client.navigation;

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
