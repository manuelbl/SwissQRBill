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
                    drawQRBill(port, bill, qrCode, 0, 0);
                    break;
            }

            return port.getResult();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void drawQRBill(GraphicsGenerator port, Bill bill, QrCode qrCode, double offsetX, double offsetY) throws IOException {

        Bill.Language language = bill.getLanguage();
        port.setTransformation(5, 5, 1);
        double yPos = 0;
        port.putText(MultilingualText.getText(MultilingualText.KEY_QR_BILL_PAYMENT_PART, language), 0, yPos, 11, true);
        yPos += FontMetrics.getLineHeight(11) + FontMetrics.getLabelPadding();
        port.putText(MultilingualText.getText(MultilingualText.KEY_SUPPORTS, language), 0, yPos, 8, true);
        yPos += FontMetrics.getLineHeight(8) + FontMetrics.getTextPadding();
        port.putText(MultilingualText.getText(MultilingualText.KEY_CREDIT_TRANSFER, language), 0, yPos, 10, false);
        drawQRCode(port, qrCode, offsetX + 5, offsetY + 30);
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
        appendDataField(sb, bill.isAmountOpen() ? "" : formatAmount(bill.getAmount())); // Amt
        appendDataField(sb, bill.getCurrency()); // Ccy
        appendDataField(sb, bill.getDueDate() != null ? formatDate(bill.getDueDate()) : ""); // ReqdExctnDt

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

    static {
        AMOUNT_FIELD_FORMAT = new DecimalFormat("#0.00");
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.ENGLISH);
        symbols.setDecimalSeparator('.');
        AMOUNT_FIELD_FORMAT.setDecimalFormatSymbols(symbols);
    }

    private static String formatAmount(double amount) {
        return AMOUNT_FIELD_FORMAT.format(amount);
    }


    private static String formatDate(LocalDate date) {
        return date.format(DateTimeFormatter.ISO_LOCAL_DATE);
    }

}
