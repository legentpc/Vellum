package dev.vellum.platform.api;

public interface VellumTextureProvider<T> {
    T resolve(
            VellumTextureReference reference
    );
}
