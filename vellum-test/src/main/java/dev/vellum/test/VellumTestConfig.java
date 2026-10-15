package dev.vellum.config;

import dev.vellum.config.annotation.ConfigOption;

import java.util.ArrayList;
import java.util.List;

public final class VellumTestConfig extends Config {
    @ConfigOption(
            name = "Enabled",
            description = "Enable the test feature",
            category = "General"
    )
    public boolean enabled = true;

    @ConfigOption(
            name = "Speed",
            description = "Test feature speed",
            category = "General",
            defaultValue = "10",
            min = 0,
            max = 100,
            step = 5
    )
    public int speed = 10;

    @ConfigOption(
            name = "Scale",
            description = "Test feature scale",
            category = "General",
            defaultValue = "1.0",
            min = 0.1,
            max = 5.0,
            step = 0.1
    )
    public double scale = 1.0D;

    @ConfigOption(
            name = "Display Name",
            description = "Name shown by the test feature",
            category = "General"
    )
    public String displayName = "Vellum";

    @ConfigOption(
            name = "Entries",
            description = "Test string entries",
            category = "Lists"
    )
    public List<String> entries =
            new ArrayList<>(List.of(
                    "First Entry",
                    "Second Entry",
                    "Third Entry"
            ));
}
