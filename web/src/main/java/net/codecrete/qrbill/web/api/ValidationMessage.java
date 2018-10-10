//
// Swiss QR Bill Generator
// Copyright (c) 2017 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
package net.codecrete.qrbill.web.api;

public class ValidationMessage {

    public enum Type {
        Warning, Error
    }

    private Type type;
    private String field;
    private String messageKey;
    private String message;
    private String[] messageParameters;

    public ValidationMessage() {
    }

    public ValidationMessage(Type type, String field, String messageKey) {
        this.type = type;
        this.field = field;
        this.messageKey = messageKey;
    }

    public ValidationMessage(Type type, String field, String messageKey, String[] messageParameters) {
        this.type = type;
        this.field = field;
        this.messageKey = messageKey;
        this.messageParameters = messageParameters;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessageKey() {
        return messageKey;
    }

    public void setMessageKey(String messageKey) {
        this.messageKey = messageKey;
    }

    public String[] getMessageParameters() {
        return messageParameters;
    }

    public void setMessageParameters(String[] messageParameters) {
        this.messageParameters = messageParameters;
    }
}
