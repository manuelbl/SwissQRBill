//
// Swiss QR Bill Generator
// Copyright (c) 2017 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
package net.codecrete.qrbill.generator;

/**
 * Exception thrown if the bill could not be generated.
 */
public class QRBillUnexpectedException extends RuntimeException {

    private static final long serialVersionUID = 6512029854952325813L;

	/**
     * Constructs a new runtime exception with the specified detail message.
     * @param message detail message
     */
    public QRBillUnexpectedException(String message) {
        super(message);
    }

    /**
     * Constructs a new runtime exception with the specified cause.
     * @param cause cause
     */
    public QRBillUnexpectedException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new runtime exception with the specified detail message and cause.
     * @param message detail message
     * @param cause cause
     */
    public QRBillUnexpectedException(String message, Throwable cause) {
        super(message, cause);
    }
}
