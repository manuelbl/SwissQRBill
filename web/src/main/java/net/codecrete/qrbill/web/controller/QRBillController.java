//
// Swiss QR Bill Generator
// Copyright (c) 2017 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
package net.codecrete.qrbill.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.codecrete.qrbill.generator.Bill;
import net.codecrete.qrbill.generator.QRBill;
import net.codecrete.qrbill.generator.QRBillValidationError;
import net.codecrete.qrbill.generator.ValidationResult;
import net.codecrete.qrbill.generator.Validator;
import net.codecrete.qrbill.web.api.QrBill;
import net.codecrete.qrbill.web.api.ValidationMessage;
import net.codecrete.qrbill.web.api.ValidationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Locale;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

import static net.codecrete.qrbill.generator.QRBill.*;

@RestController
public class QRBillController {

    @Autowired
    private MessageSource messageSource;

    /**
     * Generates the text contained in the QR code
     * @param bill the QR bill data
     * @return the text as a string if the data is valid; a list of validation messages otherwise
     */
    @RequestMapping(value = "/api/qrCodeText", method = RequestMethod.POST)
    public ResponseEntity generateQrCodeString(@RequestBody QrBill bill) {
        try {
            String text = generateQrCodeText(QrBill.toGeneratorBill(bill));
            return ResponseEntity.ok(text);
        } catch (QRBillValidationError e) {
            List<ValidationMessage> messages = ValidationMessage.fromList(e.getValidationResult().getValidationMessages());
            addLocalMessages(messages);
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(messages);
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
        if (result.hasMessages()) {
            List<ValidationMessage> messages = ValidationMessage.fromList(result.getValidationMessages());
            addLocalMessages(messages);
            response.setValidationMessages(messages);
        }
        response.setValidatedBill(QrBill.from(validatedBill));
        if (!result.hasErrors())
            response.setBillID(generateID(validatedBill));
        return response;
    }

    /**
     * Generates the QR bill as an SVG.
     * @param bill the QR bill data
     * @param format the bill format (qrCodeOnly, a6Landscape, a5Landscape, a4Portrait)
     * @return the generated bill if the data is valid; a list of validation messages otherwise
     */
    @RequestMapping(value = "/api/bill/svg/{format}", method = RequestMethod.POST)
    public ResponseEntity generateSvgBillPost(@RequestBody QrBill bill, @PathVariable("format") String format) {
        return generateBill(bill, format, GraphicsFormat.SVG);
    }

    /**
     * Generates the QR bill as an SVG.
     * @param format the bill format (qrCodeOnly, a6Landscape, a5Landscape, a4Portrait)
     * @param billId the ID of the QR bill (as returned by /api/validate)
     * @return the generated bill
     */
    @RequestMapping(value = "/api/bill/svg/{format}/{id}", method = RequestMethod.GET)
    public ResponseEntity generateSvgBillGet(@PathVariable("id") String billId, @PathVariable("format") String format) {
        BillFormat billFormat = getBillFormat(format);
        if (billFormat == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Invalid bill format in URL. Valid values: qrCodeOnly, a6Landscape, a5Landscape, a4Portrait");

        Bill bill;
        try {
            bill = decodeID(billId);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Invalid bill ID. Validate bill data to get a valid ID");
        }

        try {
            byte[] result = generate(bill, billFormat, GraphicsFormat.SVG);
            return ResponseEntity.ok().contentType(getContentType(GraphicsFormat.SVG)).body(result);
        } catch (QRBillValidationError e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Invalid bill ID. Validate bill data to get a valid ID");
        }
    }

    /**
     * Generates the QR bill as a PDF.
     * @param bill the QR bill data
     * @param format the bill format (qrCodeOnly, a6Landscape, a5Landscape, a4Portrait)
     * @return the generated bill if the data is valid; a list of validation messages otherwise
     */
    @RequestMapping(value = "/api/bill/pdf/{format}", method = RequestMethod.POST)
    public ResponseEntity generatePdfBill(@RequestBody QrBill bill, @PathVariable("format") String format) {
        return generateBill(bill, format, GraphicsFormat.PDF);
    }


    private ResponseEntity generateBill(QrBill bill, String format, GraphicsFormat graphicsFormat) {
        BillFormat billFormat = getBillFormat(format);
        if (billFormat == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Invalid bill format in URL. Valid values: qrCodeOnly, a6Landscape, a5Landscape, a4Portrait");

        try {
            byte[] result = generate(QrBill.toGeneratorBill(bill), billFormat, graphicsFormat);
            return ResponseEntity.ok().contentType(getContentType(graphicsFormat)).body(result);
        } catch (QRBillValidationError e) {
            List<ValidationMessage> messages = ValidationMessage.fromList(e.getValidationResult().getValidationMessages());
            addLocalMessages(messages);
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(messages);
        }
    }

    private static BillFormat getBillFormat(String value) {
        BillFormat format;
        switch (value) {
            case "qrCodeOnly":
                format = BillFormat.QRCodeOnly;
                break;
            case "a6Landscape":
                format = BillFormat.A6LandscapeSheet;
                break;
            case "a5Landscape":
                format = BillFormat.A5LandscapeSheet;
                break;
            case "a4Portrait":
                format = BillFormat.A4PortraitSheet;
                break;
            default:
                format = null;
        }
        return format;
    }

    private static final MediaType MEDIA_TYPE_SVG = MediaType.valueOf("image/svg+xml;charset=UTF-8");

    private static MediaType getContentType(GraphicsFormat graphicsFormat) {
        return graphicsFormat == GraphicsFormat.SVG ? MEDIA_TYPE_SVG : MediaType.APPLICATION_PDF;
    }

    private void addLocalMessages(List<ValidationMessage> messages) {
        if (messages == null)
            return;

        Locale currentLocale = LocaleContextHolder.getLocale();
        for (ValidationMessage message: messages) {
            message.setMessage(messageSource.getMessage(message.getMessageKey(), null, currentLocale));
        }
    }

    // --- ID Generation and decoding

    private String generateID(Bill bill) {
        String qrText = QRBill.generateQrCodeText(bill);
        BillPayload payload = new BillPayload();
        payload.setVersion(1);
        payload.setLanguage(bill.getLanguage().name());
        payload.setQrText(qrText);

        Base64.Encoder base64 = Base64.getUrlEncoder();
        byte[] encodedData;
        try (ByteArrayOutputStream buffer = new ByteArrayOutputStream();
             OutputStream os2 = base64.wrap(buffer);
             DeflaterOutputStream os3 = new DeflaterOutputStream(os2)) {
            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(os3, payload);
            os3.flush();
            encodedData = buffer.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String id = new String(encodedData, StandardCharsets.US_ASCII);
        return id.replace('=', '~');
    }

    private Bill decodeID(String id) {
        id = id.replace('~', '=');
        byte[] encodedData = id.getBytes(StandardCharsets.US_ASCII);
        Base64.Decoder base64 = Base64.getUrlDecoder();
        BillPayload payload;
        try (InputStream is1 = new ByteArrayInputStream(encodedData);
             InputStream is2 = base64.wrap(is1);
             InflaterInputStream is3 = new InflaterInputStream(is2)) {
            ObjectMapper mapper = new ObjectMapper();
            payload = mapper.readValue(is3, BillPayload.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Bill bill = QRBill.decodeQrCodeText(payload.getQrText());
        bill.setLanguage(Bill.Language.valueOf(payload.getLanguage()));
        return bill;
    }
}