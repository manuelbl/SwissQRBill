//
// Swiss QR Bill Generator
// Copyright (c) 2017 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
package net.codecrete.qrbill.generator;

import java.io.Serializable;
import java.util.*;

import static net.codecrete.qrbill.generator.ValidationMessage.Type;

/**
 * Container for validation results
 */
public class ValidationResult implements Serializable {

    private static final long serialVersionUID = -791181851684443602L;
    private static final List<ValidationMessage> EMPTY_LIST = Collections.emptyList();

    /** Validation messages */
    private List<ValidationMessage> validationMessages;
    /** Cleaned bill data */
    private Bill cleanedBill;

    /**
     * Creates a new validation result instance
     */
    public ValidationResult() {
        // default constructor, for JavaDoc documentation
    }

    /**
     * Gets the list of validation messages
     *
     * @return the validation messages
     */
    public List<ValidationMessage> getValidationMessages() {
        if (validationMessages == null)
            return EMPTY_LIST;
        return validationMessages;
    }

    /**
     * Gets if this validation result contains any messages
     *
     * @return {@code true} if there are validation messages, {@code false}
     * otherwise
     */
    public boolean hasMessages() {
        return validationMessages != null;
    }

    /**
     * Gets if this validation result contains any warning messages
     *
     * @return {@code true} if there are any warning messages, {@code false}
     * otherwise
     */
    public boolean hasWarnings() {
        if (validationMessages == null)
            return false;
        for (ValidationMessage message : validationMessages)
            if (message.getType() == Type.WARNING)
                return true;
        return false;
    }

    /**
     * Gets if this validation result contains any error messages
     *
     * @return {@code true} if there are any error messages, {@code false} otherwise
     */
    public boolean hasErrors() {
        if (validationMessages == null)
            return false;
        for (ValidationMessage message : validationMessages)
            if (message.getType() == Type.ERROR)
                return true;
        return false;
    }

    /**
     * Gets if the bill data is valid and the validation therefore has succeeded
     *
     * @return {@code true} if the bill data was valid, {@code false} otherwise
     */
    public boolean isValid() {
        return !hasErrors();
    }

    /**
     * Adds a validation message to this validation result
     *
     * @param type       the message type
     * @param field      the name of the affected field
     * @param messageKey the language-neutral message key
     */
    public void addMessage(Type type, String field, String messageKey) {
        ValidationMessage message = new ValidationMessage(type, field, messageKey);
        if (validationMessages == null)
            validationMessages = new ArrayList<>();
        validationMessages.add(message);
    }

    /**
     * Adds a validation message to this validation result
     *
     * @param type              the message type
     * @param field             the name of the affected field
     * @param messageKey        the language-neutral message key
     * @param messageParameters additional message parameters (text) to be inserted
     *                          into the localized message
     */
    public void addMessage(Type type, String field, String messageKey, String[] messageParameters) {
        ValidationMessage message = new ValidationMessage(type, field, messageKey, messageParameters);
        if (validationMessages == null)
            validationMessages = new ArrayList<>();
        validationMessages.add(message);
    }

    /**
     * Gets the cleaned bill data
     *
     * @return the cleaned bill data
     */
    public Bill getCleanedBill() {
        return cleanedBill;
    }

    /**
     * Sets the cleaned bill data
     *
     * @param cleanedBill the cleaned bill data
     */
    public void setCleanedBill(Bill cleanedBill) {
        this.cleanedBill = cleanedBill;
    }

