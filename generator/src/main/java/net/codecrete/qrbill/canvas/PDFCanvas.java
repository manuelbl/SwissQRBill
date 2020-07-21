//
// Swiss QR Bill Generator
// Copyright (c) 2017 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
package net.codecrete.qrbill.canvas;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.util.Matrix;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Canvas for generating PDF files.
 * <p>
 * The PDF generator currently only supports the Helvetica font.
 * </p>
 */
public class PDFCanvas extends AbstractCanvas implements ByteArrayResult {

    /**
     * Add the QR bill on the last page of the PDF document.
     */
    public static final int LAST_PAGE = -1;

    /**
     * Add the QR bill on a new page at the end of the PDF document.
     */
    public static final int NEW_PAGE_AT_END = -2;

    private static final String PDF_FONT = "Helvetica";


    private PDDocument document;
    private PDPageContentStream contentStream;
    private int lastStrokingColor = 0;
    private int lastNonStrokingColor = 0;
    private double lastLineWidth = 1;
    private LineStyle lastLineStyle = LineStyle.Solid;
    private boolean hasSavedGraphicsState = false;

    /**
     * Creates a new instance using the specified page size.
     * @param width page width, in mm
     * @param height page height, in mm
     * @throws IOException thrown if the creation fails
     */
    public PDFCanvas(double width, double height) throws IOException {
        setupFontMetrics(PDF_FONT);
        document = new PDDocument();
        document.getDocumentInformation().setTitle("Swiss QR Bill");
        PDPage page = new PDPage(new PDRectangle((float) (width * MM_TO_PT), (float) (height * MM_TO_PT)));
        document.addPage(page);
        contentStream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.OVERWRITE, true);
    }

    /**
     * Creates a new instance for adding the QR bill to an exiting PDF document.
     * <p>
     *     The QR bill can either be added to an existing page by specifying the page number
     *     of an existing page (or {@link #LAST_PAGE}), or it can be added to a new page
     *     at the end of the document (see {@link #NEW_PAGE_AT_END}).
     * </p>
     * <p>
     *     The created instance assumes that the page for the QR bill has A4 format and
     *     will add the QR bill at the bottom of the page.
     * </p>
     * @param path path to exiting document
     * @param pageNo the zero-based number of the page the QR bill should be added to
     * @throws IOException thrown if the creation fails
     */
    public PDFCanvas(Path path, int pageNo) throws IOException {
        setupFontMetrics(PDF_FONT);
        document = PDDocument.load(path.toFile());
        preparePage(pageNo);
    }

    /**
     * Creates a new instance for adding the QR bill to an exiting PDF document.
     * <p>
     *     The QR bill can either be added to an existing page by specifying the page number
     *     of an existing page (or {@link #LAST_PAGE}), or it can be added to a new page
     *     at the end of the document (see {@link #NEW_PAGE_AT_END}).
     * </p>
     * <p>
     *     The created instance assumes that the page for the QR bill has A4 format and
     *     will add the QR bill at the bottom of the page.
     * </p>
     * @param pdfDocument binary array contianing PDF document
     * @param pageNo the zero-based number of the page the QR bill should be added to
     * @throws IOException thrown if the creation fails
     */
    public PDFCanvas(byte[] pdfDocument, int pageNo) throws IOException {
        setupFontMetrics(PDF_FONT);
        document = PDDocument.load(pdfDocument);
        preparePage(pageNo);
    }

    private void preparePage(int pageNo) throws IOException {
        if (pageNo == NEW_PAGE_AT_END) {
            PDPage page = new PDPage(new PDRectangle((float) (210 * MM_TO_PT), (float) (297 * MM_TO_PT)));
            document.addPage(page);
            contentStream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.OVERWRITE, true, true);
        } else {
            if (pageNo == LAST_PAGE)
                pageNo = document.getNumberOfPages() - 1;
            PDPage page = document.getPage(pageNo);
            contentStream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true, true);
        }
    }

    @Override
    public void setTransformation(double translateX, double translateY, double rotate, double scaleX, double scaleY) throws IOException {
        translateX *= MM_TO_PT;
        translateY *= MM_TO_PT;

        if (hasSavedGraphicsState) {
            contentStream.restoreGraphicsState();
            lastStrokingColor = 0;
            lastNonStrokingColor = 0;
            lastLineWidth = 1;
        }

        contentStream.saveGraphicsState();
        hasSavedGraphicsState = true;
        Matrix matrix = new Matrix();
        matrix.translate((float) translateX, (float) translateY);
        if (rotate != 0)
            matrix.rotate(rotate);
        if (scaleX != 1 || scaleY != 1)
            matrix.scale((float) scaleX, (float) scaleY);
        contentStream.transform(matrix);
    }

    @Override
    public void putText(String text, double x, double y, int fontSize, boolean isBold) throws IOException {
        x *= MM_TO_PT;
        y *= MM_TO_PT;
        contentStream.setFont(isBold ? PDType1Font.HELVETICA_BOLD : PDType1Font.HELVETICA, fontSize);
        contentStream.beginText();
        contentStream.newLineAtOffset((float) x, (float) y);
        contentStream.showText(text);
        contentStream.endText();
    }

    @Override
    public void putTextLines(String[] lines, double x, double y, int fontSize, double leading) throws IOException {
        x *= MM_TO_PT;
        y *= MM_TO_PT;
        float lineHeight = (float) ((fontMetrics.getLineHeight(fontSize) + leading) * MM_TO_PT);
        contentStream.setFont(PDType1Font.HELVETICA, fontSize);
        contentStream.beginText();
        contentStream.newLineAtOffset((float) x, (float) y);
        boolean isFirstLine = true;
        for (String line : lines) {
            if (isFirstLine) {
                isFirstLine = false;
            } else {
                contentStream.newLineAtOffset(0, -lineHeight);
            }
            contentStream.showText(line);
        }
        contentStream.endText();
    }

    @Override
    public void startPath() {
        // path is start implicitly
    }

    @Override
    public void moveTo(double x, double y) throws IOException {
        x *= MM_TO_PT;
        y *= MM_TO_PT;
        contentStream.moveTo((float) x, (float) y);
    }

    @Override
    public void lineTo(double x, double y) throws IOException {
        x *= MM_TO_PT;
        y *= MM_TO_PT;
        contentStream.lineTo((float) x, (float) y);
    }

    @Override
    public void cubicCurveTo(double x1, double y1, double x2, double y2, double x, double y) throws IOException {
        x1 *= MM_TO_PT;
        y1 *= MM_TO_PT;
        x2 *= MM_TO_PT;
        y2 *= MM_TO_PT;
        x *= MM_TO_PT;
        y *= MM_TO_PT;
        contentStream.curveTo((float) x1, (float) y1, (float) x2, (float) y2, (float) x, (float) y);
    }

    @Override
    public void addRectangle(double x, double y, double width, double height) throws IOException {
        x *= MM_TO_PT;
        y *= MM_TO_PT;
        width *= MM_TO_PT;
        height *= MM_TO_PT;
        contentStream.addRect((float) x, (float) y, (float) width, (float) height);
    }

    @Override
    public void closeSubpath() throws IOException {
        contentStream.closePath();
    }

    @Override
    public void fillPath(int color) throws IOException {
        if (color != lastNonStrokingColor) {
            lastNonStrokingColor = color;
            int r = (color >> 16) & 0xff;
            int g = (color >> 8) & 0xff;
            int b = (color >> 8) & 0xff;
            contentStream.setNonStrokingColor(r / 255f, g / 255f, b / 255f);
        }
        contentStream.fill();
    }

    @Override
    public void strokePath(double strokeWidth, int color) throws IOException {
        strokePath(strokeWidth, color, LineStyle.Solid);
    }

    @Override
    public void strokePath(double strokeWidth, int color, LineStyle lineStyle) throws IOException {
        if (color != lastStrokingColor) {
            lastStrokingColor = color;
            int r = (color >> 16) & 0xff;
            int g = (color >> 8) & 0xff;
            int b = (color >> 8) & 0xff;
            contentStream.setStrokingColor(r / 255f, g / 255f, b / 255f);
        }
        if (lineStyle != lastLineStyle || (lineStyle != LineStyle.Solid && strokeWidth != lastLineWidth)) {
            lastLineStyle = lineStyle;
            float[] pattern;
            switch (lineStyle) {
                case Dashed:
                    pattern = new float[] { 4 * (float)strokeWidth };
                    break;
                case Dotted:
                    pattern = new float[] { 0, 3 * (float)strokeWidth };
                    break;
                default:
                    pattern = new float[] { };
            }
            contentStream.setLineCapStyle(lineStyle == LineStyle.Dotted ? 1 : 0);
            contentStream.setLineDashPattern(pattern, 0);
        }
        if (strokeWidth != lastLineWidth) {
            lastLineWidth = strokeWidth;
            contentStream.setLineWidth((float) (strokeWidth));
        }
        contentStream.stroke();
    }

    @Override
    public byte[] toByteArray() throws IOException {
        if (contentStream != null) {
            contentStream.close();
            contentStream = null;
        }
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            document.save(os);
            return os.toByteArray();
        }
    }

    /**
     * Writes the resulting PDF document to the specified output stream.
     * @param os the output stream
     * @throws IOException thrown if the image cannot be written
     */
    public void writeTo(OutputStream os) throws IOException {
        if (contentStream != null) {
            contentStream.close();
            contentStream = null;
        }
        document.save(os);
    }

    /**
     * Saves the resulting PDF document to the specified path.
     * @param path the path to write to
     * @throws IOException thrown if the image cannot be written
     */
    public void saveAs(Path path) throws IOException {
        if (contentStream != null) {
            contentStream.close();
            contentStream = null;
        }

        try (OutputStream os = Files.newOutputStream(path)) {
            document.save(os);
        }
    }

    @Override
    public void close() throws IOException {

        if (contentStream != null) {
            contentStream.close();
            contentStream = null;
        }
        if (document != null) {
            document.close();
            document = null;
        }
    }
}
