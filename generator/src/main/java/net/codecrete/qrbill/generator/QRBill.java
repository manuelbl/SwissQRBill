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

        try (GraphicsPort port = new SVGDrawing()) {
            drawQRCode(port, qrCode);
            return port.getResult();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void drawQRCode(GraphicsPort port, QrCode qrCode) throws IOException {
        int size = qrCode.size;
        port.setTransformation(0, 0, 46.0 / qrCode.size);
        port.startPath();
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                if (qrCode.getModule(x, y)) {
                    port.addRectangle(x, y, 1, 1);
                }
            }
        }
        port.fillPath(0);
        port.setTransformation(0, 0, 1);

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
        port.fillPath(0xffffff);
        port.startPath();
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
