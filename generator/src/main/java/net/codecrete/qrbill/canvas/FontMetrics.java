//
// Swiss QR Bill Generator
// Copyright (c) 2017 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
package net.codecrete.qrbill.canvas;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Simple font metrics class, independent of graphics subsystems and
 * installed fonts.
 *
 * <p>
 * It supports Helvetica, Arial, Frutiger, Liberation Sans. Kerning and ligatures are not supported.
 * </p>
 */
public class FontMetrics {

    private static final double PT_TO_MM = 25.4 / 72;

    private final String fontFamilyList;
    private final String firstFontFamily;
    private final char[] charWidthx20x7E;
    private final char[] charWidthxA0x17F;
    private final char[] charWidthx218x21B;
    private final char charDefaultWidth;
    private final char charNDashWidth;
    private final char charEuroWidth;
    private final FontMetrics boldMetrics;

    /**
     * Creates a new instance for the first supported font in the specified list.
     *
     * @param fontFamilyList list of font families
     */
    public FontMetrics(String fontFamilyList) {
        this.fontFamilyList = fontFamilyList;
        firstFontFamily = getFirstFontFamily(fontFamilyList);
        String family = firstFontFamily.toLowerCase(Locale.US);

        final char[] boldCharWidthx20x7F;
        final char[] boldCharWidthxA0x17F;
        final char[] boldCharWidthx218x21B;
        final char boldCharDefaultWidth;
        final char boldCharNDashWidth;
        final char boldCharEuroWidth;

        if (family.contains("arial")) {
            charWidthx20x7E = CharWidthData.ARIAL_NORMAL_20_7E;
            charWidthxA0x17F = CharWidthData.ARIAL_NORMAL_A0_17F;
            charWidthx218x21B = CharWidthData.ARIAL_NORMAL_218_21B;
            charDefaultWidth = CharWidthData.ARIAL_NORMAL_DEFAULT_WIDTH;
            charNDashWidth = CharWidthData.ARIAL_NORMAL_NDASH_WIDTH;
            charEuroWidth = CharWidthData.ARIAL_NORMAL_EURO_WIDTH;
            boldCharWidthx20x7F = CharWidthData.ARIAL_BOLD_20_7E;
            boldCharWidthxA0x17F = CharWidthData.ARIAL_BOLD_A0_17F;
            boldCharWidthx218x21B = CharWidthData.ARIAL_BOLD_218_21B;
            boldCharDefaultWidth = CharWidthData.ARIAL_BOLD_DEFAULT_WIDTH;
            boldCharNDashWidth = CharWidthData.ARIAL_BOLD_NDASH_WIDTH;
            boldCharEuroWidth = CharWidthData.ARIAL_BOLD_EURO_WIDTH;
        } else if (family.contains("liberation") && family.contains("sans")) {
            charWidthx20x7E = CharWidthData.LIBERATION_SANS_NORMAL_20_7E;
            charWidthxA0x17F = CharWidthData.LIBERATION_SANS_NORMAL_A0_17F;
            charWidthx218x21B = CharWidthData.LIBERATION_SANS_NORMAL_218_21B;
            charDefaultWidth = CharWidthData.LIBERATION_SANS_NORMAL_DEFAULT_WIDTH;
            charNDashWidth = CharWidthData.LIBERATION_SANS_NORMAL_NDASH_WIDTH;
            charEuroWidth = CharWidthData.LIBERATION_SANS_NORMAL_EURO_WIDTH;
            boldCharWidthx20x7F = CharWidthData.LIBERATION_SANS_BOLD_20_7E;
            boldCharWidthxA0x17F = CharWidthData.LIBERATION_SANS_BOLD_A0_17F;
            boldCharWidthx218x21B = CharWidthData.LIBERATION_SANS_BOLD_218_21B;
            boldCharDefaultWidth = CharWidthData.LIBERATION_SANS_BOLD_DEFAULT_WIDTH;
            boldCharNDashWidth = CharWidthData.LIBERATION_SANS_BOLD_NDASH_WIDTH;
            boldCharEuroWidth = CharWidthData.LIBERATION_SANS_BOLD_EURO_WIDTH;
        } else if (family.contains("frutiger")) {
            charWidthx20x7E = CharWidthData.FRUTIGER_NORMAL_20_7E;
            charWidthxA0x17F = CharWidthData.FRUTIGER_NORMAL_A0_17F;
            charWidthx218x21B = CharWidthData.FRUTIGER_NORMAL_218_21B;
            charDefaultWidth = CharWidthData.FRUTIGER_NORMAL_DEFAULT_WIDTH;
            charNDashWidth = CharWidthData.FRUTIGER_NORMAL_NDASH_WIDTH;
            charEuroWidth = CharWidthData.FRUTIGER_NORMAL_EURO_WIDTH;
            boldCharWidthx20x7F = CharWidthData.FRUTIGER_BOLD_20_7E;
            boldCharWidthxA0x17F = CharWidthData.FRUTIGER_BOLD_A0_17F;
            boldCharWidthx218x21B = CharWidthData.FRUTIGER_BOLD_218_21B;
            boldCharDefaultWidth = CharWidthData.FRUTIGER_BOLD_DEFAULT_WIDTH;
            boldCharNDashWidth = CharWidthData.FRUTIGER_BOLD_NDASH_WIDTH;
            boldCharEuroWidth = CharWidthData.FRUTIGER_BOLD_EURO_WIDTH;
        } else {
            charWidthx20x7E = CharWidthData.HELVETICA_NORMAL_20_7E;
            charWidthxA0x17F = CharWidthData.HELVETICA_NORMAL_A0_17F;
            charWidthx218x21B = CharWidthData.HELVETICA_NORMAL_218_21B;
            charDefaultWidth = CharWidthData.HELVETICA_NORMAL_DEFAULT_WIDTH;
            charNDashWidth = CharWidthData.HELVETICA_NORMAL_NDASH_WIDTH;
            charEuroWidth = CharWidthData.HELVETICA_NORMAL_EURO_WIDTH;
            boldCharWidthx20x7F = CharWidthData.HELVETICA_BOLD_20_7E;
            boldCharWidthxA0x17F = CharWidthData.HELVETICA_BOLD_A0_17F;
            boldCharWidthx218x21B = CharWidthData.HELVETICA_BOLD_218_21B;
            boldCharDefaultWidth = CharWidthData.HELVETICA_BOLD_DEFAULT_WIDTH;
            boldCharNDashWidth = CharWidthData.HELVETICA_BOLD_NDASH_WIDTH;
            boldCharEuroWidth = CharWidthData.HELVETICA_BOLD_EURO_WIDTH;
        }

        boldMetrics = new FontMetrics(boldCharWidthx20x7F, boldCharWidthxA0x17F, boldCharWidthx218x21B, boldCharDefaultWidth, boldCharNDashWidth, boldCharEuroWidth);
    }

