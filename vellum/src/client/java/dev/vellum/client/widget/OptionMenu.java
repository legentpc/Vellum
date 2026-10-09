package dev.vellum.client.widget;

import com.google.gson.Gson;

import dev.vellum.config.ConfigOptionHandle;

import java.util.Objects;

public final class OptionMenu {
    private final ConfigOptionHandle<?> option;
    private final Gson gson;
    private boolean open;

    public OptionMenu(
            ConfigOptionHandle<?> option
    ) {
        this.option = Objects.requireNonNull(
                option,
                "option"
        );

        this.gson = new Gson();
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

    public void reset() {
        option.resetToDefault(gson);
        hide();
    }

    public enum Action {
        RESET,
        CLOSE
    }

    public void perform(Action action) {
        Objects.requireNonNull(
                action,
                "action"
        );

        switch (action) {
            case RESET -> reset();
            case CLOSE -> hide();
        }
    }
}
