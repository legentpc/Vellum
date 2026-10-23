package dev.vellum;

import dev.vellum.client.editor.VellumConfigEditor;
import dev.vellum.client.screen.ConfigScreen;
import dev.vellum.config.ConfigManager;
import dev.vellum.platform.VellumPlatform;
import net.fabricmc.api.ClientModInitializer;

public final class VellumClient
        implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
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
