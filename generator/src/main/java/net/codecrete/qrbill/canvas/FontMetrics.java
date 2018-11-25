//
// Swiss QR Bill Generator
// Copyright (c) 2017 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
package net.codecrete.qrbill.canvas;

import java.util.ArrayList;

/**
 * Simple font metrics class to be independent of graphics subsystems and
 * installed fonts.
 *
 * <p>
 * Only supports Helvetica font.
 * </p>
 */
public class FontMetrics {

    private static final double PT_TO_MM = 25.4 / 72;

    private final char[] charWidth_20_7F;
    private final char[] charWidth_A0_FF;
    private final char charDefaultWidth;
    private final FontMetrics boldMetrics;

    public FontMetrics() {
        this(HELVETICA_NORMAL_20_7F, HELVATICA_NORMAL_A0_FF, HELVETICA_NORMAL_DEFAULT_WIDTH,
                new FontMetrics(HELVETICA_BOLD_20_7F, HELVATICA_BOLD_A0_FF, HELVETICA_BOLD_DEFAULT_WIDTH, null));
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


    private static final char HELVETICA_NORMAL_DEFAULT_WIDTH = 556;

    private static final char[] HELVETICA_NORMAL_20_7F = {
            278, // 0x20
            278, // 0x21 !
            355, // 0x22 "
            556, // 0x23 #
            556, // 0x24 $
            889, // 0x25 %
            667, // 0x26 &
            191, // 0x27 '
            333, // 0x28 (
            333, // 0x29 )
            389, // 0x2A *
            584, // 0x2B +
            278, // 0x2C ,
            333, // 0x2D -
            278, // 0x2E .
            278, // 0x2F /
            556, // 0x30 0
            556, // 0x31 1
            556, // 0x32 2
            556, // 0x33 3
            556, // 0x34 4
            556, // 0x35 5
            556, // 0x36 6
            556, // 0x37 7
            556, // 0x38 8
            556, // 0x39 9
            278, // 0x3A :
            278, // 0x3B ;
            584, // 0x3C <
            584, // 0x3D =
            584, // 0x3E >
            556, // 0x3F ?
            1015, // 0x40 @
            667, // 0x41 A
            667, // 0x42 B
            722, // 0x43 C
            722, // 0x44 D
            667, // 0x45 E
            611, // 0x46 F
            778, // 0x47 G
            722, // 0x48 H
            278, // 0x49 I
            500, // 0x4A J
            667, // 0x4B K
            556, // 0x4C L
            833, // 0x4D M
            722, // 0x4E N
            778, // 0x4F O
            667, // 0x50 P
            778, // 0x51 Q
            722, // 0x52 R
            667, // 0x53 S
            611, // 0x54 T
            722, // 0x55 U
            667, // 0x56 V
            944, // 0x57 W
            667, // 0x58 X
            667, // 0x59 Y
            611, // 0x5A Z
            278, // 0x5B [
            278, // 0x5C \
            278, // 0x5D ]
            0, // unused
            556, // 0x5F _
            333, // 0x60 `
            556, // 0x61 a
            556, // 0x62 b
            500, // 0x63 c
            556, // 0x64 d
            556, // 0x65 e
            278, // 0x66 f
            556, // 0x67 g
            556, // 0x68 h
            222, // 0x69 i
            222, // 0x6A j
            500, // 0x6B k
            222, // 0x6C l
            833, // 0x6D m
            556, // 0x6E n
            556, // 0x6F o
            556, // 0x70 p
            556, // 0x71 q
            333, // 0x72 r
            500, // 0x73 s
            278, // 0x74 t
            556, // 0x75 u
            500, // 0x76 v
            722, // 0x77 w
            500, // 0x78 x
            500, // 0x79 y
            500, // 0x7A z
            334, // 0x7B {
            0, // unused
            334, // 0x7D }
            584, // 0x7E ~
            0 // unused
    };

    private static final char[] HELVATICA_NORMAL_A0_FF = {
            0, // unused
            0, // unused
            0, // unused
            556, // 0xA3 £
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            333, // 0xB4 ´
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            667, // 0xC0 À
            667, // 0xC1 Á
            667, // 0xC2 Â
            0, // unused
            667, // 0xC4 Ä
            0, // unused
            0, // unused
            722, // 0xC7 Ç
            667, // 0xC8 È
            667, // 0xC9 É
            667, // 0xCA Ê
            667, // 0xCB Ë
            278, // 0xCC Ì
            278, // 0xCD Í
            278, // 0xCE Î
            278, // 0xCF Ï
            0, // unused
            722, // 0xD1 Ñ
            778, // 0xD2 Ò
            778, // 0xD3 Ó
            778, // 0xD4 Ô
            0, // unused
            778, // 0xD6 Ö
            0, // unused
            0, // unused
            722, // 0xD9 Ù
            722, // 0xDA Ú
            722, // 0xDB Û
            722, // 0xDC Ü
            0, // unused
            0, // unused
            611, // 0xDF ß
            556, // 0xE0 à
            556, // 0xE1 á
            556, // 0xE2 â
            0, // unused
            556, // 0xE4 ä
            0, // unused
            0, // unused
            500, // 0xE7 ç
            556, // 0xE8 è
            556, // 0xE9 é
            556, // 0xEA ê
            556, // 0xEB ë
            278, // 0xEC ì
            278, // 0xED í
            278, // 0xEE î
            278, // 0xEF ï
            0, // unused
            556, // 0xF1 ñ
            556, // 0xF2 ò
            556, // 0xF3 ó
            556, // 0xF4 ô
            0, // unused
            556, // 0xF6 ö
            549, // 0xF7 ÷
            0, // unused
            556, // 0xF9 ù
            556, // 0xFA ú
            556, // 0xFB û
            556, // 0xFC ü
            500, // 0xFD ý
            0, // unused
            0 // unused
    };

    private static final char HELVETICA_BOLD_DEFAULT_WIDTH = 611;

    private static final char[] HELVETICA_BOLD_20_7F = {
            278, // 0x20
            333, // 0x21 !
            474, // 0x22 "
            556, // 0x23 #
            556, // 0x24 $
            889, // 0x25 %
            722, // 0x26 &
            238, // 0x27 '
            333, // 0x28 (
            333, // 0x29 )
            389, // 0x2A *
            584, // 0x2B +
            278, // 0x2C ,
            333, // 0x2D -
            278, // 0x2E .
            278, // 0x2F /
            556, // 0x30 0
            556, // 0x31 1
            556, // 0x32 2
            556, // 0x33 3
            556, // 0x34 4
            556, // 0x35 5
            556, // 0x36 6
            556, // 0x37 7
            556, // 0x38 8
            556, // 0x39 9
            333, // 0x3A :
            333, // 0x3B ;
            584, // 0x3C <
            584, // 0x3D =
            584, // 0x3E >
            611, // 0x3F ?
            975, // 0x40 @
            722, // 0x41 A
            722, // 0x42 B
            722, // 0x43 C
            722, // 0x44 D
            667, // 0x45 E
            611, // 0x46 F
            778, // 0x47 G
            722, // 0x48 H
            278, // 0x49 I
            556, // 0x4A J
            722, // 0x4B K
            611, // 0x4C L
            833, // 0x4D M
            722, // 0x4E N
            778, // 0x4F O
            667, // 0x50 P
            778, // 0x51 Q
            722, // 0x52 R
            667, // 0x53 S
            611, // 0x54 T
            722, // 0x55 U
            667, // 0x56 V
            944, // 0x57 W
            667, // 0x58 X
            667, // 0x59 Y
            611, // 0x5A Z
            333, // 0x5B [
            278, // 0x5C \
            333, // 0x5D ]
            0, // unused
            556, // 0x5F _
            333, // 0x60 `
            556, // 0x61 a
            611, // 0x62 b
            556, // 0x63 c
            611, // 0x64 d
            556, // 0x65 e
            333, // 0x66 f
            611, // 0x67 g
            611, // 0x68 h
            278, // 0x69 i
            278, // 0x6A j
            556, // 0x6B k
            278, // 0x6C l
            889, // 0x6D m
            611, // 0x6E n
            611, // 0x6F o
            611, // 0x70 p
            611, // 0x71 q
            389, // 0x72 r
            556, // 0x73 s
            333, // 0x74 t
            611, // 0x75 u
            556, // 0x76 v
            778, // 0x77 w
            556, // 0x78 x
            556, // 0x79 y
            500, // 0x7A z
            389, // 0x7B {
            0, // unused
            389, // 0x7D }
            584, // 0x7E ~
            0 // unused
    };

    private static final char[] HELVATICA_BOLD_A0_FF = {
            0, // unused
            0, // unused
            0, // unused
            556, // 0xA3 £
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            333, // 0xB4 ´
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            722, // 0xC0 À
            722, // 0xC1 Á
            722, // 0xC2 Â
            0, // unused
            722, // 0xC4 Ä
            0, // unused
            0, // unused
            722, // 0xC7 Ç
            667, // 0xC8 È
            667, // 0xC9 É
            667, // 0xCA Ê
            667, // 0xCB Ë
            278, // 0xCC Ì
            278, // 0xCD Í
            278, // 0xCE Î
            278, // 0xCF Ï
            0, // unused
            722, // 0xD1 Ñ
            778, // 0xD2 Ò
            778, // 0xD3 Ó
            778, // 0xD4 Ô
            0, // unused
            778, // 0xD6 Ö
            0, // unused
            0, // unused
            722, // 0xD9 Ù
            722, // 0xDA Ú
            722, // 0xDB Û
            722, // 0xDC Ü
            0, // unused
            0, // unused
            611, // 0xDF ß
            556, // 0xE0 à
            556, // 0xE1 á
            556, // 0xE2 â
            0, // unused
            556, // 0xE4 ä
            0, // unused
            0, // unused
            556, // 0xE7 ç
            556, // 0xE8 è
            556, // 0xE9 é
            556, // 0xEA ê
            556, // 0xEB ë
            278, // 0xEC ì
            278, // 0xED í
            278, // 0xEE î
            278, // 0xEF ï
            0, // unused
            611, // 0xF1 ñ
            611, // 0xF2 ò
            611, // 0xF3 ó
            611, // 0xF4 ô
            0, // unused
            611, // 0xF6 ö
            549, // 0xF7 ÷
            0, // unused
            611, // 0xF9 ù
            611, // 0xFA ú
            611, // 0xFB û
            611, // 0xFC ü
            556, // 0xFD ý
            0, // unused
            0 // unused
    };

}
