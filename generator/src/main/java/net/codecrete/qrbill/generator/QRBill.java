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


public class QRBill {

    public enum GraphicsFormat {
        PDF,
        SVG
    }

    public enum BillFormat {
        A4PortraitSheet,
        A5LandscapeSheet,
        A6LandscapeSheet,
        QRCodeOnly
    }

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


    private Bill bill;
    private QRCode qrCode;
    private GraphicsGenerator graphics;
    private BillFormat billFormat;
    private GraphicsFormat graphicsFormat;


    public static ValidationResult validate(Bill bill) {
        ValidationResult result = new ValidationResult();
        Validator validator = new Validator(bill, result);
        validator.validate();
        return result;
    }

    public static byte[] generate(Bill bill, BillFormat billFormat, GraphicsFormat graphicsFormat) {
        ValidationResult result = new ValidationResult();
        Validator validator = new Validator(bill, result);
        Bill cleanedBill = validator.validate();
        if (result.hasErrors())
            throw new RuntimeException("Invalid QR bill data");

        QRBill qrBill = new QRBill();
        qrBill.bill = cleanedBill;
        qrBill.qrCode = new QRCode(bill);
        qrBill.billFormat = billFormat;
        qrBill.graphicsFormat = graphicsFormat;
        return qrBill.generateOutput();
    }



    private byte[] generateOutput() {

        double drawingWidth;
        double drawingHeight;

        switch (billFormat) {
            case QRCodeOnly:
                drawingWidth = QRCode.SIZE;
                drawingHeight = QRCode.SIZE;
                break;
            case A6LandscapeSheet:
                drawingWidth = 148.5;
                drawingHeight = 105;
                break;
            case A5LandscapeSheet:
                drawingWidth = 210;
                drawingHeight = 148.5;
                break;
            case A4PortraitSheet:
            default:
                drawingWidth = 210;
                drawingHeight = 297;
                break;
        }

        try (GraphicsGenerator g = new SVGDrawing(drawingWidth, drawingHeight)) {

            graphics = g;
            switch (billFormat) {
                case QRCodeOnly:
                    drawQRCodeOnly();
                    break;
                case A6LandscapeSheet:
                    drawQRBill(0, 0, false);
                    break;
                case A5LandscapeSheet:
                    drawQRBill(61.5, 43.5, true);
                    break;
                case A4PortraitSheet:
                    drawQRBill(61.5, 192, true);
                    break;
            }

            return graphics.getResult();

        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            graphics = null;
        }
    }

