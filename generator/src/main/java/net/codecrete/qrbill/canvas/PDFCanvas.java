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

/**
 * PDF graphics generator
 * <p>
 *     The PDF generator currently only supports the Helvetica font.
 * </p>
 */
public class PDFCanvas extends AbstractCanvas {

    private PDDocument document;
    private PDPageContentStream contentStream;
    private int lastStrokingColor = 0;
    private int lastNonStrokingColor = 0;
    private double lastLineWidth = 1;
    private boolean hasSavedGraphicsState = false;

    @Override
    public void setupPage(double width, double height, String fontFamilyList) throws IOException {
        setupFontMetrics("Helvetica");
        document = new PDDocument();
        document.getDocumentInformation().setTitle("Swiss QR Bill");
        PDPage page = new PDPage(new PDRectangle((float) (width * MM_TO_PT), (float) (height * MM_TO_PT)));
        document.addPage(page);
        contentStream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.OVERWRITE, true);
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
            contentStream.setNonStrokingColor(r, g, b);
        }
        contentStream.fill();
    }

    @Override
    public void strokePath(double strokeWidth, int color) throws IOException {
        if (color != lastStrokingColor) {
            lastStrokingColor = color;
            int r = (color >> 16) & 0xff;
            int g = (color >> 8) & 0xff;
            int b = (color >> 8) & 0xff;
            contentStream.setStrokingColor(r, g, b);
        }
        if (strokeWidth != lastLineWidth) {
            lastLineWidth = strokeWidth;
            contentStream.setLineWidth((float) (strokeWidth));
        }
        contentStream.stroke();
    }

    @Override
    public byte[] getResult() throws IOException {
        if (contentStream != null) {
            contentStream.close();
            contentStream = null;
        }
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            document.save(os);
            return os.toByteArray();
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
