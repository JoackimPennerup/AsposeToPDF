package com.example.asposetopdf.converters;

import com.example.asposetopdf.detect.FileType;

/**
 * Converter for JPEG images.
 */
public class JpegConverter extends ImageConverter {
    public JpegConverter() {
        super(FileType.JPEG);
    }
}
