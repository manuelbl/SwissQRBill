//
// Swiss QR Bill Generator
// Copyright (c) 2017 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
package net.codecrete.qrbill.generator;

import net.codecrete.qrbill.generator.ValidationMessage.Type;

import java.text.Normalizer;


/**
 * Validates and cleans QR bill data.
 */
public class Validator {

    private static final String KEY_CURRENCY_IS_CHF_OR_EUR = "currency_is_chf_or_eur";
    private static final String KEY_AMOUNT_IS_IN_VALID_RANGE = "amount_in_valid_range";
    private static final String KEY_ACCOUNT_IS_CH_LI_IBAN = "account_is_ch_li_iban";
    private static final String KEY_ACCOUNT_IS_VALID_IBAN = "account_is_valid_iban";
    private static final String KEY_VALID_ISO11649_CREDITOR_REF = "valid_iso11649_creditor_ref";
    private static final String KEY_VALID_QR_REF_NO = "valid_qr_ref_no";
    private static final String KEY_MANDATORY_FOR_QR_IBAN = "mandatory_for_qr_iban";
    private static final String KEY_FIELD_IS_MANDATORY = "field_is_mandatory";
    private static final String KEY_VALID_COUNTRY_CODE = "valid_country_code";
    private static final String KEY_FIELD_CLIPPED = "field_clipped";
    private static final String KEY_REPLACED_UNSUPPORTED_CHARACTERS = "replaced_unsupported_characters";

    private Bill billIn;
    private Bill billOut;
    private ValidationResult validationResult;

    public Validator(Bill bill, ValidationResult result) {
        billIn = bill;
        billOut = new Bill();
        validationResult = result;
    }

    public Bill validate() {

        billOut.setLanguage(billIn.getLanguage());
        billOut.setVersion(billIn.getVersion());

        validateCurrency();
        validateAmount();
        boolean isQRBillIBAN = validateAccountNumber();
        validateCreditor();
        validateFinalCreditor();
        validateReferenceNo(isQRBillIBAN);
        validateAdditionalInformation();
        validateDebtor();
        validateDueDate();

        return billOut;
    }

    private void validateCurrency() {
        String currency = trimmed(billIn.getCurrency());
        if (validateMandatory(currency, Bill.FIELD_CURRENCY)) {
            currency = currency.toUpperCase();
            if (!"CHF".equals(currency) && !"EUR".equals(currency)) {
                validationResult.addMessage(Type.ERROR, Bill.FIELD_CURRENCY, KEY_CURRENCY_IS_CHF_OR_EUR);
            } else {
                billOut.setCurrency(currency);
            }
        }
    }

    private void validateAmount() {
        Double amount = billIn.getAmount();
        if (amount == null) {
            billOut.setAmount(null);
        } else if (billIn.getAmount() < 0.01 || billIn.getAmount() > 999999999.99) {
            validationResult.addMessage(Type.ERROR, Bill.FIELD_AMOUNT, KEY_AMOUNT_IS_IN_VALID_RANGE);
        } else {
            billOut.setAmount(billIn.getAmount());
        }
    }

    private boolean validateAccountNumber() {
        boolean isQRBillIBAN = false;
        String account = trimmed(billIn.getAccount());
        if (validateMandatory(account, Bill.FIELD_ACCOUNT)) {
            account = whiteSpaceRemoved(account).toUpperCase();
            if (validateIBAN(account, Bill.FIELD_ACCOUNT)) {
                if (!account.startsWith("CH") && !account.startsWith("LI")) {
                    validationResult.addMessage(Type.ERROR, Bill.FIELD_ACCOUNT, KEY_ACCOUNT_IS_CH_LI_IBAN);
                } else if (account.length() != 21) {
                    validationResult.addMessage(Type.ERROR, Bill.FIELD_ACCOUNT, KEY_ACCOUNT_IS_VALID_IBAN);
                } else {
                    // TODO specific Swiss IBAN validation
                    billOut.setAccount(account);
                    isQRBillIBAN = account.charAt(4) == '3' && (account.charAt(5) == '0' || account.charAt(5) == '1');
                }
            }
        }
        return isQRBillIBAN;
    }

    private void validateCreditor() {
        Address creditor = validatePerson(billIn.getCreditor(), Bill.FIELDROOT_CREDITOR, true);
        billOut.setCreditor(creditor);
    }

    private void validateFinalCreditor() {
        Address finalCreditor = validatePerson(billIn.getFinalCreditor(), Bill.FIELDROOT_FINAL_CREDITOR, false);
        billOut.setFinalCreditor(finalCreditor);
    }

