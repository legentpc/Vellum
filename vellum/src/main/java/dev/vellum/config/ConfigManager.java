package dev.vellum.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.vellum.config.annotation.ConfigOption;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class ConfigManager<T extends Config> {
    private final T config;
    private final Gson gson;
    private final Path file;
    private final Map<String, List<ConfigOptionHandle<?>>> optionsByCategory;

    public ConfigManager(T config, Path file) {
        this.config = Objects.requireNonNull(config, "config");
        this.file = Objects.requireNonNull(file, "file");
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
        this.optionsByCategory = scanOptions(config);
    }

    public T config() {
        return config;
    }

    public Path file() {
        return file;
    }

    public List<ConfigOptionHandle<?>> options(String category) {
        return optionsByCategory.getOrDefault(category, List.of());
    }

    public Map<String, List<ConfigOptionHandle<?>>> optionsByCategory() {
        return Collections.unmodifiableMap(optionsByCategory);
    }

    public void load() throws IOException {
        if (Files.notExists(file)) {
            return;
        }

        try (Reader reader = Files.newBufferedReader(file)) {
            JsonElement root = JsonParser.parseReader(reader);

            if (!root.isJsonObject()) {
                throw new IOException(
                        "Config file must contain a JSON object: " + file
                );
            }

            JsonObject savedValues = root.getAsJsonObject();

            for (ConfigOptionHandle<?> option : allOptions()) {
                JsonElement value = savedValues.get(option.name());

                if (value == null || value.isJsonNull()) {
                    continue;
                }

                setValue(option, value);
            }
        }
    }

    public void save() throws IOException {
        Path parent = file.getParent();

        if (parent != null) {
            Files.createDirectories(parent);
        }

        JsonObject values = new JsonObject();

        for (ConfigOptionHandle<?> option : allOptions()) {
            values.add(option.name(), gson.toJsonTree(option.get()));
        }

        try (Writer writer = Files.newBufferedWriter(file)) {
            gson.toJson(values, writer);
        }
    }

    private Map<String, List<ConfigOptionHandle<?>>> scanOptions(T config) {
        Map<String, List<ConfigOptionHandle<?>>> result =
                new LinkedHashMap<>();

        for (Class<?> type = config.getClass();
             type != null && type != Object.class;
             type = type.getSuperclass()) {

            for (Field field : type.getDeclaredFields()) {
                ConfigOption metadata =
                        field.getAnnotation(ConfigOption.class);

                if (metadata == null) {
                    continue;
                }

                ConfigOptionHandle<?> handle =
                        new ConfigOptionHandle<>(
                                config,
                                field,
                                metadata
                        );

                result.computeIfAbsent(
                        metadata.category(),
                        ignored -> new ArrayList<>()
                ).add(handle);
            }
        }

        result.replaceAll((category, options) ->
                Collections.unmodifiableList(options)
        );

        return result;
    }

    private List<ConfigOptionHandle<?>> allOptions() {
        List<ConfigOptionHandle<?>> options = new ArrayList<>();

        for (List<ConfigOptionHandle<?>> categoryOptions
                : optionsByCategory.values()) {
            options.addAll(categoryOptions);
        }

        return options;
    }

    private void setValue(
            ConfigOptionHandle<?> option,
            JsonElement value
    ) {
        Class<?> type = option.valueType();

        if (type == boolean.class || type == Boolean.class) {
            set(option, value.getAsBoolean());
        } else if (type == byte.class || type == Byte.class) {
            set(option, value.getAsByte());
        } else if (type == short.class || type == Short.class) {
            set(option, value.getAsShort());
        } else if (type == int.class || type == Integer.class) {
            set(option, value.getAsInt());
        } else if (type == long.class || type == Long.class) {
            set(option, value.getAsLong());
        } else if (type == float.class || type == Float.class) {
            set(option, value.getAsFloat());
        } else if (type == double.class || type == Double.class) {
            set(option, value.getAsDouble());
        } else if (type == String.class) {
            set(option, value.getAsString());
        } else {
            set(option, gson.fromJson(value, type));
        }
    }

    @SuppressWarnings("unchecked")
    private <V> void set(
            ConfigOptionHandle<?> option,
            V value
    ) {
        ((ConfigOptionHandle<V>) option).set(value);
    }
}
