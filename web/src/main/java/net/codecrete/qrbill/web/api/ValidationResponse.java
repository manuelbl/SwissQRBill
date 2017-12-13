//
// Swiss QR Bill Generator
// Copyright (c) 2017 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
package net.codecrete.qrbill.web.api;

import net.codecrete.qrbill.generator.Bill;
import net.codecrete.qrbill.generator.ValidationMessage;

import java.util.List;

public class ValidationResponse {

    private List<ValidationMessage> validationMessages;
    private Bill validatedBill;

    public List<ValidationMessage> getValidationMessages() {
        return validationMessages;
    }

    public void setValidationMessages(List<ValidationMessage> validationMessages) {
        this.validationMessages = validationMessages;
    }

    public Bill getValidatedBill() {
        return validatedBill;
    }

    public void setValidatedBill(Bill validatedBill) {
        this.validatedBill = validatedBill;
    }
}
