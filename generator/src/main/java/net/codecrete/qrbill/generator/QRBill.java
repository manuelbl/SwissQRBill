//
// Swiss QR Bill Generator
// Copyright (c) 2017 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
package net.codecrete.qrbill.generator;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;


/**
 * Generates Swiss QR bills.
 */
public class QRBill {

    /**
     * Graphics format of generated QR bill.
     */
    public enum GraphicsFormat {
        /** PDF */
        PDF,
        /** SVG */
        SVG
    }

    /**
     * The output size of the QR bill
     */
    public enum BillFormat {
        /** A4 sheet in portrait orientation. The QR bill is in the bottom right. */
        A4_PORTRAIT_SHEET,
        /** A5 sheet in landscape orientation. The QR bill is in the bottom right. */
        A5_LANDSCAPE_SHEET,
        /** A6 sheet in landscape orientation. The QR bill fills the entire sheet. */
        A6_LANDSCAPE_SHEET,
        /** QR code only (46 by 46 mm). */
        QR_CODE_ONLY
    }

    private static final double PT_TO_MM = 25.4 / 72;
    private static final double MM_TO_PT = 72 / 25.4;
    private static final int FONT_SIZE_TITLE = 11; // pt
    private static final int FONT_SIZE_LABEL = 8; // pt
    private static final int FONT_SIZE_TEXT = 10; // pt
    private static final double HORIZ_BORDER = 8; // mm
    private static final double VERT_BORDER = 8; // mm
    private static final double MIDDLE_SPACING = 5; // mm
    private static final double LEFT_COLUMN_WIDTH = 56; // mm
    private static final double AMOUNT_WIDTH = 40; // mm (must not be smaller than 40)
    private static final double AMOUNT_HEIGHT = 15; // mm (must not be smaller than 15)
    private static final double RIGHT_COLUMN_WIDTH
            = 148.5 - 2 * HORIZ_BORDER - MIDDLE_SPACING - LEFT_COLUMN_WIDTH; // mm (must not be smaller than 65)
    private static final double DEBTOR_HEIGHT = 25; // mm (must no be smaller than 25)
    private static final double LABEL_TOP_PADDING = 6 * PT_TO_MM;
    private static final double TEXT_TOP_PADDING = 2 * PT_TO_MM;


    private Bill bill;
    private QRCode qrCode;
    private GraphicsGenerator graphics;
    private BillFormat billFormat;
    private GraphicsFormat graphicsFormat;

    private String account;
    private String[] creditor;
    private String[] finalCreditor;
    private String refNo;
    private String[] additionalInfo;
    private String[] debtor;
    private String dueDate;

    private double labelTopPadding;
    private double textTopPadding;
    private double labelLineHeight;
    private double textLineHeight;
    private int fontSizeLabel;
    private int fontSizeText;
    private double rightColumnExtraYOffset;


    /**
     * Validates the bill data.
     * <p>
     *     The validation result contains the error and warning
     *     messages (if any).
     * </p>
     * @param bill bill data
     * @return validation result
     */
    public static ValidationResult validate(Bill bill) {
        ValidationResult result = new ValidationResult();
        Validator validator = new Validator(bill, result);
        validator.validate();
        return result;
    }

    /**
     * Generates a QR bill.
     * <p>
     *     If the bill data does not validate, a {@link QRBillValidationError} is thrown,
     *     which contains the validation result.
     * </p>
     * @param bill the bill data
     * @param billFormat the bill's output format
     * @param graphicsFormat the bill's output size
     * @return the generated QR bill (as a byte array)
     */
    public static byte[] generate(Bill bill, BillFormat billFormat, GraphicsFormat graphicsFormat) {
        ValidationResult result = new ValidationResult();
        Validator validator = new Validator(bill, result);
        Bill cleanedBill = validator.validate();
        if (result.hasErrors())
            throw new QRBillValidationError(result);

        QRBill qrBill = new QRBill();
        qrBill.bill = cleanedBill;
        qrBill.qrCode = new QRCode(cleanedBill);
        qrBill.billFormat = billFormat;
        qrBill.graphicsFormat = graphicsFormat;
        return qrBill.generateOutput();
    }


