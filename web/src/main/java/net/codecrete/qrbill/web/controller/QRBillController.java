//
// Swiss QR Bill Generator
// Copyright (c) 2017 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
package net.codecrete.qrbill.web.controller;

import net.codecrete.qrbill.generator.Bill;
import net.codecrete.qrbill.generator.QRBill;
import net.codecrete.qrbill.generator.QRBillValidationError;
import net.codecrete.qrbill.generator.ValidationResult;
import net.codecrete.qrbill.generator.Validator;
import net.codecrete.qrbill.web.api.QrBill;
import net.codecrete.qrbill.web.api.ValidationMessage;
import net.codecrete.qrbill.web.api.ValidationResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MimeType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class QRBillController {

    /**
     * Generates the text contained in the QR code
     * @param bill the QR bill data
     * @return the text as a string if the data is valid; a list of validation messages otherwise
     */
    @RequestMapping(value = "/api/qrCodeText", method = RequestMethod.POST)
    public ResponseEntity generateQrCodeString(@RequestBody QrBill bill) {
        try {
            String text = QRBill.generateQrCodeText(QrBill.toGeneratorBill(bill));
            return ResponseEntity.ok(text);
        } catch (QRBillValidationError e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(e.getValidationResult());
        }
    }

    /**
     * Validates the QR bill data
     * @param bill the QR bill data
     * @return returns the validation result: validated, possibly modified bill and the validation messages (if any)
     */
    @RequestMapping(value = "/api/validate", method = RequestMethod.POST)
    @ResponseBody
    public ValidationResponse validate(@RequestBody QrBill bill) {
        ValidationResult result = new ValidationResult();
        Validator validator = new Validator(QrBill.toGeneratorBill(bill), result);
        Bill validatedBill = validator.validate();

        ValidationResponse response = new ValidationResponse();
        if (result.hasMessages())
            response.setValidationMessages(ValidationMessage.fromList(result.getValidationMessages()));
        response.setValidatedBill(QrBill.from(validatedBill));
        return response;
    }
    /**
     * Generates the QR bill for the specified data - either as an SVG or as a PDF.
     * <p>
     *     The type of the result (SVG or PDF) is specified using the {@code Accept} HTTP header.
     * </p>
     * @param bill the QR bill data
     * @param format the bill format (qrCodeOnly, a6Landscape, a5Landscape, a4Portrait)
     * @param headers the HTTP headers
     * @return the text as a string if the data is valid; a list of validation messages otherwise
     */
    @RequestMapping(value = "/api/bill/{format}", method = RequestMethod.POST, produces = { "image/svg+xml", "application/pdf"})
    public ResponseEntity generateBill(@RequestBody QrBill bill, @PathVariable("format") String format, @RequestHeader HttpHeaders headers) {
        QRBill.GraphicsFormat graphicsFormat = getGraphicsFormat(headers);
        if (graphicsFormat == null)
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                    .body("Specify at least one of the following mime types in the \"Accept\" header: image/svg+xml, application/pdf");

        QRBill.BillFormat billFormat = getBillFormat(format);
        if (billFormat == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Invalid bill format in URL. Valid values: qrCodeOnly, a6Landscape, a5Landscape, a4Portrait");

        try {
            byte[] result = QRBill.generate(QrBill.toGeneratorBill(bill), billFormat, graphicsFormat);
            return ResponseEntity.ok().contentType(getContentType(graphicsFormat)).body(result);
        } catch (QRBillValidationError e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(e.getValidationResult());
        }
    }

    private static QRBill.GraphicsFormat getGraphicsFormat(HttpHeaders headers) {
        List<String> acceptValues = headers.get(HttpHeaders.ACCEPT);
        for (String value : acceptValues) {
            if ("image/svg+xml".equals(value))
                return QRBill.GraphicsFormat.SVG;
            if ("application/pdf".equals(value))
                return QRBill.GraphicsFormat.PDF;
        }
        return null;
    }

    private static QRBill.BillFormat getBillFormat(String value) {
        QRBill.BillFormat format;
        switch (value) {
            case "qrCodeOnly":
                format = QRBill.BillFormat.QRCodeOnly;
                break;
            case "a6Landscape":
                format = QRBill.BillFormat.A6LandscapeSheet;
                break;
            case "a5Landscape":
                format = QRBill.BillFormat.A5LandscapeSheet;
                break;
            case "a4Portrait":
                format = QRBill.BillFormat.A4PortraitSheet;
                break;
            default:
                format = null;
        }
        return format;
    }

    private static final MediaType MEDIA_TYPE_SVG = MediaType.valueOf("image/svg+xml;charset=UTF-8");

    private static MediaType getContentType(QRBill.GraphicsFormat graphicsFormat) {
        return graphicsFormat == QRBill.GraphicsFormat.SVG ? MEDIA_TYPE_SVG : MediaType.APPLICATION_PDF;
    }
}