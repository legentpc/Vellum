package dev.vellum.client.render;

import dev.vellum.client.editor.VellumConfigEditor;

import java.util.Objects;

public final class VellumEditorRenderContext {
    private final VellumRenderContext render;
    private final VellumConfigEditor editor;

    private final int contentLeft;
    private final int contentTop;
    private final int contentRight;
    private final int contentBottom;

    public VellumEditorRenderContext(
            VellumRenderContext render,
            VellumConfigEditor editor,
            int contentLeft,
            int contentTop,
            int contentRight,
            int contentBottom
    ) {
        this.render =
                Objects.requireNonNull(
                        render,
                        "render"
                );

        this.editor =
                Objects.requireNonNull(
                        editor,
                        "editor"
                );

        this.contentLeft = contentLeft;
        this.contentTop = contentTop;
        this.contentRight = contentRight;
        this.contentBottom = contentBottom;
    }

    public VellumRenderContext render() {
        return render;
    }

    public VellumConfigEditor editor() {
        return editor;
    }

    public int contentLeft() {
        return contentLeft;
    }

    public int contentTop() {
        return contentTop;
    }

    public int contentRight() {
        return contentRight;
    }

    public int contentBottom() {
        return contentBottom;
    }
}