    /**
     * Generates the text that is embedded in the QR code.
     * <p>
     *     If the bill data does not validate, a {@link QRBillValidationError} is thrown,
     *     which contains the validation result.
     * </p>
     * @param bill the bill data
     * @return the QR code text
     */
    public static String generateQrCodeText(Bill bill) {
        ValidationResult result = new ValidationResult();
        Validator validator = new Validator(bill, result);
        Bill cleanedBill = validator.validate();
        if (result.hasErrors())
            throw new QRBillValidationError(result);

        QRCode qrCode = new QRCode(cleanedBill);
        return qrCode.getText();
    }


    /**
     * Decodes the text embedded in the QR code and fills it into a {@link Bill} data structure.
     * @param text the text to decode
     * @return the decoded bill data
     */
    public static Bill decodeQrCodeText(String text) {
        return QRCode.decodeQRCodeText(text);
    }


    private byte[] generateOutput() {

        double drawingWidth;
        double drawingHeight;

        switch (billFormat) {
            case QR_CODE_ONLY:
                drawingWidth = QRCode.SIZE;
                drawingHeight = QRCode.SIZE;
                break;
            case A6_LANDSCAPE_SHEET:
                drawingWidth = 148.5;
                drawingHeight = 105;
                break;
            case A5_LANDSCAPE_SHEET:
                drawingWidth = 210;
                drawingHeight = 148.5;
                break;
            case A4_PORTRAIT_SHEET:
            default:
                drawingWidth = 210;
                drawingHeight = 297;
                break;
        }

        try (GraphicsGenerator g = createGraphicsGenerator()) {

            graphics = g;
            graphics.setupPage(drawingWidth, drawingHeight);
            switch (billFormat) {
                case QR_CODE_ONLY:
                    drawQRCodeOnly();
                    break;
                case A6_LANDSCAPE_SHEET:
                    drawQRBill(0, 0, false);
                    break;
                case A5_LANDSCAPE_SHEET:
                    drawQRBill(61.5, 43.5, true);
                    break;
                case A4_PORTRAIT_SHEET:
                    drawQRBill(61.5, 192, true);
                    break;
                default:
                    throw new QrBillRuntimeException("Invalid bill format specified");
            }

            return graphics.getResult();

        } catch (IOException e) {
            throw new QrBillRuntimeException(e);
        } finally {
            graphics = null;
        }
    }

    private GraphicsGenerator createGraphicsGenerator() throws IOException {
        GraphicsGenerator generator;
        switch (graphicsFormat) {
            case SVG:
                generator = new SVGGenerator();
                break;
            case PDF:
                generator = new PDFGenerator();
                break;
            default:
                generator = null;
        }
        return generator;
    }