    private void validateReferenceNo(boolean isQRBillIBAN) {
        String referenceNo = trimmed(billIn.getReferenceNo());

        if (referenceNo == null) {
            if (isQRBillIBAN)
                validationResult.addMessage(Type.ERROR, Bill.FIELD_REFERENCE_NO, KEY_MANDATORY_FOR_QR_IBAN);
            return;
        }

        referenceNo = whiteSpaceRemoved(referenceNo);
        if (referenceNo.startsWith("RF")) {
            if (!isValidISO11649ReferenceNo(referenceNo)) {
                validationResult.addMessage(Type.ERROR, Bill.FIELD_REFERENCE_NO, KEY_VALID_ISO11649_CREDITOR_REF);
            } else {
                billOut.setReferenceNo(referenceNo);
            }
        } else {
            if (referenceNo.length() < 27)
                referenceNo = "00000000000000000000000000".substring(0, 27 - referenceNo.length()) + referenceNo;
            if (!isValidQRReferenceNo(referenceNo))
                validationResult.addMessage(Type.ERROR, Bill.FIELD_REFERENCE_NO, KEY_VALID_QR_REF_NO);
            else
                billOut.setReferenceNo(referenceNo);
        }
    }

    private void validateAdditionalInformation() {
        String additionalInfo = trimmed(billIn.getAdditionalInfo());
        additionalInfo = clipValue(additionalInfo, 140, Bill.FIELD_ADDITIONAL_INFO);
        billOut.setAdditionalInfo(additionalInfo);
    }

    private void validateDebtor() {
        Address debtor = validatePerson(billIn.getDebtor(), Bill.FIELDROOT_DEBTOR, false);
        billOut.setDebtor(debtor);
    }

    private void validateDueDate() {
        billOut.setDueDate(billIn.getDueDate());
    }

    private Address validatePerson(Address addressIn, String fieldRoot, boolean mandatory) {
        Address addressOut = cleanedPerson(addressIn, fieldRoot);
        if (addressOut == null) {
            if (mandatory) {
                validationResult.addMessage(Type.ERROR, fieldRoot + Bill.SUBFIELD_NAME, KEY_FIELD_IS_MANDATORY);
                validationResult.addMessage(Type.ERROR, fieldRoot + Bill.SUBFIELD_POSTAL_CODE, KEY_FIELD_IS_MANDATORY);
                validationResult.addMessage(Type.ERROR, fieldRoot + Bill.SUBFIELD_TOWN, KEY_FIELD_IS_MANDATORY);
                validationResult.addMessage(Type.ERROR, fieldRoot + Bill.SUBFIELD_COUNTRY_CODE, KEY_FIELD_IS_MANDATORY);
            }
            return null;
        }

        validateMandatory(addressOut.getName(), fieldRoot, Bill.SUBFIELD_NAME);
        validateMandatory(addressOut.getPostalCode(), fieldRoot, Bill.SUBFIELD_POSTAL_CODE);
        validateMandatory(addressOut.getTown(), fieldRoot, Bill.SUBFIELD_TOWN);
        validateMandatory(addressOut.getCountryCode(), fieldRoot, Bill.SUBFIELD_COUNTRY_CODE);

        addressOut.setName(clipValue(addressOut.getName(), 70, fieldRoot, Bill.SUBFIELD_NAME));
        addressOut.setStreet(clipValue(addressOut.getStreet(), 70, fieldRoot, Bill.SUBFIELD_STREET));
        addressOut.setHouseNo(clipValue(addressOut.getHouseNo(), 16, fieldRoot, Bill.SUBFIELD_HOUSE_NO));
        addressOut.setPostalCode(clipValue(addressOut.getPostalCode(), 16, fieldRoot, Bill.SUBFIELD_POSTAL_CODE));
        addressOut.setTown(clipValue(addressOut.getTown(), 35, fieldRoot, Bill.SUBFIELD_TOWN));

        if (addressOut.getCountryCode() != null
            && (addressOut.getCountryCode().length() != 2
                    || !isAlphaNumeric(addressOut.getCountryCode())))
                validationResult.addMessage(Type.ERROR, fieldRoot + Bill.SUBFIELD_COUNTRY_CODE, KEY_VALID_COUNTRY_CODE);

        return addressOut;
    }

    private boolean validateIBAN(String iban, String field) {
        if (!isValidIBAN(iban)) {
            validationResult.addMessage(Type.ERROR, field, KEY_ACCOUNT_IS_VALID_IBAN);
            return false;
        }
        return true;
    }

