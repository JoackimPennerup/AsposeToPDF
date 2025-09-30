package com.example.asposetopdf.converters;

import com.example.asposetopdf.detect.FileType;

/**
 * Converter for PNG images.
 */
public class PngConverter extends ImageConverter {
    public PngConverter() {
        super(FileType.PNG);
    }
}
