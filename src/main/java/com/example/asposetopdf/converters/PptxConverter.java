package com.example.asposetopdf.converters;

import com.aspose.slides.Presentation;
import com.aspose.slides.SaveFormat;
import com.example.asposetopdf.detect.FileType;

import java.nio.file.Path;

/**
 * Converter for PPTX presentations.
 */
public class PptxConverter extends BaseConverter {
    @Override
    public FileType getFileType() {
        return FileType.PPTX;
    }

    @Override
    public void convert(Path input, Path output) throws Exception {
        ensureParentDirectory(output);
        Presentation presentation = new Presentation(input.toString());
        try {
            presentation.save(output.toString(), SaveFormat.Pdf);
        } finally {
            presentation.dispose();
        }
    }
}
