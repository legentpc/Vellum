package dev.vellum.client.input;

public record VellumKeyEvent(
        int key,
        int scanCode,
        int modifiers,
        boolean pressed
) {
}
