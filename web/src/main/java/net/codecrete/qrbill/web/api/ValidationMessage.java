//
// Swiss QR Bill Generator
// Copyright (c) 2017 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
package net.codecrete.qrbill.web.api;

import java.util.ArrayList;
import java.util.List;

public class ValidationMessage {

    public enum Type {
        Warning,
        Error
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

    public static ValidationMessage from(net.codecrete.qrbill.generator.ValidationMessage msg) {
        if (msg == null)
            return null;

        ValidationMessage message = new ValidationMessage();
        message.type = typeFromUppercase(msg.getType().name());
        message.field = msg.getField();
        message.messageKey = msg.getMessageKey();
        message.messageParameters = msg.getMessageParameters();
        return message;
    }

    public static List<ValidationMessage> fromList(List<net.codecrete.qrbill.generator.ValidationMessage> list) {
        List<ValidationMessage> msgList = new ArrayList<>(list.size());
        for (net.codecrete.qrbill.generator.ValidationMessage msg: list) {
            msgList.add(from(msg));
        }

        return msgList;
    }

    private static Type typeFromUppercase(String name) {
        if ("ERROR".equals(name))
            return Type.Error;
        if ("WARNING".equals(name))
            return Type.Warning;
        return null;
    }
}
