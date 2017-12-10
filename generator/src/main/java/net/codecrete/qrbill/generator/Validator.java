//
// Swiss QR Bill Generator
// Copyright (c) 2017 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
package net.codecrete.qrbill.generator;

import net.codecrete.qrbill.generator.ValidationMessage.Type;


/**
 * Validates and cleans QR bill data.
 */
class Validator {

    private Bill billIn;
    private Bill billOut;
    private ValidationResult validationResult;

    Validator(Bill bill, ValidationResult result) {
        billIn = bill;
        billOut = new Bill();
        validationResult = result;
    }

    Bill validate() {

        billOut.setLanguage(billIn.getLanguage());
        billOut.setVersion(billIn.getVersion());

        // currency
        String currency = trimmed(billIn.getCurrency());
        if (validateMandatory(currency, Bill.FIELD_CURRENCY)) {
            currency = currency.toUpperCase();
            if (!"CHF".equals(currency) && !"EUR".equals(currency)) {
                validationResult.addMessage(Type.Error, Bill.FIELD_CURRENCY, "currency_is_chf_or_eur");
            } else {
                billOut.setCurrency(currency);
            }
        }

        // amount
        if (billIn.isAmountOpen()) {
            billOut.setAmountOpen(true);
        } else {
            Double amount = billIn.getAmount();
            if (amount == null) {
                validationResult.addMessage(Type.Error, Bill.FIELD_AMOUNT, "amount_open_or_mandatory");
            } else if (billIn.getAmount() < 0.01 || billIn.getAmount() > 999999999.99) {
                validationResult.addMessage(Type.Error, Bill.FIELD_AMOUNT, "amount_in_valid_range");
            } else {
                billOut.setAmountOpen(false);
                billOut.setAmount(billIn.getAmount());
            }
        }

        // account no
        boolean isQRBillIBAN = false;
        String account = trimmed(billIn.getAccount());
        if (validateMandatory(account, Bill.FIELD_ACCOUNT)) {
            account = whiteSpaceRemoved(account).toUpperCase();
            if (validateIBAN(account, Bill.FIELD_ACCOUNT)) {
                if (!account.startsWith("CH") && !account.startsWith("LI")) {
                    validationResult.addMessage(Type.Error, Bill.FIELD_ACCOUNT, "account_is_ch_li_iban");
                } else if (account.length() != 21) {
                    validationResult.addMessage(Type.Error, Bill.FIELD_ACCOUNT, "account_is_valid_iban");
                } else {
                    // TODO specific Swiss IBAN validation
                    billOut.setAccount(account);
                    isQRBillIBAN = account.charAt(4) == '3' && (account.charAt(5) == '0' || account.charAt(5) == '1');
                }
            }
        }

        // creditor
        Person creditor = validatePerson(billIn.getCreditor(), Bill.FIELDROOT_CREDITOR, true);
        billOut.setCreditor(creditor);

        // final creditor
        Person finalCreditor = validatePerson(billIn.getFinalCreditor(), Bill.FIELDROOT_FINAL_CREDITOR, false);
        billOut.setFinalCreditor(finalCreditor);

        // reference no
        String referenceNo = trimmed(billIn.getReferenceNo());
        if (referenceNo != null) {
            referenceNo = whiteSpaceRemoved(referenceNo);
            if (referenceNo.startsWith("RF")) {
                if (!isValidISO11649ReferenceNo(referenceNo)) {
                    validationResult.addMessage(Type.Error, Bill.FIELD_REFERENCE_NO, "valid_iso11649_creditor_ref");
                } else {
                    billOut.setReferenceNo(referenceNo);
                }
            } else {
                if (referenceNo.length() < 27)
                    referenceNo = "00000000000000000000000000".substring(0, 27 - referenceNo.length()) + referenceNo;
                if (!isValidQRReferenceNo(referenceNo))
                    validationResult.addMessage(Type.Error, Bill.FIELD_REFERENCE_NO, "valid_qr_ref_no");
                else
                    billOut.setReferenceNo(referenceNo);
            }
        } else {
            if (isQRBillIBAN)
                validationResult.addMessage(Type.Error, Bill.FIELD_REFERENCE_NO, "mandatory_for_qr_iban");
        }

        // additional information
        String additionalInfo = trimmed(billIn.getAdditionalInformation());
        additionalInfo = clipValue(additionalInfo, 140, Bill.FIELD_ADDITIONAL_INFO);
        billOut.setAdditionalInformation(additionalInfo);

        // debtor
        if (billIn.isDebtorOpen()) {
            billOut.setDebtorOpen(true);
            billOut.setDebtor(null);
        } else {
            Person debtor = validatePerson(billIn.getDebtor(), Bill.FIELDROOT_DEBTOR, false);
            billOut.setDebtorOpen(debtor == null);
            billOut.setDebtor(debtor);
        }

        // due date
        billOut.setDueDate(billIn.getDueDate());

        return billOut;
    }

