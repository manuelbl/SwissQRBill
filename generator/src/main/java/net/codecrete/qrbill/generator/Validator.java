//
// Swiss QR Bill Generator
// Copyright (c) 2017 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
package net.codecrete.qrbill.generator;

import net.codecrete.qrbill.generator.StringCleanup.CleaningResult;
import net.codecrete.qrbill.generator.ValidationMessage.Type;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Internal class for validating and cleaning QR bill data.
 */
class Validator {

    private final Bill billIn;
    private final Bill billOut;
    private final ValidationResult validationResult;

    /**
     * Validates the QR bill data and returns the validation messages (if any) and
     * the cleaned bill data.
     *
     * @param bill bill data to validate
     * @return validation result
     */
    static ValidationResult validate(Bill bill) {
        Validator validator = new Validator(bill);
        return validator.validateBill();
    }

    private Validator(Bill bill) {
        billIn = bill;
        billOut = new Bill();
        validationResult = new ValidationResult();
    }

    private ValidationResult validateBill() {

        billOut.setFormat(billIn.getFormat() != null ? new BillFormat(billIn.getFormat()) : null);
        billOut.setVersion(billIn.getVersion());

        validateAccountNumber();
        validateCreditor();
        validateCurrency();
        validateAmount();
        validateDebtor();
        validateReference();
        validateAdditionalInformation();
        validateAlternativeSchemes();

        validationResult.setCleanedBill(billOut);
        return validationResult;
    }

    private void validateCurrency() {
        String currency = Strings.trimmed(billIn.getCurrency());
        if (validateMandatory(currency, ValidationConstants.FIELD_CURRENCY)) {
            currency = currency.toUpperCase(Locale.US);
            if (!"CHF".equals(currency) && !"EUR".equals(currency)) {
                validationResult.addMessage(Type.ERROR, ValidationConstants.FIELD_CURRENCY, ValidationConstants.KEY_CURRENCY_NOT_CHF_OR_EUR);
            } else {
                billOut.setCurrency(currency);
            }
        }
    }

    private static final BigDecimal AMOUNT_MAX = BigDecimal.valueOf(99999999999L, 2);

    private void validateAmount() {
        BigDecimal amount = billIn.getAmount();
        if (amount == null) {
            billOut.setAmount(null);
        } else {
            amount = amount.setScale(2, RoundingMode.HALF_UP); // round to multiple of 0.01
            if (BigDecimal.ZERO.compareTo(amount) > 0 || AMOUNT_MAX.compareTo(amount) < 0) {
                validationResult.addMessage(Type.ERROR, ValidationConstants.FIELD_AMOUNT, ValidationConstants.KEY_AMOUNT_OUTSIDE_VALID_RANGE);
            } else {
                billOut.setAmount(amount);
            }
        }
    }

    private void validateAccountNumber() {
        String account = Strings.trimmed(billIn.getAccount());
        if (validateMandatory(account, ValidationConstants.FIELD_ACCOUNT)) {
            account = Strings.whiteSpaceRemoved(account).toUpperCase(Locale.US);
            if (validateIBAN(account)) {
                if (!account.startsWith("CH") && !account.startsWith("LI")) {
                    validationResult.addMessage(Type.ERROR, ValidationConstants.FIELD_ACCOUNT, ValidationConstants.KEY_ACCOUNT_IBAN_NOT_FROM_CH_OR_LI);
                } else if (account.length() != 21) {
                    validationResult.addMessage(Type.ERROR, ValidationConstants.FIELD_ACCOUNT, ValidationConstants.KEY_ACCOUNT_IBAN_INVALID);
                } else {
                    billOut.setAccount(account);
                }
            }
        }
    }

    private void validateCreditor() {
        Address creditor = validateAddress(billIn.getCreditor(), ValidationConstants.FIELDROOT_CREDITOR, true);
        billOut.setCreditor(creditor);
    }