    private void drawQRBill(double offsetX, double offsetY, boolean hasBorder) throws IOException {

        fontSizeLabel = FONT_SIZE_LABEL;
        fontSizeText = FONT_SIZE_TEXT;
        formatRightColumText();
        double factor = computeSpacing();

        if (factor < 0.6) {
            fontSizeLabel = FONT_SIZE_LABEL - 1;
            fontSizeText = FONT_SIZE_TEXT - 1;
            formatRightColumText();
            computeSpacing();
        }

        Bill.Language language = bill.getLanguage();

        // border
        if (hasBorder) {
            graphics.setTransformation(offsetX, offsetY, 1);
            graphics.startPath();
            graphics.moveTo(0, 105);
            graphics.lineTo(0, 0);
            graphics.lineTo(148.5, 0);
            graphics.strokePath(0.5, 0);
        }

        // title section
        graphics.setTransformation(offsetX + HORIZ_BORDER, offsetY + VERT_BORDER, 1);
        double yPos = 0;
        graphics.putText(MultilingualText.getText(MultilingualText.KEY_QR_BILL_PAYMENT_PART, language), 0, yPos, FONT_SIZE_TITLE, true);

        // scheme section
        yPos += FontMetrics.getLineHeight(FONT_SIZE_TITLE) + labelTopPadding;
        graphics.putText(MultilingualText.getText(MultilingualText.KEY_SUPPORTS, language), 0, yPos, fontSizeLabel, true);
        yPos += labelLineHeight + textTopPadding;
        graphics.putText(MultilingualText.getText(MultilingualText.KEY_CREDIT_TRANSFER, language), 0, yPos, fontSizeText, false);

        // QR code section
        yPos = FontMetrics.getLineHeight(FONT_SIZE_TITLE) + LABEL_TOP_PADDING + TEXT_TOP_PADDING
                + labelLineHeight + textLineHeight
                - FontMetrics.getLeading(fontSizeText);
        double qrCodeSpacing = (105 - VERT_BORDER * 2 - yPos - AMOUNT_HEIGHT
                - labelLineHeight - TEXT_TOP_PADDING - QRCode.SIZE) / 2;
        qrCode.draw(graphics, offsetX + HORIZ_BORDER, offsetY + VERT_BORDER + yPos + qrCodeSpacing);

        // amount section
        yPos += 2 * qrCodeSpacing + QRCode.SIZE;
        graphics.setTransformation(offsetX + HORIZ_BORDER, offsetY + VERT_BORDER + yPos, 1);
        yPos = 0;
        graphics.putText(MultilingualText.getText(MultilingualText.KEY_CURRENCY, language), 0, yPos, fontSizeLabel, true);
        yPos += labelLineHeight + textTopPadding;
        graphics.putText(bill.getCurrency(), 0, yPos, fontSizeText, false);

        yPos = 0;
        graphics.putText(MultilingualText.getText(MultilingualText.KEY_AMOUNT, language), LEFT_COLUMN_WIDTH - AMOUNT_WIDTH, yPos, fontSizeLabel, true);
        yPos += labelLineHeight + textTopPadding;
        if (bill.getAmount() == null) {
            drawCorners(LEFT_COLUMN_WIDTH - AMOUNT_WIDTH, yPos, AMOUNT_WIDTH, AMOUNT_HEIGHT);
        } else {
            graphics.putText(formatAmountForDisplay(bill.getAmount()), LEFT_COLUMN_WIDTH - AMOUNT_WIDTH, yPos, fontSizeText, false);
        }

        // information section
        graphics.setTransformation(offsetX + HORIZ_BORDER + LEFT_COLUMN_WIDTH + MIDDLE_SPACING, offsetY + VERT_BORDER + rightColumnExtraYOffset, 1);
        yPos = 0;

        // account
        graphics.putText(MultilingualText.getText(MultilingualText.KEY_ACCOUNT, language), 0, yPos, fontSizeLabel, true);
        yPos += labelLineHeight + textTopPadding;
        graphics.putText(account, 0, yPos, fontSizeText, false);
        yPos += textLineHeight + labelTopPadding;

        // creditor
        graphics.putText(MultilingualText.getText(MultilingualText.KEY_CREDITOR, language), 0, yPos, fontSizeLabel, true);
        yPos += labelLineHeight + textTopPadding;
        graphics.putTextLines(creditor, 0, yPos, fontSizeText);
        yPos += creditor.length * textLineHeight + labelTopPadding;

        // final creditor
        if (finalCreditor != null) {
            graphics.putText(MultilingualText.getText(MultilingualText.KEY_FINAL_CREDITOR, language), 0, yPos, fontSizeLabel, true);
            yPos += labelLineHeight + textTopPadding;
            graphics.putTextLines(finalCreditor, 0, yPos, fontSizeText);
            yPos += finalCreditor.length * textLineHeight + labelTopPadding;
        }

        // reference number
        if (refNo != null) {
            graphics.putText(MultilingualText.getText(MultilingualText.KEY_REFERENCE_NUMBER, language), 0, yPos, fontSizeLabel, true);
            yPos += labelLineHeight + textTopPadding;
            graphics.putText(refNo, 0, yPos, fontSizeText, false);
            yPos += textLineHeight + labelTopPadding;
        }

        // additional information
        if (additionalInfo != null) {
            graphics.putText(MultilingualText.getText(MultilingualText.KEY_ADDITIONAL_INFORMATION, language), 0, yPos, fontSizeLabel, true);
            yPos += labelLineHeight + textTopPadding;
            graphics.putTextLines(additionalInfo, 0, yPos, fontSizeText);
            yPos += additionalInfo.length * textLineHeight + labelTopPadding;
        }

        // debtor
        graphics.putText(MultilingualText.getText(MultilingualText.KEY_DEBTOR, language), 0, yPos, fontSizeLabel, true);
        yPos += labelLineHeight + textTopPadding;
        if (debtor == null) {
            drawCorners(0, yPos, RIGHT_COLUMN_WIDTH, DEBTOR_HEIGHT);
            yPos += DEBTOR_HEIGHT + labelTopPadding;
        } else {
            graphics.putTextLines(debtor, 0, yPos, fontSizeText);
            yPos += debtor.length * textLineHeight + labelTopPadding;
        }

        // due date
        if (dueDate != null) {
            graphics.putText(MultilingualText.getText(MultilingualText.KEY_DUE_DATE, language), 0, yPos, fontSizeLabel, true);
            yPos += labelLineHeight + textTopPadding;
            graphics.putText(dueDate, 0, yPos, fontSizeText, false);
        }
    }

