//
// Swiss QR Bill Generator
// Copyright (c) 2018 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//

package net.codecrete.qrbill.generator;

import java.util.Objects;

/**
 * Formatting properties for QR bill
 */
public class BillFormat implements Cloneable {

    private OutputSize outputSize = OutputSize.QR_BILL_ONLY;
    private Language language = Language.EN;
    private SeparatorType separatorType = SeparatorType.SOLID_LINE_WITH_SCISSORS;
    private String fontFamily = "Helvetica";
    private GraphicsFormat graphicsFormat = GraphicsFormat.SVG;

    /**
     * Gets the output size for the generated QR bill
     * <p>
     *     Defaults to {@link OutputSize#QR_BILL_ONLY}, i.e. the QR bill only (about 105 by 210 mm)
     * </p>
     *
     * @return output size
     */
    public OutputSize getOutputSize() {
        return outputSize;
    }

    /**
     * Sets the output size for the generated QR bill
     *
     * @param outputSize  output size
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
     *     Defaults to {@link SeparatorType#SOLID_LINE_WITH_SCISSORS}
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
     * @param separatorType  separator type
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
     *     Defaults to SVG.
     * </p>
     * @return graphics format
     */
    public GraphicsFormat getGraphicsFormat() {
        return graphicsFormat;
    }

    /**
     * Sets the graphics format for the generated QR bill
     *
     * @param graphicsFormat  graphics format
     */
    public void setGraphicsFormat(GraphicsFormat graphicsFormat) {
        this.graphicsFormat = graphicsFormat;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    protected BillFormat cloneInstance() {
        try {
            return (BillFormat) clone();
        } catch (CloneNotSupportedException e) {
            // cannot happen
            throw new RuntimeException(e);
        }
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
                graphicsFormat == that.graphicsFormat;
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
                '}';
    }
}
