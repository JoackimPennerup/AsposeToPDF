package com.example.asposetopdf.detect;

import java.util.Locale;
import java.util.Optional;

/**
 * Supported input file types.
 */
public enum FileType {
    PDF("pdf"),
    JPEG("jpg"),
    DOCX("docx"),
    EML("eml"),
    PNG("png"),
    MSG("msg"),
    XLSX("xlsx"),
    TIFF("tif"),
    VISIO("visio"),
    PPTX("pptx"),
    DWG("dwg");

    private final String defaultExtension;

    FileType(String defaultExtension) {
        this.defaultExtension = defaultExtension;
    }

    public String getDefaultExtension() {
        return defaultExtension;
    }

    public static Optional<FileType> fromExtension(String extension) {
        if (extension == null || extension.isBlank()) {
            return Optional.empty();
        }
        String normalized = extension.toLowerCase(Locale.ROOT);
        for (FileType type : values()) {
            if (type.defaultExtension.equals(normalized) ||
                (type == JPEG && normalized.equals("jpeg")) ||
                (type == TIFF && (normalized.equals("tiff"))) ||
                (type == VISIO && (normalized.equals("vsdx") || normalized.equals("vsd"))) ||
                (type == DWG && normalized.equals("dwg"))) {
                return Optional.of(type);
            }
        }
        return Optional.empty();
    }
}
