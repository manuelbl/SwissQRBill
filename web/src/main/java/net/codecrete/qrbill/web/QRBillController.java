//
// Swiss QR Bill Generator
// Copyright (c) 2017 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
package net.codecrete.qrbill.web;

import net.codecrete.qrbill.generator.*;
import org.springframework.web.bind.annotation.*;

@RestController
public class QRBillController {

    @RequestMapping(value = "/api/qrCodeText", method = RequestMethod.POST)
    @ResponseBody
    public String generateQrCodeString(@RequestBody Bill bill) {
        try {
            return QRBill.generateQrCodeText(bill);
        } catch (QRBillValidationError e) {
            return "NADA";
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