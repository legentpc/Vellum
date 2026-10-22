package dev.vellum.platform.api;

import java.util.Objects;

public record VellumTextureReference(
        String namespace,
        String path
) {
    public VellumTextureReference {
        Objects.requireNonNull(
                namespace,
                "namespace"
        );

        Objects.requireNonNull(
                path,
                "path"
        );

        if (namespace.isBlank()) {
            throw new IllegalArgumentException(
                    "namespace cannot be blank"
            );
        }

        if (path.isBlank()) {
            throw new IllegalArgumentException(
                    "path cannot be blank"
            );
        }
    }
}
