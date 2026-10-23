package dev.vellum.client.test;

import dev.vellum.config.Config;
import dev.vellum.config.annotation.ConfigOption;

public final class VellumFakeConfig
        extends Config {

    @ConfigOption(
            name = "enableFeature",
            description = "Enable the test feature",
            category = "general",
            defaultValue = "true"
    )
    public boolean enableFeature = true;

    @ConfigOption(
            name = "maxValue",
            description = "Maximum test value",
            category = "general",
            defaultValue = "50",
            min = 0,
            max = 100,
            step = 1
    )
    public int maxValue = 50;

    @ConfigOption(
            name = "mode",
            description = "Current test mode",
            category = "general",
            defaultValue = "\"Normal\""
    )
    public String mode = "Normal";

    @ConfigOption(
            name = "showOverlay",
            description = "Show test overlay",
            category = "display",
            defaultValue = "true"
    )
    public boolean showOverlay = true;

    @ConfigOption(
            name = "scale",
            description = "Overlay scale",
            category = "display",
            defaultValue = "1.0",
            min = 0.5,
            max = 3.0,
            step = 0.1
    )
    public double scale = 1.0;

    @ConfigOption(
            name = "debugMode",
            description = "Enable debug mode",
            category = "advanced",
            defaultValue = "false"
    )
    public boolean debugMode = false;
}
