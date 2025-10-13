package com.example.asposetopdf;

import com.example.asposetopdf.converters.ConversionService;
import com.example.asposetopdf.detect.FileType;
import com.example.asposetopdf.detect.FileTypeDetector;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Entry point for the Aspose to PDF command line converter.
 */
public final class App {

    private App() {
    }

    public static void main(String[] args) {
        if (args.length == 0 || args.length > 2) {
            printUsage();
            System.exit(1);
        }

        Path inputPath = Paths.get(args[0]);
        if (!Files.exists(inputPath)) {
            System.err.printf("Input file '%s' does not exist.%n", inputPath);
            System.exit(2);
        }

        Path outputPath;
        if (args.length == 2) {
            outputPath = Paths.get(args[1]);
        } else {
            String fileName = inputPath.getFileName().toString();
            int dotIndex = fileName.lastIndexOf('.');
            if (dotIndex > 0) {
                fileName = fileName.substring(0, dotIndex);
            }
            Path parent = inputPath.toAbsolutePath().getParent();
            outputPath = parent == null ? Paths.get(fileName + ".pdf") : parent.resolve(fileName + ".pdf");
        }

        try {
            LicenseLoader.applyLicenses();
            FileType type = FileTypeDetector.detect(inputPath);
            new ConversionService().convert(type, inputPath, outputPath);
            System.out.printf("Converted %s to %s%n", inputPath, outputPath);
        } catch (UnsupportedOperationException ex) {
            System.err.println(ex.getMessage());
            System.exit(3);
        } catch (IOException ex) {
            System.err.printf("Failed to detect file type: %s%n", ex.getMessage());
            System.exit(4);
        } catch (Exception ex) {
            System.err.printf("Conversion failed: %s%n", ex.getMessage());
            ex.printStackTrace(System.err);
            System.exit(5);
        }
    }

    private static void printUsage() {
        System.err.println("Usage: AsposeToPDF <input-file> [output-file]");
    }
}
