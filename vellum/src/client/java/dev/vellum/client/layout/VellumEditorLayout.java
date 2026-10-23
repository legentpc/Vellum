package dev.vellum.client.layout;

public final class VellumEditorLayout {
    private static final int PANEL_MARGIN = 24;
    private static final int HEADER_HEIGHT = 32;
    private static final int NAVIGATION_WIDTH = 180;

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

        int left = PANEL_MARGIN;
        int top =
                PANEL_MARGIN
                        + HEADER_HEIGHT;
        int right =
                width - PANEL_MARGIN;
        int bottom =
                height - PANEL_MARGIN;

        navigationLeft = left;
        navigationTop = top;
        navigationRight =
                left + NAVIGATION_WIDTH;
        navigationBottom = bottom;

        contentLeft =
                navigationRight + 12;
        contentTop = top;
        contentRight = right;
        contentBottom = bottom;
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
