package com.example.asposetopdf.converters;

import com.aspose.cells.*;
import com.example.asposetopdf.detect.FileType;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Locale;
import java.text.DecimalFormatSymbols;

/**
 * Converter for XLSX spreadsheets.
 */
public class XlsxConverter extends BaseConverter {
    static double CELL_PADDING = 0.1;

    @Override
    public FileType getFileType() {
        return FileType.XLSX;
    }

    @Override
    public void convert(Path input, Path output) throws Exception {
        ensureParentDirectory(output);
        Workbook workbook = new Workbook(input.toString());
        workbook.getSettings().setLocale(Locale.US);
        workbook.getSettings().setNumberDecimalSeparator('.');
        workbook.getSettings().setNumberGroupSeparator(',');
        workbook.calculateFormula();
        PdfSaveOptions pdfOptions = new PdfSaveOptions();
        pdfOptions.setGridlineType(GridlineType.HAIR);
        pdfOptions.setGridlineColor(Color.getLightGray());
        pdfOptions.setExportDocumentStructure(true);
        pdfOptions.setDisplayDocTitle(true);
        modifyPageSetup(workbook);
        sanitizeCharts(workbook);
        addWorksheetBookmarks(workbook, pdfOptions);
        workbook.save(output.toString(), pdfOptions);
    }

    private void addWorksheetBookmarks(Workbook workbook, PdfSaveOptions options) {
        PdfBookmarkEntry root = new PdfBookmarkEntry();
        root.setText("Worksheets");

        ArrayList<PdfBookmarkEntry> entries = new ArrayList<>();
        for (int i = 0; i < workbook.getWorksheets().getCount(); i++) {
            Worksheet sheet = workbook.getWorksheets().get(i);
            Range displayRange = sheet.getCells().getMaxDisplayRange();
            if (displayRange == null || displayRange.getRowCount() == 0 || displayRange.getColumnCount() == 0) {
                continue;
            }

            PdfBookmarkEntry entry = new PdfBookmarkEntry();
            entry.setText(sheet.getName());
            entry.setDestination(sheet.getCells().get(displayRange.getFirstRow(), displayRange.getFirstColumn()));
            entries.add(entry);
        }

        if (entries.isEmpty()) {
            return;
        }

        root.setDestination(entries.get(0).getDestination());
        root.setSubEntry(entries);
        options.setBookmark(root);
    }


    private void sanitizeCharts(Workbook workbook) {
        WorksheetCollection worksheets = workbook.getWorksheets();
        for (int sheetIndex = 0; sheetIndex < worksheets.getCount(); sheetIndex++) {
            Worksheet sheet = worksheets.get(sheetIndex);
            ChartCollection charts = sheet.getCharts();
            for (int chartIndex = 0; chartIndex < charts.getCount(); chartIndex++) {
                Chart chart = charts.get(chartIndex);
                if (chart.getType() == ChartType.LINE_WITH_DATA_MARKERS && chart.getValueAxis() != null) {
                    TickLabels labels = chart.getValueAxis().getTickLabels();
                    if (labels != null) {
                        labels.setNumberFormatLinked(false);
                        labels.setNumberFormat("0.00E+00");
                    }
                }
            }
        }
    }

    private void modifyPageSetup(Workbook workbook) {
        for (int i = 0; i < workbook.getWorksheets().getCount(); i++) {
            Worksheet ws = workbook.getWorksheets().get(i);
            Cells cells = ws.getCells();
            Range used = cells.getMaxDisplayRange();
            if (used == null || used.getRowCount() == 0 || used.getColumnCount() == 0) {
                System.out.printf("Worksheet '%s' has no visible cells; skipping.\n", ws.getName());
                continue;
            }
            int fc = used.getFirstColumn();
            int lc = fc + used.getColumnCount() - 1;
            int fr = used.getFirstRow();
            int lr = fr + used.getRowCount() - 1;
            System.out.printf("Worksheet '%s' has this displayRange Cols: %d-%d, Rows: %d-%d\n", ws.getName(), fc, lc, fr, lr);

            PageSetup pageSetup = ws.getPageSetup();
            
            // Disable "fit to page" scaling; keep 100% zoom
            pageSetup.setPercentScale(true);
            pageSetup.setZoom(100);
            pageSetup.setFitToPagesWide(0);
            pageSetup.setFitToPagesTall(0);

            // Remove old page breaks
            ws.getHorizontalPageBreaks().clear();
            ws.getVerticalPageBreaks().clear();

            // Change margins
            pageSetup.setLeftMarginInch(0.15);
            pageSetup.setRightMarginInch(0.15);
            pageSetup.setTopMarginInch(0.15);
            pageSetup.setBottomMarginInch(0.15);
            pageSetup.setHeaderMarginInch(0.1);
            pageSetup.setFooterMarginInch(0.1);

            // Add grid lines
            pageSetup.setPrintGridlines(true);
            pageSetup.setCenterHorizontally(true);
            pageSetup.setCenterVertically(true);

            // Set what to print, for predictable results
            pageSetup.setPrintArea(
                CellsHelper.cellIndexToName(fr, fc) + ":" +
                CellsHelper.cellIndexToName(lr, lc)
            );

            // Sum width/height of the used range in **inches**
            double widthIn = 0.0;
            for (int c = fc; c <= lc; c++) {
                widthIn += cells.getColumnWidth(c, true, CellsUnitType.INCH) + CELL_PADDING;
            }

            double heightIn = 0.0;
            for (int r = fr; r <= lr; r++) {
                heightIn += cells.getRowHeight(r, /*isOriginal*/true, CellsUnitType.INCH);
            }

            // Add margins, header/footer space, and set a custom paper size (units are inches)
            double leftMargin = pageSetup.getLeftMarginInch();
            double rightMargin = pageSetup.getRightMarginInch();
            double topMargin = pageSetup.getTopMarginInch();
            double bottomMargin = pageSetup.getBottomMarginInch();
            double headerMargin = pageSetup.getHeaderMarginInch();
            double footerMargin = pageSetup.getFooterMarginInch();

            double w = widthIn + leftMargin + rightMargin;
            double h = heightIn + topMargin + bottomMargin + headerMargin + footerMargin;

            double widthPadding = 0.25;
            double heightPadding = 0.15;
            w += widthPadding;
            h += heightPadding;

            pageSetup.setHeader(1, String.format("&C&\"Arial,Bold\"&12%s", ws.getName()));
            System.out.printf("Setting paper size to %.2fx%.2f inches%n", w, h);
            pageSetup.customPaperSize(w, h);  // this is the key
        }

    }
}
