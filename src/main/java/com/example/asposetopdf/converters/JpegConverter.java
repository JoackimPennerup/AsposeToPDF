package com.example.asposetopdf.converters;

import com.aspose.pdf.Page;

import com.example.asposetopdf.detect.FileType;

import java.nio.file.Path;

/**
 * Converter for JPEG images.
 */
public class JpegConverter extends ImageConverter {
    public JpegConverter() {
        super(FileType.JPEG);
    }

    @Override
    protected void embedImage(Page page, Path input, double widthPoints, double heightPoints) {
        embedNativeImage(page, input, widthPoints, heightPoints);
    }
}
