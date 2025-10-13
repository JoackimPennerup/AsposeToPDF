package com.example.asposetopdf.converters;

import com.aspose.email.MailConversionOptions;
import com.aspose.email.MailMessage;
import com.aspose.email.MapiMessage;
import com.aspose.email.SaveOptions;
import com.aspose.html.converters.Converter;
import com.aspose.html.saving.PdfSaveOptions;
import com.example.asposetopdf.detect.FileType;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.file.Path;

/**
 * Converter for Outlook MSG files.
 */
public class MsgConverter extends BaseConverter {
    @Override
    public FileType getFileType() {
        return FileType.MSG;
    }

    @Override
    public void convert(Path input, Path output) throws Exception {
        ensureParentDirectory(output);
        try (MapiMessage mapiMessage = MapiMessage.load(input.toString());
             MailMessage mailMessage = mapiMessage.toMailMessage(new MailConversionOptions());
             ByteArrayOutputStream mhtmlStream = new ByteArrayOutputStream()) {
            mailMessage.save(mhtmlStream, SaveOptions.getDefaultMhtml());
            byte[] mhtmlBytes = mhtmlStream.toByteArray();
            try (ByteArrayInputStream mhtmlInput = new ByteArrayInputStream(mhtmlBytes)) {
                Converter.convertMHTML(mhtmlInput, new PdfSaveOptions(), output.toString());
            }
        }
    }
}

