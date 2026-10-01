package dev.vellum.config;

import dev.vellum.config.annotation.ConfigOption;

import java.lang.reflect.Field;
import java.util.Objects;

public final class ConfigOptionHandle<T> {
    private final Config config;
    private final Field field;
    private final ConfigOption metadata;

    ConfigOptionHandle(
            Config config,
            Field field,
            ConfigOption metadata
    ) {
        this.config = Objects.requireNonNull(config);
        this.field = Objects.requireNonNull(field);
        this.metadata = Objects.requireNonNull(metadata);

        field.setAccessible(true);
    }

    public String name() {
        return metadata.name();
    }

    public String description() {
        return metadata.description();
    }

    public String category() {
        return metadata.category();
    }

    @SuppressWarnings("unchecked")
    public Class<T> valueType() {
        return (Class<T>) field.getType();
    }

    @SuppressWarnings("unchecked")
    public T get() {
        try {
            return (T) field.get(config);
        } catch (IllegalAccessException exception) {
            throw new IllegalStateException(
                    "Unable to read config option: " + field.getName(),
                    exception
            );
        }
    }

    public void set(T value) {
        try {
            field.set(config, value);
        } catch (IllegalAccessException exception) {
            throw new IllegalStateException(
                    "Unable to write config option: " + field.getName(),
                    exception
            );
        }
    }
}
