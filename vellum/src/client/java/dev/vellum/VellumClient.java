package dev.vellum;

import dev.vellum.client.editor.VellumConfigEditor;
import dev.vellum.client.screen.ConfigScreen;
import dev.vellum.config.ConfigManager;
import dev.vellum.platform.VellumPlatform;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;

import org.lwjgl.glfw.GLFW;

public final class VellumClient
        implements ClientModInitializer {

    private static final KeyMapping OPEN_TEST_SCREEN =
            new KeyMapping(
                    "key.vellumconfig.open_test_screen",
                    GLFW.GLFW_KEY_F8,
                    "category.vellumconfig"
            );

    @Override
    public void onInitializeClient() {
        KeyBindingHelper.registerKeyBinding(
                OPEN_TEST_SCREEN
        );

        ClientTickEvents.END_CLIENT_TICK.register(
                client -> {
                    while (OPEN_TEST_SCREEN.consumeClick()) {
                        openFakeConfigScreen();
                    }
                }
        );
    }

    public static void openFakeConfigScreen() {
        VellumPlatform.openScreen(
                ConfigScreen.create(
                        VellumConfigEditor.createForTesting(),
                        VellumPlatform.currentScreen()
                )
        );
    }

    public static void openConfigScreen(
            ConfigManager<?> configManager
    ) {
        VellumPlatform.openScreen(
                ConfigScreen.create(
                        VellumConfigEditor.create(
                                configManager
                        ),
                        VellumPlatform.currentScreen()
                )
        );
    }
}
