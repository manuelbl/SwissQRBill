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

class QRCode {
    static final double SIZE = 46; // mm
    private static final String CRLF = "\r\n";

    private Bill bill;
    private StringBuilder textBuilder = new StringBuilder();

    QRCode(Bill bill) {
        this.bill = bill;
        createQRCodeText();
    }

    String getText() {
        return textBuilder.toString();
    }

    void draw(GraphicsGenerator graphics, double offsetX, double offsetY) throws IOException {
        QrCode qrCode = QrCode.encodeText(textBuilder.toString(), QrCode.Ecc.MEDIUM);

        int size = qrCode.size;
        final double unit = 25.4 / 72;
        graphics.setTransformation(offsetX, offsetY, SIZE / qrCode.size / unit);
        graphics.startPath();
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                if (qrCode.getModule(x, y)) {
                    graphics.addRectangle(x * unit, y * unit, unit, unit);
                }
            }
        }
        graphics.fillPath(0);
        graphics.setTransformation(offsetX, offsetY, 1);

        // Swiss cross
        graphics.startPath();
        graphics.addRectangle(19.5, 19.5, 7, 7);
        graphics.fillPath(0xffffff);
        graphics.startPath();
        graphics.addRectangle(20, 20, 6, 6);
        graphics.fillPath(0);
        final double BAR_WIDTH = 7 / 6.0;
        final double BAR_LENGTH = 35 / 9.0;
        graphics.startPath();
        graphics.addRectangle(23 - BAR_WIDTH / 2, 23 - BAR_LENGTH / 2, BAR_WIDTH, BAR_LENGTH);
        graphics.addRectangle(23 - BAR_LENGTH / 2, 23 - BAR_WIDTH / 2, BAR_LENGTH, BAR_WIDTH);
        graphics.fillPath(0xffffff);
    }

    private void createQRCodeText() {
        // Header
        textBuilder.append("SPC"); // QRType
        appendDataField("0100"); // Version
        appendDataField("1"); // Coding

        // CdtrInf
        appendDataField(bill.getAccount()); // IBAN
        appendPerson(bill.getCreditor()); // Cdtr

        // UltmtCdtr
        appendPerson(bill.getFinalCreditor());

        // CCyAmtDate
        appendDataField(bill.isAmountOpen() ? "" : formatAmountForCode(bill.getAmount())); // Amt
        appendDataField(bill.getCurrency()); // Ccy
        appendDataField(bill.getDueDate() != null ? formatDateForCode(bill.getDueDate()) : ""); // ReqdExctnDt

        // UltmtDbtr
        appendPerson(bill.isDebtorOpen() ? null : bill.getDebtor());

        // RmtInf
        String referenceType = "NON";
        if (bill.getReferenceNo() != null) {
            if (bill.getReferenceNo().startsWith("RF"))
                referenceType = "SCOR";
            else if (bill.getReferenceNo().length() > 0)
                referenceType = "QRR";
        }
        appendDataField(referenceType); // Tp
        appendDataField(bill.getReferenceNo()); // Ref
        appendDataField(bill.getAdditionalInformation()); // Unstrd
    }

    private void appendPerson(Person person) {
        if (person != null) {
            appendDataField(person.getName()); // Name
            appendDataField(person.getStreet()); // StrtNm
            appendDataField(person.getHouseNumber()); // BldgNb
            appendDataField(person.getPostalCode()); // PstCd
            appendDataField(person.getCity()); // TwnNm
            appendDataField(person.getCountryCode()); // Ctrty
        } else {
            for (int i = 0; i < 6; i++)
                appendDataField("");
        }
    }

    private void appendDataField(String value) {
        if (value == null)
            value = "";

        textBuilder.append(CRLF).append(value);
    }

    private static DecimalFormat AMOUNT_FIELD_FORMAT;

    static {
        AMOUNT_FIELD_FORMAT = new DecimalFormat("#0.00");
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        symbols.setDecimalSeparator('.');
        AMOUNT_FIELD_FORMAT.setDecimalFormatSymbols(symbols);
    }

    private static String formatAmountForCode(double amount) {
        return AMOUNT_FIELD_FORMAT.format(amount);
    }

    private static String formatDateForCode(LocalDate date) {
        return date.format(DateTimeFormatter.ISO_LOCAL_DATE);
    }
}
