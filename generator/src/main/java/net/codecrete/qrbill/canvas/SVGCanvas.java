//
// Swiss QR Bill Generator
// Copyright (c) 2018 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
package net.codecrete.qrbill.canvas;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class SVGCanvas extends AbstractCanvas {

    private ByteArrayOutputStream buffer;
    private Writer stream;
    private boolean isInGroup;
    private boolean isFirstMoveInPath;
    private double lastPositionX;
    private double lastPositionY;
    private int approxPathLength;

    /**
     * Creates a new instance
     */
    public SVGCanvas() {
        // no further initialization needed here
    }

    @Override
    public void setupPage(double width, double height) throws IOException {
        buffer = new ByteArrayOutputStream();
        stream = new OutputStreamWriter(buffer, StandardCharsets.UTF_8);
        stream.write("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n"
                + "<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 1.1//EN\" \"http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd\">\n"
                + "<svg width=\"");
        stream.write(formatNumber(width));
        stream.write("mm\" height=\"");
        stream.write(formatNumber(height));
        stream.write("mm\" version=\"1.1\" viewBox=\"0 0 ");
        stream.write(formatCoordinate(width));
        stream.write(" ");
        stream.write(formatCoordinate(height));
        stream.write("\" xmlns=\"http://www.w3.org/2000/svg\">\n");
        stream.write("<g font-family=\"Helvetica,Arial\" transform=\"translate(0 ");
        stream.write(formatCoordinate(height));
        stream.write(")\">\n");
        stream.write("<title>Swiss QR Bill</title>\n");
    }

    @Override
    public void close() throws IOException {
        if (isInGroup) {
            stream.write("</g>\n");
            isInGroup = false;
        }
        if (stream != null) {
            stream.write("</g>\n");
            stream.write("</svg>\n");
            stream.close();
            stream = null;
        }
    }

    @Override
    public void startPath() throws IOException {
        stream.write("<path d=\"");
        isFirstMoveInPath = true;
        approxPathLength = 0;
    }

    @Override
    public void moveTo(double x, double y) throws IOException {
        y = -y;
        if (isFirstMoveInPath) {
            stream.write("M");
            stream.write(formatCoordinate(x));
            stream.write(",");
            stream.write(formatCoordinate(y));
            isFirstMoveInPath = false;
        } else {
            addPathNewlines(16);
            stream.write("m");
            stream.write(formatCoordinate(x - lastPositionX));
            stream.write(",");
            stream.write(formatCoordinate(y - lastPositionY));
        }
        lastPositionX = x;
        lastPositionY = y;
        approxPathLength += 16;
    }

    @Override
    public void lineTo(double x, double y) throws IOException {
        y = -y;
        addPathNewlines(16);
        stream.write("l");
        stream.write(formatCoordinate(x - lastPositionX));
        stream.write(",");
        stream.write(formatCoordinate(y - lastPositionY));
        lastPositionX = x;
        lastPositionY = y;
        approxPathLength += 16;
    }

    @Override
    public void cubicCurveTo(double x1, double y1, double x2, double y2, double x, double y) throws IOException {
        y1 = -y1;
        y2 = -y2;
        y = -y;
        addPathNewlines(48);
        stream.write("c");
        stream.write(formatCoordinate(x1 - lastPositionX));
        stream.write(",");
        stream.write(formatCoordinate(y1 - lastPositionY));
        stream.write(",");
        stream.write(formatCoordinate(x2 - lastPositionX));
        stream.write(",");
        stream.write(formatCoordinate(y2 - lastPositionY));
        stream.write(",");
        stream.write(formatCoordinate(x - lastPositionX));
        stream.write(",");
        stream.write(formatCoordinate(y - lastPositionY));
        lastPositionX = x;
        lastPositionY = y;
        approxPathLength += 48;
    }

    @Override
    public void addRectangle(double x, double y, double width, double height) throws IOException {
        addPathNewlines(40);
        moveTo(x, y + height);
        stream.write("h");
        stream.write(formatCoordinate(width));
        stream.write("v");
        stream.write(formatCoordinate(height));
        stream.write("h");
        stream.write(formatCoordinate(-width));
        stream.write("z");
        approxPathLength += 24;
    }

    @Override
    public void closeSubpath() throws IOException {
        addPathNewlines(1);
        stream.write("z");
        approxPathLength += 1;
    }

    private void addPathNewlines(int expectedLength) throws IOException {
        if (approxPathLength + expectedLength > 255) {
            stream.write("\n");
            approxPathLength = 0;
        }
    }

    @Override
    public void fillPath(int color) throws IOException {
        stream.write("\" fill=\"#");
        stream.write(formatColor(color));
        stream.write("\"/>\n");
        isFirstMoveInPath = true;
    }

    @Override
    public void strokePath(double strokeWidth, int color) throws IOException {
        stream.write("\" stroke=\"#");
        stream.write(formatColor(color));
        if (strokeWidth != 1) {
            stream.write("\" stroke-width=\"");
            stream.write(formatNumber(strokeWidth));
        }
        stream.write("\" fill=\"none\"/>\n");
        isFirstMoveInPath = true;
    }

    @Override
    public void putText(String text, double x, double y, int fontSize, boolean isBold) throws IOException {
        y = -y;
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
        stream.write("</text>\n");
    }

    @Override
    public void setTransformation(double translateX, double translateY, double scaleX, double scaleY) throws IOException {
        if (isInGroup) {
            stream.write("</g>\n");
            isInGroup = false;
        }
        if (translateX != 0 || translateY != 0 || scaleX != 1 || scaleY != 1) {
            stream.write("<g transform=\"translate(");
            stream.write(formatCoordinate(translateX));
            stream.write(" ");
            stream.write(formatCoordinate(-translateY));
            if (scaleX != 1 || scaleY != 1) {
                stream.write(") scale(");
                stream.write(formatNumber(scaleX));
                if (scaleX != scaleY) {
                    stream.write(" ");
                    stream.write(formatNumber(scaleY));
                }
            }
            stream.write(")\">\n");
            isInGroup = true;
        }
    }

    @Override
    public byte[] getResult() throws IOException {
        close();
        return buffer.toByteArray();
    }

    private static final DecimalFormat NUMBER_FORMAT = new DecimalFormat("#.###", new DecimalFormatSymbols(Locale.UK));

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
