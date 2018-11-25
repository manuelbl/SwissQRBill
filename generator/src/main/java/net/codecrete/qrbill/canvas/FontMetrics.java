//
// Swiss QR Bill Generator
// Copyright (c) 2017 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
package net.codecrete.qrbill.canvas;

import java.util.ArrayList;

/**
 * Simple font metrics class, independent of graphics subsystems and
 * installed fonts.
 *
 * <p>
 * It supports Helvetica, Arial, Frutiger, Liberation Sans. Kerning and ligatures not supported.
 * </p>
 */
public class FontMetrics {

    private static final double PT_TO_MM = 25.4 / 72;

    private final char[] charWidth_20_7F;
    private final char[] charWidth_A0_FF;
    private final char charDefaultWidth;
    private final FontMetrics boldMetrics;

    public FontMetrics() {
        this(CharWidthData.HELVETICA_NORMAL_20_7F, CharWidthData.HELVATICA_NORMAL_A0_FF,
                CharWidthData.HELVETICA_NORMAL_DEFAULT_WIDTH,
                new FontMetrics(CharWidthData.HELVETICA_BOLD_20_7F, CharWidthData.HELVATICA_BOLD_A0_FF,
                        CharWidthData.HELVETICA_BOLD_DEFAULT_WIDTH, null));
    }

    private FontMetrics(char[] charWidth_20_7F, char[] charWidth_A0_FF, char charDefaultWidth, FontMetrics boldMetrics) {
        this.charWidth_20_7F = charWidth_20_7F;
        this.charWidth_A0_FF = charWidth_A0_FF;
        this.charDefaultWidth = charDefaultWidth;
        this.boldMetrics = boldMetrics;
    }

    /**
     * Distance between baseline and top of highest letter.
     * 
     * @param fontSize the font size (in pt)
     * @return the distance (in mm)
     */
    public double getAscender(int fontSize) {
        return fontSize * 0.8 * PT_TO_MM;
    }

    /**
     * Distance between baseline and bottom of letter extending the farest below the
     * baseline.
     * 
     * @param fontSize the font size (in pt)
     * @return the distance (in mm)
     */
    public double getDescender(int fontSize) {
        return fontSize * 0.2 * PT_TO_MM;
    }

    /**
     * Distance between the baselines of two consecutive text lines.
     * 
     * @param fontSize the font size (in pt)
     * @return the distance (in mm)
     */
    public double getLineHeight(int fontSize) {
        return fontSize * PT_TO_MM;
    }

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
    public String[] splitLines(String text, double maxLength, int fontSize) {

        /* Yes, this code has a cognitive complexity of 37. Deal with it. */

        ArrayList<String> lines = new ArrayList<>();
        int max = (int) (maxLength * 1000 / fontSize);

        int len = text.length(); // length of line
        int pos = 0; // current position (0 ..< end)
        int lineStartPos = 0; // start position of current line
        int lineWidth = 0; // current line width (in AFM metric)
        boolean addEmptyLine = true; // flag if an empty line should be added as the last line

        // iterate over all characters
        while (pos < len) {

            // get current character
            char ch = text.charAt(pos);

            // skip leading white space at start of current line
            if (ch == ' ' && pos == lineStartPos) {
                lineStartPos++;
                pos++;
                continue;
            }

            // add width of character
            lineWidth += getCharWidth(ch);
            addEmptyLine = false;

            // line break is need if the maximum width has been reached
            // or if an explicit line break has been encountered
            if (ch == '\n' || lineWidth > max) {

                // find the position for the line break
                int breakPos;
                if (ch == '\n') {
                    breakPos = pos;

                } else {
                    // locate the previous space on the line
                    int spacePos = pos - 1;
                    while (spacePos > lineStartPos) {
                        if (text.charAt(spacePos) == ' ')
                            break;
                        spacePos--;
                    }

                    // if space was found, it's the break position
                    if (spacePos > lineStartPos) {
                        breakPos = spacePos;

                    } else {
                        // if no space was found, forcibly break word
                        if (pos > lineStartPos)
                            breakPos = pos;
                        else
                            breakPos = lineStartPos + 1; // at least one character
                    }
                }

                // add line to result
                addResultLine(lines, text, lineStartPos, breakPos);

                // setup start of new line
                lineStartPos = breakPos;
                if (ch == '\n') {
                    lineStartPos = breakPos + 1;
                    addEmptyLine = true;
                }
                pos = lineStartPos;
                lineWidth = 0;

            } else {
                // no line break needed; progress one character
                pos++;
            }
        }

        // complete the last line
        if (pos > lineStartPos) {
            addResultLine(lines, text, lineStartPos, pos);
        } else if (addEmptyLine) {
            lines.add("");
        }

        return lines.toArray(new String[0]);
    }

    /**
     * Add the specified text range to the resulting lines.
     * <p>
     * Trim trailing white space
     * </p>
     * 
     * @param lines resulting lines array
     * @param text  text
     * @param start start of text range (including)
     * @param end   end of text range (excluding)
     */
    private static void addResultLine(ArrayList<String> lines, String text, int start, int end) {
        while (end > start && text.charAt(end - 1) == ' ')
            end--;
        lines.add(text.substring(start, end));
    }

    /**
     * Returns the width of the specified text for the specified font size
     * @param text text
     * @param fontSize font size (in pt)
     * @param isBold   indicates if the text is in bold or regular weight
     * @return width (in mm)
     */
    public double getTextWidth(CharSequence text, int fontSize, boolean isBold) {
        if (isBold)
            return boldMetrics.getTextWidth(text, fontSize, false);

        double width = 0;
        int len = text.length();
        for (int i = 0; i < len; i++)
            width += getCharWidth(text.charAt(i));
        return width * fontSize / 1000 * PT_TO_MM;
    }

    /**
     * Returns the width of the specified character.
     * <p>
     * The width is given in 0.0001 pt for a font size of 1 pt. So to get the
     * effective width in pt (1/72 in), it must be multiplied with the font size and
     * divided by 1000.
     * </p>
     * <p>
     * The method only supports characters as defined in "Swiss Implementation
     * Guidelines for Credit Transfer Initiation". For all other characters, a
     * default width is returned.
     * </p>
     * 
     * @param ch the character
     * @return the width of the character
     */
    private double getCharWidth(char ch) {
        char width = 0;
        if (ch >= 0x20 && ch <= 0x7f)
            width = charWidth_20_7F[ch - 0x20];
        else if (ch >= 0xa0 && ch <= 0xff) {
            width = charWidth_A0_FF[ch - 0xa0];
        }
        if (width == 0)
            width = charDefaultWidth;
        return width;
    }

}
