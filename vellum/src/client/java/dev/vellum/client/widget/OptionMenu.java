package dev.vellum.client.widget;

import dev.vellum.config.ConfigOptionHandle;

import java.util.Objects;

public final class OptionMenu {
    private final ConfigOptionHandle<?> option;
    private boolean open;

    public OptionMenu(
            ConfigOptionHandle<?> option
    ) {
        this.option = Objects.requireNonNull(
                option,
                "option"
        );
    }

    public ConfigOptionHandle<?> option() {
        return option;
    }

    public boolean open() {
        return open;
    }

    public void show() {
        open = true;
    }

    public void hide() {
        open = false;
    }

    public void toggle() {
        open = !open;
    }

    public enum Action {
        CLOSE
    }

    public void perform(Action action) {
        Objects.requireNonNull(
                action,
                "action"
        );

        if (action == Action.CLOSE) {
            hide();
        }
    }
}
