//
// Swiss QR Bill Generator
// Copyright (c) 2017 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
package net.codecrete.qrbill.generator;

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
 */
public class PDFGenerator implements GraphicsGenerator {

    private static final double MM_TO_PT = 72 / 25.4;

    private PDDocument document;
    private PDPageContentStream contentStream;
    private float pageHeight;
    private int lastStrokingColor = 0;
    private int lastNonStrokingColor = 0;
    private double lastLineWidth = 1;
    private boolean hasSavedGraphicsState = false;


    /**
     * Creates a new instance of the graphics generator
     */
    public PDFGenerator() {
    }

    @Override
    public void setupPage(double width, double height) throws IOException
    {
        document = new PDDocument();
        pageHeight = (float)(height * MM_TO_PT);
        PDPage page = new PDPage(new PDRectangle((float) (width * MM_TO_PT), pageHeight));
        document.addPage(page);
        contentStream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.OVERWRITE, true);
    }

    @Override
    public void setTransformation(double translateX, double translateY, double scale) throws IOException {
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
        matrix.translate((float)translateX, (float)(pageHeight - translateY));
        matrix.scale((float)scale, (float)scale);
        contentStream.transform(matrix);
    }

    @Override
    public void putText(String text, double x, double y, int fontSize, boolean isBold) throws IOException {
        x *= MM_TO_PT;
        y *= -MM_TO_PT;
        y -= FontMetrics.getAscender(fontSize) * MM_TO_PT;
        contentStream.setFont(isBold ? PDType1Font.HELVETICA_BOLD : PDType1Font.HELVETICA, fontSize);
        contentStream.beginText();
        contentStream.newLineAtOffset((float)x, (float)y);
        contentStream.showText(text);
        contentStream.endText();
    }

    @Override
    public int putMultilineText(String text, double x, double y, double maxWidth, int fontSize) throws IOException {
        String[] lines = FontMetrics.splitLines(text, maxWidth * MM_TO_PT, fontSize);
        putTextLines(lines, x, y, fontSize);
        return lines.length;
    }

    @Override
    public void putTextLines(String[] lines, double x, double y, int fontSize) throws IOException {
        y *= -MM_TO_PT;
        y -= FontMetrics.getAscender(fontSize) * MM_TO_PT;
        contentStream.setFont(PDType1Font.HELVETICA, fontSize);
        contentStream.beginText();
        contentStream.newLineAtOffset((float)x, (float)y);
        boolean isFirstLine = true;
        for (String line : lines) {
            if (isFirstLine) {
                isFirstLine = false;
            } else {
                contentStream.newLineAtOffset(0, (float)(-FontMetrics.getLineHeight(fontSize) * MM_TO_PT));
            }
            contentStream.showText(line);
        }
        contentStream.endText();
    }

    @Override
    public void startPath() throws IOException {
        // path is start implicitly
    }

    @Override
    public void moveTo(double x, double y) throws IOException {
        x *= MM_TO_PT;
        y *= -MM_TO_PT;
        contentStream.moveTo((float)x, (float)y);
    }

    @Override
    public void lineTo(double x, double y) throws IOException {
        x *= MM_TO_PT;
        y *= -MM_TO_PT;
        contentStream.lineTo((float)x, (float)y);
    }

    @Override
    public void addRectangle(double x, double y, double width, double height) throws IOException {
        x *= MM_TO_PT;
        y *= -MM_TO_PT;
        width *= MM_TO_PT;
        height *= -MM_TO_PT;
        contentStream.addRect((float)x, (float)y, (float)width, (float)height);
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
            contentStream.setLineWidth((float)(strokeWidth));
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
