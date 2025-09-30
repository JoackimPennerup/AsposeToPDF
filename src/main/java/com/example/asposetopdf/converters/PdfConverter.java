package com.example.asposetopdf.converters;

import com.aspose.pdf.Document;
import com.example.asposetopdf.detect.FileType;

import java.nio.file.Path;

/**
 * Converter for PDF inputs. Re-saves the document to ensure consistency.
 */
public class PdfConverter extends BaseConverter {
    @Override
    public FileType getFileType() {
        return FileType.PDF;
    }

    @Override
    public void convert(Path input, Path output) throws Exception {
        ensureParentDirectory(output);
        Document document = new Document(input.toString());
        try {
            document.save(output.toString());
        } finally {
            document.close();
        }
    }
}
