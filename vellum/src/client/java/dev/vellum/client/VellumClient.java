package dev.vellum.client;

import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class VellumClient implements ClientModInitializer {
    public static final Logger LOGGER =
            LoggerFactory.getLogger("vellumconfig-client");

    @Override
    public void onInitializeClient() {
        LOGGER.info("Vellum client initialized.");
    }
}
