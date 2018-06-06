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
 * Common graphics interface for drawing
 * <p>
 * The coordinate system is initialized by {@code setupPage}. It's origin is
 * initially in the bottom left corner of the pages and extends in x-direction
 * to the right and in y-direction to the top.
 * </p>
 * <p>
 * All text has to be in the font Helvetica. Arial should also do as the font
 * metrics are similar enough and advanced text placing such a justified text is
 * not used.
 * </p>
 * <p>
 * A canvas may only be used to generate a single page. After the result has
 * been retrieved, the instance must not be used anymore.
 * </p>
 */
public interface Canvas extends Closeable {

    /**
     * Sets up the page
     * <p>
     * The page (and graphics context) is not valid until this method has been
     * called.
     * </p>
     * 
     * @param width  width of page (in mm)
     * @param height height of page (in mm)
     * @throws IOException thrown if graphics cannot be generated
     */
    void setupPage(double width, double height) throws IOException;

    /**
     * Sets a translation and a scale factor for the subsequent operations
     * <p>
     * Before a new translation is applied, the coordinate system is reset to it's
     * original state after page setup (see {@code setupPage}).
     * </p>
     * 
     * @param translateX translation in x direction (in mm)
     * @param translateY translation in y direction (in mm)
     * @param scale      scale fator (1.0 = no scaling)
     * @throws IOException thrown if the graphics cannot be generated
     */
    void setTransformation(double translateX, double translateY, double scale) throws IOException;

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
     * Adds a line segment to the open path from the previous point to the speicifed
     * position.
     * 
     * @param x x-coordinate of position
     * @param y y-coordinate of position
     * @throws IOException thrown if the graphics cannot be generated
     */
    void lineTo(double x, double y) throws IOException;

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
     * Fills the current path and ends it
     * 
     * @param color the fill color (expressed similar to HTML, e.g. 0xffffff for
     *              white)
     * @throws IOException thrown if the graphics cannot be generated
     */
    void fillPath(int color) throws IOException;

    /**
     * Strokes the current path and ends it
     * 
     * @param strokeWidth the stroke width (in pt)
     * @param color       the stroke color (expressed similar to HTML, e.g. 0xffffff
     *                    for white)
     * @throws IOException thrown if the graphics cannot be generated
     */
    void strokePath(double strokeWidth, int color) throws IOException;

    /**
     * Returns the generated graphics as a byte array
     * <p>
     * After this method was called, the page is no longer valid.
     * </p>
     * 
     * @return the byte array
     * @throws IOException thrown if the graphics cannot be generated
     */
    byte[] getResult() throws IOException;
}