    private void validateReference() {
        String account = billOut.getAccount();
        boolean isValidAccount = account != null;
        boolean isQRBillIBAN = account != null && Payments.isQRIBAN(account);

        String reference = Strings.trimmed(billIn.getReference());
        boolean hasReferenceError = false;
        if (reference != null) {
            reference = Strings.whiteSpaceRemoved(reference);
            boolean looksLikeQRRef = Payments.isNumeric(reference);
            if (looksLikeQRRef)
                validateQRReference(reference);
            else
                validateISOReference(reference);
            hasReferenceError = billOut.getReference() == null;
        }

        if (isQRBillIBAN) {
            if (Bill.REFERENCE_TYPE_NO_REF.equals(billOut.getReferenceType()) && !hasReferenceError) {
                validationResult.addMessage(Type.ERROR, ValidationConstants.FIELD_REFERENCE, ValidationConstants.KEY_QR_REF_MISSING);
            } else if (Bill.REFERENCE_TYPE_CRED_REF.equals(billOut.getReferenceType())) {
                validationResult.addMessage(Type.ERROR, ValidationConstants.FIELD_REFERENCE, ValidationConstants.KEY_CRED_REF_INVALID_USE_FOR_QR_IBAN);
            }

        } else if (isValidAccount && Bill.REFERENCE_TYPE_QR_REF.equals(billOut.getReferenceType())) {
            validationResult.addMessage(Type.ERROR, ValidationConstants.FIELD_REFERENCE, ValidationConstants.KEY_QR_REF_INVALID_USE_FOR_NON_QR_IBAN);
        }
    }

    private void validateQRReference(String cleanedReference) {
        if (cleanedReference.length() < 27)
            cleanedReference = "00000000000000000000000000".substring(0, 27 - cleanedReference.length()) + cleanedReference;
        if (!Payments.isValidQRReference(cleanedReference)) {
            validationResult.addMessage(Type.ERROR, ValidationConstants.FIELD_REFERENCE, ValidationConstants.KEY_REF_INVALID);
        } else {
            billOut.setReference(cleanedReference);
            if (!Bill.REFERENCE_TYPE_QR_REF.equals(billIn.getReferenceType()))
                validationResult.addMessage(Type.ERROR, ValidationConstants.FIELD_REFERENCE_TYPE, ValidationConstants.KEY_REF_TYPE_INVALID);
        }
    }

    private void validateISOReference(String cleanedReference) {
        if (!Payments.isValidISO11649Reference(cleanedReference)) {
            validationResult.addMessage(Type.ERROR, ValidationConstants.FIELD_REFERENCE, ValidationConstants.KEY_REF_INVALID);
        } else {
            billOut.setReference(cleanedReference);
            if (!Bill.REFERENCE_TYPE_CRED_REF.equals(billIn.getReferenceType()))
                validationResult.addMessage(Type.ERROR, ValidationConstants.FIELD_REFERENCE_TYPE, ValidationConstants.KEY_REF_TYPE_INVALID);
        }
    }

    private void validateAdditionalInformation() {

        String billInformation = Strings.trimmed(billIn.getBillInformation());
        String unstructuredMessage = Strings.trimmed(billIn.getUnstructuredMessage());

        if (billInformation != null && (!billInformation.startsWith("//") || billInformation.length() < 4)) {
            validationResult.addMessage(Type.ERROR, ValidationConstants.FIELD_BILL_INFORMATION, ValidationConstants.KEY_BILL_INFO_INVALID);
            billInformation = null;
        }

        if (billInformation == null && unstructuredMessage == null)
            return;

        if (billInformation == null) {
            unstructuredMessage = cleanedValue(unstructuredMessage, ValidationConstants.FIELD_UNSTRUCTURED_MESSAGE);
            unstructuredMessage = clippedValue(unstructuredMessage, 140, ValidationConstants.FIELD_UNSTRUCTURED_MESSAGE);
            billOut.setUnstructuredMessage(unstructuredMessage);

        } else if (unstructuredMessage == null) {
            billInformation = cleanedValue(billInformation, ValidationConstants.FIELD_BILL_INFORMATION);
            if (validateLength(billInformation, 140, ValidationConstants.FIELD_BILL_INFORMATION))
                billOut.setBillInformation(billInformation);

        } else {

            billInformation = cleanedValue(billInformation, ValidationConstants.FIELD_BILL_INFORMATION);
            unstructuredMessage = cleanedValue(unstructuredMessage, ValidationConstants.FIELD_UNSTRUCTURED_MESSAGE);

            int combinedLength = billInformation.length() + unstructuredMessage.length();
            if (combinedLength > 140) {
                validationResult.addMessage(Type.ERROR, ValidationConstants.FIELD_UNSTRUCTURED_MESSAGE, ValidationConstants.KEY_ADDITIONAL_INFO_TOO_LONG);
                validationResult.addMessage(Type.ERROR, ValidationConstants.FIELD_BILL_INFORMATION, ValidationConstants.KEY_ADDITIONAL_INFO_TOO_LONG);
            } else {
                billOut.setUnstructuredMessage(unstructuredMessage);
                billOut.setBillInformation(billInformation);
            }
        }
    }

