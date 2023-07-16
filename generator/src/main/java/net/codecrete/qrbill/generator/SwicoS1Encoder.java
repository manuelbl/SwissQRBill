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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

/**
 * Encodes structured bill information according to Swico S1 syntax.
 * <p>
 * The encoded bill information can be used in a Swiss QR bill
 * in the field {@code StrdBkgInfo}.
 * </p>
 * <p>
 * Also see <a href="http://swiss-qr-invoice.org/downloads/qr-bill-s1-syntax-de.pdf">Syntaxdefinition S1</a>.
 * </p>
 */
class SwicoS1Encoder {

    private DecimalFormat numberFormat;

    private SwicoS1Encoder() {
    }

    /**
     * Encodes the specified bill information.
     *
     * @param billInfo the bill information
     * @return encoded bill information text
     */
    static String encode(SwicoBillInformation billInfo) {
        return new SwicoS1Encoder().encodeIt(billInfo);
    }

    private String encodeIt(SwicoBillInformation billInfo) {
        StringBuilder sb = new StringBuilder();

        sb.append("//S1");

        if (billInfo.getInvoiceNumber() != null)
            sb.append("/10/").append(escapedText(billInfo.getInvoiceNumber()));

        if (billInfo.getInvoiceDate() != null)
            sb.append("/11/").append(s1Date(billInfo.getInvoiceDate()));

        if (billInfo.getCustomerReference() != null)
            sb.append("/20/").append(escapedText(billInfo.getCustomerReference()));

        if (billInfo.getVatNumber() != null)
            sb.append("/30/").append(escapedText(billInfo.getVatNumber()));

        if (billInfo.getVatDate() != null) {
            sb.append("/31/").append(s1Date(billInfo.getVatDate()));
        } else if (billInfo.getVatStartDate() != null && billInfo.getVatEndDate() != null) {
            sb.append("/31/")
                    .append(s1Date(billInfo.getVatStartDate()))
                    .append(s1Date(billInfo.getVatEndDate()));
        }

        if (billInfo.getVatRate() != null) {
            sb.append("/32/").append(s1Number(billInfo.getVatRate()));
        } else if (billInfo.getVatRateDetails() != null && !billInfo.getVatRateDetails().isEmpty()) {
            sb.append("/32/");
            appendRateDetailTupleList(sb, billInfo.getVatRateDetails());
        }

        if (billInfo.getVatImportTaxes() != null && !billInfo.getVatImportTaxes().isEmpty()) {
            sb.append("/33/");
            appendRateDetailTupleList(sb, billInfo.getVatImportTaxes());
        }
        if (billInfo.getPaymentConditions() != null && !billInfo.getPaymentConditions().isEmpty()) {
            sb.append("/40/");
            appendConditionTupleList(sb, billInfo.getPaymentConditions());
        }

        return sb.length() > 4 ? sb.toString() : null;
    }

    private static String escapedText(String text) {
        return text.replace("\\", "\\\\").replace("/", "\\/");
    }

    private static String s1Date(LocalDate date) {
        return date.format(DATE_FORMAT);
    }

    private String s1Number(BigDecimal num) {
        if (numberFormat == null)
            numberFormat = new DecimalFormat("0.###", new DecimalFormatSymbols(Locale.UK));
        return numberFormat.format(num);
    }

    private void appendRateDetailTupleList(StringBuilder sb, List<RateDetail> list) {
        boolean isFirst = true;
        for (RateDetail e : list) {
            if (!isFirst)
                sb.append(";");
            else
                isFirst = false;
            sb.append(s1Number(e.getRate())).append(":").append(s1Number(e.getAmount()));
        }
    }

    private void appendConditionTupleList(StringBuilder sb, List<PaymentCondition> list) {
        boolean isFirst = true;
        for (PaymentCondition e : list) {
            if (!isFirst)
                sb.append(";");
            else
                isFirst = false;
            sb.append(s1Number(e.getDiscount())).append(":").append(e.getDays());
        }
    }

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyMMdd", Locale.UK);
}
