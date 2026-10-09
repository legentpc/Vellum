package dev.vellum.config;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import dev.vellum.config.annotation.ConfigOption;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
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

    public Type genericType() {
        return field.getGenericType();
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

    public String defaultValue() {
        return metadata.defaultValue();
    }

    public void resetToDefault(
            Gson gson
    ) {
        String rawDefault =
                metadata.defaultValue();

        if (rawDefault.isBlank()) {
            throw new IllegalStateException(
                    "No default value configured for option: "
                            + name()
            );
        }

        JsonElement value =
                JsonParser.parseString(rawDefault);

        Object parsed =
                gson.fromJson(
                        value,
                        genericType()
                );

        setParsed(parsed);
    }

    @SuppressWarnings("unchecked")
    private void setParsed(Object value) {
        ((ConfigOptionHandle<Object>) this).set(value);
    }
}
