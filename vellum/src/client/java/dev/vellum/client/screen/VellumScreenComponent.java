package dev.vellum.client.screen;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public abstract class VellumScreenComponent
        extends Screen {
    private final Screen previousScreen;

    protected VellumScreenComponent(
            Component title,
            Screen previousScreen
    ) {
        super(title);

        this.previousScreen =
                previousScreen;
    }

    protected final Screen previousScreen() {
        return previousScreen;
    }

    protected void beforeScreenClose() {
    }

    protected void afterScreenClose() {
    }

    @Override
    public void onClose() {
        beforeScreenClose();

        if (minecraft != null) {
            super.onClose();
        }
    }

    @Override
    public void removed() {
        super.removed();
        afterScreenClose();
    }
}