    private FontMetrics(char[] charWidthx20x7E, char[] charWidthxA0x17F, char[] charWidthx218x21B, char charDefaultWidth, char charNDashWidth, char charEuroWidth) {
        fontFamilyList = null;
        firstFontFamily = null;
        this.charWidthx20x7E = charWidthx20x7E;
        this.charWidthxA0x17F = charWidthxA0x17F;
        this.charWidthx218x21B = charWidthx218x21B;
        this.charDefaultWidth = charDefaultWidth;
        this.charNDashWidth = charNDashWidth;
        this.charEuroWidth = charEuroWidth;
        this.boldMetrics = null;
    }

    /**
     * Gets the font family list.
     *
     * @return font family list (comma separated)
     */
    public String getFontFamilyList() {
        return fontFamilyList;
    }

    /**
     * Gets the first font family (from the font family list).
     *
     * @return first font family
     */
    public String getFirstFontFamily() {
        return firstFontFamily;
    }

    /**
     * Distance between baseline and top of the highest letter.
     *
     * @param fontSize the font size (in pt)
     * @return the distance (in mm)
     */
    public double getAscender(int fontSize) {
        return fontSize * 0.8 * PT_TO_MM;
    }

    /**
     * Distance between baseline and bottom of letter extending the farthest below the
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
    @SuppressWarnings("java:S3776")
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
     *
     * @param text     text
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
    private char getCharWidth(char ch) {
        char width = 0;
        if (ch >= 0x20 && ch <= 0x7e)
            width = charWidthx20x7E[ch - 0x20];
        else if (ch >= 0xa0 && ch <= 0x017f) {
            width = charWidthxA0x17F[ch - 0xa0];
        } else if (ch >= 0x0218 && ch <= 0x021b) {
                width = charWidthx218x21B[ch - 0x0218];
        } else if (ch == 0x2013) {
            width = charNDashWidth;
        } else if (ch == 0x20AC) {
            width = charEuroWidth;
        }

        if (width == 0 && ch != '\n' && ch != '\r')
            width = charDefaultWidth;
        return width;
    }

    private static String getFirstFontFamily(String fontFamilyList) {
        int index = fontFamilyList.indexOf(',');
        if (index < 0)
            return fontFamilyList;
        String fontFamily = fontFamilyList.substring(0, index).trim();
        if (fontFamily.startsWith("\""))
            fontFamily = fontFamily.substring(1);
        if (fontFamily.endsWith(("\"")))
            fontFamily = fontFamily.substring(0, fontFamily.length() - 1);
        return fontFamily;
    }
}
