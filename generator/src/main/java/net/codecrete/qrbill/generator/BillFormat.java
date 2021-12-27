//
// Swiss QR Bill Generator
// Copyright (c) 2018 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//

package net.codecrete.qrbill.generator;

import java.io.Serializable;
import java.util.Objects;

/**
 * Formatting properties for QR bill
 */
public class BillFormat implements Serializable {

    /**
     * Default width for left and right margin, in mm.
     */
    public static final double DEFAULT_MARGIN_WIDTH = 5.0;

    private static final long serialVersionUID = -2874086922578292745L;

    private OutputSize outputSize = OutputSize.QR_BILL_ONLY;
    private Language language = Language.EN;
    private SeparatorType separatorType = SeparatorType.DASHED_LINE_WITH_SCISSORS;
    private String fontFamily = "Helvetica,Arial,\"Liberation Sans\"";
    private GraphicsFormat graphicsFormat = GraphicsFormat.SVG;
    private int resolution = 144;
    private double marginLeft = DEFAULT_MARGIN_WIDTH;
    private double marginRight = DEFAULT_MARGIN_WIDTH;

    /**
     * Creates a new instance with default values
     */
    public BillFormat() {
        // default constructor
    }

    /**
     * Copy constructor: creates a copy of the specified format
     *
     * @param format format to copy
     */
    public BillFormat(BillFormat format) {
        outputSize = format.outputSize;
        language = format.language;
        separatorType = format.separatorType;
        fontFamily = format.fontFamily;
        graphicsFormat = format.graphicsFormat;
        resolution = format.resolution;
        marginLeft = format.marginLeft;
        marginRight = format.marginRight;
    }

    /**
     * Gets the output size for the generated QR bill
     * <p>
     * Defaults to {@link OutputSize#QR_BILL_ONLY}, i.e. the QR bill only (about 105 by 210 mm)
     * </p>
     *
     * @return output size
     * @see <a href="https://github.com/manuelbl/SwissQRBill/wiki/Output-Sizes">Output Sizes (in Wiki)</a>
     */
    public OutputSize getOutputSize() {
        return outputSize;
    }

    /**
     * Sets the output size for the generated QR bill
     *
     * @param outputSize output size
     * @see <a href="https://github.com/manuelbl/SwissQRBill/wiki/Output-Sizes">Output Sizes (in Wiki)</a>
     */
    public void setOutputSize(OutputSize outputSize) {
        this.outputSize = outputSize;
    }

    /**
     * Gets the bill language.
     * <p>
     * Defaults to EN (English).
     * </p>
     *
     * @return the language
     */
    public Language getLanguage() {
        return language;
    }

    /**
     * Sets the bill language
     *
     * @param language the language
     */
    public void setLanguage(Language language) {
        this.language = language;
    }

    /**
     * Gets the type of separator drawn above and between the payment part and the receipt
     * <p>
     * Defaults to {@link SeparatorType#DASHED_LINE_WITH_SCISSORS}
     * </p>
     *
     * @return separator type
     */
    public SeparatorType getSeparatorType() {
        return separatorType;
    }

    /**
     * Sets the type of separator drawn above and between the payment part and the receipt
     *
     * @param separatorType separator type
     */
    public void setSeparatorType(SeparatorType separatorType) {
        this.separatorType = separatorType;
    }

    /**
     * Gets the font family to be used for text
     * <p>
     * According to the implementation guidelines Arial, Frutiger, Helvetica and Liberation Sans
     * are the only permitted fonts.
     * </p>
     * <p>
     * Two styles of the font are used: normal/regular and bold.
     * </p>
     * <p>
     * Defaults to "Helvetica".
     * </p>
     *
     * @return font family name
     */
    public String getFontFamily() {
        return fontFamily;
    }

    /**
     * Sets the font family to be used for text
     * <p>
     * According to the implementation guidelines Arial, Frutiger, Helvetica and Liberation Sans
     * are the only permitted fonts. However, any string is accepted as the font name might be
     * more elaborate such as "Frutiger 55 Regular".
     * </p>
     * <p>
     * Two styles of the font are used: normal/regular and bold.
     * </p>
     * <p>
     * Defaults to "Helvetica".
     * </p>
     *
     * @param fontFamily font family name
     */
    public void setFontFamily(String fontFamily) {
        this.fontFamily = fontFamily;
    }

