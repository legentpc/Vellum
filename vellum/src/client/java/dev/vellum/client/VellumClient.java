package dev.vellum.client;

import dev.vellum.client.navigation.CategoryTree;
import dev.vellum.client.screen.ConfigScreen;
import dev.vellum.client.screen.ConfigScreenState;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.Minecraft;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class VellumClient implements ClientModInitializer {
    public static final Logger LOGGER =
            LoggerFactory.getLogger("vellumconfig-client");

    @Override
    public void onInitializeClient() {
        LOGGER.info("Vellum client initialized.");
    }

    public static void openConfigScreen() {
        CategoryTree tree = new CategoryTree();

        tree.addPath("general", "General");
        tree.addPath("graphics", "Graphics");
        tree.addPath("graphics.shaders", "Shaders");
        tree.addPath("performance", "Performance");

        ConfigScreenState state = new ConfigScreenState(tree);

        Minecraft.getInstance().setScreen(
                new ConfigScreen(state)
        );
    }
}
