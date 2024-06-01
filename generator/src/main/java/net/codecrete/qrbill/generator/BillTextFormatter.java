package net.codecrete.qrbill.generator;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * Formats text on a QR bill.
 * <p>
 * The resulting text often contains multiple lines, e.g. for addresses. These line breaks a represented
 * by a line feed character (U+000A). Long lines might require additional line breaks to fit into the
 * given text boxes. These additional line breaks are not included in the resulting text.
 * </p>
 */
public class BillTextFormatter {

    private final Bill bill;

    /**
     * Creates a new instance for the specified bill.
     *
     * @param bill QR bill data
     * @throws QRBillValidationError if the bill cannot be validated without errors
     */
    public BillTextFormatter(Bill bill) {
        this(bill, false);
    }

    /**
     * Creates a new instance for the given bill.
     *
     * @param bill QR bill data
     * @param isValidated indicates if the bill has already been validated
     */
    public BillTextFormatter(Bill bill, boolean isValidated) {
        if (!isValidated) {
            ValidationResult result = Validator.validate(bill);
            this.bill = result.getCleanedBill();
        } else {
            this.bill = bill;
        }
    }

    /**
     * Gets the "payable to" text (account number and creditor address).
     *
     * @return "payable to" text
     */
    public String getPayableTo() {
        return  getAccount() + "\n" + getCreditorAddress();
    }

    /**
     * Gets the "payable to" text (account number and creditor address) with a reduced address.
     * <p>
     * If space is very tight, an address without street and house number can be used.
     * </p>
     *
     * @return "payable to" text
     */
    public String getPayableToReduced() {
        return  getAccount() + "\n" + getCreditorAddressReduced();
    }

    /**
     * Gets the formatted account number.
     *
     * @return account number
     */
    public String getAccount() {
        return Payments.formatIBAN(bill.getAccount());
    }

    /**
     * Gets the formatted creditor address.
     *
     * @return formatted address
     */
    public String getCreditorAddress() {
        return formatAddressForDisplay(bill.getCreditor(), isCreditorWithCountryCode());
    }

    /**
     * Gets the reduced formatted creditor address.
     * <p>
     * If space is very tight, a reduced address without street and house number can be used.
     * </p>
     *
     * @return formatted address
     */
    public String getCreditorAddressReduced() {
        return formatAddressForDisplay(createReducedAddress(bill.getCreditor()), isCreditorWithCountryCode());
    }

    /**
     * Gets the formatted reference number.
     *
     * @return reference number, or {@code null} if no reference number has been specified
     */
    public String getReference() {
        return formatReferenceNumber(bill.getReference());
    }

    /**
     * Gets the formatted amount.
     *
     * @return the amount, or {@code null} if no amount has been specified
     */
    public String getAmount() {
        if (bill.getAmount() == null)
            return null;
        return formatAmountForDisplay(bill.getAmount());
    }

    /**
     * Gets the "payable by" text (debtor address).
     *
     * @return formatted address, or {@code null} if no debtor address has been specified
     */
    public String getPayableBy() {
        if (bill.getDebtor() == null)
            return null;
        return formatAddressForDisplay(bill.getDebtor(), isDebtorWithCountryCode());
    }

    /**
     * Gets the "payable by" text with a reduced debtor address.
     * <p>
     * If space is very tight, a reduced address without street and house number can be used.
     * </p>
     *
     * @return formatted address, or {@code null} if no debtor address has been specified
     */
    public String getPayableByReduced() {
        if (bill.getDebtor() == null)
            return null;
        return formatAddressForDisplay(createReducedAddress(bill.getDebtor()), isDebtorWithCountryCode());
    }

    /**
     * Returns the additional information.
     * <p>
     * It consists of the unstructured message, the bill information, both or none,
     * depending on what has been specified. Lines are separated by a line feed character.
     * </p>
     *
     * @return additional information, or {@code null} if neither an unstructured message nor bill information has been specified
     */
    public String getAdditionalInformation() {
        String info = bill.getUnstructuredMessage();
        if (bill.getBillInformation() != null) {
            if (info == null)
                info = bill.getBillInformation();
            else
                info = info + "\n" + bill.getBillInformation();
        }
        return info;
    }

    private static String formatAmountForDisplay(BigDecimal amount) {
        return createAmountFormatter().format(amount);
    }

    @SuppressWarnings("deprecation")
    private static String formatAddressForDisplay(Address address, boolean withCountryCode) {
        StringBuilder sb = new StringBuilder();
        sb.append(address.getName());

        if (address.getType() == Address.Type.STRUCTURED) {
            String street = address.getStreet();
            if (street != null) {
                sb.append("\n");
                sb.append(street);
            }
            String houseNo = address.getHouseNo();
            if (houseNo != null) {
                sb.append(street != null ? " " : "\n");
                sb.append(houseNo);
            }
            sb.append("\n");
            if (withCountryCode) {
                sb.append(address.getCountryCode());
                sb.append(" – ");
            }
            sb.append(address.getPostalCode());
            sb.append(" ");
            sb.append(address.getTown());

        } else if (address.getType() == Address.Type.COMBINED_ELEMENTS) {
            if (address.getAddressLine1() != null) {
                sb.append("\n");
                sb.append(address.getAddressLine1());
            }
            sb.append("\n");
            if (withCountryCode) {
                sb.append(address.getCountryCode());
                sb.append(" – ");
            }
            sb.append(address.getAddressLine2());
        }
        return sb.toString();
    }

    private static String formatReferenceNumber(String refNo) {
        if (refNo == null)
            return null;
        refNo = refNo.trim();
        int len = refNo.length();
        if (len == 0)
            return null;
        if (refNo.startsWith("RF"))
            // same format as IBAN
            return Payments.formatIBAN(refNo);

        return Payments.formatQRReferenceNumber(refNo);
    }

    private boolean isCreditorWithCountryCode() {
        // The creditor country code is even shown for a Swiss address if the debtor lives abroad
        return isForeignAddress(bill.getCreditor(), bill.getFormat()) || isForeignAddress(bill.getDebtor(), bill.getFormat());
    }

    private boolean isDebtorWithCountryCode() {
        return isForeignAddress(bill.getDebtor(), bill.getFormat());
    }

    private static boolean isForeignAddress(Address address, BillFormat format) {
        return address != null && !format.getLocalCountryCode().equals(address.getCountryCode());
    }

    @SuppressWarnings("deprecation")
    private Address createReducedAddress(Address address) {
        // Address without street / house number
        Address reducedAddress = new Address();
        reducedAddress.setName(address.getName());
        reducedAddress.setCountryCode(address.getCountryCode());
        if (address.getType() == Address.Type.STRUCTURED) {
            reducedAddress.setPostalCode(address.getPostalCode());
            reducedAddress.setTown(address.getTown());
        } else if (address.getType() == Address.Type.COMBINED_ELEMENTS) {
            reducedAddress.setAddressLine2(address.getAddressLine2());
        }

        return reducedAddress;
    }

    private static DecimalFormat createAmountFormatter() {
        DecimalFormat format = new DecimalFormat("###,##0.00");
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        symbols.setDecimalSeparator('.');
        symbols.setGroupingSeparator(' ');
        format.setDecimalFormatSymbols(symbols);
        return format;
    }

}
