package com.example.asposetopdf.converters;

import com.aspose.pdf.Page;

import com.example.asposetopdf.detect.FileType;

import java.nio.file.Path;

/**
 * Converter for PNG images.
 */
public class PngConverter extends ImageConverter {
    public PngConverter() {
        super(FileType.PNG);
    }

    @Override
    protected void embedImage(Page page, Path input, double widthPoints, double heightPoints) {
        embedNativeImage(page, input, widthPoints, heightPoints);
    }
}
