//
// Swiss QR Bill Generator
// Copyright (c) 2018 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
package net.codecrete.qrbill.canvas;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Canvas for drawing to a Graphics2D instance.
 */
public class Graphics2DCanvas extends AbstractCanvas {

    private Graphics2D graphics;
    private boolean ownsGraphics;
    private float offsetX;
    private float offsetY;
    private Path2D.Double currentPath;
    private float coordinateScale;
    private float fontScale;

    /**
     * Creates a new instance.
     * <p>
     * Before drawing to the canvas, {@link #initGraphics(Graphics2D, boolean, float)} must be called
     * to initialize it.
     * </p>
     *
     * @param fontFamily a list of font family names, separated by comma (same syntax as for CSS). The first installed font family will be used.
     */
    public Graphics2DCanvas(String fontFamily) {
        setupFontMetrics(findFontFamily(fontFamily));
    }

    /**
     * Creates a new instance for the given graphics context.
     * <p>
     * The offset is specified in the graphics context's coordinate system. Positive y coordinates point downwards.
     * </p>
     * <p>
     * The graphics context is neither owned nor disposed.
     * </p>
     *
     * @param graphics graphics context
     * @param offsetX the x-offset to the bottom left corner of the drawing area, in the graphics context's coordinate system.
     * @param offsetY the y-offset to the bottom left corner of the drawing area, in the graphics context's coordinate system.
     * @param scale the conversion factor from mm to the drawing surface coordinate system.
     * @param fontFamily a list of font family names, separated by comma (same syntax as for CSS). The first installed font family will be used.
     */
    public Graphics2DCanvas(Graphics2D graphics, float offsetX, float offsetY, float scale, String fontFamily) {
        setupFontMetrics(fontFamily);
        setOffset(offsetX, offsetY);
        initGraphics(graphics, false, scale);
    }

    /**
     * Initializes the canvas with the given graphics context.
     *
     * @param graphics graphics context
     * @param ownsGraphics if {@code true}, this instance will own the graphics surface and dispose it on closing
     * @param scale the conversion factor from mm to the drawing surface coordinate system.
     */
    protected void initGraphics(Graphics2D graphics, boolean ownsGraphics, float scale) {
        this.graphics = graphics;
        this.ownsGraphics = ownsGraphics;
        coordinateScale = scale;
        fontScale = (float)(scale * 25.4 / 72.0);

        // enable high quality output
        graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);


