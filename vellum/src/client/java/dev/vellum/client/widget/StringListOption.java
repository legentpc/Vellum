package dev.vellum.client.widget;

import dev.vellum.config.ConfigOptionHandle;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class StringListOption {
    private final ConfigOptionHandle<?> option;
    private int selectedIndex = -1;

    public StringListOption(ConfigOptionHandle<?> option) {
        this.option = Objects.requireNonNull(option, "option");

        if (!isStringList(option.genericType())) {
            throw new IllegalArgumentException(
                    "Option must be declared as List<String>: " + option.name()
            );
        }
    }

    private static boolean isStringList(Type type) {
        if (!(type instanceof ParameterizedType parameterizedType)) {
            return false;
        }

        if (!(parameterizedType.getRawType() instanceof Class<?> rawType)
                || !List.class.isAssignableFrom(rawType)) {
            return false;
        }

        Type[] arguments = parameterizedType.getActualTypeArguments();
        return arguments.length == 1 && arguments[0] == String.class;
    }

    public String name() {
        return option.name();
    }

    public String description() {
        return option.description();
    }

    public List<String> values() {
        List<?> source = rawValues();
        List<String> values = new ArrayList<>(source.size());

        for (Object value : source) {
            if (!(value instanceof String string)) {
                throw new IllegalStateException(
                        "List option contains a non-string value: "
                                + option.name()
                );
            }

            values.add(string);
        }

        return Collections.unmodifiableList(values);
    }

    public int selectedIndex() {
        return selectedIndex;
    }

    public void select(int index) {
        if (index < 0 || index >= rawValues().size()) {
            selectedIndex = -1;
            return;
        }

        selectedIndex = index;
    }

    public void add(String value) {
        Objects.requireNonNull(value, "value");

        List<String> updated = new ArrayList<>(values());
        updated.add(value);
        write(updated);
        selectedIndex = updated.size() - 1;
    }

    public void removeSelected() {
        if (selectedIndex < 0) {
            return;
        }

        List<String> updated = new ArrayList<>(values());

        if (selectedIndex >= updated.size()) {
            selectedIndex = -1;
            return;
        }

        updated.remove(selectedIndex);
        write(updated);

        if (updated.isEmpty()) {
            selectedIndex = -1;
        } else {
            selectedIndex = Math.min(
                    selectedIndex,
                    updated.size() - 1
            );
        }
    }

    public void move(
            int fromIndex,
            int toIndex
    ) {
        List<String> updated =
                new ArrayList<>(values());

        if (fromIndex < 0
                || fromIndex >= updated.size()
                || toIndex < 0
                || toIndex >= updated.size()
                || fromIndex == toIndex) {
            return;
        }

        String moved =
                updated.remove(fromIndex);

        updated.add(toIndex, moved);
        write(updated);
        selectedIndex = toIndex;
    }

    private List<?> rawValues() {
        Object value = option.get();

        if (value == null) {
            return List.of();
        }

        if (!(value instanceof List<?> list)) {
            throw new IllegalStateException(
                    "Option value is not a list: " + option.name()
            );
        }

        return list;
    }

    @SuppressWarnings("unchecked")
    private void write(List<String> values) {
        ConfigOptionHandle<List<String>> listOption =
                (ConfigOptionHandle<List<String>>) option;

        listOption.set(values);
    }
}
