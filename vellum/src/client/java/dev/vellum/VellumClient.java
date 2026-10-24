package dev.vellum;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import dev.vellum.client.editor.VellumConfigEditor;
import dev.vellum.client.screen.ConfigScreen;
import dev.vellum.config.ConfigManager;
import dev.vellum.platform.VellumPlatform;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

public final class VellumClient
        implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        registerCommand();
    }

    private void registerCommand() {
        ClientCommandRegistrationCallback.EVENT.register(
                (dispatcher, ignoredRegistryAccess) ->
                        registerVellumCommand(
                                dispatcher
                        )
        );
    }

    private void registerVellumCommand(
            CommandDispatcher<FabricClientCommandSource>
                    dispatcher
    ) {
        LiteralArgumentBuilder<FabricClientCommandSource>
                command =
                LiteralArgumentBuilder.literal(
                        "vellum"
                );

        command.executes(
                ignoredContext -> {
                    openFakeConfigScreen();
                    return 1;
                }
        );

        dispatcher.register(
                command
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
