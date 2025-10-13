package com.example.asposetopdf.converters;

import com.example.asposetopdf.detect.FileType;

import java.nio.file.Path;

/**
 * Base interface for type specific converters.
 */
public interface FormatConverter {
    FileType getFileType();

    void convert(Path input, Path output) throws Exception;
}