    private void drawQRBill(double offsetX, double offsetY, boolean hasBorder) throws IOException {

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
        yPos += FontMetrics.getLineHeight(FONT_SIZE_TITLE) + FontMetrics.getLabelPadding();
        graphics.putText(MultilingualText.getText(MultilingualText.KEY_SUPPORTS, language), 0, yPos, FONT_SIZE_LABEL, true);
        yPos += FontMetrics.getLineHeight(FONT_SIZE_LABEL) + FontMetrics.getTextPadding();
        graphics.putText(MultilingualText.getText(MultilingualText.KEY_CREDIT_TRANSFER, language), 0, yPos, FONT_SIZE_TEXT, false);
        yPos += FontMetrics.getLineHeight(FONT_SIZE_LABEL);

        // QR code section
        double qrCodeSpacing = (105 - VERT_BORDER * 2 - yPos - AMOUNT_HEIGHT
                - FontMetrics.getLineHeight(FONT_SIZE_LABEL) - FontMetrics.getTextPadding() - QRCode.SIZE) / 2;
        qrCode.draw(graphics, offsetX + HORIZ_BORDER, offsetY + VERT_BORDER + yPos + qrCodeSpacing);

        // amount section
        graphics.setTransformation(offsetX + HORIZ_BORDER, offsetY + VERT_BORDER + yPos + 2 * qrCodeSpacing + QRCode.SIZE, 1);
        yPos = 0;
        graphics.putText(MultilingualText.getText(MultilingualText.KEY_CURRENCY, language), 0, yPos, FONT_SIZE_LABEL, true);
        yPos += FontMetrics.getLineHeight(FONT_SIZE_LABEL) + FontMetrics.getTextPadding();
        graphics.putText(bill.getCurrency(), 0, yPos, FONT_SIZE_TEXT, false);

        yPos = 0;
        graphics.putText(MultilingualText.getText(MultilingualText.KEY_AMOUNT, language), LEFT_COLUMN_WIDTH - AMOUNT_WIDTH, yPos, FONT_SIZE_LABEL, true);
        yPos += FontMetrics.getLineHeight(FONT_SIZE_LABEL) + FontMetrics.getTextPadding();
        if (bill.isAmountOpen()) {
            drawCorners(LEFT_COLUMN_WIDTH - AMOUNT_WIDTH, yPos, AMOUNT_WIDTH, AMOUNT_HEIGHT);
        } else {
            graphics.putText(formatAmountForDisplay(bill.getAmount()), LEFT_COLUMN_WIDTH - AMOUNT_WIDTH, yPos, FONT_SIZE_TEXT, false);
        }

        // information section
        graphics.setTransformation(offsetX + HORIZ_BORDER + LEFT_COLUMN_WIDTH + MIDDLE_SPACING, offsetY + VERT_BORDER, 1);
        yPos = 0;
        // account
        graphics.putText(MultilingualText.getText(MultilingualText.KEY_ACCOUNT, language), 0, yPos, FONT_SIZE_LABEL, true);
        yPos += FontMetrics.getLineHeight(FONT_SIZE_LABEL) + FontMetrics.getTextPadding();
        graphics.putText(formatIBANForDisplay(bill.getAccount()), 0, yPos, FONT_SIZE_TEXT, false);
        yPos += FontMetrics.getLineHeight(FONT_SIZE_TEXT) + FontMetrics.getLabelPadding();

        // creditor
        graphics.putText(MultilingualText.getText(MultilingualText.KEY_CREDITOR, language), 0, yPos, FONT_SIZE_LABEL, true);
        yPos += FontMetrics.getLineHeight(FONT_SIZE_LABEL) + FontMetrics.getTextPadding();
        int numLines = graphics.putMultilineText(formatPersonForDisplay(bill.getCreditor()), 0, yPos, RIGHT_COLUMN_WIDTH, FONT_SIZE_TEXT);
        yPos += numLines * FontMetrics.getLineHeight(FONT_SIZE_TEXT) + FontMetrics.getLabelPadding();

        // final creditor
        if (bill.getFinalCreditor() != null && bill.getFinalCreditor().getName() != null && bill.getFinalCreditor().getName().trim().length() != 0) {
            graphics.putText(MultilingualText.getText(MultilingualText.KEY_FINAL_CREDITOR, language), 0, yPos, FONT_SIZE_LABEL, true);
            yPos += FontMetrics.getLineHeight(FONT_SIZE_LABEL) + FontMetrics.getTextPadding();
            numLines = graphics.putMultilineText(formatPersonForDisplay(bill.getFinalCreditor()), 0, yPos, RIGHT_COLUMN_WIDTH, FONT_SIZE_TEXT);
            yPos += numLines * FontMetrics.getLineHeight(FONT_SIZE_TEXT) + FontMetrics.getLabelPadding();
        }

        // reference number
        String refNo = formatReferenceNumber(bill.getReferenceNo());
        if (refNo != null) {
            graphics.putText(MultilingualText.getText(MultilingualText.KEY_REFERENCE_NUMBER, language), 0, yPos, FONT_SIZE_LABEL, true);
            yPos += FontMetrics.getLineHeight(FONT_SIZE_LABEL) + FontMetrics.getTextPadding();
            graphics.putText(refNo, 0, yPos, FONT_SIZE_TEXT, false);
            yPos += FontMetrics.getLineHeight(FONT_SIZE_TEXT) + FontMetrics.getLabelPadding();
        }

        // additional information
        String additionalInfo = bill.getAdditionalInformation();
        if (additionalInfo != null && additionalInfo.trim().length() != 0) {
            int p = additionalInfo.indexOf("##");
            if (p > 0)
                additionalInfo = additionalInfo.substring(0, p) + '\n' + additionalInfo.substring(p);
            graphics.putText(MultilingualText.getText(MultilingualText.KEY_ADDITIONAL_INFORMATION, language), 0, yPos, FONT_SIZE_LABEL, true);
            yPos += FontMetrics.getLineHeight(FONT_SIZE_LABEL) + FontMetrics.getTextPadding();
            numLines = graphics.putMultilineText(additionalInfo, 0, yPos, RIGHT_COLUMN_WIDTH, FONT_SIZE_TEXT);
            yPos += numLines * FontMetrics.getLineHeight(FONT_SIZE_TEXT) + FontMetrics.getLabelPadding();
        }

        // debtor
        graphics.putText(MultilingualText.getText(MultilingualText.KEY_DEBTOR, language), 0, yPos, FONT_SIZE_LABEL, true);
        yPos += FontMetrics.getLineHeight(FONT_SIZE_LABEL) + FontMetrics.getTextPadding();
        if (bill.isDebtorOpen()) {
            drawCorners(0, yPos, RIGHT_COLUMN_WIDTH, DEBTOR_HEIGHT);
            yPos += 25 + FontMetrics.getLabelPadding();
        } else {
            numLines = graphics.putMultilineText(formatPersonForDisplay(bill.getDebtor()), 0, yPos, RIGHT_COLUMN_WIDTH, FONT_SIZE_TEXT);
            yPos += numLines * FontMetrics.getLineHeight(FONT_SIZE_TEXT) + FontMetrics.getLabelPadding();
        }

        if (bill.getDueDate() != null) {
            graphics.putText(MultilingualText.getText(MultilingualText.KEY_DUE_DATE, language), 0, yPos, FONT_SIZE_LABEL, true);
            yPos += FontMetrics.getLineHeight(FONT_SIZE_LABEL) + FontMetrics.getTextPadding();
            graphics.putText(formatDateForDisplay(bill.getDueDate()), 0, yPos, FONT_SIZE_TEXT, false);
        }
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



    private static DecimalFormat AMOUNT_DISPLAY_FORMAT;
    private static DateTimeFormatter DATE_DISPLAY_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    static {
        AMOUNT_DISPLAY_FORMAT = new DecimalFormat("###,##0.00");
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        symbols.setDecimalSeparator('.');
        symbols.setGroupingSeparator('\'');
        AMOUNT_DISPLAY_FORMAT.setDecimalFormatSymbols(symbols);
    }

    private static String formatAmountForDisplay(double amount) {
        return AMOUNT_DISPLAY_FORMAT.format(amount);
    }

    private static String formatDateForDisplay(LocalDate date) {
        return date.format(DATE_DISPLAY_FORMAT);
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

    private static String formatPersonForDisplay(Person person) {
        StringBuilder sb = new StringBuilder();
        sb.append(person.getName());
        String street = person.getStreet();
        if (street != null) {
            sb.append("\n");
            sb.append(street);
        }
        String houseNo = person.getHouseNumber();
        if (houseNo != null) {
            sb.append(street != null ? " " : "\n");
            sb.append(houseNo);
        }
        sb.append("\n");
        sb.append(person.getCountryCode());
        sb.append("-");
        sb.append(person.getPostalCode());
        sb.append(" ");
        sb.append(person.getCity());
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