    private Address cleanedPerson(Address addressIn, String fieldRoot) {
        if (addressIn == null)
            return null;
        Address addressOut = new Address();
        addressOut.setName(trimmed(cleanedValue(addressIn.getName(), fieldRoot, Bill.SUBFIELD_NAME)));
        addressOut.setStreet(trimmed(cleanedValue(addressIn.getStreet(), fieldRoot, Bill.SUBFIELD_STREET)));
        addressOut.setHouseNo(trimmed(cleanedValue(addressIn.getHouseNo(), fieldRoot, Bill.SUBFIELD_HOUSE_NO)));
        addressOut.setPostalCode(trimmed(cleanedValue(addressIn.getPostalCode(), fieldRoot, Bill.SUBFIELD_POSTAL_CODE)));
        addressOut.setTown(trimmed(cleanedValue(addressIn.getTown(), fieldRoot, Bill.SUBFIELD_TOWN)));
        addressOut.setCountryCode(trimmed(addressIn.getCountryCode()));

        if (addressOut.getName() == null && addressOut.getStreet() == null
                && addressOut.getHouseNo() == null && addressOut.getPostalCode() == null
                && addressOut.getTown() == null && addressOut.getCountryCode() == null)
            return null;

        return addressOut;
    }

    private boolean validateMandatory(String value, String field) {
        if (isNullOrEmpty(value)) {
            validationResult.addMessage(Type.ERROR, field, KEY_FIELD_IS_MANDATORY);
            return false;
        }

        return true;
    }

    private boolean validateMandatory(String value, String fieldRoot, String subfield) {
        if (isNullOrEmpty(value)) {
            validationResult.addMessage(Type.ERROR, fieldRoot + subfield, KEY_FIELD_IS_MANDATORY);
            return false;
        }

        return true;
    }

    private String clipValue(String value, int maxLength, String field) {
        if (value != null && value.length() > maxLength) {
            validationResult.addMessage(Type.WARNING, field, KEY_FIELD_CLIPPED, new String[] { Integer.toString(maxLength) });
            return value.substring(0, maxLength);
        }

        return value;
    }

    private String clipValue(String value, int maxLength, String fieldRoot, String subfield) {
        if (value != null && value.length() > maxLength) {
            validationResult.addMessage(Type.WARNING, fieldRoot + subfield, KEY_FIELD_CLIPPED, new String[] { Integer.toString(maxLength) });
            return value.substring(0, maxLength);
        }

        return value;
    }

    /**
     * Returns a string where all unsupported characters have been replaced.
     * <p>
     *     If characters beyond 0xff are detected, the string is first normalized
     *     such that letters with umlauts or accents expressed with two code points
     *     are merged into a single code point (if possible), some of which might
     *     become valid.
     * </p>
     * <p>
     *     If the resulting strings is all white space, {@code null} is
     *     returned and no warning is added.
     * </p>
     * @param value string to process
     * @param isNormalized indicates if normalization has already been run
     * @param fieldRoot field root
     * @param subfield sub field name
     * @return the cleaned string
     */
    private String cleanedValue(String value, boolean isNormalized, String fieldRoot, String subfield) {

        /* This code has cognitive complexity 31. Deal with it. */

        if (value == null)
            return null;

        int len = value.length(); // length of value
        boolean justProcessedSpace = false; // flag indicating whether we've just processed a space character
        StringBuilder sb = null; // String builder for result
        int lastCopiedPos = 0; // last position (excluding) copied to the result

        // String processing pattern: Iterate all characters and focus on runs of valid characters
        // that can simply be copied. If all characters are valid, no memory is allocated.
        int pos = 0;
        while (pos < len) {
            char ch = value.charAt(pos); // current character

            if (isValidQRBillCharacter(ch)) {
                justProcessedSpace = ch == ' ';
                pos++;
                continue;
            }

            // Check for normalization
            if (ch > 0xff && !isNormalized) {
                isNormalized = Normalizer.isNormalized(value, Normalizer.Form.NFC);
                if (!isNormalized) {
                    // Normalize string and start over
                    value = Normalizer.normalize(value, Normalizer.Form.NFC);
                    return cleanedValue(value, true, fieldRoot, subfield);
                }
            }

            if (sb == null)
                sb = new StringBuilder(value.length());

            // copy processed characters to result before taking care of the invalid character
            if (pos > lastCopiedPos)
                sb.append(value, lastCopiedPos, pos);

            if (Character.isHighSurrogate(ch)) {
                // Proper Unicode handling to prevent surrogates and combining characters
                // from being replaced with multiples periods.
                int codePoint = value.codePointAt(pos);
                if (Character.getType(codePoint) != Character.COMBINING_SPACING_MARK)
                    sb.append('.');
                justProcessedSpace = false;
                pos++;
            } else {
                if (Character.isWhitespace(ch)) {
                    if (!justProcessedSpace)
                        sb.append(' ');
                    justProcessedSpace = true;
                } else {
                    sb.append('.');
                    justProcessedSpace = false;
                }
            }
            pos++;
            lastCopiedPos = pos;
        }

        if (sb == null)
            return value;

        if (lastCopiedPos < len)
            sb.append(value, lastCopiedPos, len);

        String result = sb.toString().trim();
        if (result.length() == 0)
            return null;

        validationResult.addMessage(Type.WARNING, fieldRoot + subfield, KEY_REPLACED_UNSUPPORTED_CHARACTERS);

        return result;
    }

