package com.example.asposetopdf.converters;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Shared utilities for converters.
 */
abstract class BaseConverter implements FormatConverter {

    protected void ensureParentDirectory(Path output) throws IOException {
        Path parent = output.toAbsolutePath().getParent();
        if (parent != null && !Files.exists(parent)) {
            Files.createDirectories(parent);
        }
    }
}
