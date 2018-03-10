//
// Swiss QR Bill Generator
// Copyright (c) 2017 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
package net.codecrete.qrbill.generator;

import net.codecrete.io.nayuki.qrcodegen.QrCode;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Locale;

import static net.codecrete.qrbill.generator.ValidationMessage.*;

/**
 * Generates the QR code for the Swiss QR bill.
 * <p>
 *     Also provides functions to generate and decode the string embedded in the QR code.
 * </p>
 */
public class QRCode {
    static final double SIZE = 46; // mm
    private static final String CRLF = "\r\n";

    private Bill bill;
    private StringBuilder textBuilder = new StringBuilder();

    /**
     * Creates an instance of the QR code for the specified bill data.
     * @param bill bill data
     */
    public QRCode(Bill bill) {
        this.bill = bill;
        createQRCodeText();
    }

    /**
     * Gets the text embedded in the QR code (according to the data structure defined by SIX)
     * @return QR code text
     */
    public String getText() {
        return textBuilder.toString();
    }

    /**
     * Draws the QR code to the specified graphics context. The QR code is always 46 mm by 46 mm.
     * @param graphics graphics context
     * @param offsetX x offset
     * @param offsetY y offset
     * @throws IOException exception thrown in case of error in graphics context
     */
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

    private static DecimalFormat amountFieldFormat;

    static {
        amountFieldFormat = new DecimalFormat("#0.00");
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        symbols.setDecimalSeparator('.');
        amountFieldFormat.setDecimalFormatSymbols(symbols);
    }

    private static String formatAmountForCode(double amount) {
        return amountFieldFormat.format(amount);
    }

    private static String formatDateForCode(LocalDate date) {
        return date.format(DateTimeFormatter.ISO_LOCAL_DATE);
    }

    /**
     * Decodes the specified text and returns the bill data.
     * <p>
     *     The Text is assumed to be in the data structured for the QR code
     *     defined by SIX.
     * </p>
     * <p>
     *     The returned data is not validated. Only the format and the header are checked.
     * </p>
     * @param text the text to decode
     * @return the bill data
     */
    public static Bill decodeQRCodeText(String text) {
        String[] lines = splitLines(text);
        if (lines.length < 28 || lines.length > 30)
            throwSingleValidationError(Bill.FIELD_QR_TYPE, "valid_data_structure");
        if (!"SPC".equals(lines[0]))
            throwSingleValidationError(Bill.FIELD_QR_TYPE, "valid_data_structure");
        if (!"0100".equals(lines[1]))
            throwSingleValidationError(Bill.FIELD_VERSION, "supported_version");
        if (!"1".equals(lines[2]))
            throwSingleValidationError(Bill.FIELD_CODING_TYPE, "supported_coding_type");

        Bill bill = new Bill();
        bill.setVersion(Bill.Version.V1_0);

        bill.setAccount(lines[3]);

        Address creditor = new Address();
        bill.setCreditor(creditor);
        creditor.setName(lines[4]);
        creditor.setStreet(lines[5]);
        creditor.setHouseNo(lines[6]);
        creditor.setPostalCode(lines[7]);
        creditor.setTown(lines[8]);
        creditor.setCountryCode(lines[9]);

        if (lines[10].length() > 0 && lines[11].length() > 0 && lines[12].length() > 0
                && lines[13].length() > 0 && lines[14].length() > 0 && lines[15].length() > 0) {
            Address finalCreditor = new Address();
            bill.setFinalCreditor(finalCreditor);
            finalCreditor.setName(lines[10]);
            finalCreditor.setStreet(lines[11]);
            finalCreditor.setHouseNo(lines[12]);
            finalCreditor.setPostalCode(lines[13]);
            finalCreditor.setTown(lines[14]);
            finalCreditor.setCountryCode(lines[15]);
        } else {
            bill.setFinalCreditor(null);
        }

        if (lines[16].length() > 0) {
            try {
                bill.setAmount(Double.valueOf(lines[16]));
            } catch (NumberFormatException nfe) {
                throwSingleValidationError(Bill.FIELD_AMOUNT, "valid_number");
            }
        } else {
            bill.setAmount(null);
        }

        bill.setCurrency(lines[17]);

        if (lines[18].length() > 0) {
            try {
                bill.setDueDate(LocalDate.parse(lines[18], DateTimeFormatter.ISO_LOCAL_DATE));
            } catch (DateTimeParseException dtpe) {
                throwSingleValidationError(Bill.FIELD_DUE_DATE, "valid_date");
            }
        } else {
            bill.setDueDate(null);
        }

        if (lines[19].length() > 0 && lines[20].length() > 0 && lines[21].length() > 0
                && lines[22].length() > 0 && lines[23].length() > 0 && lines[24].length() > 0) {
            Address debtor = new Address();
            bill.setDebtor(debtor);
            debtor.setName(lines[19]);
            debtor.setStreet(lines[20]);
            debtor.setHouseNo(lines[21]);
            debtor.setPostalCode(lines[22]);
            debtor.setTown(lines[23]);
            debtor.setCountryCode(lines[24]);
        } else {
            bill.setDebtor(null);
        }

        // reference type is ignored (line 25)
        bill.setReferenceNo(lines[26]);
        bill.setAdditionalInfo(lines[27]);

        // remaining lines (alternative schemes) are ignored
        return bill;
    }

    private static String[] splitLines(String text) {
        ArrayList<String> lines = new ArrayList<>(32);
        int lastPos = 0;
        while (true) {
            int pos = text.indexOf(CRLF, lastPos);
            if (pos < 0)
                break;
            lines.add(text.substring(lastPos, pos));
            lastPos = pos + CRLF.length();
        }

        // add last line
        lines.add(text.substring(lastPos));
        return lines.toArray(new String[lines.size()]);
    }

    private static void throwSingleValidationError(String field, String messageKey) {
        ValidationResult result = new ValidationResult();
        result.addMessage(Type.ERROR, field, messageKey);
        throw new QRBillValidationError(result);
    }
}
