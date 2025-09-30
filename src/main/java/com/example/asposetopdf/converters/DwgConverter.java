package com.example.asposetopdf.converters;

import com.aspose.cad.Image;
import com.aspose.cad.imageoptions.CadRasterizationOptions;
import com.aspose.cad.imageoptions.PdfOptions;
import com.example.asposetopdf.detect.FileType;

import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Converter for DWG CAD drawings.
 */
public class DwgConverter extends BaseConverter {
    @Override
    public FileType getFileType() {
        return FileType.DWG;
    }

    @Override
    public void convert(Path input, Path output) throws Exception {
        ensureParentDirectory(output);
        try (Image image = Image.load(input.toString())) {
            CadRasterizationOptions rasterization = new CadRasterizationOptions();
            rasterization.setAutomaticLayoutsScaling(true);
            rasterization.setNoScaling(false);

            String[] layouts = determineLayouts(image);
            if (layouts.length > 0) {
                rasterization.setLayouts(layouts);
            } else {
                rasterization.setLayouts(new String[]{"Model"});
            }

            int width = image.getWidth();
            int height = image.getHeight();
            if (width > 0 && height > 0) {
                rasterization.setPageWidth(width);
                rasterization.setPageHeight(height);
            }

            PdfOptions pdfOptions = new PdfOptions();
            pdfOptions.setVectorRasterizationOptions(rasterization);
            image.save(output.toString(), pdfOptions);
        }
    }

    private String[] determineLayouts(Image image) {
        Set<String> names = new LinkedHashSet<>();
        names.add("Model");
        for (String name : extractLayoutsViaReflection(image)) {
            if (name != null && !name.isBlank()) {
                names.add(name);
            }
        }
        return names.toArray(String[]::new);
    }

    private List<String> extractLayoutsViaReflection(Image image) {
        List<String> layouts = new ArrayList<>();
        try {
            Method getLayouts = image.getClass().getMethod("getLayouts");
            Object layoutsObject = getLayouts.invoke(image);
            if (layoutsObject == null) {
                return layouts;
            }
            if (layoutsObject instanceof String[] stringArray) {
                for (String layout : stringArray) {
                    layouts.add(layout);
                }
                return layouts;
            }
            if (layoutsObject instanceof Iterable<?> iterable) {
                for (Object entry : iterable) {
                    String name = extractLayoutName(entry);
                    if (name != null) {
                        layouts.add(name);
                    }
                }
                return layouts;
            }
            Method toArray = layoutsObject.getClass().getMethod("toArray");
            Object asArray = toArray.invoke(layoutsObject);
            if (asArray instanceof Object[] objects) {
                for (Object entry : objects) {
                    String name = extractLayoutName(entry);
                    if (name != null) {
                        layouts.add(name);
                    }
                }
            }
        } catch (ReflectiveOperationException ignored) {
            // If the API differs, fall back to default Model layout.
        }
        return layouts;
    }

    private String extractLayoutName(Object layoutObject) {
        if (layoutObject == null) {
            return null;
        }
        for (String methodName : new String[]{"getLayoutName", "getName", "getKey"}) {
            try {
                Method method = layoutObject.getClass().getMethod(methodName);
                Object value = method.invoke(layoutObject);
                if (value instanceof String name) {
                    return name;
                }
            } catch (ReflectiveOperationException ignored) {
                // Try the next accessor name.
            }
        }
        return null;
    }
}
