package dev.vellum.client.input;

public record VellumMousePosition(
        double x,
        double y
) {
    public int blockX() {
        return (int) Math.floor(x);
    }

    public int blockY() {
        return (int) Math.floor(y);
    }
}
