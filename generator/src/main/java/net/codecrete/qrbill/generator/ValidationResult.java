//
// Swiss QR Bill Generator
// Copyright (c) 2017 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
package net.codecrete.qrbill.generator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.codecrete.qrbill.generator.ValidationMessage.Type;

/**
 * Container for validation results
 */
public class ValidationResult implements Serializable {

    private static final long serialVersionUID = -791181851684443602L;
    private static final List<ValidationMessage> EMPTY_LIST = Collections.emptyList();

    private List<ValidationMessage> validationMessages;
    private Bill cleanedBill;

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
            if (message.getMessageKey().equals("field_is_mandatory"))
                desc = String.format(desc, message.getField());
            else if (message.getMessageKey().equals("field_value_too_long"))
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
        errorMessages.put("currency_is_chf_or_eur", "currency should be \"CHF\" or \"EUR\"");
        errorMessages.put("amount_in_valid_range", "amount should be between 0.01 and 999 999 999.99");
        errorMessages.put("account_is_ch_li_iban", "account number should start with \"CH\" or \"LI\"");
        errorMessages.put("account_is_valid_iban", "IBAN is invalid (format or checksum)");
        errorMessages.put("valid_iso11649_creditor_ref", "reference is invalid (reference should be empty or start with \"RF\")");
        errorMessages.put("valid_qr_ref_no", "reference is invalid (numeric QR reference required)");
        errorMessages.put("mandatory_for_qr_iban", "reference is needed for a payment to this account (QR-IBAN)");
        errorMessages.put("field_is_mandatory", "field \"%s\" may not be empty");
        errorMessages.put("valid_country_code", "country code is invalid; it should consist of two letters");
        errorMessages.put("address_type_conflict", "fields for either structured address or combined elements address may be filled but not both");
        errorMessages.put("alt_scheme_max_exceed", "no more than two alternative schemes may be used");
        errorMessages.put("bill_info_invalid", "structured bill information must start with \"//\"");
        errorMessages.put("field_value_too_long", "the value for field \"%s\" should not exceed a length of %s characters");
        errorMessages.put("additional_info_too_long", "the additional information and the structured bill information combined should not exceed 140 characters");
        ERROR_MESSAGES = Collections.unmodifiableMap(errorMessages);
    }
}
