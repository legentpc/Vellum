package dev.vellum.client.input;

import dev.vellum.client.focus.VellumFocusManager;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public final class VellumKeyboardNavigation {
    private final VellumFocusManager focusManager;

    public VellumKeyboardNavigation(
            VellumFocusManager focusManager
    ) {
        this.focusManager = focusManager;
    }

    public boolean handleKey(
            int keyCode,
            int scanCode,
            int modifiers
    ) {
        return switch (keyCode) {
            case 264, 265 -> {
                if (keyCode == 264) {
                    focusManager.focusNext();
                } else {
                    focusManager.focusPrevious();
                }
                yield true;
            }
            default -> false;
        };
    }

    public boolean isActivateKey(int keyCode) {
        return keyCode == 257
                || keyCode == 335
                || keyCode == 32;
    }

    public boolean isCancelKey(int keyCode) {
        return keyCode == 256;
    }
}
