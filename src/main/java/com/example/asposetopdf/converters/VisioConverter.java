package com.example.asposetopdf.converters;

import com.aspose.diagram.Diagram;
import com.aspose.diagram.SaveFileFormat;
import com.example.asposetopdf.detect.FileType;

import java.nio.file.Path;

/**
 * Converter for Visio drawings.
 */
public class VisioConverter extends BaseConverter {
    @Override
    public FileType getFileType() {
        return FileType.VISIO;
    }

    @Override
    public void convert(Path input, Path output) throws Exception {
        ensureParentDirectory(output);
        Diagram diagram = new Diagram(input.toString());
        try {
            diagram.save(output.toString(), SaveFileFormat.PDF);
        } finally {
            diagram.dispose();
        }
    }
}

