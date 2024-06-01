//
// Swiss QR Bill Generator
// Copyright (c) 2018 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//

package net.codecrete.qrbill.generator;

import net.codecrete.qrbill.generator.SwicoBillInformation.PaymentCondition;
import net.codecrete.qrbill.generator.SwicoBillInformation.RateDetail;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Decodes structured bill information according to Swico S1 syntax.
 * <p>
 * The encoded bill information can be found in a Swiss QR bill in th field {@code StrdBkgInf}.
 * </p>
 * <p>
 * Also see <a href="http://swiss-qr-invoice.org/downloads/qr-bill-s1-syntax-de.pdf">Syntaxdefinition S1</a>
 * </p>
 */
public class SwicoS1Decoder {

    private static final int INVOICE_NUMBER_TAG = 10;

    private static final int INVOICE_DATE_TAG = 11;

    private static final int CUSTOMER_REFERENCE_TAG = 20;

    private static final int VAT_NUMBER_TAG = 30;

    private static final int VAT_DATE_TAG = 31;

    private static final int VAT_RATE_DETAILS_TAG = 32;

    private static final int VAT_IMPORT_TAXES_TAG = 33;

    private static final int PAYMENT_CONDITIONS_TAG = 40;


    private NumberFormat numberFormat;


    private SwicoS1Decoder() {
    }

    /**
     * Decodes the specified text.
     * <p>
     * As much data as possible is decoded. Invalid data is silently ignored.
     * </p>
     *
     * @param billInfoText the encoded structured bill information text
     * @return the decoded bill information (or {@code null} if no valid Swico bill information is found)
     */
    static SwicoBillInformation decode(String billInfoText) {
        return new SwicoS1Decoder().decodeIt(billInfoText);
    }

    private SwicoBillInformation decodeIt(String billInfoText) {
        if (billInfoText == null || !billInfoText.startsWith("//S1/"))
            return null;

        // Split text at slashes
        String[] parts = split(billInfoText.substring(5));

        // Create a list of tuples (tag, value)
        List<InfoTuple> tuples = new ArrayList<>();
        int len = parts.length;
        for (int i = 0; i < len - 1; i += 2) {
            try {
                int tag = Integer.parseInt(parts[i]);
                tuples.add(new InfoTuple(tag, parts[i + 1]));
            } catch (NumberFormatException e) {
                // ignore
            }
        }

        // Process the tuples and assign them to bill information
        SwicoBillInformation billInformation = new SwicoBillInformation();
        for (InfoTuple tuple : tuples)
            decodeElement(billInformation, tuple.tag, tuple.value);

        return billInformation;
    }

    private void decodeElement(SwicoBillInformation billInformation, int tag, String value) {
        if (value.isEmpty())
            return;

        switch (tag) {
            case INVOICE_NUMBER_TAG:
                billInformation.setInvoiceNumber(value);
                break;
            case INVOICE_DATE_TAG:
                billInformation.setInvoiceDate(getDateValue(value));
                break;
            case CUSTOMER_REFERENCE_TAG:
                billInformation.setCustomerReference(value);
                break;
            case VAT_NUMBER_TAG:
                billInformation.setVatNumber(value);
                break;
            case VAT_DATE_TAG:
                setVatDates(billInformation, value);
                break;
            case VAT_RATE_DETAILS_TAG:
                setVatRateDetails(billInformation, value);
                break;
            case VAT_IMPORT_TAXES_TAG:
                billInformation.setVatImportTaxes(parseDetailList(value));
                break;
            case PAYMENT_CONDITIONS_TAG:
                setPaymentConditions(billInformation, value);
                break;
            default:
                // ignore unknown tags
        }
    }

    private static void setVatDates(SwicoBillInformation billInformation, String value) {
        if (value.length() != 6 && value.length() != 12)
            return;

        if (value.length() == 6) {
            // Single VAT date
            LocalDate date = getDateValue(value);
            if (date != null) {
                billInformation.setVatDate(date);
                billInformation.setVatStartDate(null);
                billInformation.setVatEndDate(null);
            }
        } else {
            // VAT date range
            LocalDate startDate = getDateValue(value.substring(0, 6));
            LocalDate endDate = getDateValue(value.substring(6, 12));
            if (startDate != null && endDate != null) {
                billInformation.setVatStartDate(startDate);
                billInformation.setVatEndDate(endDate);
                billInformation.setVatDate(null);
            }
        }
    }

