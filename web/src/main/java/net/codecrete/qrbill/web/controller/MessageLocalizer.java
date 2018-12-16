//
// Swiss QR Bill Generator
// Copyright (c) 2017 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
package net.codecrete.qrbill.web.controller;

import net.codecrete.qrbill.web.model.ValidationMessage;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;

@Component
class MessageLocalizer {

    private final MessageSource messageSource;

    /**
     * Creates an instance.
     * <p>
     * Single constructor for Spring dependency injection.
     * </p>
     */
    MessageLocalizer(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    void addLocalMessages(List<ValidationMessage> messages) {

        Locale currentLocale = LocaleContextHolder.getLocale();
        for (ValidationMessage message : messages) {
            message.setMessage(getLocalMessage(message.getMessageKey(), message.getMessageParameters(), currentLocale));
        }
    }

    String getLocalMessage(String messageKey, List<String> messageParameters, Locale locale) {
        String[] parameters = null;
        if (messageParameters != null && messageParameters.size() > 0)
            parameters = messageParameters.toArray(new String[0]);
        return messageSource.getMessage(messageKey, parameters, locale);
    }
}
