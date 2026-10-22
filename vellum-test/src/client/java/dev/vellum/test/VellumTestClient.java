package dev.vellum.test;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import dev.vellum.client.editor.VellumConfigEditor;
import dev.vellum.client.navigation.CategoryTree;
import dev.vellum.client.screen.ConfigScreen;
import dev.vellum.client.screen.ConfigScreenState;
import dev.vellum.config.ConfigManager;
import dev.vellum.platform.VellumPlatform;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Path;

public final class VellumTestClient
        implements ClientModInitializer {
    private static final Logger LOGGER =
            VellumPlatform.logger(
                    "vellumconfig-client"
            );

    private ConfigManager<VellumTestConfig>
            configManager;

    @Override
    public void onInitializeClient() {
        initializeConfig();
        registerCommand();
    }

    private void initializeConfig() {
        VellumTestConfig config =
                new VellumTestConfig();

        configManager =
                new ConfigManager<>(
                        config,
                        Path.of(
                                "config",
                                "vellum-test.json"
                        )
                );

        try {
            configManager.load();
        } catch (IOException exception) {
            LOGGER.error(
                    "Unable to load Vellum test configuration",
                    exception
            );
        }
    }

    private void registerCommand() {
        ClientCommandRegistrationCallback.EVENT.register(
                (dispatcher, ignoredRegistryAccess) ->
                        registerVellumCommand(dispatcher)
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

        command.executes(ignoredContext -> {
            openConfigScreen();
            return 1;
        });

        dispatcher.register(command);
    }

    private void openConfigScreen() {
        public static VellumConfigEditor create(
                ConfigManager<?> configManager
        ) {
            CategoryTree tree =
                    CategoryTree.fromConfigManager(
                            configManager
                    );

            return new VellumConfigEditor(
                    new ConfigScreenState(tree),
                    configManager
            );
        }
    }
}
