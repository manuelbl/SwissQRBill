//
// Swiss QR Bill Generator
// Copyright (c) 2017 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
package net.codecrete.qrbill.canvas;

import java.io.Closeable;
import java.io.IOException;

/**
 * Common interface for all output formats to draw the QR bill.
 * <p>
 * The coordinate system has its origin in the bottom left corner.
 * The y-axis extends from the bottom to the top.
 * </p>
 * <p>
 * The graphics model is similar to the one used by PDF, in particular with
 * regards to the orientation of the y axis, the concept of a current path,
 * and using the baseline for positioning text.
 * </p>
 * <p>
 * Instance of this class are expected to use a single font family for
 * the QR bill (regular and bold font weight).
 * </p>
 */
public interface Canvas extends Closeable {

    /**
     * Line style
     */
    enum LineStyle {
        /**
         * Solid line
         */
        Solid,
        /** 
         * Dashed line (dashes are about 4 times the line width long and apart)
         */
        Dashed,
        /**
         * Dotted line (dots are spaced 3 times the line width apart)
         */
        Dotted
    }

    /**
     * Sets a translation, rotation and scaling for the subsequent operations
     * <p>
     * Before a new translation is applied, the coordinate system is reset to it's
     * original state.
     * </p>
     * <p>
     * The transformations are applied in the order translation, rotation, scaling.
     * </p>
     *
     * @param translateX translation in x direction (in mm)
     * @param translateY translation in y direction (in mm)
     * @param rotate     rotation angle, in radians
     * @param scaleX     scale factor in x direction (1.0 = no scaling)
     * @param scaleY     scale factor in y direction (1.0 = no scaling)
     * @throws IOException thrown if the graphics cannot be generated
     */
    void setTransformation(double translateX, double translateY, double rotate, double scaleX, double scaleY) throws IOException;

    /**
     * Adds text to the graphics.
     * <p>
     * The text position refers to the left most point on the text's baseline.
     * </p>
     *
     * @param text     the text
     * @param x        x position of the text's start (in mm)
     * @param y        y position of the text's top (in mm)
     * @param fontSize the font size (in pt)
     * @param isBold   indicates if the text is in bold or regular weight
     * @throws IOException thrown if the graphics cannot be generated
     */
    void putText(String text, double x, double y, int fontSize, boolean isBold) throws IOException;

    /**
     * Adds several lines of text to the graphics.
     * <p>
     * The text position refers to the left most point on the baseline of the first
     * text line. Additional lines then follow below.
     * </p>
     *
     * @param lines    the text lines
     * @param x        x position of the text's start (in mm)
     * @param y        y position of the text's top (in mm)
     * @param fontSize the font size (in pt)
     * @param leading  additional vertical space between text lines (in mm)
     * @throws IOException thrown if the graphics cannot be generated
     */
    void putTextLines(String[] lines, double x, double y, int fontSize, double leading) throws IOException;

    /**
     * Starts a path that can be filled or stroked
     *
     * @throws IOException thrown if the graphics cannot be generated
     */
    void startPath() throws IOException;

    /**
     * Moves the current point of the open path to the specified position.
     *
     * @param x x-coordinate of position
     * @param y y-coordinate of position
     * @throws IOException thrown if the graphics cannot be generated
     */
    void moveTo(double x, double y) throws IOException;

    /**
     * Adds a line segment to the open path from the previous point to the specified
     * position.
     *
     * @param x x-coordinate of position
     * @param y y-coordinate of position
     * @throws IOException thrown if the graphics cannot be generated
     */
    void lineTo(double x, double y) throws IOException;

    /**
     * Adds a cubic Beziér curve to the open path going from the previous point to the specified
     * position. Two control points control the curve
     *
     * @param x1 x-coordinate of first control point
     * @param y1 y-coordinate of first control point
     * @param x2 x-coordinate of second control point
     * @param y2 y-coordinate of second control point
     * @param x  x-coordinate of position
     * @param y  y-coordinate of position
     * @throws IOException thrown if the graphics cannot be generated
     */
    void cubicCurveTo(double x1, double y1, double x2, double y2, double x, double y) throws IOException;

    /**
     * Adds a rectangle to the path
     *
     * @param x      the rectangle's left position (in mm)
     * @param y      the rectangle's top position (in mm)
     * @param width  the rectangle's width (in mm)
     * @param height rectangle's height (in mm)
     * @throws IOException thrown if the graphics cannot be generated
     */
    void addRectangle(double x, double y, double width, double height) throws IOException;

    /**
     * Closes the current subpath
     *
     * @throws IOException thrown if the graphics cannot be generated
     */
    void closeSubpath() throws IOException;

    /**
     * Fills the current path and ends it
     *
     * @param color the fill color (expressed similar to HTML, e.g. 0xffffff for white)
     * @param smoothing {@code true} for using smoothing techniques such as antialiasing, {@code false} otherwise
     * @throws IOException thrown if the graphics cannot be generated
     */
    void fillPath(int color, boolean smoothing) throws IOException;

    /**
     * Strokes the current path and ends it
     *
     * @param strokeWidth the stroke width (in pt)
     * @param color       the stroke color (expressed similar to HTML, e.g. 0xffffff
     *                    for white)
     * @param lineStyle   the line style
     * @param smoothing {@code true} for using smoothing techniques such as antialiasing, {@code false} otherwise
     * @throws IOException thrown if the graphics cannot be generated
     */
    void strokePath(double strokeWidth, int color, LineStyle lineStyle, boolean smoothing) throws IOException;

    /**
     * Distance between baseline and top of the highest letter.
     *
     * @param fontSize the font size (in pt)
     * @return the distance (in mm)
     */
    double getAscender(int fontSize);

    /**
     * Distance between baseline and bottom of letter extending the farthest below the
     * baseline.
     *
     * @param fontSize the font size (in pt)
     * @return the distance (in mm)
     */
    double getDescender(int fontSize);

    /**
     * Distance between the baselines of two consecutive text lines.
     *
     * @param fontSize the font size (in pt)
     * @return the distance (in mm)
     */
    double getLineHeight(int fontSize);

    /**
     * Returns the width of the specified text for the specified font size
     *
     * @param text     text
     * @param fontSize font size (in pt)
     * @param isBold   indicates if the text is in bold or regular weight
     * @return width (in mm)
     */
    double getTextWidth(CharSequence text, int fontSize, boolean isBold);

    /**
     * Splits the text into lines.
     * <p>
     * If a line would exceed the specified maximum length, line breaks are
     * inserted. Newlines are treated as fixed line breaks.
     * </p>
     *
     * @param text      the text
     * @param maxLength the maximum line length (in pt)
     * @param fontSize  the font size (in pt)
     * @return an array of text lines
     */
    String[] splitLines(String text, double maxLength, int fontSize);
}
