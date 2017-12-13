//
// Swiss QR Bill Generator
// Copyright (c) 2017 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
package net.codecrete.qrbill.web.api;

import java.util.List;

public class ValidationResponse {

    private List<ValidationMessage> validationMessages;
    private QrBill validatedBill;

    public List<ValidationMessage> getValidationMessages() {
        return validationMessages;
    }

    public void setValidationMessages(List<ValidationMessage> validationMessages) {
        this.validationMessages = validationMessages;
    }

    public QrBill getValidatedBill() {
        return validatedBill;
    }

    public void setValidatedBill(QrBill validatedBill) {
        this.validatedBill = validatedBill;
    }
}
