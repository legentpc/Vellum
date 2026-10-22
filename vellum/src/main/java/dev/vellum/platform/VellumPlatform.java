package dev.vellum.platform;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.Font;
import org.slf4j.Logger;
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

    public static Logger logger(
            String name
    ) {
        Objects.requireNonNull(
                name,
                "name"
        );

        return LoggerFactory.getLogger(name);
    }

    public static Font font() {
        return minecraft().font;
    }
}
