package dev.vellum.client.input;

public record VellumCharacterEvent(
        int codePoint
) {
    public char character() {
        return (char) codePoint;
    }
}
