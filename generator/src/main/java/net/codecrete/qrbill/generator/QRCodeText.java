//
// Swiss QR Bill Generator
// Copyright (c) 2018 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
package net.codecrete.qrbill.generator;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Locale;

public class QRCodeText {

    private Bill bill;
    private StringBuilder textBuilder;

    private QRCodeText(Bill bill) {
        this.bill = bill;
    }

    /**
     * Gets the text embedded in the QR code (according to the data structure
     * defined by SIX)
     *
     * @param bill bill data
     *
     * @return QR code text
     */
    public static String create(Bill bill) {
        QRCodeText qrCodeText = new QRCodeText(bill);
        return qrCodeText.createText();
    }

    private String createText() {
        textBuilder = new StringBuilder();

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
        if (bill.getReference() != null) {
            if (bill.getReference().startsWith("RF"))
                referenceType = "SCOR";
            else if (bill.getReference().length() > 0)
                referenceType = "QRR";
        }
        appendDataField(referenceType); // Tp
        appendDataField(bill.getReference()); // Ref

        // AddInf
        appendDataField(bill.getUnstructuredMessage()); // Unstrd
        appendDataField("EPD"); // Trailer
        appendDataField(bill.getBillInformation()); // StrdBkgInf

        // AltPmtInf
        if (bill.getAlternativeSchemes() != null && bill.getAlternativeSchemes().length > 0) {
            appendDataField(bill.getAlternativeSchemes()[0].getInstruction()); // AltPmt
            if (bill.getAlternativeSchemes().length > 1)
                appendDataField(bill.getAlternativeSchemes()[1].getInstruction()); // AltPmt
        }

        return textBuilder.toString();
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

    private static final DecimalFormat amountFieldFormat;

    static {
        amountFieldFormat = new DecimalFormat("#0.00");
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        symbols.setDecimalSeparator('.');
        amountFieldFormat.setDecimalFormatSymbols(symbols);
        amountFieldFormat.setParseBigDecimal(true);
    }

    private static String formatAmountForCode(BigDecimal amount) {
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
    public static Bill decode(String text) {
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

        if (lines[18].length() > 0) {
            ParsePosition position = new ParsePosition(0);
            BigDecimal amount = (BigDecimal)amountFieldFormat.parse(lines[18], position);
            if (position.getIndex() == lines[18].length())
                bill.setAmount(amount);
            else
                throwSingleValidationError(Bill.FIELD_AMOUNT, QRBill.KEY_VALID_NUMBER);
        } else {
            bill.setAmount(null);
        }

        bill.setCurrency(lines[19]);

        bill.setDebtor(decodeAddress(lines, 20, true));

        // reference type is ignored (line 27)
        bill.setReference(lines[28]);
        bill.setUnstructuredMessage(lines[29]);
        if (!"EPD".equals(lines[30]))
            throwSingleValidationError(Bill.FIELD_QR_TYPE, QRBill.KEY_VALID_DATA_STRUCTURE);

        bill.setBillInformation(lines[31]);

        AlternativeScheme[] alternativeSchemes = null;
        int numSchemes = lines.length - 32;
        if (numSchemes > 0) {
            alternativeSchemes = new AlternativeScheme[numSchemes];
            for (int i = 0; i < numSchemes; i++) {
                AlternativeScheme scheme = new AlternativeScheme();
                scheme.setInstruction(lines[32 + i]);
                alternativeSchemes[i] = scheme;
            }
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
        result.addMessage(ValidationMessage.Type.ERROR, field, messageKey);
        throw new QRBillValidationError(result);
    }
}
