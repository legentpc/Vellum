package dev.vellum.platform.client;

import dev.vellum.platform.api.VellumLogger;

import org.slf4j.Logger;

import java.util.Objects;

public final class Slf4jVellumLogger
        implements VellumLogger {
    private final Logger logger;

    public Slf4jVellumLogger(
            Logger logger
    ) {
        this.logger = Objects.requireNonNull(
                logger,
                "logger"
        );
    }

    @Override
    public void info(
            String message,
            Object... arguments
    ) {
        logger.info(
                message,
                arguments
        );
    }

    @Override
    public void warn(
            String message,
            Object... arguments
    ) {
        logger.warn(
                message,
                arguments
        );
    }

    @Override
    public void error(
            String message,
            Object... arguments
    ) {
        logger.error(
                message,
                arguments
        );
    }

    @Override
    public void error(
            String message,
            Throwable throwable
    ) {
        logger.error(
                message,
                throwable
        );
    }
}
