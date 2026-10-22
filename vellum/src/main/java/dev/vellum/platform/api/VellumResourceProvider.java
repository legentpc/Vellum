package dev.vellum.platform.api;

import java.io.IOException;
import java.io.InputStream;

public interface VellumResourceProvider {
    InputStream open(
            String namespace,
            String path
    ) throws IOException;
}
