//
// Swiss QR Bill Generator
// Copyright (c) 2017 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
package net.codecrete.qrbill.generator;

import io.nayuki.qrcodegen.QrCode;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;


public class QRBill {

    public enum GraphicsFormat {
        PDF,
        SVG,
        PNG
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
    private static final double LEFT_COLUMN_WIDTH = 54; // mm
    private static final double AMOUNT_WIDTH = 40; // mm (must not be smaller than 40)
    private static final double AMOUNT_HEIGHT = 15; // mm (must not be smaller than 15)
    private static final double RIGHT_COLUMN_WIDTH
            = 148.5 - 2 * HORIZ_BORDER - MIDDLE_SPACING - LEFT_COLUMN_WIDTH; // mm (must not be smaller than 65)
    private static final double DEBTOR_HEIGHT = 25; // mm (must no be smaller than 25)


    public ValidationResult[] validate() {
        return null;
    }

    public static byte[] generate(Bill bill, BillFormat billFormat, GraphicsFormat graphicsFormat) {

        String qrCodeText = createQRCodeText(bill);
        QrCode qrCode = QrCode.encodeText(qrCodeText, QrCode.Ecc.MEDIUM);

        double drawingWidth;
        double drawingHeight;

        switch (billFormat) {
            case QRCodeOnly:
                drawingWidth = 46;
                drawingHeight = 46;
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

        try (GraphicsGenerator port = new SVGDrawing(drawingWidth, drawingHeight)) {

            switch (billFormat) {
                case QRCodeOnly:
                    drawQRCodeOnly(port, qrCode);
                    break;
                case A6LandscapeSheet:
                    drawQRBill(port, bill, qrCode);
                    break;
            }

            return port.getResult();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void drawQRBill(GraphicsGenerator port, Bill bill, QrCode qrCode) throws IOException {

        Bill.Language language = bill.getLanguage();

        // title section
        port.setTransformation(HORIZ_BORDER, VERT_BORDER, 1);
        double yPos = 0;
        port.putText(MultilingualText.getText(MultilingualText.KEY_QR_BILL_PAYMENT_PART, language), 0, yPos, FONT_SIZE_TITLE, true);

        // scheme section
        yPos += FontMetrics.getLineHeight(FONT_SIZE_TITLE) + FontMetrics.getLabelPadding();
        port.putText(MultilingualText.getText(MultilingualText.KEY_SUPPORTS, language), 0, yPos, FONT_SIZE_LABEL, true);
        yPos += FontMetrics.getLineHeight(FONT_SIZE_LABEL) + FontMetrics.getTextPadding();
        port.putText(MultilingualText.getText(MultilingualText.KEY_CREDIT_TRANSFER, language), 0, yPos, FONT_SIZE_TEXT, false);
        yPos += FontMetrics.getLineHeight(FONT_SIZE_LABEL);

        // QR code section
        double qrCodeSpacing = (105 - VERT_BORDER * 2 - yPos - AMOUNT_HEIGHT - FontMetrics.getLineHeight(FONT_SIZE_LABEL) - FontMetrics.getTextPadding() - 46) / 2;
        drawQRCode(port, qrCode, HORIZ_BORDER, VERT_BORDER + yPos + qrCodeSpacing);

        // amount section
        port.setTransformation(HORIZ_BORDER, VERT_BORDER + yPos + 2 * qrCodeSpacing + 46, 1);
        yPos = 0;
        port.putText(MultilingualText.getText(MultilingualText.KEY_CURRENCY, language), 0, yPos, FONT_SIZE_LABEL, true);
        yPos += FontMetrics.getLineHeight(FONT_SIZE_LABEL) + FontMetrics.getTextPadding();
        port.putText(bill.getCurrency(), 0, yPos, FONT_SIZE_TEXT, false);

        yPos = 0;
        port.putText(MultilingualText.getText(MultilingualText.KEY_AMOUNT, language), LEFT_COLUMN_WIDTH - AMOUNT_WIDTH, yPos, FONT_SIZE_LABEL, true);
        yPos += FontMetrics.getLineHeight(FONT_SIZE_LABEL) + FontMetrics.getTextPadding();
        if (bill.isAmountOpen()) {
            drawCorners(port, LEFT_COLUMN_WIDTH - AMOUNT_WIDTH, yPos, AMOUNT_WIDTH, AMOUNT_HEIGHT);
        } else {
            port.putText(formatAmountForDisplay(bill.getAmount()), LEFT_COLUMN_WIDTH - AMOUNT_WIDTH, yPos, FONT_SIZE_TEXT, false);
        }

        // information section
        port.setTransformation(HORIZ_BORDER + LEFT_COLUMN_WIDTH + MIDDLE_SPACING, VERT_BORDER, 1);
        yPos = 0;
        // account
        port.putText(MultilingualText.getText(MultilingualText.KEY_ACCOUNT, language), 0, yPos, FONT_SIZE_LABEL, true);
        yPos += FontMetrics.getLineHeight(FONT_SIZE_LABEL) + FontMetrics.getTextPadding();
        port.putText(formatIBANForDisplay(bill.getAccount()), 0, yPos, FONT_SIZE_TEXT, false);
        yPos += FontMetrics.getLineHeight(FONT_SIZE_TEXT) + FontMetrics.getLabelPadding();

        // creditor
        port.putText(MultilingualText.getText(MultilingualText.KEY_CREDITOR, language), 0, yPos, FONT_SIZE_LABEL, true);
        yPos += FontMetrics.getLineHeight(FONT_SIZE_LABEL) + FontMetrics.getTextPadding();
        int numLines = port.putMultilineText(formatPersonForDisplay(bill.getCreditor()), 0, yPos, RIGHT_COLUMN_WIDTH, FONT_SIZE_TEXT, false);
        yPos += numLines * FontMetrics.getLineHeight(FONT_SIZE_TEXT) + FontMetrics.getLabelPadding();

        // final creditor
        if (bill.getFinalCreditor() != null && bill.getFinalCreditor().getName() != null && bill.getFinalCreditor().getName().trim().length() != 0) {
            port.putText(MultilingualText.getText(MultilingualText.KEY_FINAL_CREDITOR, language), 0, yPos, FONT_SIZE_LABEL, true);
            yPos += FontMetrics.getLineHeight(FONT_SIZE_LABEL) + FontMetrics.getTextPadding();
            numLines = port.putMultilineText(formatPersonForDisplay(bill.getFinalCreditor()), 0, yPos, RIGHT_COLUMN_WIDTH, FONT_SIZE_TEXT, false);
            yPos += numLines * FontMetrics.getLineHeight(FONT_SIZE_TEXT) + FontMetrics.getLabelPadding();
        }

        // reference number
        String refNo = formatReferenceNumber(bill.getReferenceNo());
        if (refNo != null) {
            port.putText(MultilingualText.getText(MultilingualText.KEY_REFERENCE_NUMBER, language), 0, yPos, FONT_SIZE_LABEL, true);
            yPos += FontMetrics.getLineHeight(FONT_SIZE_LABEL) + FontMetrics.getTextPadding();
            port.putText(refNo, 0, yPos, FONT_SIZE_TEXT, false);
            yPos += FontMetrics.getLineHeight(FONT_SIZE_TEXT) + FontMetrics.getLabelPadding();
        }

        // additional information
        String additionalInfo = bill.getAdditionalInformation();
        if (additionalInfo != null && additionalInfo.trim().length() != 0) {
            int p = additionalInfo.indexOf("##");
            if (p > 0)
                additionalInfo = additionalInfo.substring(0, p) + '\n' + additionalInfo.substring(p);
            port.putText(MultilingualText.getText(MultilingualText.KEY_ADDITIONAL_INFORMATION, language), 0, yPos, FONT_SIZE_LABEL, true);
            yPos += FontMetrics.getLineHeight(FONT_SIZE_LABEL) + FontMetrics.getTextPadding();
            numLines = port.putMultilineText(additionalInfo, 0, yPos, RIGHT_COLUMN_WIDTH, FONT_SIZE_TEXT, false);
            yPos += numLines * FontMetrics.getLineHeight(FONT_SIZE_TEXT) + FontMetrics.getLabelPadding();
        }

        // debtor
        port.putText(MultilingualText.getText(MultilingualText.KEY_DEBTOR, language), 0, yPos, FONT_SIZE_LABEL, true);
        yPos += FontMetrics.getLineHeight(FONT_SIZE_LABEL) + FontMetrics.getTextPadding();
        if (bill.isDebtorOpen()) {
            drawCorners(port, 0, yPos, RIGHT_COLUMN_WIDTH, DEBTOR_HEIGHT);
            yPos += 25 + FontMetrics.getLabelPadding();
        } else {
            numLines = port.putMultilineText(formatPersonForDisplay(bill.getDebtor()), 0, yPos, RIGHT_COLUMN_WIDTH, FONT_SIZE_TEXT, false);
            yPos += numLines * FontMetrics.getLineHeight(FONT_SIZE_TEXT) + FontMetrics.getLabelPadding();
        }

        if (bill.getDueDate() != null) {
            port.putText(MultilingualText.getText(MultilingualText.KEY_DUE_DATE, language), 0, yPos, FONT_SIZE_LABEL, true);
            yPos += FontMetrics.getLineHeight(FONT_SIZE_LABEL) + FontMetrics.getTextPadding();
            port.putText(formatDateForDisplay(bill.getDueDate()), 0, yPos, FONT_SIZE_TEXT, false);
        }
    }

    private static void drawQRCodeOnly(GraphicsGenerator port, QrCode qrCode) throws IOException {
        drawQRCode(port, qrCode, 0, 0);
    }

    private static void drawQRCode(GraphicsGenerator port, QrCode qrCode, double offsetX, double offsetY) throws IOException {
        int size = qrCode.size;
        final double unit = 25.4 / 72;
        port.setTransformation(offsetX, offsetY, 46.0 / qrCode.size / unit);
        port.startPath();
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                if (qrCode.getModule(x, y)) {
                    port.addRectangle(x * unit, y * unit, unit, unit);
                }
            }
        }
        port.fillPath(0);
        port.setTransformation(offsetX, offsetY, 1);

        // Swiss cross
        port.startPath();
        port.addRectangle(19.5, 19.5, 7, 7);
        port.fillPath(0xffffff);
        port.startPath();
        port.addRectangle(20, 20, 6, 6);
        port.fillPath(0);
        final double BAR_WIDTH = 7 / 6.0;
        final double BAR_LENGTH = 35 / 9.0;
        port.startPath();
        port.addRectangle(23 - BAR_WIDTH / 2, 23 - BAR_LENGTH / 2, BAR_WIDTH, BAR_LENGTH);
        port.addRectangle(23 - BAR_LENGTH / 2, 23 - BAR_WIDTH / 2, BAR_LENGTH, BAR_WIDTH);
        port.fillPath(0xffffff);
    }

    private static void drawCorners(GraphicsGenerator generator, double x, double y, double width, double height) throws IOException {
        final double lwh = 0.5 / 72 * 25.4;
        final double s = 3;

        generator.startPath();

        generator.moveTo(x + lwh, y + s);
        generator.lineTo(x + lwh, y + lwh);
        generator.lineTo(x + s, y + lwh);

        generator.moveTo(x + width - s, y + lwh);
        generator.lineTo(x + width - lwh, y + lwh);
        generator.lineTo(x + width - lwh, y + s);

        generator.moveTo(x + width - lwh, y + height - s);
        generator.lineTo(x + width - lwh, y + height - lwh);
        generator.lineTo(x + width - s, y + height - lwh);

        generator.moveTo(x + s, y + height - lwh);
        generator.lineTo(x + lwh, y + height - lwh);
        generator.lineTo(x + lwh, y + height - s);

        generator.strokePath(1, 0);
    }


    public static String createQRCodeText(Bill bill) {
        StringBuilder sb = new StringBuilder();

        // Header
        sb.append("SPC"); // QRType
        appendDataField(sb, "0100"); // Version
        appendDataField(sb, "1"); // Coding

        // CdtrInf
        appendDataField(sb, bill.getAccount()); // IBAN
        appendPerson(sb, bill.getCreditor()); // Cdtr

        // UltmtCdtr
        appendPerson(sb, bill.getFinalCreditor());

        // CCyAmtDate
        appendDataField(sb, bill.isAmountOpen() ? "" : formatAmountForCode(bill.getAmount())); // Amt
        appendDataField(sb, bill.getCurrency()); // Ccy
        appendDataField(sb, bill.getDueDate() != null ? formatDateForCode(bill.getDueDate()) : ""); // ReqdExctnDt

        // UltmtDbtr
        appendPerson(sb, bill.isDebtorOpen() ? null : bill.getDebtor());

        // RmtInf
        String referenceType = "NON";
        if (bill.getReferenceNo() != null) {
            if (bill.getReferenceNo().startsWith("RF"))
                referenceType = "SCOR";
            else if (bill.getReferenceNo().length() > 0)
                referenceType = "QRR";
        }
        appendDataField(sb, referenceType); // Tp
        appendDataField(sb, bill.getReferenceNo()); // Ref
        appendDataField(sb, bill.getAdditionalInformation()); // Unstrd

        return sb.toString();
    }

    private static void appendPerson(StringBuilder sb, Person person) {
        if (person != null) {
            appendDataField(sb, person.getName()); // Name
            appendDataField(sb, person.getStreet()); // StrtNm
            appendDataField(sb, person.getHouseNumber()); // BldgNb
            appendDataField(sb, person.getPostalCode()); // PstCd
            appendDataField(sb, person.getCity()); // TwnNm
            appendDataField(sb, person.getCountryCode()); // Ctrty
        } else {
            for (int i = 0; i < 6; i++)
                appendDataField(sb, "");
        }
    }

    private static final String CRLF = "\r\n";

    private static void appendDataField(StringBuilder sb, String value) {
        if (value == null)
            value = "";

        sb.append(CRLF).append(value);
    }

    private static DecimalFormat AMOUNT_FIELD_FORMAT;
    private static DecimalFormat AMOUNT_DISPLAY_FORMAT;
    private static DateTimeFormatter DATE_DISPLAY_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    static {
        AMOUNT_FIELD_FORMAT = new DecimalFormat("#0.00");
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        symbols.setDecimalSeparator('.');
        AMOUNT_FIELD_FORMAT.setDecimalFormatSymbols(symbols);
        AMOUNT_DISPLAY_FORMAT = new DecimalFormat("###,##0.00");
        symbols = new DecimalFormatSymbols(Locale.US);
        symbols.setDecimalSeparator('.');
        symbols.setGroupingSeparator('\'');
        AMOUNT_DISPLAY_FORMAT.setDecimalFormatSymbols(symbols);
    }

    private static String formatAmountForCode(double amount) {
        return AMOUNT_FIELD_FORMAT.format(amount);
    }

    private static String formatAmountForDisplay(double amount) {
        return AMOUNT_DISPLAY_FORMAT.format(amount);
    }

    private static String formatDateForCode(LocalDate date) {
        return date.format(DateTimeFormatter.ISO_LOCAL_DATE);
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
