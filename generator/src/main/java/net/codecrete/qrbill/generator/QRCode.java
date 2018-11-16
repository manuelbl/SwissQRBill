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
import java.util.ArrayList;
import java.util.Locale;

import net.codecrete.io.nayuki.qrcodegen.QrCode;
import net.codecrete.qrbill.canvas.Canvas;
import net.codecrete.qrbill.generator.ValidationMessage.Type;

/**
 * Generates the QR code for the Swiss QR bill.
 * <p>
 * Also provides functions to generate and decode the string embedded in the QR
 * code.
 * </p>
 */
class QRCode {

    static final double SIZE = 46; // mm

    private Bill bill;
    private final StringBuilder textBuilder = new StringBuilder();

    /**
     * Creates an instance of the QR code for the specified bill data.
     * <p>
     * The bill data must have been validated and cleaned.
     * </p>
     * 
     * @param bill bill data
     */
    QRCode(Bill bill) {
        this.bill = bill;
        createQRCodeText();
    }

    /**
     * Gets the text embedded in the QR code (according to the data structure
     * defined by SIX)
     * 
     * @return QR code text
     */
    String getText() {
        return textBuilder.toString();
    }

    /**
     * Draws the QR code to the specified graphics context (canvas). The QR code is
     * always 46 mm by 46 mm.
     * 
     * @param graphics graphics context
     * @param offsetX  x offset
     * @param offsetY  y offset
     * @throws IOException exception thrown in case of error in graphics context
     */
    void draw(Canvas graphics, double offsetX, double offsetY) throws IOException {
        QrCode qrCode = QrCode.encodeText(textBuilder.toString(), QrCode.Ecc.MEDIUM);

        boolean[][] modules = copyModules(qrCode);
        clearSwissCrossArea(modules);

        graphics.setTransformation(offsetX, offsetY, SIZE / modules.length / 25.4 * 72, SIZE / modules.length / 25.4 * 72);
        graphics.startPath();
        drawModulesPath(graphics, modules);
        graphics.fillPath(0);
        graphics.setTransformation(offsetX, offsetY, 1, 1);

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

    private void drawModulesPath(Canvas graphics, boolean[][] modules) throws IOException {
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

    // Simple algorithms to reduce the number of rectangles for drawing the QR code
    // and reduce SVG size
    private void drawLargestRectangle(Canvas graphics, boolean[][] modules, int x, int y) throws IOException {
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
        graphics.addRectangle(x * unit, (size - y - bestH) * unit, bestW * unit, bestH * unit);
        clearRectangle(modules, x, y, bestW, bestH);
    }

    private static void clearSwissCrossArea(boolean[][] modules) {
        // The Swiss cross area is supposed to be 7 by 7 mm in the center of
        // the QR code, which is 46 by 46 mm.
        // We clear sufficient modules to make room for the cross.
        int size = modules.length;
        int start = (int) Math.floor((46 - 6.8) / 2 * size / 46);
        clearRectangle(modules, start, start, size - 2 * start, size - 2 * start);
    }

    private void createQRCodeText() {
        // Header
        textBuilder.append("SPC\n"); // QRType
        textBuilder.append("0200\n"); // Version
        textBuilder.append("1"); // Coding

        // CdtrInf
        appendDataField(bill.getAccount()); // IBAN
        appendPerson(bill.getCreditor()); // Cdtr
        textBuilder.append("\n\n\n\n\n\n\n"); // UltmtCdtr

        // CcyAmt
        appendDataField(bill.getAmount() == null ? "" : formatAmountForCode(bill.getAmount())); // Amt
        appendDataField(bill.getCurrency()); // Ccy

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

        // AddInf
        appendDataField(bill.getUnstructuredMessage()); // Unstrd
        appendDataField("EPD"); // Trailer
        appendDataField(bill.getBillInformation()); // StrdBkgInf

        // AltPmtInf
        if (bill.getAlternativeSchemes() != null && bill.getAlternativeSchemes().length > 0) {
            appendDataField(bill.getAlternativeSchemes()[0]); // AltPmt
            if (bill.getAlternativeSchemes().length > 1)
                appendDataField(bill.getAlternativeSchemes()[1]); // AltPmt
        }

    }

    private void appendPerson(Address address) {
        if (address != null) {
            appendDataField(address.getType() == Address.Type.STRUCTURED ? "S" : "K"); // AdrTp
            appendDataField(address.getName()); // Name
            appendDataField(address.getType() == Address.Type.STRUCTURED
                    ? address.getStreet() : address.getAddressLine1()); // StrtNmOrAdrLine1
            appendDataField(address.getType() == Address.Type.STRUCTURED
                    ? address.getHouseNo() : address.getAddressLine2()); // StrtNmOrAdrLine2
            appendDataField(address.getPostalCode()); // PstCd
            appendDataField(address.getTown()); // TwnNm
            appendDataField(address.getCountryCode()); // Ctry
        } else {
            textBuilder.append("\n\n\n\n\n\n\n");
        }
    }

    private void appendDataField(String value) {
        if (value == null)
            value = "";

        textBuilder.append('\n').append(value);
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

    private static final DecimalFormat amountFieldFormat;

    static {
        amountFieldFormat = new DecimalFormat("#0.00");
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        symbols.setDecimalSeparator('.');
        amountFieldFormat.setDecimalFormatSymbols(symbols);
    }

    private static String formatAmountForCode(double amount) {
        return amountFieldFormat.format(amount);
    }

    /**
     * Decodes the specified text and returns the bill data.
     * <p>
     * The Text is assumed to be in the data structured for the QR code defined by
     * SIX.
     * </p>
     * <p>
     * The returned data is only minimally validated. The format and the header are
     * checked. Amount and date must be parsable.
     * </p>
     * 
     * @param text the text to decode
     * @return the bill data
     * @throws QRBillValidationError if a validation error occurs
     */
    public static Bill decodeQRCodeText(String text) {
        String[] lines = splitLines(text);
        if (lines.length < 32 || lines.length > 34)
            throwSingleValidationError(Bill.FIELD_QR_TYPE, QRBill.KEY_VALID_DATA_STRUCTURE);
        if (!"SPC".equals(lines[0]))
            throwSingleValidationError(Bill.FIELD_QR_TYPE, QRBill.KEY_VALID_DATA_STRUCTURE);
        if (!"0200".equals(lines[1]))
            throwSingleValidationError(Bill.FIELD_VERSION, QRBill.KEY_SUPPORTED_VERSION);
        if (!"1".equals(lines[2]))
            throwSingleValidationError(Bill.FIELD_CODING_TYPE, QRBill.KEY_SUPPORTED_CODING_TYPE);

        Bill bill = new Bill();
        bill.setVersion(Bill.Version.V2_0);

        bill.setAccount(lines[3]);

        bill.setCreditor(decodeAddress(lines, 4, false));

        bill.setFinalCreditor(decodeAddress(lines, 11, true));

        if (lines[18].length() > 0) {
            try {
                bill.setAmount(Double.valueOf(lines[18]));
            } catch (NumberFormatException nfe) {
                throwSingleValidationError(Bill.FIELD_AMOUNT, QRBill.KEY_VALID_NUMBER);
            }
        } else {
            bill.setAmount(null);
        }

        bill.setCurrency(lines[19]);

        bill.setDebtor(decodeAddress(lines, 20, true));

        // reference type is ignored (line 27)
        bill.setReferenceNo(lines[28]);
        bill.setUnstructuredMessage(lines[29]);
        if (!"EPD".equals(lines[30]))
            throwSingleValidationError(Bill.FIELD_QR_TYPE, QRBill.KEY_VALID_DATA_STRUCTURE);

        bill.setBillInformation(lines[31]);

        String[] alternativeSchemes = null;
        if (lines.length > 32) {
            alternativeSchemes = new String[lines.length - 32];
            System.arraycopy(lines, 32, alternativeSchemes, 0, lines.length - 32);
        }
        bill.setAlternativeSchemes(alternativeSchemes);

        return bill;
    }

    /**
     * Process seven lines and extract and address
     * 
     * @param lines      line array
     * @param startLine  index of first line to process
     * @param isOptional indicates if address is optional
     * @return decoded address or {@code null} if address is optional and empty
     */
    private static Address decodeAddress(String[] lines, int startLine, boolean isOptional) {

        boolean isEmpty = lines[startLine].length() == 0 && lines[startLine + 1].length() == 0
                && lines[startLine + 2].length() == 0 && lines[startLine + 3].length() == 0
                && lines[startLine + 4].length() == 0 && lines[startLine + 5].length() == 0
                && lines[startLine + 6].length() == 0;

        if (isEmpty && isOptional)
            return null;

        Address address = new Address();
        boolean isStructuredAddress = "S".equals(lines[startLine]);
        address.setName(lines[startLine + 1]);
        if (isStructuredAddress) {
            address.setStreet(lines[startLine + 2]);
            address.setHouseNo(lines[startLine + 3]);
        } else {
            address.setAddressLine1(lines[startLine + 2]);
            address.setAddressLine2(lines[startLine + 3]);
        }
        if (lines[startLine + 4].length() > 0)
            address.setPostalCode(lines[startLine + 4]);
        if (lines[startLine + 5].length() > 0)
            address.setTown(lines[startLine + 5]);
        address.setCountryCode(lines[startLine + 6]);
        return address;
    }

    private static String[] splitLines(String text) {
        ArrayList<String> lines = new ArrayList<>(32);
        int lastPos = 0;
        while (true) {
            int pos = text.indexOf('\n', lastPos);
            if (pos < 0)
                break;
            int pos2 = pos;
            if (pos2 > lastPos + 1 && text.charAt(pos2 - 1) == '\r')
                pos2--;
            lines.add(text.substring(lastPos, pos2));
            lastPos = pos + 1;
        }

        // add last line
        int pos2 = text.length();
        if (pos2 > lastPos + 1 && text.charAt(pos2 - 1) == '\r')
            pos2--;
        lines.add(text.substring(lastPos, pos2));
        return lines.toArray(new String[0]);
    }

    private static void throwSingleValidationError(String field, String messageKey) {
        ValidationResult result = new ValidationResult();
        result.addMessage(Type.ERROR, field, messageKey);
        throw new QRBillValidationError(result);
    }
}
