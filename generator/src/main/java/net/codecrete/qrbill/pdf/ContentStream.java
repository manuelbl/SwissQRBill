//
// Swiss QR Bill Generator
// Copyright (c) 2022 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//

package net.codecrete.qrbill.pdf;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.DeflaterOutputStream;

/**
 * Stream of PDF commands (contents of a page).
 */
public class ContentStream implements Writable {

    private final ByteArrayOutputStream buffer;
    private final DocumentWriter contentWriter;
    private final GeneralDict dict;
    private final ResourceDict resources;

    public ContentStream(ResourceDict resources) {
        this.resources = resources;
        buffer = new ByteArrayOutputStream();
        DeflaterOutputStream deflateStream = new DeflaterOutputStream(buffer);
        contentWriter = new DocumentWriter(deflateStream);
        dict = new GeneralDict();
    }

    /**
     * Saves the graphics state.
     */
    public void saveGraphicsState() {
        writeOperator("q");
    }

    /**
     * Restores the graphics state.
     */
    public void restoreGraphicsState() {
        writeOperator("Q");
    }

    /**
     * Sets the transfomation matrix.
     *
     * @param matrix The transformation matrix.
     */
    public void transform(double[] matrix) {
        for (double d : matrix) {
            writeOperand(d);
        }

        writeOperator("cm");
    }

    /**
     * Sets the stroking color.
     *
     * @param red   Red color component (between 0.0 and 1.0)
     * @param green Green color component (between 0.0 and 1.0)
     * @param blue  Blue Color component (between 0.0 and 1.0)
     */
    public void setStrokingColor(double red, double green, double blue) {
        writeOperand(red);
        writeOperand(green);
        writeOperand(blue);
        writeOperator("RG");
    }

    /**
     * Sets the non-stroking color.
     *
     * @param red   Red color component (between 0.0 and 1.0)
     * @param green Green color component (between 0.0 and 1.0)
     * @param blue  Blue Color component (between 0.0 and 1.0)
     */
    public void setNonStrokingColor(double red, double green, double blue) {
        writeOperand(red);
        writeOperand(green);
        writeOperand(blue);
        writeOperator("rg");
    }

    /**
     * Sets the line width.
     *
     * @param width Line width, in point.
     */
    public void setLineWidth(double width) {
        writeOperand(width);
        writeOperator("w");
    }

    /**
     * Sets the line cap style.
     *
     * @param style Line cap style (see PDF reference).
     */
    public void setLineCapStyle(int style) {
        writeOperand(style);
        writeOperator("J");
    }

    /**
     * Sets the line dash pattern.
     *
     * @param pattern Array of on and off length.
     * @param offset  Offset to first on element.
     */
    public void setLineDashPattern(double[] pattern, double offset) {
        writeOperand(pattern);
        writeOperand(offset);
        writeOperator("d");
    }

    /**
     * Moves the current point of the current path.
     *
     * @param x x-coordinate
     * @param y y-coordinate
     */
    public void moveTo(double x, double y) {
        writeOperand(x);
        writeOperand(y);
        writeOperator("m");
    }

    /**
     * Adds a straight line to the current path.
     *
     * @param x x-coordinate
     * @param y y-coordinate
     */
    public void lineTo(double x, double y) {
        writeOperand(x);
        writeOperand(y);
        writeOperator("l");
    }

    /**
     * Adds a Bézier curve to the current path.
     *
     * @param x1 x-coordinate of control point 1
     * @param y1 y-coordinate of control point 1
     * @param x2 x-coordinate of control point 2
     * @param y2 y-coordinate of control point 2
     * @param x  x-coordinate
     * @param y  y-coordinate
     */
    public void curveTo(double x1, double y1, double x2, double y2, double x, double y) {
        writeOperand(x1);
        writeOperand(y1);
        writeOperand(x2);
        writeOperand(y2);
        writeOperand(x);
        writeOperand(y);
        writeOperator("c");
    }

    /**
     * Adds a closed rectangle to the current path.
     *
     * @param x      x-coordinate
     * @param y      y-coordinate
     * @param width  The width.
     * @param height The height.
     */
    public void addRect(double x, double y, double width, double height) {
        writeOperand(x);
        writeOperand(y);
        writeOperand(width);
        writeOperand(height);
        writeOperator("re");
    }

    /**
     * Closes the current path.
     */
    public void closePath() {
        writeOperator("h");
    }

    /**
     * Stores the current path.
     */
    public void stroke() {
        writeOperator("S");
    }

    /**
     * Fills the current path using the non-zero winding rule.
     */
    public void fill() {
        writeOperator("f");
    }

    /**
     * Sets the current font.
     *
     * @param font     the font.
     * @param fontSize The font size.
     */
    public void setFont(Font font, double fontSize) {
        Name fontName = resources.addFont(font);
        writeOperand(fontName);
        writeOperand(fontSize);
        writeOperator("Tf");
    }

    /**
     * Begins a text object.
     */
    public void beginText() {
        writeOperator("BT");
    }

    /**
     * Ends a text object.
     */
    public void endText() {
        writeOperator("ET");
    }

    /**
     * Moves to the next line, offset by the specified distance from the current one.
     *
     * @param tx x-distance
     * @param ty y-distance
     */
    public void newLineAtOffset(double tx, double ty) {
        writeOperand(tx);
        writeOperand(ty);
        writeOperator("Td");
    }

    /**
     * Add the specified text to the curren text object.
     *
     * @param text The text.
     */
    public void showText(String text) {
        writeTextOperand(text);
        writeOperator("Tj");
    }

    private void writeOperand(int val) {
        try {
            contentWriter.write(Integer.toString(val));
            contentWriter.write(" ");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void writeOperand(double val) {
        try {
            if (Math.abs(val) < 0.0005) {
                contentWriter.write("0 ");
            } else {
                contentWriter.write(val);
                contentWriter.write(" ");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void writeOperand(double[] array) {
        try {
            contentWriter.write("[");
            for (double val : array)
                writeOperand(val);

            contentWriter.write("] ");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void writeOperand(Name name) {
        try {
            contentWriter.write("/");
            contentWriter.write(name.getValue());
            contentWriter.write(" ");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void writeTextOperand(String text) {
        try {
            contentWriter.writeString(text);
            contentWriter.write(" ");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void writeOperator(String oper) {
        try {
            contentWriter.write(oper);
            contentWriter.write("\n");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void write(DocumentWriter writer) throws IOException {
        contentWriter.close();

        dict.add("Length", buffer.size());
        dict.add("Filter", new Name("FlateDecode"));
        dict.write(writer);

        writer.write("stream\r\n");

        writer.write(buffer.toByteArray());
        writer.write("\r\nendstream\n");
    }
}
        