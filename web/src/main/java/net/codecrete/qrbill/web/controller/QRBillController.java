//
// Swiss QR Bill Generator
// Copyright (c) 2017 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
package net.codecrete.qrbill.web.controller;

import net.codecrete.qrbill.generator.*;
import net.codecrete.qrbill.web.api.ValidationResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class QRBillController {

    /**
     * Generates the text contained in the QR code
     * @param bill the bill data
     * @return the text as a string if the data is valid, a list of validation messages otherwise
     */
    @RequestMapping(value = "/api/qrCodeText", method = RequestMethod.POST)
    public ResponseEntity generateQrCodeString(@RequestBody Bill bill) {
        try {
            String text = QRBill.generateQrCodeText(bill);
            return ResponseEntity.ok(text);
        } catch (QRBillValidationError e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(e.getValidationResult());
        }
    }

    @RequestMapping(value = "/api/validate", method = RequestMethod.POST)
    @ResponseBody
    public ValidationResponse validate(@RequestBody Bill bill) {
        ValidationResult result = new ValidationResult();
        Validator validator = new Validator(bill, result);
        Bill validatedBill = validator.validate();

        ValidationResponse response = new ValidationResponse();
        if (result.hasMessages())
            response.setValidationMessages(result.getValidationMessages());
        response.setValidatedBill(validatedBill);
        return response;
    }
}