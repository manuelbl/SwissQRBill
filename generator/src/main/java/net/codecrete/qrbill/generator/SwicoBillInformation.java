//
// Swiss QR Bill Generator
// Copyright (c) 2020 Christian Bernasconi
// Copyright (c) 2020 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
package net.codecrete.qrbill.generator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

/**
 * Bill information (according to Swico S1) for automated processing of invoices.
 * <p>
 * Swico S1 (see <a href="http://swiss-qr-invoice.org/downloads/qr-bill-s1-syntax-de.pdf">Syntaxdefinition S1</a>)
 * is one of the supported standards for adding structured billing information to a QR bill
 * (in the field <code>StrdBkgInf</code>).
 * </p>
 * <p>
 * All properties of this bean are optional.
 * </p>
 */
public class SwicoBillInformation {

    private String invoiceNumber;
    private LocalDate invoiceDate;
    private String customerReference;
    private String vatNumber;
    private LocalDate vatDate;
    private LocalDate vatStartDate;
    private LocalDate vatEndDate;
    private BigDecimal vatRate;
    private List<RateDetail> vatRateDetails;
    private List<RateDetail> vatImportTaxes;
    private List<PaymentCondition> paymentConditions;

    /**
     * Creates a new instance with {@code null} values.
     */
    public SwicoBillInformation() {
        // default constructor, for JavaDoc documentation
    }

    /**
     * Gets the invoice number.
     *
     * @return the invoice number
     */
    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    /**
     * Sets the invoice number.
     *
     * @param invoiceNumber the invoice number
     */
    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    /**
     * Gets the invoice date.
     *
     * @return the invoice date
     */
    public LocalDate getInvoiceDate() {
        return invoiceDate;
    }