    private void formatRightColumText() {
        account = formatIBANForDisplay(bill.getAccount());
        creditor = FontMetrics.splitLines(formatPersonForDisplay(bill.getCreditor()), RIGHT_COLUMN_WIDTH * MM_TO_PT, fontSizeText);
        finalCreditor = null;
        if (bill.getFinalCreditor() != null)
            finalCreditor = FontMetrics.splitLines(formatPersonForDisplay(bill.getFinalCreditor()), RIGHT_COLUMN_WIDTH * MM_TO_PT, fontSizeText);
        refNo = formatReferenceNumber(bill.getReferenceNo());
        additionalInfo = null;
        String info = bill.getAdditionalInfo();
        if (info != null) {
            int p = info.indexOf("##");
            if (p > 0)
                info = info.substring(0, p) + '\n' + info.substring(p);
            additionalInfo = FontMetrics.splitLines(info, RIGHT_COLUMN_WIDTH * MM_TO_PT, fontSizeText);
        }
        debtor = null;
        if (bill.getDebtor() != null)
            debtor = FontMetrics.splitLines(formatPersonForDisplay(bill.getDebtor()), RIGHT_COLUMN_WIDTH * MM_TO_PT, fontSizeText);

        dueDate = null;
        if (bill.getDueDate() != null)
            dueDate = formatDateForDisplay(bill.getDueDate());
    }

    private double computeSpacing() {

        int numBlocks = 3;
        if (finalCreditor != null)
            numBlocks++;
        if (refNo != null)
            numBlocks++;
        if (additionalInfo != null)
            numBlocks++;
        if (dueDate != null)
            numBlocks++;

        int numTextLines = 1;
        numTextLines += creditor.length;
        if (finalCreditor != null)
            numTextLines += finalCreditor.length;
        if (refNo != null)
            numTextLines += 1;
        if (additionalInfo != null)
            numTextLines += additionalInfo.length;
        if (debtor != null)
            numTextLines += debtor.length;
        if (dueDate != null)
            numTextLines += 1;

        double heightWithoutSpacing =
                numBlocks * (FontMetrics.getLineHeight(fontSizeLabel) - FontMetrics.getLeading(fontSizeLabel))
                + numTextLines * (FontMetrics.getLineHeight(fontSizeText) - FontMetrics.getLeading(fontSizeText))
                + (debtor == null ? DEBTOR_HEIGHT : 0);
        double uncompressedSpacing = numBlocks * TEXT_TOP_PADDING + (numBlocks - 1) * LABEL_TOP_PADDING
                + numBlocks * FontMetrics.getLeading(fontSizeLabel)
                + (numTextLines - 1) * FontMetrics.getLeading(fontSizeText);

        double regularHeight = heightWithoutSpacing + uncompressedSpacing;
        double factor = 1;
        if (regularHeight <= 105 - 2 * VERT_BORDER) {
            // text fits without compressed spacing
            textTopPadding = TEXT_TOP_PADDING;
            labelTopPadding = LABEL_TOP_PADDING;
            labelLineHeight = FontMetrics.getLineHeight(fontSizeLabel);
            textLineHeight = FontMetrics.getLineHeight(fontSizeText);

            double titleHeight = FontMetrics.getLineHeight(FONT_SIZE_TITLE) + LABEL_TOP_PADDING;
            if (regularHeight <= 105 - 2 * VERT_BORDER - titleHeight) {
                // align right column with "Supports" line
                rightColumnExtraYOffset = titleHeight;
            } else {
                // align right column at the top
                rightColumnExtraYOffset = 0;
            }
        } else {
            // compressed spacing
            double remainingSpacing = 105 - 2 * VERT_BORDER - heightWithoutSpacing;
            factor = remainingSpacing / uncompressedSpacing;
            textTopPadding = TEXT_TOP_PADDING * factor;
            labelTopPadding = LABEL_TOP_PADDING * factor;
            labelLineHeight = FontMetrics.getLineHeight(fontSizeLabel)
                    - (1 - factor) * FontMetrics.getLeading(fontSizeLabel);
            textLineHeight = FontMetrics.getLineHeight(fontSizeText)
                    - (1 - factor) * FontMetrics.getLeading(fontSizeText);
        }

        return factor;
    }

