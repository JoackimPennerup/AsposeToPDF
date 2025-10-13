package com.example.asposetopdf.converters;

import com.aspose.email.MailMessage;
import com.aspose.email.SaveOptions;
import com.aspose.html.converters.Converter;
import com.aspose.html.saving.PdfSaveOptions;
import com.example.asposetopdf.detect.FileType;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.file.Path;

/**
 * Converter for EML email messages.
 */
public class EmlConverter extends BaseConverter {
    @Override
    public FileType getFileType() {
        return FileType.EML;
    }

    @Override
    public void convert(Path input, Path output) throws Exception {
        ensureParentDirectory(output);
        try (MailMessage mailMessage = MailMessage.load(input.toString());
             ByteArrayOutputStream mhtmlStream = new ByteArrayOutputStream()) {
            mailMessage.save(mhtmlStream, SaveOptions.getDefaultMhtml());
            byte[] mhtmlBytes = mhtmlStream.toByteArray();
            try (ByteArrayInputStream mhtmlInput = new ByteArrayInputStream(mhtmlBytes)) {
                Converter.convertMHTML(mhtmlInput, new PdfSaveOptions(), output.toString());
            }
        }
    }
}
