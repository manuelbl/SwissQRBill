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
import java.util.List;

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
     *         otherwise
     */
    public boolean hasMessages() {
        return validationMessages != null;
    }

    /**
     * Gets if this validation result contains any warning messages
     * 
     * @return {@code true} if there are any warning messages, {@code false}
     *         otherwise
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
     * @param messageKey the language-netural message key
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
     * @param messageKey        the language-netural message key
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
     * @return the cleand bill data
     */
    public Bill getCleanedBill() {
        return cleanedBill;
    }

    /**
     * Sets the cleaned bill data
     * 
     * @param cleanedBill the cleand bill data
     */
    public void setCleanedBill(Bill cleanedBill) {
        this.cleanedBill = cleanedBill;
    }
}
