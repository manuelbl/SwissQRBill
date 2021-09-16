//
// Swiss QR Bill Generator
// Copyright (c) 2017 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
package net.codecrete.qrbill.generator;

/**
 * Exception thrown if the bill could not be generated.
 * <p>
 * If the bill data is not valid, a {@link QRBillValidationError} exception is thrown instead.
 * </p>
 */
public class QRBillGenerationException extends RuntimeException {

    private static final long serialVersionUID = 6512029854952325813L;

    /**
     * Constructs a new runtime exception with the specified detail message.
     *
     * @param message detail message
     */
    public QRBillGenerationException(String message) {
        super(message);
    }

    /**
     * Constructs a new runtime exception with the specified cause.
     *
     * @param cause cause
     */
    public QRBillGenerationException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new runtime exception with the specified detail message and
     * cause.
     *
     * @param message detail message
     * @param cause   cause
     */
    public QRBillGenerationException(String message, Throwable cause) {
        super(message, cause);
    }
}