    /**
     * Gets the graphics format for the generated QR bill
     * <p>
     * Defaults to SVG.
     * </p>
     *
     * @return graphics format
     */
    public GraphicsFormat getGraphicsFormat() {
        return graphicsFormat;
    }

    /**
     * Sets the graphics format for the generated QR bill
     *
     * @param graphicsFormat graphics format
     */
    public void setGraphicsFormat(GraphicsFormat graphicsFormat) {
        this.graphicsFormat = graphicsFormat;
    }

    /**
     * Gets the resolution for pixel graphics formats.
     *
     * <p>
     * Defaults to 144 dpi.
     * </p>
     *
     * @return graphics resolution, in dpi
     */
    public int getResolution() {
        return resolution;
    }

    /**
     * Sets the resolution for pixel graphics formats.
     *
     * @param resolution graphics resolution, in dpi
     */
    public void setResolution(int resolution) {
        this.resolution = resolution;
    }

    /**
     * Gets the left margin width (from edge of paper to start of text).
     *
     * <p>
     * Valid values are between 5mm and 12mm. The default is 5mm.
     * </p>
     * <p>
     * Values other than 5mm are not fully standard compliant as the fields in the receipt on the left-hand side
     * become narrower. It is especially obvious if <i>Payable by</i> or <i>Amount</i> are not pre-filled
     * so that the black corners are printed instead of text. Yet values higher than 5mm are more compatible
     * with typical office and home printers, which are not capable of printing up to the edge of the paper and
     * require a margin wider than 5mm.
     * </p>
     *
     * @return margin width, in mm
     */
    public double getMarginLeft() {
        return marginLeft;
    }

    /**
     * Sets the left margin width (from edge of paper to start of text).
     *
     * <p>
     * Valid values are between 5mm and 12mm. The default is 5mm.
     * </p>
     * <p>
     * Values other than 5mm are not fully standard compliant as the fields in the receipt on the left-hand side
     * become narrower. It is especially obvious if <i>Payable by</i> or <i>Amount</i> are not pre-filled
     * so that the black corners are printed instead of text. Yet values higher than 5mm are more compatible
     * with typical office and home printers, which are not capable of printing up to the edge of the paper and
     * require a margin wider than 5mm.
     * </p>
     *
     * @param marginLeft margin width, in mm
     */
    public void setMarginLeft(double marginLeft) {
        this.marginLeft = marginLeft;
    }

    /**
     * Gets the the right margin width (from the end of the text to the edge of the paper).
     *
     * <p>
     * Valid values are between 5mm and 12mm. The default is 5m.
     * </p>
     * <p>
     * Values other than 5mm are not fully standard compliant but are more compatible with typical office and home
     * printers, which are not capable of printing up to the edge of the paper and require a margin wider than 5mm.
     * </p>
     *
     * @return width width, in mm
     */
    public double getMarginRight() {
        return marginRight;
    }

    /**
     * Gets the the right margin width (from the end of the text to the edge of the paper).
     *
     * <p>
     * Valid values are between 5mm and 12mm. The default is 5m.
     * </p>
     * <p>
     * Values other than 5mm are not fully standard compliant but are more compatible with typical office and home
     * printers, which are not capable of printing up to the edge of the paper and require a margin wider than 5mm.
     * </p>
     *
     * @param marginRight margin width, in mm
     */
    public void setMarginRight(double marginRight) {
        this.marginRight = marginRight;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BillFormat that = (BillFormat) o;
        return outputSize == that.outputSize &&
                language == that.language &&
                separatorType == that.separatorType &&
                Objects.equals(fontFamily, that.fontFamily) &&
                graphicsFormat == that.graphicsFormat &&
                resolution == that.resolution &&
                marginLeft == that.marginLeft &&
                marginRight == that.marginRight;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {

        return Objects.hash(outputSize, language, separatorType, fontFamily, graphicsFormat);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "BillFormat{" +
                "outputSize=" + outputSize +
                ", language=" + language +
                ", separatorType=" + separatorType +
                ", fontFamily='" + fontFamily + '\'' +
                ", graphicsFormat=" + graphicsFormat +
                ", resolution=" + resolution +
                ", marginLeft=" + marginLeft +
                ", marginRight=" + marginRight +
                '}';
    }
}