    private void validateAlternativeSchemes() {
        AlternativeScheme[] schemesOut = null;
        if (billIn.getAlternativeSchemes() != null) {

            List<AlternativeScheme> schemeList = createCleanSchemeList();
            if (!schemeList.isEmpty()) {
                schemesOut = schemeList.toArray(new AlternativeScheme[0]);

                if (schemesOut.length > 2) {
                    validationResult.addMessage(Type.ERROR, ValidationConstants.FIELD_ALTERNATIVE_SCHEMES, ValidationConstants.KEY_ALT_SCHEME_MAX_EXCEEDED);
                    schemesOut = Arrays.copyOfRange(schemesOut, 0, 2);
                }
            }
        }
        billOut.setAlternativeSchemes(schemesOut);
    }

    private List<AlternativeScheme> createCleanSchemeList() {
        int len = billIn.getAlternativeSchemes().length;
        List<AlternativeScheme> schemeList = new ArrayList<>(len);

        for (AlternativeScheme schemeIn : billIn.getAlternativeSchemes()) {

            AlternativeScheme schemeOut = new AlternativeScheme();
            schemeOut.setName(Strings.trimmed(schemeIn.getName()));
            schemeOut.setInstruction(Strings.trimmed(schemeIn.getInstruction()));
            if ((schemeOut.getName() != null || schemeOut.getInstruction() != null)
                    && validateLength(schemeOut.getInstruction(), 100, ValidationConstants.FIELD_ALTERNATIVE_SCHEMES)) {
                schemeList.add(schemeOut);
            }
        }
        return schemeList;
    }

    private void validateDebtor() {
        Address debtor = validateAddress(billIn.getDebtor(), ValidationConstants.FIELDROOT_DEBTOR, false);
        billOut.setDebtor(debtor);
    }

    private Address validateAddress(Address addressIn, String fieldRoot, boolean mandatory) {
        Address addressOut = cleanedPerson(addressIn, fieldRoot);
        if (addressOut == null) {
            validateEmptyAddress(fieldRoot, mandatory);
            return null;
        }

        if (addressOut.getType() == Address.Type.CONFLICTING)
            emitErrorsForConflictingType(addressOut, fieldRoot);

        checkMandatoryAddressFields(addressOut, fieldRoot);

        if (addressOut.getCountryCode() != null
                && (addressOut.getCountryCode().length() != 2 || !Payments.isAlpha(addressOut.getCountryCode())))
            validationResult.addMessage(Type.ERROR, fieldRoot + ValidationConstants.SUBFIELD_COUNTRY_CODE,
                    ValidationConstants.KEY_COUNTRY_CODE_INVALID);

        cleanAddressFields(addressOut, fieldRoot);

        return addressOut;
    }

    private void validateEmptyAddress(String fieldRoot, boolean mandatory) {
        if (mandatory) {
            validationResult.addMessage(Type.ERROR, fieldRoot + ValidationConstants.SUBFIELD_NAME, ValidationConstants.KEY_FIELD_VALUE_MISSING);
            validationResult.addMessage(Type.ERROR, fieldRoot + ValidationConstants.SUBFIELD_POSTAL_CODE,
                    ValidationConstants.KEY_FIELD_VALUE_MISSING);
            validationResult.addMessage(Type.ERROR, fieldRoot + ValidationConstants.SUBFIELD_ADDRESS_LINE_2,
                    ValidationConstants.KEY_FIELD_VALUE_MISSING);
            validationResult.addMessage(Type.ERROR, fieldRoot + ValidationConstants.SUBFIELD_TOWN, ValidationConstants.KEY_FIELD_VALUE_MISSING);
            validationResult.addMessage(Type.ERROR, fieldRoot + ValidationConstants.SUBFIELD_COUNTRY_CODE,
                    ValidationConstants.KEY_FIELD_VALUE_MISSING);
        }
    }

