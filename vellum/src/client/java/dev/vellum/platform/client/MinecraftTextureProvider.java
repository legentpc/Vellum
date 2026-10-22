package dev.vellum.platform.client;

import dev.vellum.platform.api.VellumTextureProvider;
import dev.vellum.platform.api.VellumTextureReference;

import net.minecraft.resources.Identifier;

import java.util.Objects;

public final class MinecraftTextureProvider
        implements VellumTextureProvider<Identifier> {
    @Override
    public Identifier resolve(
            VellumTextureReference reference
    ) {
        Objects.requireNonNull(
                reference,
                "reference"
        );

        return Identifier.fromNamespaceAndPath(
                reference.namespace(),
                reference.path()
        );
    }
}
