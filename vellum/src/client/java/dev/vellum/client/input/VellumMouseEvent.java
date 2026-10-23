package dev.vellum.client.input;

public record VellumMouseEvent(
        VellumMousePosition position,
        int button,
        double deltaX,
        double deltaY,
        boolean doubleClick
) {
}