    private void drawQRCodeOnly() throws IOException {
        qrCode.draw(graphics, 0, 0);
    }

    private void drawCorners(double x, double y, double width, double height) throws IOException {
        final double lwh = 0.5 / 72 * 25.4;
        final double s = 3;

        graphics.startPath();

        graphics.moveTo(x + lwh, y + s);
        graphics.lineTo(x + lwh, y + lwh);
        graphics.lineTo(x + s, y + lwh);

        graphics.moveTo(x + width - s, y + lwh);
        graphics.lineTo(x + width - lwh, y + lwh);
        graphics.lineTo(x + width - lwh, y + s);

        graphics.moveTo(x + width - lwh, y + height - s);
        graphics.lineTo(x + width - lwh, y + height - lwh);
        graphics.lineTo(x + width - s, y + height - lwh);

        graphics.moveTo(x + s, y + height - lwh);
        graphics.lineTo(x + lwh, y + height - lwh);
        graphics.lineTo(x + lwh, y + height - s);

        graphics.strokePath(1, 0);
    }



    private static DecimalFormat amountDisplayFormat;
    private static DateTimeFormatter dateDisplayFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    static {
        amountDisplayFormat = new DecimalFormat("###,##0.00");
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        symbols.setDecimalSeparator('.');
        symbols.setGroupingSeparator('\'');
        amountDisplayFormat.setDecimalFormatSymbols(symbols);
    }

    private static String formatAmountForDisplay(double amount) {
        return amountDisplayFormat.format(amount);
    }

    private static String formatDateForDisplay(LocalDate date) {
        return date.format(dateDisplayFormat);
    }

    private static String formatIBANForDisplay(String iban) {
        StringBuilder sb = new StringBuilder(25);
        sb.append(iban, 0, 4);
        sb.append(" ");
        sb.append(iban, 4, 8);
        sb.append(" ");
        sb.append(iban, 8, 12);
        sb.append(" ");
        sb.append(iban, 12, 16);
        sb.append(" ");
        sb.append(iban, 16, 21);
        return sb.toString();
    }

    private static String formatPersonForDisplay(Address address) {
        StringBuilder sb = new StringBuilder();
        sb.append(address.getName());
        String street = address.getStreet();
        if (street != null) {
            sb.append("\n");
            sb.append(street);
        }
        String houseNo = address.getHouseNo();
        if (houseNo != null) {
            sb.append(street != null ? " " : "\n");
            sb.append(houseNo);
        }
        sb.append("\n");
        sb.append(address.getCountryCode());
        sb.append("-");
        sb.append(address.getPostalCode());
        sb.append(" ");
        sb.append(address.getTown());
        return sb.toString();
    }

    private static String formatReferenceNumber(String refNo) {
        if (refNo == null)
            return null;
        refNo = refNo.trim();
        int len = refNo.length();
        if (len == 0)
            return null;
        if (refNo.startsWith("RF"))
            return refNo;

        StringBuilder sb = new StringBuilder();
        int t = 0;
        while (t < len) {
            int n = t + (len - t - 1) % 4 + 1;
            if (t != 0)
                sb.append(" ");
            sb.append(refNo.substring(t, n));
            t = n;
        }

        return sb.toString();
    }
}
