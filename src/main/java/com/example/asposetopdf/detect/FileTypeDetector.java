package com.example.asposetopdf.detect;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Detects the {@link FileType} for an input file using magic bytes and structural checks.
 */
public final class FileTypeDetector {
    private static final byte[] PDF_MAGIC = "%PDF".getBytes(StandardCharsets.US_ASCII);
    private static final byte[] JPEG_MAGIC = new byte[]{(byte) 0xFF, (byte) 0xD8, (byte) 0xFF};
    private static final byte[] PNG_MAGIC = new byte[]{(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A};
    private static final byte[] TIFF_MAGIC_LE = new byte[]{0x49, 0x49, 0x2A, 0x00};
    private static final byte[] TIFF_MAGIC_BE = new byte[]{0x4D, 0x4D, 0x00, 0x2A};
    private static final byte[] MSG_MAGIC = new byte[]{(byte) 0xD0, (byte) 0xCF, 0x11, (byte) 0xE0, (byte) 0xA1, (byte) 0xB1, 0x1A, (byte) 0xE1};
    private static final byte[] DWG_MAGIC_PREFIX = "AC10".getBytes(StandardCharsets.US_ASCII);
    private static final byte[] ZIP_MAGIC = new byte[]{0x50, 0x4B, 0x03, 0x04};

    private FileTypeDetector() {
    }

    public static FileType detect(Path input) throws IOException {
        byte[] header = readHeader(input, 512);

        if (startsWith(header, PDF_MAGIC)) {
            return FileType.PDF;
        }
        if (startsWith(header, JPEG_MAGIC)) {
            return FileType.JPEG;
        }
        if (startsWith(header, PNG_MAGIC)) {
            return FileType.PNG;
        }
        if (startsWith(header, TIFF_MAGIC_LE) || startsWith(header, TIFF_MAGIC_BE)) {
            return FileType.TIFF;
        }
        if (startsWith(header, MSG_MAGIC)) {
            return FileType.MSG;
        }
        if (startsWith(header, DWG_MAGIC_PREFIX)) {
            return FileType.DWG;
        }
        if (startsWith(header, ZIP_MAGIC)) {
            FileType zippedType = detectZipBasedType(input);
            if (zippedType != null) {
                return zippedType;
            }
        }

        if (looksLikeEmail(header)) {
            return FileType.EML;
        }

        Optional<FileType> fallback = FileType.fromExtension(getExtension(input));
        return fallback.orElseThrow(() ->
            new UnsupportedOperationException("Unsupported file type for input: " + input));
    }

    private static boolean startsWith(byte[] data, byte[] prefix) {
        if (data.length < prefix.length) {
            return false;
        }
        for (int i = 0; i < prefix.length; i++) {
            if (data[i] != prefix[i]) {
                return false;
            }
        }
        return true;
    }

    private static byte[] readHeader(Path input, int length) throws IOException {
        byte[] buffer = new byte[length];
        int read;
        try (InputStream stream = Files.newInputStream(input)) {
            read = stream.read(buffer);
        }
        if (read == -1) {
            return new byte[0];
        }
        return Arrays.copyOf(buffer, read);
    }

    private static String getExtension(Path input) {
        String name = input.getFileName().toString();
        int dot = name.lastIndexOf('.');
        if (dot == -1) {
            return "";
        }
        return name.substring(dot + 1).toLowerCase(Locale.ROOT);
    }

    private static boolean looksLikeEmail(byte[] header) {
        String snippet = new String(header, StandardCharsets.US_ASCII).toLowerCase(Locale.ROOT);
        return snippet.contains("mime-version:") || snippet.contains("content-type:")
            || snippet.startsWith("from ") || snippet.contains("return-path:");
    }

    private static FileType detectZipBasedType(Path input) throws IOException {
        try (ZipFile zipFile = new ZipFile(input.toFile())) {
            boolean hasWord = false;
            boolean hasExcel = false;
            boolean hasPowerPoint = false;
            boolean hasVisio = false;

            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                String name = entry.getName();
                if (name.startsWith("word/")) {
                    hasWord = true;
                }
                if (name.startsWith("xl/")) {
                    hasExcel = true;
                }
                if (name.startsWith("ppt/")) {
                    hasPowerPoint = true;
                }
                if (name.startsWith("visio/")) {
                    hasVisio = true;
                }
                if ("[Content_Types].xml".equals(name)) {
                    try (InputStream in = zipFile.getInputStream(entry)) {
                        String content = new String(in.readAllBytes(), StandardCharsets.UTF_8);
                        if (content.contains("wordprocessingml.document")) {
                            hasWord = true;
                        }
                        if (content.contains("spreadsheetml.sheet")) {
                            hasExcel = true;
                        }
                        if (content.contains("presentationml.presentation")) {
                            hasPowerPoint = true;
                        }
                        if (content.contains("visio.document")) {
                            hasVisio = true;
                        }
                    }
                }
            }

            if (hasWord) {
                return FileType.DOCX;
            }
            if (hasExcel) {
                return FileType.XLSX;
            }
            if (hasPowerPoint) {
                return FileType.PPTX;
            }
            if (hasVisio) {
                return FileType.VISIO;
            }
        }
        return null;
    }
}
