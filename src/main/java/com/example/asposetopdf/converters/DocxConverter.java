package com.example.asposetopdf.converters;

import com.aspose.words.Document;
import com.aspose.words.SaveFormat;
import com.example.asposetopdf.detect.FileType;

import java.nio.file.Path;

/**
 * Converter for DOCX files.
 */
public class DocxConverter extends BaseConverter {
    @Override
    public FileType getFileType() {
        return FileType.DOCX;
    }

    @Override
    public void convert(Path input, Path output) throws Exception {
        ensureParentDirectory(output);
        Document document = new Document(input.toString());
        document.save(output.toString(), SaveFormat.PDF);
    }
}
