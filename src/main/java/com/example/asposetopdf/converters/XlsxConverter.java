package com.example.asposetopdf.converters;

import com.aspose.cells.SaveFormat;
import com.aspose.cells.Workbook;
import com.example.asposetopdf.detect.FileType;

import java.nio.file.Path;

/**
 * Converter for XLSX spreadsheets.
 */
public class XlsxConverter extends BaseConverter {
    @Override
    public FileType getFileType() {
        return FileType.XLSX;
    }

    @Override
    public void convert(Path input, Path output) throws Exception {
        ensureParentDirectory(output);
        Workbook workbook = new Workbook(input.toString());
        workbook.save(output.toString(), SaveFormat.PDF);
    }
}