    private boolean validateMandatory(String value, String field) {
        if (isNullOrEmpty(value)) {
            validationResult.addMessage(Type.Error, field, "field_is_mandatory");
            return false;
        }

        return true;
    }

    private boolean validateMandatory(String value, String fieldRoot, String subfield) {
        if (isNullOrEmpty(value)) {
            validationResult.addMessage(Type.Error, fieldRoot + subfield, "field_is_mandatory");
            return false;
        }

        return true;
    }

    private String clipValue(String value, int maxLength, String field) {
        if (value != null && value.length() > maxLength) {
            validationResult.addMessage(Type.Warning, field, "field_clipped", new String[] { Integer.toString(maxLength) });
            return value.substring(0, maxLength);
        }

        return value;
    }

    private String clipValue(String value, int maxLength, String fieldRoot, String subfield) {
        if (value != null && value.length() > maxLength) {
            validationResult.addMessage(Type.Warning, fieldRoot + subfield, "field_clipped", new String[] { Integer.toString(maxLength) });
            return value.substring(0, maxLength);
        }

        return value;
    }

    private Person validatePerson(Person personIn, String fieldRoot, boolean mandatory) {
        Person personOut = cleanedPerson(personIn);
        if (personOut == null) {
            if (mandatory) {
                validationResult.addMessage(Type.Error, fieldRoot + Bill.SUBFIELD_NAME, "field_is_mandatory");
                validationResult.addMessage(Type.Error, fieldRoot + Bill.SUBFIELD_POSTAL_CODE, "field_is_mandatory");
                validationResult.addMessage(Type.Error, fieldRoot + Bill.SUBFIELD_CITY, "field_is_mandatory");
                validationResult.addMessage(Type.Error, fieldRoot + Bill.SUBFIELD_COUNTRY_CODE, "field_is_mandatory");
            }
            return null;
        }

        validateMandatory(personOut.getName(), fieldRoot, Bill.SUBFIELD_NAME);
        validateMandatory(personOut.getPostalCode(), fieldRoot, Bill.SUBFIELD_POSTAL_CODE);
        validateMandatory(personOut.getCity(), fieldRoot, Bill.SUBFIELD_CITY);
        validateMandatory(personOut.getCountryCode(), fieldRoot, Bill.SUBFIELD_COUNTRY_CODE);

        personOut.setName(clipValue(personOut.getName(), 70, fieldRoot, Bill.SUBFIELD_NAME));
        personOut.setStreet(clipValue(personOut.getStreet(), 70, fieldRoot, Bill.SUBFIELD_STREET));
        personOut.setHouseNumber(clipValue(personOut.getHouseNumber(), 16, fieldRoot, Bill.SUBFIELD_HOUSE_NO));
        personOut.setPostalCode(clipValue(personOut.getPostalCode(), 16, fieldRoot, Bill.SUBFIELD_POSTAL_CODE));
        personOut.setCity(clipValue(personOut.getCity(), 35, fieldRoot, Bill.SUBFIELD_CITY));

        if (personOut.getCountryCode() != null) {
            if (personOut.getCountryCode().length() != 2
                    || !isAlphaNumeric(personOut.getCountryCode()))
                validationResult.addMessage(Type.Error, fieldRoot + Bill.SUBFIELD_COUNTRY_CODE, "valid_country_code");
        }

        return personOut;
    }

    private boolean validateIBAN(String iban, String field) {
        if (!isValidIBAN(iban)) {
            validationResult.addMessage(Type.Error, field, "account_is_valid_iban");
            return false;
        }
        return true;
    }

    private Person cleanedPerson(Person personIn) {
        if (personIn == null)
            return null;
        Person personOut = new Person();
        personOut.setName(trimmed(personIn.getName()));
        personOut.setStreet(trimmed(personIn.getStreet()));
        personOut.setHouseNumber(trimmed(personIn.getHouseNumber()));
        personOut.setPostalCode(trimmed(personIn.getPostalCode()));
        personOut.setCity(trimmed(personIn.getCity()));
        personOut.setCountryCode(trimmed(personIn.getCountryCode()));

        if (personOut.getName() == null && personOut.getStreet() == null
                && personOut.getHouseNumber() == null && personOut.getPostalCode() == null
                && personOut.getCity() == null && personOut.getCountryCode() == null)
            return null;

        return personOut;
    }

    private static final int IBAN_CHECK_MODULUS = 97;

    private static boolean isValidIBAN(String iban) {
        if (iban.length() < 5)
            return false;
        if (!isAlphaNumeric(iban))
            return false;
        if (!Character.isLetter(iban.charAt(0)) || !Character.isLetter(iban.charAt(1))
                || !Character.isDigit(iban.charAt(2)) || !Character.isDigit(iban.charAt(3)))
            return false;

        String rearranged = iban.substring(4) + iban.substring(0, 4);
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
                sum = sum % IBAN_CHECK_MODULUS;
        }

        sum = sum % IBAN_CHECK_MODULUS;
        return sum == 1;
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

        // TODO verify check digits

        return true;
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
}
