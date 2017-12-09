//
// Swiss QR Bill Generator
// Copyright (c) 2017 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
package net.codecrete.qrbill.generator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static net.codecrete.qrbill.generator.ValidationMessage.Type;

/**
 * Container for validation results
 */
public class ValidationResult {

    private static final List<ValidationMessage> EMPTY_LIST = Collections.emptyList();

    private List<ValidationMessage> validationMessages;

    public List<ValidationMessage> getValidationMessages() {
        if (validationMessages == null)
            return EMPTY_LIST;
        return validationMessages;
    }

    public boolean hasMessages() {
        return validationMessages != null;
    }

    public boolean hasWarnings() {
        if (validationMessages == null)
            return false;
        for (ValidationMessage message : validationMessages)
            if (message.getType() == Type.Warning)
                return true;
        return false;
    }

    public boolean hasErrors() {
        if (validationMessages == null)
            return false;
        for (ValidationMessage message : validationMessages)
            if (message.getType() == Type.Error)
                return true;
        return false;
    }

    public void addMessage(Type type, String field, String messageKey) {
        ValidationMessage message = new ValidationMessage(type, field, messageKey);
        if (validationMessages == null)
            validationMessages = new ArrayList<>();
        validationMessages.add(message);
    }

    public void addMessage(Type type, String field, String messageKey, String[] messageParameters) {
        ValidationMessage message = new ValidationMessage(type, field, messageKey, messageParameters);
        if (validationMessages == null)
            validationMessages = new ArrayList<>();
        validationMessages.add(message);
    }
}
