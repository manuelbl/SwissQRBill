//
// Swiss QR Bill Generator
// Copyright (c) 2017 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
package net.codecrete.qrbill.generator;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import net.codecrete.qrbill.generator.Payments.CleaningResult;
import net.codecrete.qrbill.generator.ValidationMessage.Type;

/**
 * Validates and cleans QR bill data.
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

        billOut.setFormat(billIn.getFormat() != null ? billIn.getFormat().cloneInstance() : null);
        billOut.setVersion(billIn.getVersion());

        validateCurrency();
        validateAmount();
        boolean isQRBillIBAN = validateAccountNumber();
        validateCreditor();
        validateReferenceNo(isQRBillIBAN);
        validateUnstructuredMessage();
        validateDebtor();
        validateBillInformation();
        validateAlternativeSchemes();

        validationResult.setCleanedBill(billOut);
        return validationResult;
    }

    private void validateCurrency() {
        String currency = Strings.trimmed(billIn.getCurrency());
        if (validateMandatory(currency, Bill.FIELD_CURRENCY)) {
            currency = currency.toUpperCase(Locale.US);
            if (!"CHF".equals(currency) && !"EUR".equals(currency)) {
                validationResult.addMessage(Type.ERROR, Bill.FIELD_CURRENCY, QRBill.KEY_CURRENCY_IS_CHF_OR_EUR);
            } else {
                billOut.setCurrency(currency);
            }
        }
    }

    private void validateAmount() {
        Double amount = billIn.getAmount();
        if (amount == null) {
            billOut.setAmount(null);
        } else {
            amount = Math.round(amount * 100) / 100.0; // round to multiple of 0.01
            if (amount < 0.01 || amount > 999999999.99) {
                validationResult.addMessage(Type.ERROR, Bill.FIELD_AMOUNT, QRBill.KEY_AMOUNT_IS_IN_VALID_RANGE);
            } else {
                billOut.setAmount(amount);
            }
        }
    }

    private boolean validateAccountNumber() {
        boolean isQRBillIBAN = false;
        String account = Strings.trimmed(billIn.getAccount());
        if (validateMandatory(account, Bill.FIELD_ACCOUNT)) {
            account = Strings.whiteSpaceRemoved(account).toUpperCase(Locale.US);
            if (validateIBAN(account)) {
                if (!account.startsWith("CH") && !account.startsWith("LI")) {
                    validationResult.addMessage(Type.ERROR, Bill.FIELD_ACCOUNT, QRBill.KEY_ACCOUNT_IS_CH_LI_IBAN);
                } else if (account.length() != 21) {
                    validationResult.addMessage(Type.ERROR, Bill.FIELD_ACCOUNT, QRBill.KEY_ACCOUNT_IS_VALID_IBAN);
                } else {
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

    private void validateReferenceNo(boolean isQRBillIBAN) {
        String referenceNo = Strings.trimmed(billIn.getReferenceNo());

        if (referenceNo == null) {
            if (isQRBillIBAN)
                validationResult.addMessage(Type.ERROR, Bill.FIELD_REFERENCE, QRBill.KEY_MANDATORY_FOR_QR_IBAN);
            return;
        }

        referenceNo = Strings.whiteSpaceRemoved(referenceNo);
        if (referenceNo.startsWith("RF")) {
            if (!Payments.isValidISO11649Reference(referenceNo)) {
                validationResult.addMessage(Type.ERROR, Bill.FIELD_REFERENCE,
                        QRBill.KEY_VALID_ISO11649_CREDITOR_REF);
            } else {
                billOut.setReferenceNo(referenceNo);
            }
        } else {
            if (referenceNo.length() < 27)
                referenceNo = "00000000000000000000000000".substring(0, 27 - referenceNo.length()) + referenceNo;
            if (!Payments.isValidQRReferenceNo(referenceNo))
                validationResult.addMessage(Type.ERROR, Bill.FIELD_REFERENCE, QRBill.KEY_VALID_QR_REF_NO);
            else
                billOut.setReferenceNo(referenceNo);
        }
    }

    private void validateUnstructuredMessage() {
        String unstructuredMessage = Strings.trimmed(billIn.getUnstructuredMessage());
        unstructuredMessage = clippedValue(unstructuredMessage, 140, Bill.FIELD_UNSTRUCTURED_MESSAGE);
        billOut.setUnstructuredMessage(unstructuredMessage);
    }

    private void validateBillInformation() {
        String billInformation = Strings.trimmed(billIn.getBillInformation());
        billInformation = clippedValue(billInformation, 140, Bill.FIELD_BILL_INFORMATION);
        billOut.setBillInformation(billInformation);
    }

    private void validateAlternativeSchemes() {
        AlternativeScheme[] schemesOut = null;
        if (billIn.getAlternativeSchemes() != null) {
            int len = billIn.getAlternativeSchemes().length;
            List<AlternativeScheme> schemeList = new ArrayList<>(len);
            for (AlternativeScheme schemeIn : billIn.getAlternativeSchemes()) {
                AlternativeScheme schemeOut = new AlternativeScheme();
                schemeOut.setName(Strings.trimmed(schemeIn.getName()));
                schemeOut.setInstruction(Strings.trimmed(schemeIn.getInstruction()));
                if (schemeOut.getName() != null || schemeOut.getInstruction() != null) {
                    if (schemeList.size() == 2) {
                        validationResult.addMessage(Type.ERROR, Bill.FIELD_ALTERNATIVE_SCHEMES, QRBill.KEY_ALT_SCHEME_MAX_EXCEEDED);
                    } else {
                        schemeList.add(schemeOut);
                    }
                }
            }
            if (schemeList.size() > 0)
                schemesOut = schemeList.toArray(new AlternativeScheme[schemeList.size()]);
        }
        billOut.setAlternativeSchemes(schemesOut);
    }

    private void validateDebtor() {
        Address debtor = validatePerson(billIn.getDebtor(), Bill.FIELDROOT_DEBTOR, false);
        billOut.setDebtor(debtor);
    }

    private Address validatePerson(Address addressIn, String fieldRoot, boolean mandatory) {
        Address addressOut = cleanedPerson(addressIn, fieldRoot);
        if (addressOut == null) {
            if (mandatory) {
                validationResult.addMessage(Type.ERROR, fieldRoot + Bill.SUBFIELD_NAME, QRBill.KEY_FIELD_IS_MANDATORY);
                validationResult.addMessage(Type.ERROR, fieldRoot + Bill.SUBFIELD_POSTAL_CODE,
                        QRBill.KEY_FIELD_IS_MANDATORY);
                validationResult.addMessage(Type.ERROR, fieldRoot + Bill.SUBFIELD_ADDRESS_LINE_2,
                        QRBill.KEY_FIELD_IS_MANDATORY);
                validationResult.addMessage(Type.ERROR, fieldRoot + Bill.SUBFIELD_TOWN, QRBill.KEY_FIELD_IS_MANDATORY);
                validationResult.addMessage(Type.ERROR, fieldRoot + Bill.SUBFIELD_COUNTRY_CODE,
                        QRBill.KEY_FIELD_IS_MANDATORY);
            }
            return null;
        }

        if (addressOut.getType() == Address.Type.CONFLICTING) {
            if (addressOut.getAddressLine1() != null)
                validationResult.addMessage(Type.ERROR, fieldRoot + Bill.SUBFIELD_ADDRESS_LINE_1, QRBill.KEY_ADDRESS_TYPE_CONFLICT);
            if (addressOut.getAddressLine2() != null)
                validationResult.addMessage(Type.ERROR, fieldRoot + Bill.SUBFIELD_ADDRESS_LINE_2, QRBill.KEY_ADDRESS_TYPE_CONFLICT);
            if (addressOut.getStreet() != null)
                validationResult.addMessage(Type.ERROR, fieldRoot + Bill.SUBFIELD_STREET, QRBill.KEY_ADDRESS_TYPE_CONFLICT);
            if (addressOut.getHouseNo() != null)
                validationResult.addMessage(Type.ERROR, fieldRoot + Bill.SUBFIELD_HOUSE_NO, QRBill.KEY_ADDRESS_TYPE_CONFLICT);
            if (addressOut.getPostalCode() != null)
                validationResult.addMessage(Type.ERROR, fieldRoot + Bill.SUBFIELD_POSTAL_CODE, QRBill.KEY_ADDRESS_TYPE_CONFLICT);
            if (addressOut.getTown() != null)
                validationResult.addMessage(Type.ERROR, fieldRoot + Bill.SUBFIELD_TOWN, QRBill.KEY_ADDRESS_TYPE_CONFLICT);
        }

        if (addressOut.getCountryCode() != null)
            addressOut.setCountryCode(addressOut.getCountryCode().toUpperCase(Locale.US));

        validateMandatory(addressOut.getName(), fieldRoot, Bill.SUBFIELD_NAME);
        if (addressOut.getType() == Address.Type.STRUCTURED || addressOut.getType() == Address.Type.UNDETERMINED) {
            validateMandatory(addressOut.getPostalCode(), fieldRoot, Bill.SUBFIELD_POSTAL_CODE);
            validateMandatory(addressOut.getTown(), fieldRoot, Bill.SUBFIELD_TOWN);
        }
        if (addressOut.getType() == Address.Type.COMBINED_ELEMENTS || addressOut.getType() == Address.Type.UNDETERMINED) {
            validateMandatory(addressOut.getAddressLine2(), fieldRoot, Bill.SUBFIELD_ADDRESS_LINE_2);
        }
        validateMandatory(addressOut.getCountryCode(), fieldRoot, Bill.SUBFIELD_COUNTRY_CODE);

        addressOut.setName(clippedValue(addressOut.getName(), 70, fieldRoot, Bill.SUBFIELD_NAME));
        if (addressOut.getType() == Address.Type.STRUCTURED) {
            addressOut.setStreet(clippedValue(addressOut.getStreet(), 70, fieldRoot, Bill.SUBFIELD_STREET));
            addressOut.setHouseNo(clippedValue(addressOut.getHouseNo(), 16, fieldRoot, Bill.SUBFIELD_HOUSE_NO));
            addressOut.setPostalCode(clippedValue(addressOut.getPostalCode(), 16, fieldRoot, Bill.SUBFIELD_POSTAL_CODE));
            addressOut.setTown(clippedValue(addressOut.getTown(), 35, fieldRoot, Bill.SUBFIELD_TOWN));
        }
        if (addressOut.getType() == Address.Type.COMBINED_ELEMENTS) {
            addressOut.setAddressLine1(clippedValue(addressOut.getAddressLine1(), 70, fieldRoot, Bill.SUBFIELD_ADDRESS_LINE_1));
            addressOut.setAddressLine2(clippedValue(addressOut.getAddressLine2(), 70, fieldRoot, Bill.SUBFIELD_ADDRESS_LINE_2));
        }

        if (addressOut.getCountryCode() != null
                && (addressOut.getCountryCode().length() != 2 || !Payments.isAlphaNumeric(addressOut.getCountryCode())))
            validationResult.addMessage(Type.ERROR, fieldRoot + Bill.SUBFIELD_COUNTRY_CODE,
                    QRBill.KEY_VALID_COUNTRY_CODE);

        return addressOut;
    }

    private boolean validateIBAN(String iban) {
        if (!Payments.isValidIBAN(iban)) {
            validationResult.addMessage(Type.ERROR, Bill.FIELD_ACCOUNT, QRBill.KEY_ACCOUNT_IS_VALID_IBAN);
            return false;
        }
        return true;
    }

    private Address cleanedPerson(Address addressIn, String fieldRoot) {
        if (addressIn == null)
            return null;
        Address addressOut = new Address();
        addressOut.setName(cleanedValue(addressIn.getName(), fieldRoot, Bill.SUBFIELD_NAME));
        String value = cleanedValue(addressIn.getAddressLine1(), fieldRoot, Bill.SUBFIELD_ADDRESS_LINE_1);
        if (value != null)
            addressOut.setAddressLine1(value);
        value = cleanedValue(addressIn.getAddressLine2(), fieldRoot, Bill.SUBFIELD_ADDRESS_LINE_2);
        if (value != null)
            addressOut.setAddressLine2(value);
        value = cleanedValue(addressIn.getStreet(), fieldRoot, Bill.SUBFIELD_STREET);
        if (value != null)
            addressOut.setStreet(value);
        value = cleanedValue(addressIn.getHouseNo(), fieldRoot, Bill.SUBFIELD_HOUSE_NO);
        if (value != null)
            addressOut.setHouseNo(value);
        value = cleanedValue(addressIn.getPostalCode(), fieldRoot, Bill.SUBFIELD_POSTAL_CODE);
        if (value != null)
            addressOut.setPostalCode(value);
        value = cleanedValue(addressIn.getTown(), fieldRoot, Bill.SUBFIELD_TOWN);
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
            validationResult.addMessage(Type.ERROR, field, QRBill.KEY_FIELD_IS_MANDATORY);
            return false;
        }

        return true;
    }

    private void validateMandatory(String value, String fieldRoot, String subfield) {
        if (Strings.isNullOrEmpty(value))
            validationResult.addMessage(Type.ERROR, fieldRoot + subfield, QRBill.KEY_FIELD_IS_MANDATORY);
    }

    private String clippedValue(String value, int maxLength, String field) {
        if (value != null && value.length() > maxLength) {
            validationResult.addMessage(Type.WARNING, field, QRBill.KEY_FIELD_CLIPPED,
                    new String[] { Integer.toString(maxLength) });
            return value.substring(0, maxLength);
        }

        return value;
    }

    private String clippedValue(String value, int maxLength, String fieldRoot, String subfield) {
        if (value != null && value.length() > maxLength) {
            validationResult.addMessage(Type.WARNING, fieldRoot + subfield, QRBill.KEY_FIELD_CLIPPED,
                    new String[] { Integer.toString(maxLength) });
            return value.substring(0, maxLength);
        }

        return value;
    }

    private String cleanedValue(String value, String fieldRoot, String subfield) {
        CleaningResult result = new CleaningResult();
        Payments.cleanValue(value, result);
        if (result.replacedUnsupportedChars)
            validationResult.addMessage(Type.WARNING, fieldRoot + subfield, QRBill.KEY_REPLACED_UNSUPPORTED_CHARACTERS);
        return result.cleanedString;
    }
}
