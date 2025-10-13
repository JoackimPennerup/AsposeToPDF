package com.example.asposetopdf.converters;

import com.aspose.imaging.Image;
import com.aspose.imaging.ResolutionSetting;
import com.aspose.imaging.fileformats.tiff.TiffFrame;
import com.aspose.imaging.fileformats.tiff.TiffImage;
import com.aspose.imaging.fileformats.tiff.enums.TiffExpectedFormat;
import com.aspose.imaging.imageoptions.PngOptions;
import com.aspose.imaging.imageoptions.TiffOptions;
import com.aspose.pdf.Document;
import com.aspose.pdf.ImageFilterType;
import com.aspose.pdf.MarginInfo;
import com.aspose.pdf.Page;
import com.example.asposetopdf.detect.FileType;

import java.io.ByteArrayOutputStream;
import java.nio.file.Path;

/**
 * Converter for TIFF images.
 */
public class TiffConverter extends ImageConverter {
    public TiffConverter() {
        super(FileType.TIFF);
    }

    @Override
    public void convert(Path input, Path output) throws Exception {
        ensureParentDirectory(output);

        try (Image image = Image.load(input.toString())) {
            if (!(image instanceof TiffImage)) {
                super.convert(input, output);
                return;
            }

            TiffImage tiff = (TiffImage) image;
            Document document = new Document();
            try {
                MarginInfo margin = new MarginInfo(0, 0, 0, 0);
                document.getPageInfo().setMargin(margin);

                for (TiffFrame frame : tiff.getFrames()) {
                    Page page = document.getPages().add();
                    page.getPageInfo().setMargin(margin);
                    setPageDimensions(page, frame.getWidth(), frame.getHeight(),
                            frame.getHorizontalResolution(), frame.getVerticalResolution());

                    double widthPoints = page.getPageInfo().getWidth();
                    double heightPoints = page.getPageInfo().getHeight();

                    if (frame.getBitsPerPixel() == 1) {
                        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
                             TiffOptions tiffOptions = new TiffOptions(TiffExpectedFormat.TiffCcittFax4)) {
                            tiffOptions.setResolutionSettings(new ResolutionSetting(frame.getHorizontalResolution(), frame.getVerticalResolution()));
                            frame.save(baos, tiffOptions);
                            embedImage(page, baos.toByteArray(), widthPoints, heightPoints, ImageFilterType.CCITTFax);
                        }
                        continue;
                    }

                    try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                        PngOptions options = new PngOptions();
                        options.setCompressionLevel(0);
                        frame.save(baos, options);
                        embedImage(page, baos.toByteArray(), widthPoints, heightPoints);
                    }
                }

                document.save(output.toString());
            } finally {
                document.close();
            }
        }
    }

}
