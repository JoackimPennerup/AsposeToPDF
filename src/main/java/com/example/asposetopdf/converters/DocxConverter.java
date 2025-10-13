package com.example.asposetopdf.converters;

import com.aspose.words.*;
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
        PdfSaveOptions pdfOptions = new PdfSaveOptions();
        pdfOptions.setExportDocumentStructure(true);
        pdfOptions.setDisplayDocTitle(true);
        pdfOptions.setExportLanguageToSpanTag(true);
        pdfOptions.setExportParagraphGraphicsToArtifact(true);
        pdfOptions.setPageMode(PdfPageMode.USE_OUTLINES);

        OutlineOptions outline = pdfOptions.getOutlineOptions();
        outline.setDefaultBookmarksOutlineLevel(1);
        outline.setHeadingsOutlineLevels(3);
        document.save(output.toString(), pdfOptions);
    }
}




