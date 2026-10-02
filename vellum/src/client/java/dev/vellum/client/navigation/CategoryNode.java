package dev.vellum.client.navigation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class CategoryNode {
    private final String id;
    private final String title;
    private final CategoryNode parent;
    private final List<CategoryNode> children = new ArrayList<>();

    private boolean expanded;

    public CategoryNode(
            String id,
            String title,
            CategoryNode parent
    ) {
        this.id = Objects.requireNonNull(id, "id");
        this.title = Objects.requireNonNull(title, "title");
        this.parent = parent;
    }

    public String id() {
        return id;
    }

    public String title() {
        return title;
    }

    public CategoryNode parent() {
        return parent;
    }

    public List<CategoryNode> children() {
        return Collections.unmodifiableList(children);
    }

    public boolean expanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    public CategoryNode child(String id, String title) {
        for (CategoryNode child : children) {
            if (child.id().equals(id)) {
                return child;
            }
        }

        CategoryNode child = new CategoryNode(id, title, this);
        children.add(child);
        return child;
    }

    public boolean hasChildren() {
        return !children.isEmpty();
    }

    public int depth() {
        int depth = 0;
        CategoryNode current = parent;

        while (current != null && current.parent() != null) {
            depth++;
            current = current.parent();
        }

        return depth;
    }
}
