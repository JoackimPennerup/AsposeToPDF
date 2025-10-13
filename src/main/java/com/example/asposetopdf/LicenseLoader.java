package com.example.asposetopdf;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Applies Aspose licenses when available.
 */
public final class LicenseLoader {
    private static final String LICENSE_ENV = "ASPOSE_LICENSE_PATH";

    private LicenseLoader() {
    }

    public static void applyLicenses() {
        String licensePath = System.getenv(LICENSE_ENV);
        if (licensePath == null || licensePath.isBlank()) {
            return;
        }
        Path path = Path.of(licensePath);
        if (!Files.exists(path)) {
            System.err.printf("Aspose license file not found at %s%n", path);
            return;
        }

        setLicense(() -> new com.aspose.pdf.License().setLicense(path.toString()));
        setLicense(() -> new com.aspose.words.License().setLicense(path.toString()));
        setLicense(() -> new com.aspose.cells.License().setLicense(path.toString()));
        setLicense(() -> new com.aspose.slides.License().setLicense(path.toString()));
        setLicense(() -> new com.aspose.diagram.License().setLicense(path.toString()));
        setLicense(() -> new com.aspose.email.License().setLicense(path.toString()));
        setLicense(() -> new com.aspose.imaging.License().setLicense(path.toString()));
    }

    @FunctionalInterface
    private interface LicenseApplier {
        void apply() throws Exception;
    }

    private static void setLicense(LicenseApplier applier) {
        try {
            applier.apply();
        } catch (Exception ex) {
            System.err.printf("Failed to apply license: %s%n", ex.getMessage());
        }
    }
}
