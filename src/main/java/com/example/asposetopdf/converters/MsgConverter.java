package com.example.asposetopdf.converters;

import com.aspose.email.MailConversionOptions;
import com.aspose.email.MailMessage;
import com.aspose.email.MapiMessage;
import com.aspose.email.SaveOptions;
import com.aspose.words.Document;
import com.aspose.words.LoadFormat;
import com.aspose.words.LoadOptions;
import com.aspose.words.SaveFormat;
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
        MapiMessage mapiMessage = MapiMessage.load(input.toString());
        MailMessage mailMessage = mapiMessage.toMailMessage(new MailConversionOptions());
        try (ByteArrayOutputStream mhtmlStream = new ByteArrayOutputStream()) {
            mailMessage.save(mhtmlStream, SaveOptions.getDefaultMhtml());
            Document document = new Document(new ByteArrayInputStream(mhtmlStream.toByteArray()),
                new LoadOptions(LoadFormat.MHTML));
            document.save(output.toString(), SaveFormat.PDF);
        }
    }
}