    private String cleanedValue(String value, String fieldRoot, String subfield) {
        return cleanedValue(value, false, fieldRoot, subfield);
    }

    private static boolean isValidIBAN(String iban) {
        if (iban.length() < 5)
            return false;
        if (!isAlphaNumeric(iban))
            return false;
        if (!Character.isLetter(iban.charAt(0)) || !Character.isLetter(iban.charAt(1))
                || !Character.isDigit(iban.charAt(2)) || !Character.isDigit(iban.charAt(3)))
            return false;

        return hasValidMod97CheckDigits(iban);
    }

    private static final int[] MOD_10 = { 0, 9, 4, 6, 8, 2, 7, 1, 3, 5 };

    private static boolean isValidQRReferenceNo(String referenceNo) {
        if (!isNumeric(referenceNo))
            return false;

        int carry = 0;
        int len = referenceNo.length();
        if (len != 27)
            return false;

        for (int i = 0; i < len; i++) {
            int digit = referenceNo.charAt(i) - '0';
            carry = MOD_10[(carry + digit) % 10];
        }

        return carry == 0;
    }

    private static boolean isValidISO11649ReferenceNo(String referenceNo) {
        if (referenceNo.length() < 5 || referenceNo.length() > 25)
            return false;

        if (!isAlphaNumeric(referenceNo))
            return false;

        if (!Character.isDigit(referenceNo.charAt(2)) || !Character.isDigit(referenceNo.charAt(3)))
            return false;

        return hasValidMod97CheckDigits(referenceNo);
    }

    private static boolean hasValidMod97CheckDigits(String number) {
        String rearranged = number.substring(4) + number.substring(0, 4);
        int len = rearranged.length();
        int sum = 0;
        for (int i = 0; i < len; i++) {
            char ch = rearranged.charAt(i);
            if (ch >= '0' && ch <= '9') {
                sum = sum * 10 + (ch - '0');
            } else if (ch >= 'A' && ch <= 'Z') {
                sum = sum * 100 + (ch - 'A' + 10);
            } else if (ch >= 'a' && ch <= 'z') {
                sum = sum * 100 + (ch - 'a' + 10);
            } else {
                return false;
            }
            if (sum > 9999999)
                sum = sum % 97;
        }

        sum = sum % 97;
        return sum == 1;
    }

    private static String trimmed(String value) {
        if (value == null)
            return null;
        value = value.trim();
        if (value.length() == 0)
            return null;
        return value;
    }

    private static String whiteSpaceRemoved(String value) {
        StringBuilder sb = null;
        int len = value.length();
        int lastCopied = 0;
        for (int i = 0; i < len; i++) {
            char ch = value.charAt(i);
            if (ch == ' ') {
                if (i > lastCopied) {
                    if (sb == null)
                        sb = new StringBuilder();
                    sb.append(value, lastCopied, i);
                }
                lastCopied = i + 1;
            }
        }

        if (sb == null)
            return value;

        if (len > lastCopied)
            sb.append(value, lastCopied, len);
        return sb.toString();
    }

    private static boolean isNumeric(String value) {
        int len = value.length();
        for (int i = 0; i < len; i++) {
            char ch = value.charAt(i);
            if (ch < '0' || ch > '9')
                return false;
        }
        return true;
    }

    private static boolean isAlphaNumeric(String value) {
        int len = value.length();
        for (int i = 0; i < len; i++) {
            char ch = value.charAt(i);
            if (ch >= '0' && ch <= '9')
                continue;
            if (ch >= 'A' && ch <= 'Z')
                continue;
            if (ch >= 'a' && ch <= 'z')
                continue;
            return false;
        }
        return true;
    }

    private static boolean isNullOrEmpty(String value) {
        return value == null || value.trim().length() == 0;
    }

    private static boolean isValidQRBillCharacter(char ch) {
        if (ch < 0x20)
            return false;
        if (ch == 0x5e)
            return false;
        if (ch <= 0x7e)
            return true;
        if (ch == 0xa3 || ch == 0xb4)
            return true;
        if (ch < 0xc0 || ch > 0xfd)
            return false;
        if (ch == 0xc3 || ch == 0xc5 || ch == 0xc6)
            return false;
        if (ch == 0xd0 || ch == 0xd5 || ch == 0xd7 || ch == 0xd8)
            return false;
        if (ch == 0xdd || ch == 0xde)
            return false;
        if (ch == 0xe3 || ch == 0xe5 || ch == 0xe6)
            return false;
        if (ch == 0xf0 || ch == 0xf5 || ch == 0xf8)
            return false;
        return true;
    }
}