    /**
     * Sets the invoice date.
     *
     * @param invoiceDate the invoice date
     */
    public void setInvoiceDate(LocalDate invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    /**
     * Gets the customer reference.
     *
     * @return the customer reference
     */
    public String getCustomerReference() {
        return customerReference;
    }

    /**
     * Sets the customer reference.
     *
     * @param customerReference the customer reference
     */
    public void setCustomerReference(String customerReference) {
        this.customerReference = customerReference;
    }

    /**
     * Gets the invoicing party's VAT number.
     * <p>
     * The number is supplied without any prefix, white space, separator or suffix,
     * i.e. "106017086" instead of "CHE-106.017.086 MWST".
     * </p>
     *
     * @return the VAT number
     */
    public String getVatNumber() {
        return vatNumber;
    }

    /**
     * Sets the invoicing party's VAT number.
     * <p>
     * The number is supplied without any prefix, white space, separator or suffix,
     * i.e. "106017086" instead of "CHE-106.017.086 MWST".
     * </p>
     *
     * @param vatNumber the VAT number
     */
    public void setVatNumber(String vatNumber) {
        this.vatNumber = vatNumber;
    }

    /**
     * Gets the date when the goods or service were supplied.
     * <p>
     * If this VAT date is given, VAT start and end date must be {@code null}.
     * </p>
     *
     * @return the VAT date
     */
    public LocalDate getVatDate() {
        return vatDate;
    }

    /**
     * Sets the date when the goods or service were supplied.
     * <p>
     * If this VAT date is set, VAT start and end date must be {@code null}.
     * </p>
     *
     * @param vatDate the VAT date
     */
    public void setVatDate(LocalDate vatDate) {
        this.vatDate = vatDate;
    }

    /**
     * Gets the start date of the period when the service was supplied (e.g. a subscription).
     * <p>
     * If this VAT start date is given, the VAT end date must all be given
     * and the VAT date must be {@code null}.
     * </p>
     *
     * @return the VAT start date
     */
    public LocalDate getVatStartDate() {
        return vatStartDate;
    }

    /**
     * Sets the start date of the period when the service was supplied (e.g. a subscription).
     * <p>
     * If this VAT start date is given, the VAT end date must also be given
     * and the VAT date must be {@code null}.
     * </p>
     *
     * @param vatStartDate the VAT start date
     */
    public void setVatStartDate(LocalDate vatStartDate) {
        this.vatStartDate = vatStartDate;
    }

    /**
     * Gets the end date of the period when the service was supplied (e.g. a subscription).
     * <p>
     * If this VAT end date is given, the VAT start date must also be given
     * and the VAT date must be {@code null}.
     * </p>
     *
     * @return the VAT end date
     */
    public LocalDate getVatEndDate() {
        return vatEndDate;
    }

    /**
     * Sets the end date of the period when the service was supplied (e.g. a subscription).
     * <p>
     * If this VAT end date is given, the VAT start date must also be given
     * and the VAT date must be {@code null}.
     * </p>
     *
     * @param vatEndDate the VAT end date
     */
    public void setVatEndDate(LocalDate vatEndDate) {
        this.vatEndDate = vatEndDate;
    }

    /**
     * Gets the VAT rate in case the same rate applies to the entire invoice.
     * <p>
     * If different rates apply to invoice line items, this property is {@code null}
     * and {@link #getVatRateDetails()} is used instead.
     * </p>
     *
     * @return the VAT rate (in percent)
     */
    public BigDecimal getVatRate() {
        return vatRate;
    }

    /**
     * Sets the VAT rate in case the same rate applies to the entire invoice.
     * <p>
     * If different rates apply to invoice line items, this property is {@code null}
     * and {@link #setVatRateDetails(List)} is used instead.
     * </p>
     *
     * @param vatRate the VAT rate (in percent)
     */
    public void setVatRate(BigDecimal vatRate) {
        this.vatRate = vatRate;
    }

    /**
     * Gets a list of VAT rates.
     * <p>
     * Each element in the list is a tuple of VAT rate and amount. It indicates that the specified
     * VAT rate applies to the specified net amount (partial amount) of the invoice.
     * </p>
     * <p>
     * If a single VAT rate applies to the entire invoice, this list is {@code null} and
     * {@link #getVatRate()} is used instead.
     * </p>
     * <p>
     * Example: If the list contained (8, 1000), (2.5, 51.8), (7.7, 250) for an invoice in CHF,
     * a VAT rate of 8% would apply to CHF 1000.00, 2.5% for CHF 51.80 and 7.7% for CHF 250.00.
     * </p>
     *
     * @return the list of VAT rate/amount tuples
     */
    public List<RateDetail> getVatRateDetails() {
        return vatRateDetails;
    }

    /**
     * Sets a list of VAT rates.
     * <p>
     * Each element in the list is a tuple of VAT rate and amount. It indicates that the specified
     * VAT rate applies to the specified net amount (partial amount) of the invoice.
     * </p>
     * <p>
     * If a single VAT rate applies to the entire invoice, this list is {@code null} and
     * {@link #setVatRate(BigDecimal)} is used instead.
     * </p>
     * <p>
     * Example: If the list contained (8, 1000), (2.5, 51.8), (7.7, 250) for an invoice in CHF,
     * a VAT rate of 8% would apply to CHF 1000.00, 2.5% for CHF 51.80 and 7.7% for CHF 250.00.
     * </p>
     *
     * @param vatRateDetails the list of VAT rate/amount tuples
     */
    public void setVatRateDetails(List<RateDetail> vatRateDetails) {
        this.vatRateDetails = vatRateDetails;
    }

    /**
     * Gets the list of VAT import taxes.
     * <p>
     * Each element in the list is a tuple of VAT rate and VAT amount.
     * It indicates that the specified VAT rate was applied and resulted in the specified tax amount.
     * </p>
     * <p>
     * Example: If the list contained (7.7, 48.37), (2.5, 12.4) for an invoice in CHF, a VAT rate of 7.7% has
     * been applied to a part of the items resulting in CHF 48.37 in tax and a rate of 2.5% has been
     * applied to another part of the items resulting in CHF 12.40 in tax.
     * </p>
     *
     * @return the list of VAT rate/amount tuples
     */
    public List<RateDetail> getVatImportTaxes() {
        return vatImportTaxes;
    }

    /**
     * Sets the list of VAT import taxes.
     * <p>
     * Each element in the list is a tuple of VAT rate and VAT amount.
     * It indicates that the specified VAT rate was applied and resulted in the specified tax amount.
     * </p>
     * <p>
     * Example: If the list contained (7.7, 48.37), (2.5, 12.4) for an invoice in CHF, a VAT rate of 7.7% has
     * been applied to a part of the items resulting in CHF 48.37 in tax and a rate of 2.5% has been
     * applied to another part of the items resulting in CHF 12.40 in tax.
     * </p>
     *
     * @param vatImportTaxes the list of VAT rate/amount tuples
     */
    public void setVatImportTaxes(List<RateDetail> vatImportTaxes) {
        this.vatImportTaxes = vatImportTaxes;
    }

    /**
     * Gets the payment conditions.
     * <p>
     * Each element in the list is a tuple of a payment discount and a deadline
     * (in days from the invoice date).
     * </p>
     * <p>
     * If the list contained (2, 10), (0, 60), a discount of 2% applies if the payment is made
     * by 10 days after invoice data. The payment is due 60 days after invoice date.
     * </p>
     *
     * @return the list of discount/days tuples
     */
    public List<PaymentCondition> getPaymentConditions() {
        return paymentConditions;
    }

    /**
     * Sets the payment conditions.
     * <p>
     * Each element in the list is a tuple of a payment discount and a deadline
     * (in days from the invoice date).
     * </p>
     * <p>
     * If the list contained (2, 10), (0, 60), a discount of 2% applies if the payment is made
     * by 10 days after invoice data. The payment is due 60 days after invoice date.
     * </p>
     *
     * @param paymentConditions the list of discount/days tuples
     */
    public void setPaymentConditions(List<PaymentCondition> paymentConditions) {
        this.paymentConditions = paymentConditions;
    }

    /**
     * Gets the payment due date.
     * <p>
     * The due date is calculated from the invoice date and the payment condition with a discount of 0.
     * </p>
     *
     * @return the due date (or {@code null} if the invoice date or the relevant payment condition is missing)
     */
    public LocalDate getDueDate() {
        if (invoiceDate == null || paymentConditions == null)
            return null;

        for (PaymentCondition cond : paymentConditions) {
            if (BigDecimal.ZERO.compareTo(cond.getDiscount()) == 0)
                return invoiceDate.plusDays(cond.getDays());
        }

        return null;
    }

    /**
     * Encodes this bill information as a single text string suitable
     * to be added to a Swiss QR bill.
     *
     * @return the encoded text
     */
    public String encodeAsText() {
        return SwicoS1Encoder.encode(this);
    }

    /**
     * Decodes the text of structured billing information and
     * creates a {@link SwicoBillInformation} instance.
     *
     * @param text he structured billing information encoded according to Swico S1 syntax.
     * @return the decoded billing information
     */
    public static SwicoBillInformation decodeText(String text) {
        return SwicoS1Decoder.decode(text);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SwicoBillInformation that = (SwicoBillInformation) o;
        return Objects.equals(invoiceNumber, that.invoiceNumber) &&
                Objects.equals(invoiceDate, that.invoiceDate) &&
                Objects.equals(customerReference, that.customerReference) &&
                Objects.equals(vatNumber, that.vatNumber) &&
                Objects.equals(vatDate, that.vatDate) &&
                Objects.equals(vatStartDate, that.vatStartDate) &&
                Objects.equals(vatEndDate, that.vatEndDate) &&
                Objects.equals(vatRate, that.vatRate) &&
                Objects.equals(vatRateDetails, that.vatRateDetails) &&
                Objects.equals(vatImportTaxes, that.vatImportTaxes) &&
                Objects.equals(paymentConditions, that.paymentConditions);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(invoiceNumber, invoiceDate, customerReference, vatNumber, vatDate, vatStartDate, vatEndDate, vatRate, vatRateDetails, vatImportTaxes, paymentConditions);
    }

    @Override
    public String toString() {
        return "SwicoBillInformation{" +
                "invoiceNumber='" + invoiceNumber + '\'' +
                ", invoiceDate=" + invoiceDate +
                ", customerReference='" + customerReference + '\'' +
                ", vatNumber='" + vatNumber + '\'' +
                ", vatDate=" + vatDate +
                ", vatStartDate=" + vatStartDate +
                ", vatEndDate=" + vatEndDate +
                ", vatRate=" + vatRate +
                ", vatRateDetails=" + vatRateDetails +
                ", vatImportTaxes=" + vatImportTaxes +
                ", paymentConditions=" + paymentConditions +
                '}';
    }


    /**
     * VAT rate detail: a tuple of VAT rate and amount.
     */
    public static class RateDetail {
        private BigDecimal rate;
        private BigDecimal amount;

        /**
         * Creates a new instance with {@code null} values.
         */
        public RateDetail() {
        }

        /**
         * Creates a new instance with the specified values.
         *
         * @param rate   the VAT rate (in percent)
         * @param amount the amount (in the bill currency)
         */
        public RateDetail(BigDecimal rate, BigDecimal amount) {
            this.rate = rate;
            this.amount = amount;
        }

        /**
         * Gets the VAT rate.
         *
         * @return the VAT rate (in percent)
         */
        public BigDecimal getRate() {
            return rate;
        }

        /**
         * Sets the VAT rate.
         *
         * @param rate the VAT rate (in percent)
         */
        public void setRate(BigDecimal rate) {
            this.rate = rate;
        }

        /**
         * Gets the amount.
         *
         * @return the amount (in bill currency)
         */
        public BigDecimal getAmount() {
            return amount;
        }

        /**
         * Sets the amount.
         *
         * @param amount the amount (in bill currency)
         */
        public void setAmount(BigDecimal amount) {
            this.amount = amount;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            RateDetail that = (RateDetail) o;
            return Objects.equals(rate, that.rate) &&
                    Objects.equals(amount, that.amount);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int hashCode() {
            return Objects.hash(rate, amount);
        }

        @Override
        public String toString() {
            return "RateDetail{" +
                    "rate=" + rate +
                    ", amount=" + amount +
                    '}';
        }
    }


    /**
     * Payment condition detail: a tuple of discount and validity in days.
     */
    public static class PaymentCondition {
        private BigDecimal discount;
        private int days;

        /**
         * Creates a new instance with {@code null} values.
         */
        public PaymentCondition() {
        }

        /**
         * Creates a new instance with the specified values.
         *
         * @param discount the discount (in percent)
         * @param days     the number of days
         */
        public PaymentCondition(BigDecimal discount, int days) {
            this.discount = discount;
            this.days = days;
        }

        /**
         * Gets the discount.
         *
         * @return the discount (in percent)
         */
        public BigDecimal getDiscount() {
            return discount;
        }

        /**
         * Sets the discount.
         *
         * @param discount the discount (in percent)
         */
        public void setDiscount(BigDecimal discount) {
            this.discount = discount;
        }

        /**
         * Gts the number of days the discount is valid.
         *
         * @return the number of days
         */
        public int getDays() {
            return days;
        }

        /**
         * Sets the number of days the discount is valid.
         *
         * @param days the number of days
         */
        public void setDays(int days) {
            this.days = days;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            PaymentCondition that = (PaymentCondition) o;
            return days == that.days &&
                    Objects.equals(discount, that.discount);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int hashCode() {
            return Objects.hash(discount, days);
        }

        @Override
        public String toString() {
            return "PaymentCondition{" +
                    "discount=" + discount +
                    ", days=" + days +
                    '}';
        }
    }
}
