//
// Swiss QR Bill Generator
// Copyright (c) 2017 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
package net.codecrete.qrbill.generator;

import java.io.Closeable;
import java.io.IOException;

/**
 * Common interface implemented by graphics generators
 */
public interface GraphicsGenerator extends Closeable {

    /**
     * Sets a translation and a scale factor for the subsequent operations
     * @param translateX translation in x direction (in mm)
     * @param translateY translation in y direction (in mm)
     * @param scale scale fator (1.0 = no scaling)
     * @throws IOException thrown if the graphics cannot be generated
     */
    void setTransformation(double translateX, double translateY, double scale) throws IOException;

    /**
     * Adds text to the graphics.
     * @param text the text
     * @param x x position of the text's start (in mm)
     * @param y y position of the text's top (in mm)
     * @param fontSize the font size (in pt)
     * @param isBold indicates if the text is in bold or regular weight
     * @throws IOException thrown if the graphics cannot be generated
     */
    void putText(String text, double x, double y, int fontSize, boolean isBold) throws IOException;

    /**
     * Adds several lines of text to the graphics.
     * <p>
     *     The specified text is automatically broken into several lines if a text line
     *     would exceed the specified maximum length of a text line. Newlines can be
     *     used to force a line break.
     * </p>
     * @param text the text
     * @param x x position of the text's start (in mm)
     * @param y y position of the text's top (in mm)
     * @param maxWidth maximum length of text line / width of text block (in mm)
     * @param fontSize the font size (in pt)
     * @throws IOException thrown if the graphics cannot be generated
     * @return the number of text lines that were added
     */
    int putMultilineText(String text, double x, double y, double maxWidth, int fontSize) throws IOException;

    /**
     * Adds several lines of text to the graphics.
     * @param lines the text lines
     * @param x x position of the text's start (in mm)
     * @param y y position of the text's top (in mm)
     * @param fontSize the font size (in pt)
     * @throws IOException thrown if the graphics cannot be generated
     */
    void putTextLines(String[] lines, double x, double y, int fontSize) throws IOException;

    /**
     * Starts a path that can be filled or stroked
     * @throws IOException thrown if the graphics cannot be generated
     */
    void startPath() throws IOException;

    /**
     * Moves the current point of the open path to the specified position.
     * @param x x-coordinate of position
     * @param y y-coordinate of position
     * @throws IOException thrown if the graphics cannot be generated
     */
    void moveTo(double x, double y) throws IOException;

    /**
     * Adds a line segment to the open path from the previous point to the speicifed position.
     * @param x x-coordinate of position
     * @param y y-coordinate of position
     * @throws IOException thrown if the graphics cannot be generated
     */
    void lineTo(double x, double y) throws IOException;

    /**
     * Adds a rectangle to the path
     * @param x the rectangle's left position (in mm)
     * @param y the rectangle's top position (in mm)
     * @param width the rectangle's width (in mm)
     * @param height rectangle's height (in mm)
     * @throws IOException thrown if the graphics cannot be generated
     */
    void addRectangle(double x, double y, double width, double height) throws IOException;

    /**
     * Fills the current path and ends it
     * @param color the fill color (expressed similar to HTML, e.g. 0xffffff for white)
     * @throws IOException thrown if the graphics cannot be generated
     */
    void fillPath(int color) throws IOException;

    /**
     * Strokes the current path and ends it
     * @param strokeWidth the stroke width (in pt)
     * @param color the stroke color (expressed similar to HTML, e.g. 0xffffff for white)
     * @throws IOException thrown if the graphics cannot be generated
     */
    void strokePath(double strokeWidth, int color) throws IOException;

    /**
     * Returns the generated graphics as a byte array
     * @return the byte array
     * @throws IOException thrown if the graphics cannot be generated
     */
    byte[] getResult() throws IOException;
}
