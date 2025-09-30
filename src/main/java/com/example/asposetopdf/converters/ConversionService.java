package com.example.asposetopdf.converters;

import com.example.asposetopdf.detect.FileType;

import java.nio.file.Path;
import java.util.EnumMap;
import java.util.Map;

/**
 * Dispatches conversion to the proper format specific converter.
 */
public class ConversionService {
    private final Map<FileType, FormatConverter> converters = new EnumMap<>(FileType.class);

    public ConversionService() {
        register(new PdfConverter());
        register(new JpegConverter());
        register(new PngConverter());
        register(new TiffConverter());
        register(new DocxConverter());
        register(new EmlConverter());
        register(new MsgConverter());
        register(new XlsxConverter());
        register(new VisioConverter());
        register(new PptxConverter());
        register(new DwgConverter());
    }

    private void register(FormatConverter converter) {
        converters.put(converter.getFileType(), converter);
    }

    public void convert(FileType type, Path input, Path output) throws Exception {
        FormatConverter converter = converters.get(type);
        if (converter == null) {
            throw new UnsupportedOperationException("No converter registered for type " + type);
        }
        converter.convert(input, output);
    }
}