        // initialize transformation
        setTransformation(0, 0, 0, 1, 1);
    }

    /**
     * Sets the offset to the bottom left corner of the drawing area.
     * <p>
     * This method must be called before calling {@link #initGraphics(Graphics2D, boolean, float)}.
     * </p>
     *
     * @param x the x-offset to the bottom left corner of the drawing area, in the graphics context's coordinate system.
     * @param y the y-offset to the bottom left corner of the drawing area, in the graphics context's coordinate system.
     */
    protected void setOffset(float x, float y) {
        offsetX = x;
        offsetY = y;
    }

    @Override
    public void close() {
        if (ownsGraphics) {
            graphics.dispose();
            graphics = null;
        }
    }

    /**
     * Find the first family that's actually installed.
     * <p>
     * If none of the specified font families is installed, the input parameter is returned unchanged.
     * </p>
     *
     * @param fontFamilyList list of font families, separated by commas
     * @return the font family name of the first installed font
     */
    private String findFontFamily(String fontFamilyList) {
        for (String family : splitCommaSeparated(fontFamilyList)) {
            Font font = new Font(family, Font.PLAIN, 12);
            if (font.getFamily().toLowerCase(Locale.US).contains(family.toLowerCase(Locale.US)))
                return family;
        }
        return fontFamilyList;
    }

    private static final Pattern QUOTED_SPLITTER = Pattern.compile("(?:^|,)(\"[^\"]+\"|[^,]*)");

    /**
     * Splits a comma separated list into its elements.
     * <p>
     * Elements can be quoted if they contain commas.
     * </p>
     *
     * @param input comma separated string
     * @return list of strings
     */
    private static java.util.List<String> splitCommaSeparated(String input) {
        List<String> result = new ArrayList<>();
        Matcher matcher = QUOTED_SPLITTER.matcher(input);
        while (matcher.find()) {
            String match = matcher.group(1).trim();
            if (match.charAt(0) == '"' && match.charAt(match.length() - 1) == '"')
                match = match.substring(1, match.length() - 1);
            result.add(match);
        }
        return result;
    }

    @Override
    public void setTransformation(double translateX, double translateY, double rotate, double scaleX, double scaleY) {
        // Our coordinate system extends from the bottom up. Java Graphics2D's system
        // extends from the top down. So Y coordinates need to be treated specially.
        translateX *= coordinateScale;
        translateY *= coordinateScale;
        AffineTransform at = new AffineTransform();
        at.translate(offsetX + translateX, offsetY - translateY);
        if (rotate != 0)
            at.rotate(-rotate);
        if (scaleX != 1 || scaleY != 1)
            at.scale(scaleX, scaleY);
        graphics.setTransform(at);
    }

    @Override
    public void putText(String text, double x, double y, int fontSize, boolean isBold) {
        x *= coordinateScale;
        y *= -coordinateScale;
        graphics.setColor(new Color(0));
        Font font = new Font(fontMetrics.getFirstFontFamily(), isBold ? Font.BOLD : Font.PLAIN, (int) (fontSize * fontScale + 0.5));
        graphics.setFont(font);
        graphics.drawString(text, (float) x, (float) y);
    }

    @Override
    public void startPath() {
        currentPath = new Path2D.Double(Path2D.WIND_NON_ZERO);
    }

    @Override
    public void moveTo(double x, double y) {
        x *= coordinateScale;
        y *= -coordinateScale;
        currentPath.moveTo(x, y);
    }

    @Override
    public void lineTo(double x, double y) {
        x *= coordinateScale;
        y *= -coordinateScale;
        currentPath.lineTo(x, y);
    }

    @Override
    public void cubicCurveTo(double x1, double y1, double x2, double y2, double x, double y) {
        x1 *= coordinateScale;
        y1 *= -coordinateScale;
        x2 *= coordinateScale;
        y2 *= -coordinateScale;
        x *= coordinateScale;
        y *= -coordinateScale;
        currentPath.curveTo(x1, y1, x2, y2, x, y);
    }

    @Override
    public void addRectangle(double x, double y, double width, double height) {
        x *= coordinateScale;
        y *= -coordinateScale;
        width *= coordinateScale;
        height *= -coordinateScale;
        currentPath.moveTo(x, y);
        currentPath.lineTo(x, y + height);
        currentPath.lineTo(x + width, y + height);
        currentPath.lineTo(x + width, y);
        currentPath.closePath();
    }

    @Override
    public void closeSubpath() {
        currentPath.closePath();
    }

    @Override
    public void fillPath(int color, boolean smoothing) {
        if (!smoothing) {
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
            graphics.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
        }
        graphics.setColor(new Color(color));
        graphics.fill(currentPath);
        if (!smoothing) {
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            graphics.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        }
    }

    @Override
    public void strokePath(double strokeWidth, int color, LineStyle lineStyle, boolean smoothing) {
        graphics.setColor(new Color(color));
        BasicStroke stroke;
        switch (lineStyle) {
            case Dashed:
                stroke = new BasicStroke((float) (strokeWidth * fontScale), BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER,
                        10, new float[] { 4 * (float) strokeWidth * fontScale }, 0);
                break;
            case Dotted:
                stroke = new BasicStroke((float) (strokeWidth * fontScale), BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER,
                        10, new float[] { 0, 3 * (float) strokeWidth * fontScale }, 0);
                break;
            default:
                stroke = new BasicStroke((float) (strokeWidth * fontScale), BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);
        }
        graphics.setStroke(stroke);
        if (!smoothing) {
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
            graphics.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
        }

        graphics.draw(currentPath);

        if (!smoothing) {
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            graphics.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        }
    }
}
