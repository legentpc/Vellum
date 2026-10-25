package dev.vellum.client.layout;

public final class VellumEditorLayout {
    private static final int OUTER_MARGIN = 18;
    private static final int HEADER_HEIGHT = 42;
    private static final int NAVIGATION_WIDTH = 196;
    private static final int CONTENT_GAP = 14;
    private static final int PANEL_PADDING = 16;

    private int width;
    private int height;

    private int navigationLeft;
    private int navigationTop;
    private int navigationRight;
    private int navigationBottom;

    private int contentLeft;
    private int contentTop;
    private int contentRight;
    private int contentBottom;

    public void update(
            int width,
            int height
    ) {
        this.width = width;
        this.height = height;

        int left = OUTER_MARGIN;
        int top =
                OUTER_MARGIN
                        + HEADER_HEIGHT;
        int right =
                width - OUTER_MARGIN;
        int bottom =
                height - OUTER_MARGIN;

        navigationLeft = left;
        navigationTop = top;
        navigationRight =
                left + NAVIGATION_WIDTH;
        navigationBottom = bottom;

        contentLeft =
                navigationRight + CONTENT_GAP;
        contentTop = top + PANEL_PADDING;
        contentRight = right - PANEL_PADDING;
        contentBottom = bottom - PANEL_PADDING;
    }

    public int width() {
        return width;
    }

    public int height() {
        return height;
    }

    public int navigationLeft() {
        return navigationLeft;
    }

    public int navigationTop() {
        return navigationTop;
    }

    public int navigationRight() {
        return navigationRight;
    }

    public int navigationBottom() {
        return navigationBottom;
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

    public int optionTop() {
        return contentTop + 42;
    }
}
