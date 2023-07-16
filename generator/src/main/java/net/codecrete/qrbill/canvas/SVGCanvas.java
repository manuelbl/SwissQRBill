//
// Swiss QR Bill Generator
// Copyright (c) 2018 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
package net.codecrete.qrbill.canvas;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * Canvas for generating SVG files.
 */
public class SVGCanvas extends AbstractCanvas implements ByteArrayResult {

    private final ByteArrayOutputStream buffer;
    private Writer stream;
    private boolean isInGroup;
    private boolean isFirstMoveInPath;
    private double lastPositionX;
    private double lastPositionY;
    private StringBuilder path;
    private int approxPathLength;
    private final DecimalFormat numberFormat = new DecimalFormat("#.###", new DecimalFormatSymbols(Locale.UK));

    private final DecimalFormat angleFormat = new DecimalFormat("#.#####", new DecimalFormatSymbols(Locale.UK));

    /**
     * Creates a new instance of the specified size.
     * <p>
     * For all text, the specified font family list will be used.
     * </p>
     *
     * @param width          width of image, in mm
     * @param height         height of image, in mm
     * @param fontFamilyList font family list (comma separated list, CSS syntax)
     * @throws IOException thrown if the instance cannot be created
     */
    public SVGCanvas(double width, double height, String fontFamilyList) throws IOException {
        setupFontMetrics(fontFamilyList);

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
        stream.write("<g font-family=\"");
        stream.write(escapeXML(fontMetrics.getFontFamilyList()));
        stream.write("\" transform=\"translate(0 ");
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
        path = new StringBuilder();
        isFirstMoveInPath = true;
        approxPathLength = 0;
    }

    @Override
    public void moveTo(double x, double y) throws IOException {
        y = -y;
        if (isFirstMoveInPath) {
            path.append("M");
            path.append(formatCoordinate(x));
            path.append(",");
            path.append(formatCoordinate(y));
            isFirstMoveInPath = false;
        } else {
            addPathNewlines(16);
            path.append("m");
            path.append(formatCoordinate(x - lastPositionX));
            path.append(",");
            path.append(formatCoordinate(y - lastPositionY));
        }
        lastPositionX = x;
        lastPositionY = y;
        approxPathLength += 16;
    }

    @Override
    public void lineTo(double x, double y) throws IOException {
        y = -y;
        addPathNewlines(16);
        path.append("l");
        path.append(formatCoordinate(x - lastPositionX));
        path.append(",");
        path.append(formatCoordinate(y - lastPositionY));
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
        path.append("c");
        path.append(formatCoordinate(x1 - lastPositionX));
        path.append(",");
        path.append(formatCoordinate(y1 - lastPositionY));
        path.append(",");
        path.append(formatCoordinate(x2 - lastPositionX));
        path.append(",");
        path.append(formatCoordinate(y2 - lastPositionY));
        path.append(",");
        path.append(formatCoordinate(x - lastPositionX));
        path.append(",");
        path.append(formatCoordinate(y - lastPositionY));
        lastPositionX = x;
        lastPositionY = y;
        approxPathLength += 48;
    }

    @Override
    public void addRectangle(double x, double y, double width, double height) throws IOException {
        addPathNewlines(40);
        moveTo(x, y + height);
        path.append("h");
        path.append(formatCoordinate(width));
        path.append("v");
        path.append(formatCoordinate(height));
        path.append("h");
        path.append(formatCoordinate(-width));
        path.append("z");
        approxPathLength += 24;
    }

    @Override
    public void closeSubpath() throws IOException {
        addPathNewlines(1);
        path.append("z");
        approxPathLength += 1;
    }

    private void addPathNewlines(int expectedLength) {
        if (approxPathLength + expectedLength > 255) {
            path.append("\n");
            approxPathLength = 0;
        }
    }

    @Override
    public void fillPath(int color, boolean smoothing) throws IOException {
        stream.write("<path fill=\"#");
        stream.write(formatColor(color));
        if (!smoothing)
            stream.write("\" shape-rendering=\"crispEdges");
        stream.write("\"\nd=\"");
        stream.append(path);
        stream.write("\"/>\n");
        path = null;
        isFirstMoveInPath = true;
    }

    @Override
    public void strokePath(double strokeWidth, int color, LineStyle lineStyle, boolean smoothing) throws IOException {
        stream.write("<path stroke=\"#");
        stream.write(formatColor(color));
        if (strokeWidth != 1) {
            stream.write("\" stroke-width=\"");
            stream.write(formatNumber(strokeWidth));
        }
        if (lineStyle == LineStyle.Dashed) {
            stream.write("\" stroke-dasharray=\"");
            stream.write(formatNumber(strokeWidth * 4));
        } else if (lineStyle == LineStyle.Dotted) {
            stream.write("\" stroke-linecap=\"round\" stroke-dasharray=\"0 ");
            stream.write(formatNumber(strokeWidth * 3));
        }
        if (!smoothing)
            stream.write("\" shape-rendering=\"crispEdges");
        stream.write("\" fill=\"none\"\nd=\"");
        stream.append(path);
        stream.write("\"/>\n");
        path = null;
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
    public void setTransformation(double translateX, double translateY, double rotate, double scaleX, double scaleY) throws IOException {
        if (isInGroup) {
            stream.write("</g>\n");
            isInGroup = false;
        }
        if (translateX != 0 || translateY != 0 || scaleX != 1 || scaleY != 1) {
            stream.write("<g transform=\"translate(");
            stream.write(formatCoordinate(translateX));
            stream.write(" ");
            stream.write(formatCoordinate(-translateY));
            if (rotate != 0) {
                stream.write(") rotate(");
                stream.write(angleFormat.format(-rotate / Math.PI * 180));
            }
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
    public byte[] toByteArray() throws IOException {
        close();
        return buffer.toByteArray();
    }

    /**
     * Writes the resulting SVG image to the specified output stream.
     *
     * @param os the output stream
     * @throws IOException thrown if the image cannot be written
     */
    public void writeTo(OutputStream os) throws IOException {
        close();
        buffer.writeTo(os);
    }

    /**
     * Saves the resulting SVG image to the specified path.
     *
     * @param path the path to write to
     * @throws IOException thrown if the image cannot be written
     */
    public void saveAs(Path path) throws IOException {
        close();
        try (OutputStream os = Files.newOutputStream(path)) {
            buffer.writeTo(os);
        }
    }

    private String formatNumber(double value) {
        return numberFormat.format(value);
    }

    private String formatCoordinate(double value) {
        return numberFormat.format(value * MM_TO_PT);
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
