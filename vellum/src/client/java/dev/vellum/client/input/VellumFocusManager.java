package dev.vellum.client.input;

import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;

public final class VellumFocusManager {
    private EditBox focusedInput;

    public void focus(
            Screen screen,
            EditBox input
    ) {
        if (focusedInput != null) {
            focusedInput.setFocused(false);
        }

        focusedInput = input;
        focusedInput.setFocused(true);
        screen.setFocused(input);
    }

    public void clear(
            Screen screen
    ) {
        if (focusedInput != null) {
            focusedInput.setFocused(false);
        }

        focusedInput = null;
        screen.setFocused(null);
    }

    public EditBox focusedInput() {
        return focusedInput;
    }
}
