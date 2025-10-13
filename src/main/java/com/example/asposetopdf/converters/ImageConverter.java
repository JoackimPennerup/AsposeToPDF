package com.example.asposetopdf.converters;

import com.aspose.imaging.Image;
import com.aspose.imaging.RasterImage;
import com.aspose.pdf.Document;
import com.aspose.pdf.MarginInfo;
import com.aspose.pdf.Matrix;
import com.aspose.pdf.Page;
import com.aspose.pdf.Rectangle;
import com.aspose.pdf.ImageFilterType;
import com.aspose.pdf.XImageCollection;
import com.aspose.pdf.operators.ConcatenateMatrix;
import com.aspose.pdf.operators.Do;
import com.aspose.pdf.operators.GRestore;
import com.aspose.pdf.operators.GSave;
import com.example.asposetopdf.detect.FileType;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
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
            int pixelWidth = imageInfo.getWidth();
            int pixelHeight = imageInfo.getHeight();
            double horizontalDpi = FALLBACK_DPI;
            double verticalDpi = FALLBACK_DPI;

            if (imageInfo instanceof RasterImage raster) {
                horizontalDpi = raster.getHorizontalResolution();
                verticalDpi = raster.getVerticalResolution();
            }

            Document document = new Document();
            try {
                MarginInfo margin = new MarginInfo(0, 0, 0, 0);
                document.getPageInfo().setMargin(margin);

                Page page = document.getPages().add();
                page.getPageInfo().setMargin(margin);
                setPageDimensions(page, pixelWidth, pixelHeight, horizontalDpi, verticalDpi);

                double widthPoints = toPoints(pixelWidth, horizontalDpi);
                double heightPoints = toPoints(pixelHeight, verticalDpi);
                embedImage(page, input, widthPoints, heightPoints);

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
        Rectangle crop = new Rectangle(0, 0, widthPoints, heightPoints);
        page.setCropBox(crop);
        page.setMediaBox(crop);
        page.setTrimBox(crop);
        page.setBleedBox(crop);
        page.setArtBox(crop);
    }

    protected void embedImage(Page page, Path input, double widthPoints, double heightPoints) {
        try {
            byte[] imageData = Files.readAllBytes(input);
            embedImage(page, imageData, widthPoints, heightPoints, getDefaultImageFilter());
        } catch (IOException e) {
            throw new RuntimeException("Failed to embed image " + input, e);
        }
    }

    protected void embedImage(Page page, byte[] imageData, double widthPoints, double heightPoints) {
        embedImage(page, imageData, widthPoints, heightPoints, getDefaultImageFilter());
    }

    protected void embedImage(Page page, byte[] imageData, double widthPoints, double heightPoints, int filterType) {
        XImageCollection images = page.getResources().getImages();
        try (ByteArrayInputStream imageStream = new ByteArrayInputStream(imageData)) {
            String imageName = images.addWithImageFilterType(imageStream, filterType);

            page.getContents().add(new GSave());
            Matrix matrix = new Matrix(new double[]{widthPoints, 0, 0, heightPoints, 0, 0});
            page.getContents().add(new ConcatenateMatrix(matrix));
            page.getContents().add(new Do(imageName));
            page.getContents().add(new GRestore());
        } catch (IOException e) {
            throw new RuntimeException("Failed to embed image with filter " + filterType, e);
        }
    }

    protected int getDefaultImageFilter() {
        return ImageFilterType.Flate;
    }

    private double toPoints(int pixels, double dpi) {
        double effectiveDpi = dpi > 0 ? dpi : FALLBACK_DPI;
        return pixels / effectiveDpi * POINTS_PER_INCH;
    }
}
