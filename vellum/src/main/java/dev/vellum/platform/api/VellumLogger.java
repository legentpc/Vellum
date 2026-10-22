package dev.vellum.platform.api;

public interface VellumLogger {
    void info(
            String message,
            Object... arguments
    );

    void warn(
            String message,
            Object... arguments
    );

    void error(
            String message,
            Object... arguments
    );

    void error(
            String message,
            Throwable throwable
    );
}
