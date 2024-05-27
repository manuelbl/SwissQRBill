package net.codecrete.qrbill.canvas;

import java.nio.file.Path;

/**
 * Sets the font to use for a PDF canvas.
 *
 * <p>
 * To render a QR bill to a PDF document, a regular and a bold font face are needed. According to the Swiss
 * QR bill specification, only the non-serif fonts Helvetica, Arial, Liberation Sans and Frutiger are allowed.
 * </p>
 * <p>
 * There are three options for fonts:
 * </p>
 * <ul>
 *     <li>
 *     Use the standard Helvetica font. It does not need to be embedded in the PDF document
 *     as it is supported by all PDF viewers. However, it only covers the WinANSI character set.
 *     This is sufficient for the <i>Latin 1 Subset</i> character set but not for the
 *     <i>Extended Latin</i> character set.
 *     </li>
 *     <li>
 *     Use the Liberation Sans font that comes with this library. It covers a wide range of characters and
 *     is sufficient for the <i>Extended Latin</i> character set. The subset of used characters will be
 *     embedded in the PDF document. The font has been published under SIL OPEN FONT LICENSE Version 1.1
 *     and is free for use.
 *     </li>
 *     <li>
 *     Provide paths to a regular and bold font face in TrueType format. The subset of the used characters will be
 *     embedded in the PDF document.
 *     </li>
 * </ul>
 */
public class PDFFontSettings {
    private final FontEmbedding fontEmbedding;
    private final String fontFamily;
    private final Path regularFontPath;
    private final Path boldFontPath;

    private PDFFontSettings(FontEmbedding fontEmbedding, String fontFamily, Path regularFontPath, Path boldFontPath) {
        this.fontEmbedding = fontEmbedding;
        this.fontFamily = fontFamily;
        this.regularFontPath = regularFontPath;
        this.boldFontPath = boldFontPath;
    }

    /**
     * Creates a font settings instance for the standard Helvetica font.
     * @return font settings instance
     */
    public static PDFFontSettings standardHelvetica() {
        return new PDFFontSettings(FontEmbedding.STANDARD_HELVETICA, "Helvetica", null, null);
    }

    /**
     * Creates a font settings instance for the Liberation Sans font.
     * @return font settings instance
     */
    public static PDFFontSettings embeddedLiberationSans() {
        return new PDFFontSettings(FontEmbedding.EMBEDDED_LIBERATION_SANS, "Liberation Sans", null, null);
    }

    /**
     * Creates a font settings instance for a custom font.
     * <p>
     * The font family name is used to determine what font information to use for calculating line breaks.
     * </p>
     * @param fontFamily font family name
     * @param regularFontPath path to the regular font face in TrueType format
     * @param boldFontPath path to the bold font face in TrueType format
     * @return font settings instance
     */
    public static PDFFontSettings embeddedCustomFont(String fontFamily, Path regularFontPath, Path boldFontPath) {
        return new PDFFontSettings(FontEmbedding.EMBEDDED_CUSTOM, fontFamily, regularFontPath, boldFontPath);
    }

    /**
     * Gets the font embedding.
     * @return font embedding option
     */
    public FontEmbedding getFontEmbedding() {
        return fontEmbedding;
    }

    /**
     * Gets the font family name relevant for calculating line breaks.
     * @return font family name
     */
    public String getFontFamily() {
        return fontFamily;
    }

    /**
     * Gets the path to the regular font face in TrueType format.
     * @return font path
     */
    public Path getRegularFontPath() {
        return regularFontPath;
    }

    /**
     * Gets the path to the bold font face in TrueType format.
     * @return font path
     */
    public Path getBoldFontPath() {
        return boldFontPath;
    }

    /**
     * Font embedding options.
     */
    public enum FontEmbedding {
        /**
         * Standard Helvetica font, without embedding.
         */
        STANDARD_HELVETICA,
        /**
         * Liberation Sans font included in this library, with embedding.
         */
        EMBEDDED_LIBERATION_SANS,
        /**
         * Custom font provided by caller, with embedding.
         */
        EMBEDDED_CUSTOM
    }
}