    private void setVatRateDetails(SwicoBillInformation billInformation, String value) {
        // Test for single VAT rate vs list of tuples
        if (!value.contains(":") && !value.contains(";")) {
            billInformation.setVatRate(getDecimalValue(value));
            billInformation.setVatRateDetails(null);
        } else {
            billInformation.setVatRateDetails(parseDetailList(value));
            billInformation.setVatRate(null);
        }
    }

    private void setPaymentConditions(SwicoBillInformation billInformation, String value) {
        // Split into tuples
        String[] tuples = value.split(";");

        List<PaymentCondition> list = new ArrayList<>();
        for (String listEntry : tuples) {
            // Split into tuple (discount, days)
            String[] detail = listEntry.split(":");
            if (detail.length != 2)
                continue;

            BigDecimal discount = getDecimalValue(detail[0]);
            Integer days = getIntValue(detail[1]);
            if (discount != null && days != null)
                list.add(new PaymentCondition(discount, days));
        }

        if (!list.isEmpty())
            billInformation.setPaymentConditions(list);
    }

    private List<RateDetail> parseDetailList(String text) {
        // Split into tuples
        String[] tuples = text.split(";");

        List<RateDetail> list = new ArrayList<>();
        for (String vatEntry : tuples) {
            // Split into tuple (rate, amount)
            String[] vatDetails = vatEntry.split(":");
            if (vatDetails.length != 2)
                continue;

            BigDecimal vatRate = getDecimalValue(vatDetails[0]);
            BigDecimal vatAmount = getDecimalValue(vatDetails[1]);
            if (vatRate != null && vatAmount != null)
                list.add(new RateDetail(vatRate, vatAmount));
        }
        return list.isEmpty() ? null : list;
    }

    private static final DateTimeFormatter SWICO_DATE_FORMAT_SPECIFICATION
            = DateTimeFormatter.ofPattern("yyMMdd", Locale.UK);
    private static final DateTimeFormatter SWICO_DATE_FORMAT_WILDERNESS_1
            = DateTimeFormatter.ofPattern("yyMMddHHmmss", Locale.UK);
    private static final DateTimeFormatter SWICO_DATE_FORMAT_WILDERNESS_2
            = DateTimeFormatter.ofPattern("yyMMddHHmm", Locale.UK);

    private static LocalDate getDateValue(String dateText) {
        if (dateText.length() == 6) { // Consistent with specification
            try {
                return LocalDate.parse(dateText, SWICO_DATE_FORMAT_SPECIFICATION);
            } catch (DateTimeParseException e) {
                // fall through to default
            }
        } else if (dateText.length() == 12) { // Not consistent with specifications but seen in production (year, month, day, hour, minute, second)
            try {
                return LocalDate.parse(dateText, SWICO_DATE_FORMAT_WILDERNESS_1);
            } catch (DateTimeParseException e) {
                // fall through to default
            }
        } else if (dateText.length() == 10) {  // Not consistent with specifications but seen in production (year, month, day, hour, minute)
            try {
                return LocalDate.parse(dateText, SWICO_DATE_FORMAT_WILDERNESS_2);
            } catch (DateTimeParseException e) {
                // fall through to default
            }
        }
        return null;
    }

    private static Integer getIntValue(String intText) {
        try {
            return Integer.parseInt(intText);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private BigDecimal getDecimalValue(String decimalText) {
        if (numberFormat == null) {
            DecimalFormat format = new DecimalFormat("0.###", new DecimalFormatSymbols(Locale.UK));
            format.setParseBigDecimal(true);
            numberFormat = format;
        }

        ParsePosition position = new ParsePosition(0);
        BigDecimal decimal = (BigDecimal) numberFormat.parse(decimalText, position);
        return (position.getIndex() == decimalText.length()) ? decimal : null;
    }

    /**
     * Splits the text at slash characters.
     * <p>
     * Additonally, the escaping with back slahes is undone.
     * </p>
     *
     * @param text the text to split
     * @return array of substrings
     */
    private static String[] split(String text) {
        // Use placeholders for escaped characters (outside valid QR bill character set)
        // and undo backslash escaping.
        text = text.replace("\\\\", "☁").replace("\\/", "★");

        // Split
        String[] parts = text.split("/");

        // Fix placeholders
        for (int i = 0; i < parts.length; i++) {
            parts[i] = parts[i].replace('★', '/').replace('☁', '\\');
        }

        return parts;
    }

    static class InfoTuple {
        final int tag;
        final String value;

        InfoTuple(int tag, String value) {
            this.tag = tag;
            this.value = value;
        }
    }
}