    /**
     * Gets a human-readable description of the validation problems.
     *
     * @return description
     */
    public String getDescription() {
        if (!hasErrors())
            return "Valid bill data";

        StringBuilder sb = new StringBuilder();

        for (ValidationMessage message : getValidationMessages()) {
            if (message.getType() != Type.ERROR)
                continue;
            if (sb.length() > 0)
                sb.append("; ");

            String desc = ERROR_MESSAGES.getOrDefault(message.getMessageKey(), "Unknown error");
            if (message.getMessageKey().equals(ValidationConstants.KEY_FIELD_VALUE_MISSING)
                    || message.getMessageKey().equals(ValidationConstants.KEY_REPLACED_UNSUPPORTED_CHARACTERS))
                desc = String.format(desc, message.getField());
            else if (message.getMessageKey().equals(ValidationConstants.KEY_FIELD_VALUE_TOO_LONG)
                    || message.getMessageKey().equals(ValidationConstants.KEY_FIELD_VALUE_CLIPPED))
                desc = String.format(desc, message.getField(), message.getMessageParameters()[0]);

            sb.append(desc);
            sb.append(" (");
            sb.append(message.getMessageKey());
            sb.append(")");
        }

        return sb.toString();
    }

    private static final Map<String, String> ERROR_MESSAGES;

    static {
        Map<String, String> errorMessages = new HashMap<>();
        errorMessages.put(ValidationConstants.KEY_CURRENCY_NOT_CHF_OR_EUR, "currency should be \"CHF\" or \"EUR\"");
        errorMessages.put(ValidationConstants.KEY_AMOUNT_OUTSIDE_VALID_RANGE, "amount should be between 0.01 and 999 999 999.99");
        errorMessages.put(ValidationConstants.KEY_ACCOUNT_IBAN_NOT_FROM_CH_OR_LI, "account number should start with \"CH\" or \"LI\"");
        errorMessages.put(ValidationConstants.KEY_ACCOUNT_IBAN_INVALID, "account number is not a valid IBAN (invalid format or checksum)");
        errorMessages.put(ValidationConstants.KEY_REF_INVALID, "reference is invalid; it is neither a valid QR reference nor a valid ISO 11649 reference");
        errorMessages.put(ValidationConstants.KEY_QR_REF_MISSING, "QR reference is missing; it is mandatory for payments to a QR-IBAN account");
        errorMessages.put(ValidationConstants.KEY_CRED_REF_INVALID_USE_FOR_QR_IBAN, "for payments to a QR-IBAN account, a QR reference is required (an ISO 11649 reference may not be used)");
        errorMessages.put(ValidationConstants.KEY_QR_REF_INVALID_USE_FOR_NON_QR_IBAN, "a QR reference is only allowed for payments to a QR-IBAN account");
        errorMessages.put(ValidationConstants.KEY_REF_TYPE_INVALID, "reference type should be one of \"QRR\", \"SCOR\" and \"NON\" and match the reference");
        errorMessages.put(ValidationConstants.KEY_FIELD_VALUE_MISSING, "field \"%s\" may not be empty");
        errorMessages.put(ValidationConstants.KEY_ADDRESS_TYPE_CONFLICT, "fields for either structured address or combined elements address may be filled but not both");
        errorMessages.put(ValidationConstants.KEY_COUNTRY_CODE_INVALID, "country code is invalid; it should consist of two letters");
        errorMessages.put(ValidationConstants.KEY_FIELD_VALUE_CLIPPED, "the value for field \"%s\" has been clipped to not exceed the maximum length of %s characters");
        errorMessages.put(ValidationConstants.KEY_FIELD_VALUE_TOO_LONG, "the value for field \"%s\" should not exceed a length of %s characters");
        errorMessages.put(ValidationConstants.KEY_ADDITIONAL_INFO_TOO_LONG, "the additional information and the structured bill information combined should not exceed 140 characters");
        errorMessages.put(ValidationConstants.KEY_REPLACED_UNSUPPORTED_CHARACTERS, "unsupported characters have been replaced in field \"%s\"");
        errorMessages.put(ValidationConstants.KEY_ALT_SCHEME_MAX_EXCEEDED, "no more than two alternative schemes may be used");
        errorMessages.put(ValidationConstants.KEY_BILL_INFO_INVALID, "structured bill information must start with \"//\"");
        ERROR_MESSAGES = Collections.unmodifiableMap(errorMessages);
    }
}
