package dev.vellum.platform;

import dev.vellum.platform.api.VellumLogger;
import dev.vellum.platform.api.VellumResourceProvider;
import dev.vellum.platform.api.VellumTextureProvider;
import dev.vellum.platform.api.VellumTextureReference;
import dev.vellum.platform.client.MinecraftResourceProvider;
import dev.vellum.platform.client.MinecraftTextureProvider;
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

    private static VellumTextureProvider<Identifier>
            textureProvider;

    public static Minecraft minecraft() {
        return Minecraft.getInstance();
    }

    private static VellumResourceProvider resources;


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
            String namespace,
            String path
    ) throws IOException {
        return resources()
                .open(
                        namespace,
                        path
                );
    }

    public static VellumResourceProvider resources() {
        if (resources == null) {
            resources =
                    new MinecraftResourceProvider(
                            minecraft()
                    );
        }

        return resources;
    }

    public static boolean resourceExists(
            String namespace,
            String path
    ) {
        return resources().exists(
                namespace,
                path
        );
    }

    public static VellumTextureProvider<Identifier>
    textureProvider() {
        if (textureProvider == null) {
            textureProvider =
                    new MinecraftTextureProvider();
        }

        return textureProvider;
    }

    public static Identifier texture(
            String namespace,
            String path
    ) {
        return textureProvider().resolve(
                new VellumTextureReference(
                        namespace,
                        path
                )
        );
    }
}
