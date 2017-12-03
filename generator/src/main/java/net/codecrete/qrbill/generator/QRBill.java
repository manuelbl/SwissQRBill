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

    public enum Language {
        German,
        French,
        Italian,
        English
    }

    public enum Version {
        Version_1_0
    }

    public static final String FIELD_AMOUNT = "amount";
    public static final String FIELD_CURRENCY = "currency";
    public static final String FIELD_ACCOUNT = "account";
    public static final String FIELD_CREDITOR = "creditor";
    public static final String FIELD_FINAL_CREDITOR = "final_creditor";
    public static final String FIELD_REFERENCE_NO = "reference_no";
    public static final String FIELD_ADDITIONAL_INFO = "additional_info";
    public static final String FIELD_DEBTOR = "debtor";

    private Language language = Language.English;
    private Version version = Version.Version_1_0;

    private boolean isAmountOpen = false;
    private double amount = 0;
    private String currency = "CHF";
    private String account = "CH";
    private Person creditor = new Person();
    private Person finalCreditor = null;
    private String referenceNo = "0";
    private String additionalInformation = null;
    private boolean isDebtorOpen = false;
    private Person debtor = new Person();
    private LocalDate dueDate = null;

    private boolean isValidated = false;
    private boolean isValid = false;


    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
        isValidated = false;
    }

    public Version getVersion() {
        return version;
    }

    public void setVersion(Version version) {
        this.version = version;
        isValidated = false;
    }

    public boolean isAmountOpen() {
        return isAmountOpen;
    }

    public void setAmountOpen(boolean amountOpen) {
        isAmountOpen = amountOpen;
        isValidated = false;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
        isValidated = false;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
        isValidated = false;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
        isValidated = false;
    }

    public Person getCreditor() {
        return creditor;
    }

    public void setCreditor(Person creditor) {
        this.creditor = creditor;
        isValidated = false;
    }

    public Person getFinalCreditor() {
        return finalCreditor;
    }

    public void setFinalCreditor(Person finalCreditor) {
        this.finalCreditor = finalCreditor;
        isValidated = false;
    }

    public String getReferenceNo() {
        return referenceNo;
    }

    public void setReferenceNo(String referenceNo) {
        this.referenceNo = referenceNo;
        isValidated = false;
    }

    public String getAdditionalInformation() {
        return additionalInformation;
    }

    public void setAdditionalInformation(String additionalInformation) {
        this.additionalInformation = additionalInformation;
        isValidated = false;
    }

    public boolean isDebtorOpen() {
        return isDebtorOpen;
    }

    public void setDebtorOpen(boolean debtorOpen) {
        isDebtorOpen = debtorOpen;
        isValidated = false;
    }

    public Person getDebtor() {
        return debtor;
    }

    public void setDebtor(Person debtor) {
        this.debtor = debtor;
        isValidated = false;
    }

    public ValidationResult[] validate() {
        return null;
    }

    public byte[] generate(BillFormat billFormat, GraphicsFormat graphicsFormat) {

        String qrCodeText = createQRCodeText();
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
        port.startQRCode(0, 0, 46, qrCode.size);
        port.startPath();
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                if (qrCode.getModule(x, y)) {
                    port.addRectangle(x, y, 1, 1);
                }
            }
        }
        port.fillPath(0);
        port.endQRCode();

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


    public String createQRCodeText() {
        StringBuilder sb = new StringBuilder();

        // Header
        sb.append("SPC"); // QRType
        appendDataField(sb, "0100"); // Version
        appendDataField(sb, "1"); // Coding

        // CdtrInf
        appendDataField(sb, account); // IBAN
        appendPerson(sb, creditor); // Cdtr

        // UltmtCdtr
        appendPerson(sb, finalCreditor);

        // CCyAmtDate
        appendDataField(sb, isAmountOpen ? "" : formatAmount(amount)); // Amt
        appendDataField(sb, currency); // Ccy
        appendDataField(sb, dueDate != null ? formatDate(dueDate) : ""); // ReqdExctnDt

        // UltmtDbtr
        appendPerson(sb, isDebtorOpen ? null : debtor);

        // RmtInf
        String referenceType = "NON";
        if (referenceNo != null) {
            if (referenceNo.startsWith("RF"))
                referenceType = "SCOR";
            else if (referenceNo.length() > 0)
                referenceType = "QRR";
        }
        appendDataField(sb, referenceType); // Tp
        appendDataField(sb, referenceNo); // Ref
        appendDataField(sb, additionalInformation); // Unstrd

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


/*

--- Sample ---

SPC
0100
1
CH5800791123000889012
Robert Schneider AG
Rue du Lac
1268
2501
Biel
CH






3949.75
CHF
2019-10-31
Pia Rutschmann
Marktgasse
28
9400
Rorschach
CH
NON

Rechnung Nr. 3139 für Gartenarbeiten und Entsorgung Schnittmaterial.

 */