    private void emitErrorsForConflictingType(Address addressOut, String fieldRoot) {
        if (addressOut.getAddressLine1() != null)
            validationResult.addMessage(Type.ERROR, fieldRoot + ValidationConstants.SUBFIELD_ADDRESS_LINE_1, ValidationConstants.KEY_ADDRESS_TYPE_CONFLICT);
        if (addressOut.getAddressLine2() != null)
            validationResult.addMessage(Type.ERROR, fieldRoot + ValidationConstants.SUBFIELD_ADDRESS_LINE_2, ValidationConstants.KEY_ADDRESS_TYPE_CONFLICT);
        if (addressOut.getStreet() != null)
            validationResult.addMessage(Type.ERROR, fieldRoot + ValidationConstants.SUBFIELD_STREET, ValidationConstants.KEY_ADDRESS_TYPE_CONFLICT);
        if (addressOut.getHouseNo() != null)
            validationResult.addMessage(Type.ERROR, fieldRoot + ValidationConstants.SUBFIELD_HOUSE_NO, ValidationConstants.KEY_ADDRESS_TYPE_CONFLICT);
        if (addressOut.getPostalCode() != null)
            validationResult.addMessage(Type.ERROR, fieldRoot + ValidationConstants.SUBFIELD_POSTAL_CODE, ValidationConstants.KEY_ADDRESS_TYPE_CONFLICT);
        if (addressOut.getTown() != null)
            validationResult.addMessage(Type.ERROR, fieldRoot + ValidationConstants.SUBFIELD_TOWN, ValidationConstants.KEY_ADDRESS_TYPE_CONFLICT);
    }

    private void checkMandatoryAddressFields(Address addressOut, String fieldRoot) {
        validateMandatory(addressOut.getName(), fieldRoot, ValidationConstants.SUBFIELD_NAME);
        if (addressOut.getType() == Address.Type.STRUCTURED || addressOut.getType() == Address.Type.UNDETERMINED) {
            validateMandatory(addressOut.getPostalCode(), fieldRoot, ValidationConstants.SUBFIELD_POSTAL_CODE);
            validateMandatory(addressOut.getTown(), fieldRoot, ValidationConstants.SUBFIELD_TOWN);
        }
        if (addressOut.getType() == Address.Type.COMBINED_ELEMENTS || addressOut.getType() == Address.Type.UNDETERMINED) {
            validateMandatory(addressOut.getAddressLine2(), fieldRoot, ValidationConstants.SUBFIELD_ADDRESS_LINE_2);
        }
        validateMandatory(addressOut.getCountryCode(), fieldRoot, ValidationConstants.SUBFIELD_COUNTRY_CODE);
    }

    private void cleanAddressFields(Address addressOut, String fieldRoot) {
        addressOut.setName(clippedValue(addressOut.getName(), 70, fieldRoot, ValidationConstants.SUBFIELD_NAME));
        if (addressOut.getType() == Address.Type.STRUCTURED) {
            addressOut.setStreet(clippedValue(addressOut.getStreet(), 70, fieldRoot, ValidationConstants.SUBFIELD_STREET));
            addressOut.setHouseNo(clippedValue(addressOut.getHouseNo(), 16, fieldRoot, ValidationConstants.SUBFIELD_HOUSE_NO));
            addressOut.setPostalCode(clippedValue(addressOut.getPostalCode(), 16, fieldRoot, ValidationConstants.SUBFIELD_POSTAL_CODE));
            addressOut.setTown(clippedValue(addressOut.getTown(), 35, fieldRoot, ValidationConstants.SUBFIELD_TOWN));
        }
        if (addressOut.getType() == Address.Type.COMBINED_ELEMENTS) {
            addressOut.setAddressLine1(clippedValue(addressOut.getAddressLine1(), 70, fieldRoot, ValidationConstants.SUBFIELD_ADDRESS_LINE_1));
            addressOut.setAddressLine2(clippedValue(addressOut.getAddressLine2(), 70, fieldRoot, ValidationConstants.SUBFIELD_ADDRESS_LINE_2));
        }
        if (addressOut.getCountryCode() != null)
            addressOut.setCountryCode(addressOut.getCountryCode().toUpperCase(Locale.US));
    }

    private boolean validateIBAN(String iban) {
        if (!Payments.isValidIBAN(iban)) {
            validationResult.addMessage(Type.ERROR, ValidationConstants.FIELD_ACCOUNT, ValidationConstants.KEY_ACCOUNT_IBAN_INVALID);
            return false;
        }
        return true;
    }

