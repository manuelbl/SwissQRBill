//
// Swiss QR Bill Generator
// Copyright (c) 2017 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
package net.codecrete.qrbill.generator;

import java.io.Serializable;

/**
 * QR bill validation message.
 */
public class ValidationMessage implements Serializable {

    private static final long serialVersionUID = -243381506848710081L;

    /**
     * Type of validation message
     */
    public enum Type {
        /**
         * Warning.
         * <p>
         * A warning does not prevent the QR bill from being generated. Warnings usually
         * indicate that data was truncated or otherwise modified.
         * </p>
         */
        WARNING,
        /**
         * Error.
         * <p>
         * Errors prevent the QR bill from being generated.
         * </p>
         */
        ERROR
    }

    /** Message type */
    private Type type;
    /** Affected data field */
    private String field;
    /** Message key */
    private String messageKey;
    /** Message parameters for placeholders */
    private String[] messageParameters;

    /**
     * Constructs a new validation message.
     */
    public ValidationMessage() {
    }

    /**
     * Constructs a new validation message with the given values.
     *
     * @param type       the message type
     * @param field      the affect field
     * @param messageKey the language-neutral key of the message
     */
    public ValidationMessage(Type type, String field, String messageKey) {
        this.type = type;
        this.field = field;
        this.messageKey = messageKey;
    }

    /**
     * Constructs a new validation message with the given values.
     *
     * @param type              the message type
     * @param field             the affect field
     * @param messageKey        the language-neutral key of the message
     * @param messageParameters variable text parts that will be inserted into the
     *                          localized message
     */
    public ValidationMessage(Type type, String field, String messageKey, String[] messageParameters) {
        this.type = type;
        this.field = field;
        this.messageKey = messageKey;
        this.messageParameters = messageParameters;
    }

    /**
     * Gets the type of message
     *
     * @return the message type
     */
    public Type getType() {
        return type;
    }

    /**
     * Sets the type of message
     *
     * @param type message type
     */
    public void setType(Type type) {
        this.type = type;
    }

    /**
     * Gets the name of the affected field.
     * <p>
     * All field names are available as constants in {@link Bill}. Examples are:
     * "account", "creditor.street"
     * </p>
     *
     * @return the field name
     */
    public String getField() {
        return field;
    }

    /**
     * Sets the name of the affected field.
     * <p>
     * All field names are available as constants in {@link Bill}. Examples are:
     * "account", "creditor.street"
     * </p>
     *
     * @param field the field name
     */
    public void setField(String field) {
        this.field = field;
    }

    /**
     * Gets the language neutral key of the message.
     *
     * @return the message key
     */
    public String getMessageKey() {
        return messageKey;
    }

    /**
     * Sets the language neutral key of the message.
     *
     * @param messageKey the message key
     */
    public void setMessageKey(String messageKey) {
        this.messageKey = messageKey;
    }

    /**
     * Gets additional message parameters (text) that are inserted into the
     * localized message.
     *
     * @return the additional message parameters
     */
    public String[] getMessageParameters() {
        return messageParameters;
    }

    /**
     * Sets additional message parameters (text) that are inserted into the
     * localized message.
     *
     * @param messageParameters the additional message parameters
     */
    public void setMessageParameters(String[] messageParameters) {
        this.messageParameters = messageParameters;
    }
}
