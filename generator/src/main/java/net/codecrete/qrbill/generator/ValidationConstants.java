//
// Swiss QR Bill Generator
// Copyright (c) 2019 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//

package net.codecrete.qrbill.generator;

/**
 * Constants for bill validation messages: message keys and field names.
 */
public class ValidationConstants {
    /**
     * Validation message key: currency must be "CHF" or "EUR"
     */
    public static final String KEY_CURRENCY_NOT_CHF_OR_EUR = "currency_not_chf_or_eur";
    /**
     * Validation message key: amount must be between 0.01 and 999999999.99
     */
    public static final String KEY_AMOUNT_OUTSIDE_VALID_RANGE = "amount_outside_valid_range";
    /**
     * Validation message key: account number should start with "CH" or "LI"
     */
    public static final String KEY_ACCOUNT_IBAN_NOT_FROM_CH_OR_LI = "account_iban_not_from_ch_or_li";
    /**
     * Validation message key: IBAN is not valid (incorrect format or check digit)
     */
    public static final String KEY_ACCOUNT_IBAN_INVALID = "account_iban_invalid";
    /**
     * Validation message key: The reference is invalid. It is neither a valid QR reference nor a valid ISO 11649
     * reference.
     */
    public static final String KEY_REF_INVALID = "ref_invalid";
    /**
     * Validation message key: QR reference is missing; it is mandatory for payments to a QR-IBAN account.
     */
    public static final String KEY_QR_REF_MISSING = "qr_ref_missing";
    /**
     * Validation message key: For payments to a QR-IBAN account, a QR reference is required. An ISO 11649 reference
     * may not be used.
     */
    public static final String KEY_CRED_REF_INVALID_USE_FOR_QR_IBAN = "cred_ref_invalid_use_for_qr_iban";
    /**
     * Validation message key: A QR reference is only allowed for payments to a QR-IBAN account.
     */
    public static final String KEY_QR_REF_INVALID_USE_FOR_NON_QR_IBAN = "qr_ref_invalid_use_for_non_qr_iban";
    /**
     * Validation message key: Reference type should be one of "QRR", "SCOR" and "NON" and match the reference.
     */
    public static final String KEY_REF_TYPE_INVALID = "ref_type_invalid";
    /**
     * Validation message key: Field must not be empty
     */
    public static final String KEY_FIELD_VALUE_MISSING = "field_value_missing";
    /**
     * Validation message key: Conflicting fields for both structured and combined elements address type have been used
     */
    public static final String KEY_ADDRESS_TYPE_CONFLICT = "address_type_conflict";
    /**
     * Validation message key: Country code must consist of two letters
     */
    public static final String KEY_COUNTRY_CODE_INVALID = "country_code_invalid";
    /**
     * Validation message key: Field has been clipped to not exceed the maximum length
     */
    public static final String KEY_FIELD_VALUE_CLIPPED = "field_value_clipped";
    /**
     * Validation message key: Field value exceed the maximum length
     */
    public static final String KEY_FIELD_VALUE_TOO_LONG = "field_value_too_long";
    /**
     * Validation message key: Unstructured message and bill information combined exceed the maximum length
     */
    public static final String KEY_ADDITIONAL_INFO_TOO_LONG = "additional_info_too_long";
    /**
     * Validation message key: Unsupported characters have been replaced
     */
    public static final String KEY_REPLACED_UNSUPPORTED_CHARACTERS = "replaced_unsupported_characters";
    /**
     * Validation message key: Invalid data structure; it must start with "SPC" and consists
     * of 32 to 34 lines of text (with exceptions)
     */
    public static final String KEY_DATA_STRUCTURE_INVALID = "data_structure_invalid";
    /**
     * Validation message key: Version 02.00 is supported only
     */
    public static final String KEY_VERSION_UNSUPPORTED = "version_unsupported";
    /**
     * Validation message key: Coding type 1 is supported only
     */
    public static final String KEY_CODING_TYPE_UNSUPPORTED = "coding_type_unsupported";
    /**
     * Validation message key: Valid number required (nnnnn.nn)
     */
    public static final String KEY_NUMBER_INVALID = "number_invalid";
    /**
     * Validation message key: The maximum of 2 alternative schemes has been exceeded
     */
    public static final String KEY_ALT_SCHEME_MAX_EXCEEDED = "alt_scheme_max_exceed";
    /**
     * Validation message key: The bill information is invalid (does not start with // or is too short)
     */
    public static final String KEY_BILL_INFO_INVALID = "bill_info_invalid";
    /**
     * Relative field name of an address' name
     */
    public static final String SUBFIELD_NAME = ".name";
    /**
     * Relative field of an address' line 1
     */
    public static final String SUBFIELD_ADDRESS_LINE_1 = ".addressLine1";
    /**
     * Relative field of an address' line 2
     */
    public static final String SUBFIELD_ADDRESS_LINE_2 = ".addressLine2";
    /**
     * Relative field of an address' street
     */
    public static final String SUBFIELD_STREET = ".street";
    /**
     * Relative field of an address' house number
     */
    public static final String SUBFIELD_HOUSE_NO = ".houseNo";
    /**
     * Relative field of an address' postal code
     */
    public static final String SUBFIELD_POSTAL_CODE = ".postalCode";
    /**
     * Relative field of an address' town
     */
    public static final String SUBFIELD_TOWN = ".town";
    /**
     * Relative field of an address' country code
     */
    public static final String SUBFIELD_COUNTRY_CODE = ".countryCode";
    /**
     * Field name of the QR code type
     */
    public static final String FIELD_QR_TYPE = "qrText";
    /**
     * Field name of the QR bill version
     */
    public static final String FIELD_VERSION = "version";
    /**
     * Field name of the QR bill's coding type
     */
    public static final String FIELD_CODING_TYPE = "codingType";
    /**
     * Field name of the QR bill's trailer ("EPD")
     */
    public static final String FIELD_TRAILER = "trailer";
    /**
     * Field name of the currency
     */
    public static final String FIELD_CURRENCY = "currency";
    /**
     * Field name of the amount
     */
    public static final String FIELD_AMOUNT = "amount";
    /**
     * Field name of the account number
     */
    public static final String FIELD_ACCOUNT = "account";
    /**
     * Field name of the reference type
     */
    public static final String FIELD_REFERENCE_TYPE = "referenceType";
    /**
     * Field name of the reference
     */
    public static final String FIELD_REFERENCE = "reference";
    /**
     * Start of field name of the creditor address
     */
    public static final String FIELDROOT_CREDITOR = "creditor";
    /**
     * Field name of the creditor's name
     */
    public static final String FIELD_CREDITOR_NAME = "creditor.name";
    /**
     * Field name of the creditor's street
     */
    public static final String FIELD_CREDITOR_STREET = "creditor.street";
    /**
     * Field name of the creditor's house number
     */
    public static final String FIELD_CREDITOR_HOUSE_NO = "creditor.houseNo";
    /**
     * Field name of the creditor's postal codde
     */
    public static final String FIELD_CREDITOR_POSTAL_CODE = "creditor.postalCode";
    /**
     * Field name of the creditor's town
     */
    public static final String FIELD_CREDITOR_TOWN = "creditor.town";
    /**
     * Field name of the creditor's country code
     */
    public static final String FIELD_CREDITOR_COUNTRY_CODE = "creditor.countryCode";
    /**
     * Field name of the unstructured message
     */
    public static final String FIELD_UNSTRUCTURED_MESSAGE = "unstructuredMessage";
    /**
     * Field name of the bill information
     */
    public static final String FIELD_BILL_INFORMATION = "billInformation";
    /**
     * Field name of the alternative schemes
     */
    public static final String FIELD_ALTERNATIVE_SCHEMES = "altSchemes";
    /**
     * Start of field name of the debtor's address
     */
    public static final String FIELDROOT_DEBTOR = "debtor";
    /**
     * Field name of the debtor's name
     */
    public static final String FIELD_DEBTOR_NAME = "debtor.name";
    /**
     * Field name of the debtor's street
     */
    public static final String FIELD_DEBTOR_STREET = "debtor.street";
    /**
     * Field name of the debtor's house number
     */
    public static final String FIELD_DEBTOR_HOUSE_NO = "debtor.houseNo";
    /**
     * Field name of the debtor's postal code
     */
    public static final String FIELD_DEBTOR_POSTAL_CODE = "debtor.postalCode";
    /**
     * Field name of the debtor's town
     */
    public static final String FIELD_DEBTOR_TOWN = "debtor.town";
    /**
     * Field name of the debtor's country code
     */
    public static final String FIELD_DEBTOR_COUNTRY_CODE = "debtor.countryCode";


    private ValidationConstants() {
        // do not create instance
    }
}
