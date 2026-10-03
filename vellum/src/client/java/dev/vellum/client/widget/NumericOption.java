package dev.vellum.client.widget;

import dev.vellum.config.ConfigOptionHandle;

import java.util.Objects;

public final class NumericOption {
    private final ConfigOptionHandle<?> option;

    public NumericOption(ConfigOptionHandle<?> option) {
        this.option = Objects.requireNonNull(
                option,
                "option"
        );

        if (!isNumericType(option.valueType())) {
            throw new IllegalArgumentException(
                    "Option must be numeric: "
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

    public Number value() {
        Object value = option.get();

        if (!(value instanceof Number number)) {
            throw new IllegalStateException(
                    "Numeric option contains a non-number value: "
                            + option.name()
            );
        }

        return number;
    }

    public void increment() {
        write(changeBy(step()));
    }

    public void decrement() {
        write(changeBy(-step()));
    }

    private Number changeBy(double amount) {
        Number current = value();
        Class<?> type = option.valueType();
        double changed = current.doubleValue() + amount;

        if (type == byte.class || type == Byte.class) {
            return (byte) changed;
        }

        if (type == short.class || type == Short.class) {
            return (short) changed;
        }

        if (type == int.class || type == Integer.class) {
            return (int) changed;
        }

        if (type == long.class || type == Long.class) {
            return (long) changed;
        }

        if (type == float.class || type == Float.class) {
            return (float) changed;
        }

        if (type == double.class || type == Double.class) {
            return changed;
        }

        throw new IllegalStateException(
                "Unsupported numeric option type: "
                        + type.getName()
        );
    }

    private double step() {
        Class<?> type = option.valueType();

        if (type == float.class
                || type == Float.class
                || type == double.class
                || type == Double.class) {
            return 0.1D;
        }

        return 1D;
    }

    @SuppressWarnings("unchecked")
    private void write(Number value) {
        ConfigOptionHandle<Object> numericOption =
                (ConfigOptionHandle<Object>) option;

        numericOption.set(value);
    }

    private static boolean isNumericType(Class<?> type) {
        return type == byte.class
                || type == Byte.class
                || type == short.class
                || type == Short.class
                || type == int.class
                || type == Integer.class
                || type == long.class
                || type == Long.class
                || type == float.class
                || type == Float.class
                || type == double.class
                || type == Double.class;
    }
}
