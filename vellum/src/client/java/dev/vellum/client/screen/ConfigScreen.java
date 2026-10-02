package dev.vellum.client.screen;

import dev.vellum.client.navigation.CategoryTree;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public final class ConfigScreen extends Screen {
    private static final int PANEL_MARGIN = 24;
    private static final int HEADER_HEIGHT = 32;
    private static final int NAVIGATION_WIDTH = 180;

    private final ConfigScreenState state;

    private int navigationLeft;
    private int navigationTop;
    private int navigationRight;
    private int navigationBottom;

    private int contentLeft;
    private int contentTop;
    private int contentRight;
    private int contentBottom;

    public ConfigScreen(ConfigScreenState state) {
        super(Component.literal("Vellum Configuration"));
        this.state = state;
    }

    public ConfigScreenState state() {
        return state;
    }

    @Override
    protected void init() {
        updateLayout();
    }

    @Override
    public void extractRenderState(
            GuiGraphicsExtractor graphics,
            int mouseX,
            int mouseY,
            float partialTick
    ) {
        updateLayout();

        drawBackground(graphics);
        drawHeader(graphics);
        drawNavigationPanel(graphics);
        drawContentPanel(graphics);
    }

    private void updateLayout() {
        int left = PANEL_MARGIN;
        int top = PANEL_MARGIN + HEADER_HEIGHT;
        int right = width - PANEL_MARGIN;
        int bottom = height - PANEL_MARGIN;

        navigationLeft = left;
        navigationTop = top;
        navigationRight = left + NAVIGATION_WIDTH;
        navigationBottom = bottom;

        contentLeft = navigationRight + 12;
        contentTop = top;
        contentRight = right;
        contentBottom = bottom;
    }

    private void drawBackground(GuiGraphicsExtractor graphics) {
        graphics.fill(
                0,
                0,
                width,
                height,
                0xFF101216
        );
    }

    private void drawHeader(GuiGraphicsExtractor graphics) {
        graphics.fill(
                PANEL_MARGIN,
                PANEL_MARGIN,
                width - PANEL_MARGIN,
                PANEL_MARGIN + HEADER_HEIGHT,
                0xFF1B1E24
        );

        graphics.text(
                font,
                title,
                PANEL_MARGIN + 10,
                PANEL_MARGIN + 10,
                0xFFFFFFFF,
                false
        );
    }

    private void drawNavigationPanel(
            GuiGraphicsExtractor graphics
    ) {
        graphics.fill(
                navigationLeft,
                navigationTop,
                navigationRight,
                navigationBottom,
                0xFF191C21
        );

        graphics.text(
                font,
                Component.literal("Categories"),
                navigationLeft + 10,
                navigationTop + 10,
                0xFFE6E8EB,
                false
        );
    }

    private void drawContentPanel(
            GuiGraphicsExtractor graphics
    ) {
        graphics.fill(
                contentLeft,
                contentTop,
                contentRight,
                contentBottom,
                0xFF191C21
        );

        Component selectedTitle = state.selectedCategory() == null
                ? Component.literal("Select a category")
                : Component.literal(
                state.selectedCategory().title()
        );

        graphics.text(
                font,
                selectedTitle,
                contentLeft + 12,
                contentTop + 12,
                0xFFFFFFFF,
                false
        );
    }
}
