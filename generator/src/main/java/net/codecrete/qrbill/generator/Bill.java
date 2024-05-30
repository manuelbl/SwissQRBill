//
// Swiss QR Bill Generator
// Copyright (c) 2017 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
package net.codecrete.qrbill.generator;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Objects;

/**
 * QR bill data
 */
public class Bill implements Serializable {

    private static final long serialVersionUID = -8104086304378262190L;


    /**
     * Reference type: without reference.
     */
    public static final String REFERENCE_TYPE_NO_REF = "NON";
    /**
     * Reference type: QR reference.
     */
    public static final String REFERENCE_TYPE_QR_REF = "QRR";
    /**
     * Reference type: creditor reference (ISO 11649)
     */
    public static final String REFERENCE_TYPE_CRED_REF = "SCOR";

    /**
     * QR bill version
     */
    public enum Version {
        /**
         * Version 2.0
         */
        V2_0
    }

    /** Version of QR bill standard */
    private Version version = Version.V2_0;
    /** Payment amount */
    private BigDecimal amount = null;
    /** Payment currency (ISO code) */
    private String currency = "CHF";
    /** Creditor's account number */
    private String account = null;
    /** Creditor address */
    private Address creditor = new Address();
    /** Payment reference type */
    private String referenceType = REFERENCE_TYPE_NO_REF;
    /** Payment reference (number) */
    private String reference = null;
    /** Debtor address */
    private Address debtor = null;
    /** Unstructured message */
    private String unstructuredMessage = null;
    /** Structured bill information */
    private String billInformation = null;
    /** Alternative schemes */
    private AlternativeScheme[] alternativeSchemes = null;
    /** Bill format */
    private BillFormat format = new BillFormat();
    /** Data separator for QR code data */
    private QrDataSeparator separator = QrDataSeparator.LF;
    /** Character set used for the QR bill data */
    private SPSCharacterSet characterSet = SPSCharacterSet.LATIN_1_SUBSET;

    /**
     * Creates a new instance with default values for the format.
     */
    public Bill() {
        // default constructor, for JavaDoc documentation
    }

    /**
     * Gets the version of the QR bill standard.
     *
     * @return the version
     */
    public Version getVersion() {
        return version;
    }

    /**
     * Sets the version of the QR bill standard.
     *
     * @param version the version
     */
    public void setVersion(Version version) {
        this.version = version;
    }

    /**
     * Gets the payment amount
     *
     * @return the amount
     */
    public BigDecimal getAmount() {
        return amount;
    }

    /**
     * Gets the payment amount as a {@code Double} instance
     *
     * @return the amount
     */
    public Double getAmountAsDouble() {
        if (amount != null)
            return amount.doubleValue();
        else
            return null;
    }

    /**
     * Sets the payment amount.
     * <p>
     * Valid values are between 0.01 and 999,999,999.99
     * </p>
     *
     * @param amount the amount
     */
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    /**
     * Sets the payment amount from a {@code Double} solid-line-with_scissors
     * <p>
     * The value is saved with a scale of 2.
     * </p>
     *
     * @param amount the amount
     */
    public void setAmountFromDouble(Double amount) {
        if (amount != null)
            this.amount = BigDecimal.valueOf((long) (amount * 100 + 0.5), 2);
        else
            this.amount = null;
    }

    /**
     * Gets the payment currency.
     *
     * @return the currency
     */
    public String getCurrency() {
        return currency;
    }

    /**
     * Sets the payment currency.
     * <p>
     * Valid values are "CHF" and "EUR".
     * </p>
     *
     * @param currency the currency
     */
    public void setCurrency(String currency) {
        this.currency = currency;
    }

    /**
     * Gets the creditor's account number.
     *
     * @return the account number
     */
    public String getAccount() {
        return account;
    }

    /**
     * Sets the creditor's account number.
     * <p>
     * Account numbers must be valid IBANs of a bank of Switzerland or
     * Liechtenstein. Spaces are allowed in the account number.
     * </p>
     *
     * @param account the account number
     */
    public void setAccount(String account) {
        this.account = account;
    }

    /**
     * Gets the creditor address.
     *
     * @return the creditor address
     */
    public Address getCreditor() {
        return creditor;
    }

    /**
     * Sets the creditor address.
     *
     * @param creditor the creditor address.
     */
    public void setCreditor(Address creditor) {
        this.creditor = creditor;
    }

