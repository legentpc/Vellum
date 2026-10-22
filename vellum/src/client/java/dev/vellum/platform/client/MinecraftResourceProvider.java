package dev.vellum.platform.client;

import dev.vellum.platform.api.VellumResourceProvider;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public final class MinecraftResourceProvider
        implements VellumResourceProvider {
    private final Minecraft minecraft;

    public MinecraftResourceProvider(
            Minecraft minecraft
    ) {
        this.minecraft =
                Objects.requireNonNull(
                        minecraft,
                        "minecraft"
                );
    }

    @Override
    public InputStream open(
            String namespace,
            String path
    ) throws IOException {
        Identifier identifier =
                Identifier.fromNamespaceAndPath(
                        namespace,
                        path
                );

        return minecraft
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
