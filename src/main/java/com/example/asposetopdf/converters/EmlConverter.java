package com.example.asposetopdf.converters;

import com.aspose.email.MailMessage;
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
        MailMessage message = MailMessage.load(input.toString());
        try (ByteArrayOutputStream mhtmlStream = new ByteArrayOutputStream()) {
            message.save(mhtmlStream, SaveOptions.getDefaultMhtml());
            Document document = new Document(new ByteArrayInputStream(mhtmlStream.toByteArray()),
                new LoadOptions(LoadFormat.MHTML));
            document.save(output.toString(), SaveFormat.PDF);
        }
    }
}