    /**
     * Gets the type of payment reference.
     * <p>
     * The reference type is automatically set when a payment reference is set.
     * </p>
     *
     * @return one of the constants REFERENCE_TYPE_xxx.
     * @see #REFERENCE_TYPE_QR_REF
     * @see #REFERENCE_TYPE_CRED_REF
     * @see #REFERENCE_TYPE_NO_REF
     */
    public String getReferenceType() {
        return referenceType;
    }

    /**
     * Sets the type of payment reference.
     * <p>
     * Usually there is no need to set the reference type as it is
     * automatically set when a payment reference is set..
     * </p>
     *
     * @param referenceType one of the constants {@code REFERENCE_TYPE_xx}
     * @see #REFERENCE_TYPE_QR_REF
     * @see #REFERENCE_TYPE_CRED_REF
     * @see #REFERENCE_TYPE_NO_REF
     */
    public void setReferenceType(String referenceType) {
        this.referenceType = referenceType;
    }

    /**
     * Updates the reference type by deriving it from the payment reference.
     */
    public void updateReferenceType() {
        String ref = Strings.trimmed(reference);
        if (ref != null) {
            if (ref.startsWith("RF"))
                referenceType = REFERENCE_TYPE_CRED_REF;
            else if (!ref.isEmpty())
                referenceType = REFERENCE_TYPE_QR_REF;
            else
                referenceType = REFERENCE_TYPE_NO_REF;
        } else {
            referenceType = REFERENCE_TYPE_NO_REF;
        }
    }

    /**
     * Gets the payment reference
     *
     * @return the reference
     */
    public String getReference() {
        return reference;
    }

    /**
     * Sets the payment reference.
     * <p>
     * The reference is mandatory for QR IBANs, i.e. IBANs in the range
     * CHxx30000xxxxxx through CHxx31999xxxxx. QR IBANs require a valid QR
     * reference (numeric reference corresponding to the ISR reference format).
     * </p>
     * <p>
     * For non-QR IBANs, the reference is optional. If it is provided,
     * it must be valid creditor reference according to ISO 11649 ("RFxxxx").
     * </p>
     * <p>
     * Both types of references may contain spaces for formatting.
     * </p>
     *
     * @param reference the payment reference number
     * @see #createAndSetCreditorReference(String)
     * @see #createAndSetQRReference(String)
     */
    public void setReference(String reference) {
        this.reference = reference;
        updateReferenceType();
    }

    /**
     * Creates and sets a ISO11649 creditor reference from a raw string by prefixing
     * the String with "RF" and the modulo 97 checksum.
     * <p>
     * Whitespace is removed from the reference
     * </p>
     *
     * @param rawReference The raw string
     * @throws IllegalArgumentException if {@code rawReference} contains invalid characters
     */
    public void createAndSetCreditorReference(String rawReference) {
        setReference(Payments.createISO11649Reference(rawReference));
    }

    /**
     * Creates and sets a QR reference from a raw string by appending the checksum digit
     * and prepending zeros to make it the correct length.
     * <p>
     * As the QR reference is numeric, the raw string must consist of digits and
     * whitespace only. Whitespace is removed from the reference.
     * </p>
     *
     * @param rawReference The raw string
     * @throws IllegalArgumentException if {@code rawReference} contains invalid characters
     */
    public void createAndSetQRReference(String rawReference) {
        setReference(Payments.createQRReference(rawReference));
    }

    /**
     * Gets the debtor's address.
     *
     * @return the debtor address
     */
    public Address getDebtor() {
        return debtor;
    }

    /**
     * Sets the debtor's address.
     * <p>
     * The debtor is optional. If it is omitted, both setting this field to
     * {@code null} or setting an address with all {@code null} or empty values is
     * ok.
     * </p>
     *
     * @param debtor the debtor address
     */
    public void setDebtor(Address debtor) {
        this.debtor = debtor;
    }

    /**
     * Gets the additional unstructured message.
     *
     * @return the unstructured message
     */
    public String getUnstructuredMessage() {
        return unstructuredMessage;
    }

    /**
     * Sets the additional unstructured message.
     *
     * @param unstructuredMessage the unstructured message
     */
    public void setUnstructuredMessage(String unstructuredMessage) {
        this.unstructuredMessage = unstructuredMessage;
    }

    /**
     * Gets the additional bill information.
     *
     * @return bill information
     */
    public String getBillInformation() {
        return billInformation;
    }

    /**
     * Sets the additional bill information
     *
     * @param billInformation bill information
     */
    public void setBillInformation(String billInformation) {
        this.billInformation = billInformation;
    }

