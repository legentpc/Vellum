package dev.vellum.client.widget;

import dev.vellum.config.ConfigOptionHandle;

import java.util.Objects;

public final class StringOption {
    private final ConfigOptionHandle<?> option;

    public StringOption(ConfigOptionHandle<?> option) {
        this.option = Objects.requireNonNull(
                option,
                "option"
        );

        if (option.valueType() != String.class) {
            throw new IllegalArgumentException(
                    "Option must contain a String value: "
                            + option.name()
            );
        }
    }

    public String name() {
        return option.name();
    }

    public String description() {
        return option.description();
    }

    public String value() {
        Object value = option.get();

        if (value == null) {
            return "";
        }

        if (!(value instanceof String string)) {
            throw new IllegalStateException(
                    "String option contains a non-string value: "
                            + option.name()
            );
        }

        return string;
    }

    public void setValue(String value) {
        Objects.requireNonNull(
                value,
                "value"
        );

        @SuppressWarnings("unchecked")
        ConfigOptionHandle<String> stringOption =
                (ConfigOptionHandle<String>) option;

        stringOption.set(value);
    }
}
