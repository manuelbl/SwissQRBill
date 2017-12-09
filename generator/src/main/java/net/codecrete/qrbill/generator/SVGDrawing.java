//
// Swiss QR Bill Generator
// Copyright (c) 2017 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
package net.codecrete.qrbill.generator;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.Locale;

public class SVGDrawing implements GraphicsGenerator {

    private static final double MM_TO_PT = 72 / 25.4;

    private ByteArrayOutputStream buffer;
    private Writer stream;
    private boolean isInGroup = false;

    public SVGDrawing(double width, double height) throws IOException {
        buffer = new ByteArrayOutputStream();
        stream = new OutputStreamWriter(buffer, StandardCharsets.UTF_8);
        stream.write(
                "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\r\n" +
                "<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 1.1//EN\" \"http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd\">\r\n" +
                "<svg width=\"");
        stream.write(formatNumber(width));
        stream.write("mm\" height=\"");
        stream.write(formatNumber(height));
        stream.write("mm\" version=\"1.1\" viewBox=\"0 0 ");
        stream.write(formatCoordinate(width));
        stream.write(" ");
        stream.write(formatCoordinate(height));
        stream.write("\" xmlns=\"http://www.w3.org/2000/svg\">\r\n");
        stream.write("<g font-family=\"Helvetica,Arial\">\r\n");
    }

    public void close() throws IOException {
        if (isInGroup) {
            stream.write("</g>\r\n");
            isInGroup = false;
        }
        if (stream != null) {
            stream.write("</g>\r\n");
            stream.write("</svg>\r\n");
            stream.close();
            stream = null;
        }
    }

    public void startPath() throws IOException {
        stream.write("<path d=\"");
    }

    public void moveTo(double x, double y) throws IOException {
        stream.write("M");
        stream.write(formatCoordinate(x));
        stream.write(",");
        stream.write(formatCoordinate(y));
        stream.write(" ");
    }

    public void lineTo(double x, double y) throws IOException {
        stream.write("L");
        stream.write(formatCoordinate(x));
        stream.write(",");
        stream.write(formatCoordinate(y));
        stream.write(" ");
    }

    public void addRectangle(double x, double y, double width, double height) throws IOException {
        stream.write("M");
        stream.write(formatCoordinate(x));
        stream.write(",");
        stream.write(formatCoordinate(y));
        stream.write("h");
        stream.write(formatCoordinate(width));
        stream.write("v");
        stream.write(formatCoordinate(height));
        stream.write("h");
        stream.write(formatCoordinate(-width));
        stream.write("z ");
    }

    public void fillPath(int color) throws IOException {
        stream.write("\" fill=\"#");
        stream.write(formatColor(color));
        stream.write("\"/>\r\n");
    }

    public void strokePath(double strokeWidth, int color) throws IOException {
        stream.write("\" stroke=\"#");
        stream.write(formatColor(color));
        if (strokeWidth != 1) {
            stream.write("\" stroke-width=\"");
            stream.write(formatNumber(strokeWidth));
        }
        stream.write("\" fill=\"none\"/>\r\n");
    }

    public void putText(String text, double x, double y, int fontSize, boolean isBold) throws IOException {
        y += FontMetrics.getAscender(fontSize);
        stream.write("<text x=\"");
        stream.write(formatCoordinate(x));
        stream.write("\" y=\"");
        stream.write(formatCoordinate(y));
        stream.write("\" font-size=\"");
        stream.write(formatNumber(fontSize));
        if (isBold)
            stream.write("\" font-weight=\"bold");
        stream.write("\">");
        stream.write(escapeXML(text));
        stream.write("</text>\r\n");
    }

    public int putMultilineText(String text, double x, double y, double maxWidth, int fontSize) throws IOException {
        String[] lines = FontMetrics.splitLines(text, maxWidth * MM_TO_PT, fontSize);
        for (String line : lines) {
            putText(line, x, y, fontSize, false);
            y += FontMetrics.getLineHeight(fontSize);
        }
        return lines.length;
    }

    public void setTransformation(double translateX, double translateY, double scale) throws IOException {
        if (isInGroup) {
            stream.write("</g>\r\n");
            isInGroup = false;
        }
        if (translateX != 0 || translateY != 0 || scale != 1) {
            stream.write("<g transform=\"translate(");
            stream.write(formatCoordinate(translateX));
            stream.write(" ");
            stream.write(formatCoordinate(translateY));
            if (scale != 1) {
                stream.write(") scale(");
                stream.write(formatNumber(scale));
            }
            stream.write(")\">\r\n");
            isInGroup = true;
        }
    }

    public byte[] getResult() throws IOException {
        close();
        return buffer.toByteArray();
    }

    private static final DecimalFormat NUMBER_FORMAT = new DecimalFormat("#.###");

    private static String formatNumber(double value) {
        return NUMBER_FORMAT.format(value);
    }

    private static String formatCoordinate(double value) {
        return NUMBER_FORMAT.format(value * MM_TO_PT);
    }

    private static String formatColor(int color) {
        return String.format(Locale.US, "%06x", color);
    }

    private static String escapeXML(String text) {
        int length = text.length();
        int lastCopiedPosition = 0;
        StringBuilder result = null;
        for (int i = 0; i < length; i++) {
            char ch = text.charAt(i);
            if (ch == '<' || ch == '>' || ch == '&' || ch == '\'' || ch == '"') {
                if (result == null)
                    result = new StringBuilder(length + 10);
                if (i > lastCopiedPosition)
                    result.append(text, lastCopiedPosition, i);
                String entity;
                switch (ch) {
                    case '<':
                        entity = "&lt;";
                        break;
                    case '>':
                        entity = "&gt;";
                        break;
                    case '&':
                        entity = "&amp;";
                        break;
                    case '\'':
                        entity = "&apos;";
                        break;
                    default:
                        entity = "&quot;";
                }
                result.append(entity);
                lastCopiedPosition = i + 1;
            }
        }

        if (result == null)
            return text;
        if (length > lastCopiedPosition)
            result.append(text, lastCopiedPosition, length);
        return result.toString();
    }
}