    /**
     * Get the alternative schemes.
     * <p>
     * A maximum of two schemes are allowed.
     * </p>
     *
     * @return alternative schemes
     */
    public AlternativeScheme[] getAlternativeSchemes() {
        return alternativeSchemes;
    }

    /**
     * Sets the alternative scheme parameters.
     * <p>
     * A maximum of two schemes with parameters are allowed.
     * </p>
     *
     * @param alternativeSchemes alternative payment scheme information
     */
    public void setAlternativeSchemes(AlternativeScheme[] alternativeSchemes) {
        this.alternativeSchemes = alternativeSchemes;
    }

    /**
     * Gets the bill format.
     *
     * @return bill format
     */
    public BillFormat getFormat() {
        return format;
    }

    /**
     * Sets the bill format.
     *
     * @param format bill format
     */
    public void setFormat(BillFormat format) {
        this.format = format;
    }

    /**
     * Gets the line separator for the QR code data fields.
     * <p>
     * The default is {@link QrDataSeparator#LF}. There is no need to change it except
     * for improving compatibility with a non-compliant software processing the QR code data.
     * </p>
     * @return the line separator for the QR code data fields.
     */
    public QrDataSeparator getSeparator() {
        return separator;
    }

    /**
     * Sets the line separator for the QR code data fields.
     * <p>
     * The default is {@link QrDataSeparator#LF}. There is no need to change it except
     * for improving compatibility with a non-compliant software processing the QR code data.
     * </p>
     * @param separator  the line separator for the QR code data fields.
     */
    public void setSeparator(QrDataSeparator separator) {
        this.separator = separator;
    }

    /**
     * Gets the character set used for the QR bill data.
     * <p>
     * Defaults to {@link SPSCharacterSet#LATIN_1_SUBSET}.
     * </p>
     * <p>
     * Until November 21, 2025, {@link SPSCharacterSet#LATIN_1_SUBSET} is the only value that will generate
     * QR bills accepted by all banks. This will change by November 21, 2025.
     * </p>
     * @return the character set used for the QR bill data.
     */
    public SPSCharacterSet getCharacterSet() {
        return characterSet;
    }

    /**
     * Sets the character set used for the QR bill data.
     * <p>
     * Defaults to {@link SPSCharacterSet#LATIN_1_SUBSET}.
     * </p>
     * <p>
     * Until November 21, 2025, {@link SPSCharacterSet#LATIN_1_SUBSET} is the only value that will generate
     * QR bills accepted by all banks. This will change by November 21, 2025.
     * </p>
     * @param characterSet  the character set used for the QR bill data.
     */
    public void setCharacterSet(SPSCharacterSet characterSet) {
        this.characterSet = characterSet;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Bill bill = (Bill) o;
        return version == bill.version &&
                Objects.equals(amount, bill.amount) &&
                Objects.equals(currency, bill.currency) &&
                Objects.equals(account, bill.account) &&
                Objects.equals(creditor, bill.creditor) &&
                Objects.equals(referenceType, bill.referenceType) &&
                Objects.equals(reference, bill.reference) &&
                Objects.equals(debtor, bill.debtor) &&
                Objects.equals(unstructuredMessage, bill.unstructuredMessage) &&
                Objects.equals(billInformation, bill.billInformation) &&
                Arrays.equals(alternativeSchemes, bill.alternativeSchemes) &&
                Objects.equals(format, bill.format) &&
                Objects.equals(separator, bill.separator) &&
                Objects.equals(characterSet, bill.characterSet);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {

        int result = Objects.hash(version, amount, currency, account, creditor, referenceType, reference,
                debtor, unstructuredMessage, billInformation, format, separator, characterSet);
        result = 31 * result + Arrays.hashCode(alternativeSchemes);
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "Bill{" +
                "version=" + version +
                ", amount=" + amount +
                ", currency='" + currency + '\'' +
                ", account='" + account + '\'' +
                ", creditor=" + creditor +
                ", referenceType='" + referenceType + '\'' +
                ", reference='" + reference + '\'' +
                ", debtor=" + debtor +
                ", unstructuredMessage='" + unstructuredMessage + '\'' +
                ", billInformation='" + billInformation + '\'' +
                ", alternativeSchemes=" + Arrays.toString(alternativeSchemes) +
                ", format=" + format +
                ", qrDataSeparator=" + separator +
                ", characterSet=" + characterSet +
                '}';
    }
}
