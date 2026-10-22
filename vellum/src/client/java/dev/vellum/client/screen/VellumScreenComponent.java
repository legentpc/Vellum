package dev.vellum.client.screen;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public abstract class VellumScreenComponent
        extends Screen {

    protected VellumScreenComponent(
            Component title
    ) {
        super(title);
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
