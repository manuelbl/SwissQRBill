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
    public static final String KEY_CURRENCY_IS_CHF_OR_EUR = "currency_is_chf_or_eur";
    /**
     * Validation message key: amount must be between 0.01 and 999999999.99
     */
    public static final String KEY_AMOUNT_IS_IN_VALID_RANGE = "amount_in_valid_range";
    /**
     * Validation message key: IBAN must be from bank in Switzerland or
     * Liechtenstein
     */
    public static final String KEY_ACCOUNT_IS_CH_LI_IBAN = "account_is_ch_li_iban";
    /**
     * Validation message key: IBAN must have valid format and check digit
     */
    public static final String KEY_ACCOUNT_IS_VALID_IBAN = "account_is_valid_iban";
    /**
     * Validation message key: Due to regular IBAN (outside QR-IID range) an ISO 11649 references is expected
     * but it has invalid format or check digit
     */
    public static final String KEY_VALID_ISO11649_CREDITOR_REF = "valid_iso11649_creditor_ref";
    /**
     * Validation message key: Due to QR-IBAN (IBAN in QR-IID range) a QR reference number is expected
     * but it has invalid format or check digit
     */
    public static final String KEY_VALID_QR_REF_NO = "valid_qr_ref_no";
    /**
     * Validation message key: For QR-IBANs (IBAN in QR-IID range) a QR reference is mandatory
     */
    public static final String KEY_MANDATORY_FOR_QR_IBAN = "mandatory_for_qr_iban";
    /**
     * Validation message key: Reference type must be one of "QRR", "SCOR" and "NON" and match the reference.
     */
    public static final String KEY_VALID_REF_TYPE = "valid_ref_type";
    /**
     * Validation message key: Field is mandatory
     */
    public static final String KEY_FIELD_IS_MANDATORY = "field_is_mandatory";
    /**
     * Validation message key: Conflicting fields for both structured and combined elements address type have been used
     */
    public static final String KEY_ADDRESS_TYPE_CONFLICT = "address_type_conflict";
    /**
     * Validation message key: Country code must consist of two letters
     */
    public static final String KEY_VALID_COUNTRY_CODE = "valid_country_code";
    /**
     * Validation message key: Field has been clipped to not exceed the maximum
     * length
     */
    public static final String KEY_FIELD_CLIPPED = "field_clipped";
    /**
     * Validation message key: Field value exceed the maximum length
     */
    public static final String KEY_FIELD_TOO_LONG = "field_value_too_long";
    /**
     * Validation message key: Unstructured message and bill information combined exceed the maximum length
     */
    public static final String ADDITIONAL_INFO_TOO_LONG = "additional_info_too_long";
    /**
     * Validation message key: Unsupported characters have been replaced
     */
    public static final String KEY_REPLACED_UNSUPPORTED_CHARACTERS = "replaced_unsupported_characters";
    /**
     * Validation message key: Valid data structure starts with "SPC" and consists
     * of 32 to 34 lines of text (with exceptions)
     */
    public static final String KEY_VALID_DATA_STRUCTURE = "valid_data_structure";
    /**
     * Validation message key: Version 02.00 is supported only
     */
    public static final String KEY_SUPPORTED_VERSION = "supported_version";
    /**
     * Validation message key: Coding type 1 is supported only
     */
    public static final String KEY_SUPPORTED_CODING_TYPE = "supported_coding_type";
    /**
     * Validation message key: Valid number required (nnnnn.nn)
     */
    public static final String KEY_VALID_NUMBER = "valid_number";
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