    private Address cleanedPerson(Address addressIn, String fieldRoot) {
        if (addressIn == null)
            return null;
        Address addressOut = new Address();
        addressOut.setName(cleanedValue(addressIn.getName(), fieldRoot, ValidationConstants.SUBFIELD_NAME));
        String value = cleanedValue(addressIn.getAddressLine1(), fieldRoot, ValidationConstants.SUBFIELD_ADDRESS_LINE_1);
        if (value != null)
            addressOut.setAddressLine1(value);
        value = cleanedValue(addressIn.getAddressLine2(), fieldRoot, ValidationConstants.SUBFIELD_ADDRESS_LINE_2);
        if (value != null)
            addressOut.setAddressLine2(value);
        value = cleanedValue(addressIn.getStreet(), fieldRoot, ValidationConstants.SUBFIELD_STREET);
        if (value != null)
            addressOut.setStreet(value);
        value = cleanedValue(addressIn.getHouseNo(), fieldRoot, ValidationConstants.SUBFIELD_HOUSE_NO);
        if (value != null)
            addressOut.setHouseNo(value);
        value = cleanedValue(addressIn.getPostalCode(), fieldRoot, ValidationConstants.SUBFIELD_POSTAL_CODE);
        if (value != null)
            addressOut.setPostalCode(value);
        value = cleanedValue(addressIn.getTown(), fieldRoot, ValidationConstants.SUBFIELD_TOWN);
        if (value != null)
            addressOut.setTown(value);
        addressOut.setCountryCode(Strings.trimmed(addressIn.getCountryCode()));

        if (addressOut.getName() == null && addressOut.getCountryCode() == null
                && addressOut.getType() == Address.Type.UNDETERMINED)
            return null;

        return addressOut;
    }

    private boolean validateMandatory(String value, String field) {
        if (Strings.isNullOrEmpty(value)) {
            validationResult.addMessage(Type.ERROR, field, ValidationConstants.KEY_FIELD_VALUE_MISSING);
            return false;
        }

        return true;
    }

    private void validateMandatory(String value, String fieldRoot, String subfield) {
        if (Strings.isNullOrEmpty(value))
            validationResult.addMessage(Type.ERROR, fieldRoot + subfield, ValidationConstants.KEY_FIELD_VALUE_MISSING);
    }

    private boolean validateLength(String value, int maxLength, String field) {
        if (value != null && value.length() > maxLength) {
            validationResult.addMessage(Type.ERROR, field, ValidationConstants.KEY_FIELD_VALUE_TOO_LONG,
                    new String[] { Integer.toString(maxLength) });
            return false;
        } else {
            return true;
        }
    }

    private String clippedValue(String value, int maxLength, String field) {
        if (value != null && value.length() > maxLength) {
            validationResult.addMessage(Type.WARNING, field, ValidationConstants.KEY_FIELD_VALUE_CLIPPED,
                    new String[] { Integer.toString(maxLength) });
            return value.substring(0, maxLength);
        }

        return value;
    }

    private String clippedValue(String value, int maxLength, String fieldRoot, String subfield) {
        if (value != null && value.length() > maxLength) {
            validationResult.addMessage(Type.WARNING, fieldRoot + subfield, ValidationConstants.KEY_FIELD_VALUE_CLIPPED,
                    new String[] { Integer.toString(maxLength) });
            return value.substring(0, maxLength);
        }

        return value;
    }

    private String cleanedValue(String value, String fieldRoot, String subfield) {
        CleaningResult result = new CleaningResult();
        StringCleanup.cleanText(value, billOut.getFormat().getCharacterSet(), true, result);
        if (result.replacedUnsupportedChars)
            validationResult.addMessage(Type.WARNING, fieldRoot + subfield, ValidationConstants.KEY_REPLACED_UNSUPPORTED_CHARACTERS);
        return result.cleanedString;
    }

    private String cleanedValue(String value, String fieldName) {
        CleaningResult result = new CleaningResult();
        StringCleanup.cleanText(value, billOut.getFormat().getCharacterSet(), true, result);
        if (result.replacedUnsupportedChars)
            validationResult.addMessage(Type.WARNING, fieldName, ValidationConstants.KEY_REPLACED_UNSUPPORTED_CHARACTERS);
        return result.cleanedString;
    }
}
