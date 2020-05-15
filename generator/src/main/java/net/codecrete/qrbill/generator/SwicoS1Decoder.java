//
// Swiss QR Bill Generator
// Copyright (c) 2018 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//

package net.codecrete.qrbill.generator;
/// <summary>
/// Decodes structured bill information according to Swico S1 syntax.
/// <para>
/// The encoded bill information can be found in a Swiss QR bill in th field <c>StrdBkgInf</c>.
/// </para>
/// <para>
/// Also see http://swiss-qr-invoice.org/downloads/qr-bill-s1-syntax-de.pdf
/// </para>
/// </summary>

import net.codecrete.qrbill.generator.SwicoBillInformation.PaymentCondition;
import net.codecrete.qrbill.generator.SwicoBillInformation.RateDetail;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
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
 * Also see http://swiss-qr-invoice.org/downloads/qr-bill-s1-syntax-de.pdf
 * </p>
 */
public class SwicoS1Decoder {

    private static final int InvoiceNumberTag = 10;

    private static final int InvoiceDateTag = 11;

    private static final int CustomerReferenceTag = 20;

    private static final int VatNumberTag = 30;

    private static final int VatDateTag = 31;

    private static final int VatRateDetailsTag = 32;

    private static final int VatImportTaxesTag = 33;

    private static final int PaymentConditionsTag = 40;

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
        if (billInfoText == null || !billInfoText.startsWith("//S1/")) {
            return null;
        }

        // Split text as slashes
        String[] parts = Split(billInfoText.substring(5));

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
        for (InfoTuple tuple : tuples) {
            decodeElement(billInformation, tuple.tag, tuple.value);
        }

        return billInformation;
    }

    private static void decodeElement(SwicoBillInformation billInformation, int tag, String value) {
        if (value.length() == 0) {
            return;
        }

        switch (tag) {
            case InvoiceNumberTag:
                billInformation.setInvoiceNumber(value);
                break;
            case InvoiceDateTag:
                billInformation.setInvoiceDate(getDateValue(value));
                break;
            case CustomerReferenceTag:
                billInformation.setCustomerReference(value);
                break;
            case VatNumberTag:
                billInformation.setVatNumber(value);
                break;
            case VatDateTag:
                setVatDates(billInformation, value);
                break;
            case VatRateDetailsTag:
                SetVatRateDetails(billInformation, value);
                break;
            case VatImportTaxesTag:
                billInformation.setVatImportTaxes(parseDetailList(value));
                break;
            case PaymentConditionsTag:
                setPaymentConditions(billInformation, value);
                break;
        }
    }

    private static void setVatDates(SwicoBillInformation billInformation, String value) {
        if (value.length() != 6 && value.length() != 12) {
            return;
        }

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

    private static void SetVatRateDetails(SwicoBillInformation billInformation, String value) {
        // Test for single VAT rate vs list of tuples
        if (!value.contains(":") && !value.contains(";")) {
            billInformation.setVatRate(getDecimalValue(value));
            billInformation.setVatRateDetails(null);
        } else {
            billInformation.setVatRateDetails(parseDetailList(value));
            billInformation.setVatRate(null);
        }
    }

    private static void setPaymentConditions(SwicoBillInformation billInformation, String value) {
        // Split into tuples
        String[] tuples = value.split(";");

        List<PaymentCondition> list = new ArrayList<>();
        for (String listEntry : tuples) {
            // Split into tuple (discount, days)
            String[] detail = listEntry.split(":");
            if (detail.length != 2) {
                continue;
            }

            BigDecimal discount = getDecimalValue(detail[0]);
            Integer days = getIntValue(detail[1]);
            if (discount != null && days != null) {
                list.add(new PaymentCondition(discount, days));
            }
        }

        if (list.size() > 0) {
            billInformation.setPaymentConditions(list);
        }
    }

    private static List<RateDetail> parseDetailList(String text) {
        // Split into tuples
        String[] tuples = text.split(";");

        List<RateDetail> list = new ArrayList<>();
        for (String vatEntry : tuples) {
            // Split into tuple (rate, amount)
            String[] vatDetails = vatEntry.split(":");
            if (vatDetails.length != 2) {
                continue;
            }

            BigDecimal vatRate = getDecimalValue(vatDetails[0]);
            BigDecimal vatAmount = getDecimalValue(vatDetails[1]);
            if (vatRate != null && vatAmount != null) {
                list.add(new RateDetail(vatRate, vatAmount));
            }
        }
        return list.size() > 0 ? list : null;
    }

    private static final DateTimeFormatter SWICO_DATE_FORMAT
            = DateTimeFormatter.ofPattern("yyMMdd", Locale.UK);

    private static LocalDate getDateValue(String dateText) {
        try {
            return LocalDate.parse(dateText, SWICO_DATE_FORMAT);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    private static Integer getIntValue(String intText) {
        try {
            return Integer.parseInt(intText);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static final DecimalFormat SWICO_NUMBER_FORMAT;

    static {
        SWICO_NUMBER_FORMAT = new DecimalFormat("0.###", new DecimalFormatSymbols(Locale.UK));
        SWICO_NUMBER_FORMAT.setParseBigDecimal(true);
    }

    private static BigDecimal getDecimalValue(String decimalText) {
        ParsePosition position = new ParsePosition(0);
        BigDecimal decimal = (BigDecimal) SWICO_NUMBER_FORMAT.parse(decimalText, position);
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
    private static String[] Split(String text) {
        // Use placeholders for escaped characters (outside of valid QR bill character set)
        // and undo back slash escaping.
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
        int tag;
        String value;
        InfoTuple(int tag, String value) {
            this.tag = tag;
            this.value = value;
        }
    }
}
