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

        boolean[][] modules = copyModules(qrCode);
        clearSwissCrossArea(modules);

        graphics.setTransformation(offsetX, offsetY, SIZE / modules.length / 25.4 * 72);
        graphics.startPath();
        drawModulesPath(graphics, modules);
        graphics.fillPath(0);
        graphics.setTransformation(offsetX, offsetY, 1);

        // Swiss cross
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

    private void drawModulesPath(GraphicsGenerator graphics, boolean[][] modules) throws IOException {
        // Simple algorithm to reduce the number of drawn rectangles
        int size = modules.length;
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                if (modules[y][x]) {
                    drawLargestRectangle(graphics, modules, x, y);
                }
            }
        }
    }

    // Simple algorithms to reduce the number of rectangles for drawing the QR code and reduce SVG size
    private void drawLargestRectangle(GraphicsGenerator graphics, boolean[][] modules, int x, int y) throws IOException {
        int size = modules.length;

        int bestW = 1;
        int bestH = 1;
        int maxArea = 1;

        int xLimit = size;
        int iy = y;
        while (iy < size && modules[iy][x]) {
            int w = 0;
            while (x + w < xLimit && modules[iy][x + w])
                w++;
            int area = w * (iy - y + 1);
            if (area > maxArea) {
                maxArea = area;
                bestW = w;
                bestH = iy - y + 1;
            }
            xLimit = x + w;
            iy++;
        }

        final double unit = 25.4 / 72;
        graphics.addRectangle(x * unit, y * unit, bestW * unit, bestH * unit);
        clearRectangle(modules, x, y, bestW, bestH);
    }

    private static void clearSwissCrossArea(boolean[][] modules) {
        // The Swiss cross area is supposed to be 7 by 7 mm in the center of
        // the QR code, which is 46 by 46 mm.
        // We clear sufficient modules to make room for the cross.
        int size = modules.length;
        int start = (int)Math.floor((46 - 6.8) / 2 * size / 46);
        clearRectangle(modules, start, start, size - 2 * start, size - 2 * start);
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
        appendDataField(bill.getAmount() == null ? "" : formatAmountForCode(bill.getAmount())); // Amt
        appendDataField(bill.getCurrency()); // Ccy
        appendDataField(bill.getDueDate() != null ? formatDateForCode(bill.getDueDate()) : ""); // ReqdExctnDt

        // UltmtDbtr
        appendPerson(bill.getDebtor());

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
        appendDataField(bill.getAdditionalInfo()); // Unstrd
    }

    private void appendPerson(Address address) {
        if (address != null) {
            appendDataField(address.getName()); // Name
            appendDataField(address.getStreet()); // StrtNm
            appendDataField(address.getHouseNo()); // BldgNb
            appendDataField(address.getPostalCode()); // PstCd
            appendDataField(address.getTown()); // TwnNm
            appendDataField(address.getCountryCode()); // Ctrty
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

    private static boolean[][] copyModules(QrCode qrCode) {
        int size = qrCode.size;
        boolean[][] modules = new boolean[size][size];
        for (int y = 0; y < size; y++)
            for (int x = 0; x < size; x++)
                modules[y][x] = qrCode.getModule(x, y);
        return modules;
    }

    private static void clearRectangle(boolean[][] modules, int x, int y, int width, int height) {
        for (int iy = y; iy < y + height; iy++)
            for (int ix = x; ix < x + width; ix++)
                modules[iy][ix] = false;
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
