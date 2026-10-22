package dev.vellum.platform;

import dev.vellum.platform.api.VellumLogger;
import dev.vellum.platform.client.Slf4jVellumLogger;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.Font;
import net.minecraft.resources.Identifier;

import java.io.IOException;
import java.io.InputStream;

import org.slf4j.LoggerFactory;

import java.util.Objects;

public final class VellumPlatform {
    private VellumPlatform() {
    }

    public static Minecraft minecraft() {
        return Minecraft.getInstance();
    }

    public static void openScreen(
            Screen screen
    ) {
        Objects.requireNonNull(
                screen,
                "screen"
        );

        minecraft().setScreen(screen);
    }

    public static Screen currentScreen() {
        return minecraft().screen;
    }

    public static boolean isClientReady() {
        return minecraft().level != null;
    }

    public static VellumLogger logger(
            String name
    ) {
        Objects.requireNonNull(
                name,
                "name"
        );

        return new Slf4jVellumLogger(
                LoggerFactory.getLogger(name)
        );
    }

    public static Font font() {
        return minecraft().font;
    }

    public static InputStream openResource(
            Identifier identifier
    ) throws IOException {
        Objects.requireNonNull(
                identifier,
                "identifier"
        );

        return minecraft()
                .getResourceManager()
                .getResource(identifier)
                .orElseThrow(
                        () -> new IOException(
                                "Resource not found: "
                                        + identifier
                        )
                )
                .open();
    }
}
