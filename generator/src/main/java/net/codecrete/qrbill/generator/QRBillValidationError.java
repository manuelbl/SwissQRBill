//
// Swiss QR Bill Generator
// Copyright (c) 2017 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
package net.codecrete.qrbill.generator;

/**
 * Exception thrown if the bill data is not valid.
 */
public class QRBillValidationError extends RuntimeException {

    private static final long serialVersionUID = -959460901342010774L;

    /** Validation result */
    private final ValidationResult validationResult;

    /**
     * Constructs a new instance with the specified validation result.
     *
     * @param validationResult validation result
     */
    public QRBillValidationError(ValidationResult validationResult) {
        super("QR bill data is invalid");
        this.validationResult = validationResult;
    }

    @Override
    public String getMessage() {
        return "QR bill data is invalid: " + validationResult.getDescription();
    }

    /**
     * Gets the validation result with the error messages.
     *
     * @return the validation result
     */
    public ValidationResult getValidationResult() {
        return validationResult;
    }
}
