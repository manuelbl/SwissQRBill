//
// Swiss QR Bill Generator
// Copyright (c) 2022 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//

package net.codecrete.qrbill.canvas;

import net.codecrete.qrbill.pdf.*;

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

    private static final double COLOR_SCALE = 1.0 / 255;
    private Document document;
    private ContentStream contentStream;
    private int lastStrokingColor;
    private int lastNonStrokingColor;
    private double lastLineWidth = 1;
    private boolean hasSavedGraphicsState;
    private Font lastFont;
    private double lastFontSize;
    private LineStyle lastLineStyle;

    /**
     * Initializes a new instance of the PDF canvas with the specified page size.
     * <p>
     * A PDF with a single page of the specified size will be created. The QR bill
     * will be drawn in the bottom left corner of the page.
     * </p>
     *
     * @param width  the page width, in mm.
     * @param height the page height, in mm.
     */
    public PDFCanvas(double width, double height) {
        setupFontMetrics("Helvetica");
        document = new Document("Swiss QR Bill");
        Page page = document.createPage(width * MM_TO_PT, height * MM_TO_PT);
        contentStream = page.getContents();
    }

    @Override
    public void setTransformation(double translateX, double translateY, double rotate, double scaleX, double scaleY) {
        translateX *= MM_TO_PT;
        translateY *= MM_TO_PT;

        if (hasSavedGraphicsState) {
            contentStream.restoreGraphicsState();
            lastStrokingColor = 0;
            lastNonStrokingColor = 0;
            lastLineWidth = 1;
        }

        lastFont = null;
        lastFontSize = 0;

        contentStream.saveGraphicsState();
        hasSavedGraphicsState = true;

        TransformationMatrix matrix = new TransformationMatrix();
        matrix.translate(translateX, translateY);
        matrix.rotate(rotate);
        matrix.scale(scaleX, scaleY);

        contentStream.transform(matrix.getElements());
    }

    private void setFont(boolean isBold, int fontSize) {
        Font font = isBold ? Font.HelveticaBold : Font.Helvetica;
        if (font == lastFont && fontSize == lastFontSize) {
            return;
        }

        contentStream.setFont(font, fontSize);
        lastFont = font;
        lastFontSize = fontSize;
    }

    @Override
    public void putText(String text, double x, double y, int fontSize, boolean isBold) {
        x *= MM_TO_PT;
        y *= MM_TO_PT;

        setFont(isBold, fontSize);

        contentStream.beginText();
        contentStream.newLineAtOffset(x, y);
        contentStream.showText(text);
        contentStream.endText();
    }

    @Override
    public void putTextLines(String[] lines, double x, double y, int fontSize, double leading) {
        x *= MM_TO_PT;
        y *= MM_TO_PT;
        double lineHeight = (fontMetrics.getLineHeight(fontSize) + leading) * MM_TO_PT;

        setFont(false, fontSize);

        contentStream.beginText();
        contentStream.newLineAtOffset(x, y);
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
    public void moveTo(double x, double y) {
        x *= MM_TO_PT;
        y *= MM_TO_PT;
        contentStream.moveTo(x, y);
    }

    @Override
    public void lineTo(double x, double y) {
        x *= MM_TO_PT;
        y *= MM_TO_PT;
        contentStream.lineTo(x, y);
    }

    @Override
    public void cubicCurveTo(double x1, double y1, double x2, double y2, double x, double y) {
        x1 *= MM_TO_PT;
        y1 *= MM_TO_PT;
        x2 *= MM_TO_PT;
        y2 *= MM_TO_PT;
        x *= MM_TO_PT;
        y *= MM_TO_PT;
        contentStream.curveTo(x1, y1, x2, y2, x, y);
    }

    @Override
    public void addRectangle(double x, double y, double width, double height) {
        x *= MM_TO_PT;
        y *= MM_TO_PT;
        width *= MM_TO_PT;
        height *= MM_TO_PT;
        contentStream.addRect(x, y, width, height);
    }

    @Override
    public void closeSubpath() {
        contentStream.closePath();
    }

    @Override
    public void fillPath(int color, boolean smoothing) {
        if (color != lastNonStrokingColor) {
            lastNonStrokingColor = color;
            double r = COLOR_SCALE * ((color >> 16) & 0xff);
            double g = COLOR_SCALE * ((color >> 8) & 0xff);
            double b = COLOR_SCALE * ((color >> 0) & 0xff);
            contentStream.setNonStrokingColor(r, g, b);
        }
        contentStream.fill();
    }

    @Override
    public void strokePath(double strokeWidth, int color, LineStyle lineStyle, boolean smoothing) {
        if (color != lastStrokingColor) {
            lastStrokingColor = color;
            double r = COLOR_SCALE * ((color >> 16) & 0xff);
            double g = COLOR_SCALE * ((color >> 8) & 0xff);
            double b = COLOR_SCALE * ((color >> 0) & 0xff);
            contentStream.setStrokingColor(r, g, b);
        }
        if (lineStyle != lastLineStyle || (lineStyle != LineStyle.Solid && strokeWidth != lastLineWidth)) {
            lastLineStyle = lineStyle;
            double[] pattern;
            switch (lineStyle) {
                case Dashed:
                    pattern = new double[]{4 * strokeWidth};
                    break;
                case Dotted:
                    pattern = new double[]{0, 3 * strokeWidth};
                    break;
                default:
                    pattern = new double[]{};
                    break;
            }
            contentStream.setLineCapStyle(lineStyle == LineStyle.Dotted ? 1 : 0);
            contentStream.setLineDashPattern(pattern, 0);
        }
        if (strokeWidth != lastLineWidth) {
            lastLineWidth = strokeWidth;
            contentStream.setLineWidth(strokeWidth);
        }
        contentStream.stroke();
    }

    /**
     * Gets the resulting graphics as a PDF document in a byte array.
     * <p>
     * The canvas can no longer be used for drawing after calling this method.
     * </p>
     *
     * @return the byte array containing the PDF document
     */
    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        document.save(buffer);
        close();
        return buffer.toByteArray();
    }

    /**
     * Writes the resulting graphics as a PDF document to the specified stream.
     * <p>
     * The canvas can no longer be used for drawing after calling this method.
     * </p>
     *
     * @param stream the output stream to write to
     */
    public void writeTo(OutputStream stream) throws IOException {
        document.save(stream);
        close();
    }

    /**
     * Writes the resulting graphics as a PDF document to the specified file path.
     * <p>
     * The canvas can no longer be used for drawing after calling this method.
     * </p>
     *
     * @param path the path (file name) to write to.
     */
    public void saveAs(Path path) throws IOException {
        try (OutputStream os = Files.newOutputStream(path)) {
            document.save(os);
        }
        close();
    }

    @Override
    public void close() {
        contentStream = null;
        document = null;
    }
}
