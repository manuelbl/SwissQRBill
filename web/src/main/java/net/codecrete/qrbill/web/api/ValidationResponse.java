//
// Swiss QR Bill Generator
// Copyright (c) 2017 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
package net.codecrete.qrbill.web.api;

import java.util.List;

public class ValidationResponse {

    private boolean valid;
    private List<ValidationMessage> validationMessages;
    private QrBill validatedBill;
    private String qrCodeText;
    private String billID;

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

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

    public String getQrCodeText() {
        return qrCodeText;
    }

    public void setQrCodeText(String qrCodeText) {
        this.qrCodeText = qrCodeText;
    }

    public String getBillID() {
        return billID;
    }

    public void setBillID(String billID) {
        this.billID = billID;
    }
}
