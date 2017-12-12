//
// Swiss QR Bill Generator
// Copyright (c) 2017 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
package net.codecrete.qrbill.generator;

public class QRBillValidationError extends RuntimeException {

    private final ValidationResult validationResult;

    public QRBillValidationError(ValidationResult validationResult) {
        super("QR bill data is invalid");
        this.validationResult = validationResult;
    }

    public ValidationResult getValidationResult() {
        return validationResult;
    }
}
