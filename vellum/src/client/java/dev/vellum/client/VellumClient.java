package dev.vellum.client;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import dev.vellum.client.navigation.CategoryTree;
import dev.vellum.client.screen.ConfigScreen;
import dev.vellum.client.screen.ConfigScreenState;
import dev.vellum.config.ConfigManager;
import dev.vellum.config.VellumTestConfig;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

import net.minecraft.client.Minecraft;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;

public final class VellumClient
        implements ClientModInitializer {
    public static final Logger LOGGER =
            LoggerFactory.getLogger(
                    "vellumconfig-client"
            );

    private static ConfigManager<?> configManager;

    @Override
    public void onInitializeClient() {
        LOGGER.info(
                "Vellum client initialized."
        );

        initializeTestConfig();
        registerVellumCommand();
    }

    private void initializeTestConfig() {
        VellumTestConfig config =
                new VellumTestConfig();

        ConfigManager<VellumTestConfig> manager =
                new ConfigManager<>(
                        config,
                        Path.of(
                                "config",
                                "vellum.json"
                        )
                );

        try {
            manager.load();

            LOGGER.info(
                    "Vellum test configuration loaded from {}.",
                    manager.file()
            );
        } catch (IOException exception) {
            LOGGER.error(
                    "Unable to load Vellum configuration",
                    exception
            );
        }

        setConfigManager(manager);

        LOGGER.info(
                "Vellum test config manager initialized."
        );
    }

    public static void openConfigScreen(
            ConfigManager<?> manager
    ) {
        LOGGER.info(
                "Preparing Vellum configuration screen."
        );

        CategoryTree tree =
                CategoryTree.fromConfigManager(
                        manager
                );

        LOGGER.info(
                "Vellum category tree contains {} visible nodes.",
                tree.visibleNodes().size()
        );

        ConfigScreenState state =
                new ConfigScreenState(tree);

        Minecraft.getInstance().setScreen(
                new ConfigScreen(
                        state,
                        manager
                )
        );

        LOGGER.info(
                "Vellum configuration screen submitted to Minecraft."
        );
    }

    private void registerVellumCommand() {
        LOGGER.info(
                "Registering Vellum client command callback."
        );

        ClientCommandRegistrationCallback.EVENT.register(
                (dispatcher, ignoredRegistryAccess) -> {
                    LOGGER.info(
                            "Registering /vellum command."
                    );

                    registerCommand(dispatcher);
                }
        );
    }

    private void registerCommand(
            CommandDispatcher<FabricClientCommandSource> dispatcher
    ) {
        LiteralArgumentBuilder<FabricClientCommandSource>
                command =
                LiteralArgumentBuilder.literal(
                        "vellum"
                );

        command.executes(ignoredContext -> {
            LOGGER.info(
                    "Opening Vellum test configuration screen."
            );

            if (configManager == null) {
                LOGGER.error(
                        "Cannot open Vellum screen: "
                                + "config manager is null."
                );

                return 0;
            }

            openConfigScreen(configManager);
            return 1;
        });

        dispatcher.register(command);
    }

    public static void setConfigManager(
            ConfigManager<?> manager
    ) {
        configManager = manager;
    }
}
