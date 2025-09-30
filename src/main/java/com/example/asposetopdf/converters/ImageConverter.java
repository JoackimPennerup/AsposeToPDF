package com.example.asposetopdf.converters;

import com.aspose.imaging.Image;
import com.aspose.pdf.Document;
import com.aspose.pdf.MarginInfo;
import com.aspose.pdf.Page;
import com.example.asposetopdf.detect.FileType;

import java.nio.file.Path;

/**
 * Base converter for raster image types.
 */
public class ImageConverter extends BaseConverter {
    private final FileType fileType;
    private static final double POINTS_PER_INCH = 72.0;
    private static final double FALLBACK_DPI = 96.0;

    public ImageConverter(FileType fileType) {
        this.fileType = fileType;
    }

    @Override
    public FileType getFileType() {
        return fileType;
    }

    @Override
    public void convert(Path input, Path output) throws Exception {
        ensureParentDirectory(output);
        try (Image imageInfo = Image.load(input.toString())) {
            Document document = new Document();
            try {
                MarginInfo margin = new MarginInfo(0, 0, 0, 0);
                document.getPageInfo().setMargin(margin);

                Page page = document.getPages().add();
                page.getPageInfo().setMargin(margin);
                setPageDimensions(page, imageInfo.getWidth(), imageInfo.getHeight(),
                        imageInfo.getHorizontalResolution(), imageInfo.getVerticalResolution());

                com.aspose.pdf.Image pdfImage = new com.aspose.pdf.Image();
                pdfImage.setFile(input.toString());
                pdfImage.setFixWidth(page.getPageInfo().getWidth());
                pdfImage.setFixHeight(page.getPageInfo().getHeight());
                pdfImage.setIsApplyResolution(true);
                page.getParagraphs().add(pdfImage);

                document.save(output.toString());
            } finally {
                document.close();
            }
        }
    }

    protected void setPageDimensions(Page page, int pixelWidth, int pixelHeight, double horizontalDpi, double verticalDpi) {
        double widthPoints = toPoints(pixelWidth, horizontalDpi);
        double heightPoints = toPoints(pixelHeight, verticalDpi);
        page.getPageInfo().setWidth(widthPoints);
        page.getPageInfo().setHeight(heightPoints);
    }

    private double toPoints(int pixels, double dpi) {
        double effectiveDpi = dpi > 0 ? dpi : FALLBACK_DPI;
        return pixels / effectiveDpi * POINTS_PER_INCH;
    }
}
