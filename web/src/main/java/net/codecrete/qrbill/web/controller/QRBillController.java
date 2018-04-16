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
import net.codecrete.qrbill.web.api.QrBill;
import net.codecrete.qrbill.web.api.QrCodeInformation;
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
     * Validates the QR bill data
     * @param bill the QR bill data
     * @return returns the validation result: validated, possibly modified bill, the validation messages (if any),
     *  a bill ID (if the bill is valid) and the QR code text (if the bill is valid)
     */
    @RequestMapping(value = "/bill/validate", method = RequestMethod.POST)
    @ResponseBody
    public ValidationResponse validate(@RequestBody QrBill bill) {
        // Validate data
        ValidationResult result = QRBill.validate(QrBill.toGeneratorBill(bill));
        Bill validatedBill = result.getCleanedBill();

        ValidationResponse response = new ValidationResponse();
        response.setValid(result.isValid());

        // Generate localized messages
        if (result.hasMessages()) {
            List<ValidationMessage> messages = ValidationMessage.fromList(result.getValidationMessages());
            addLocalMessages(messages);
            response.setValidationMessages(messages);
        }
        response.setValidatedBill(QrBill.from(validatedBill));

        // generate QR code text and bill ID
        if (!result.hasErrors()) {
            String qrCodeText = QRBill.encodeQrCodeText(validatedBill);
            response.setQrCodeText(qrCodeText);
            response.setBillID(generateID(qrCodeText, validatedBill.getLanguage().name()));
        }

        return response;
    }

    /**
     * Decodes the text from the QR code and validates the information.
     * @param info the text from the QR code
     * @return returns the validation result: decoded bill data, the validation messages (if any),
     *  a bill ID (if the bill is valid) and the QR code text
     */
    @RequestMapping(value = "/bill/decode", method = RequestMethod.POST)
    @ResponseBody
    public ValidationResponse decodeQRCodeText(@RequestBody QrCodeInformation info) {
        Bill bill = QRBill.decodeQrCodeText(info.getQrCodeText());

        // Validate data
        ValidationResult result = QRBill.validate(bill);
        Bill validatedBill = result.getCleanedBill();

        ValidationResponse response = new ValidationResponse();
        response.setValid(result.isValid());

        // Generate localized messages
        if (result.hasMessages()) {
            List<ValidationMessage> messages = ValidationMessage.fromList(result.getValidationMessages());
            addLocalMessages(messages);
            response.setValidationMessages(messages);
        }
        response.setValidatedBill(QrBill.from(validatedBill));

        // generate QR code text and bill ID
        if (!result.hasErrors()) {
            String qrCodeText = QRBill.encodeQrCodeText(validatedBill);
            response.setQrCodeText(qrCodeText);
            response.setBillID(generateID(qrCodeText, validatedBill.getLanguage().name()));
        }

        return response;
    }

    /**
     * Generates the QR bill as an SVG.
     * @param bill the QR bill data
     * @param format the bill format (qrCodeOnly, a6Landscape, a5Landscape, a4Portrait)
     * @return the generated bill if the data is valid; a list of validation messages otherwise
     */
    @RequestMapping(value = "/bill/svg/{format}", method = RequestMethod.POST)
    public ResponseEntity<Object> generateSvgBillPost(@RequestBody QrBill bill, @PathVariable("format") String format) {
        return generateBill(bill, format, GraphicsFormat.SVG);
    }

    /**
     * Generates the QR bill as an SVG.
     * @param format the bill format (qrCodeOnly, a6Landscape, a5Landscape, a4Portrait)
     * @param billId the ID of the QR bill (as returned by /validate)
     * @return the generated bill
     */
    @RequestMapping(value = "/bill/svg/{format}/{id}", method = RequestMethod.GET)
    public ResponseEntity<Object> generateSvgBillGet(@PathVariable("id") String billId, @PathVariable("format") String format) {
        return generateBillFromID(billId, format, GraphicsFormat.SVG);
    }

    /**
     * Generates the QR bill as a PDF.
     * @param bill the QR bill data
     * @param format the bill format (qrCodeOnly, a6Landscape, a5Landscape, a4Portrait)
     * @return the generated bill if the data is valid; a list of validation messages otherwise
     */
    @RequestMapping(value = "/bill/pdf/{format}", method = RequestMethod.POST)
    public ResponseEntity<Object> generatePdfBill(@RequestBody QrBill bill, @PathVariable("format") String format) {
        return generateBill(bill, format, GraphicsFormat.PDF);
    }

    /**
     * Generates the QR bill as a PDF.
     * @param format the bill format (qrCodeOnly, a6Landscape, a5Landscape, a4Portrait)
     * @param billId the ID of the QR bill (as returned by /validate)
     * @return the generated bill
     */
    @RequestMapping(value = "/bill/pdf/{format}/{id}", method = RequestMethod.GET)
    public ResponseEntity<Object> generatePdfBillGet(@PathVariable("id") String billId, @PathVariable("format") String format) {
        return generateBillFromID(billId, format, GraphicsFormat.PDF);
    }


    private ResponseEntity<Object> generateBill(QrBill bill, String format, GraphicsFormat graphicsFormat) {
        BillFormat billFormat = getBillFormat(format);
        if (billFormat == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Invalid bill format in URL. Valid values: qr-code-only, a6-landscape, a5-landscape, a4-portrait");

        try {
            byte[] result = generate(QrBill.toGeneratorBill(bill), billFormat, graphicsFormat);
            return ResponseEntity.ok().contentType(getContentType(graphicsFormat)).body(result);
        } catch (QRBillValidationError e) {
            List<ValidationMessage> messages = ValidationMessage.fromList(e.getValidationResult().getValidationMessages());
            addLocalMessages(messages);
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(messages);
        }
    }

    private ResponseEntity<Object> generateBillFromID(String billId, String format, GraphicsFormat graphicsFormat) {
        BillFormat billFormat = getBillFormat(format);
        if (billFormat == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid bill format in URL. Valid values: qr-code-only, a6-landscape, a5-landscape, a4-portrait");

        Bill bill;
        try {
            bill = decodeID(billId);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid bill ID. Validate bill data to get a valid ID");
        }

        try {
            byte[] result = generate(bill, billFormat, graphicsFormat);
            return ResponseEntity.ok().contentType(getContentType(graphicsFormat)).body(result);
        } catch (QRBillValidationError e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid bill ID. Validate bill data to get a valid ID");
        }
    }

    private static BillFormat getBillFormat(String value) {
        BillFormat format;
        switch (value) {
            case "qrCodeOnly":
            case "qr-code-only":
                format = BillFormat.QR_CODE_ONLY;
                break;
            case "a6Landscape":
            case "a6-landscape":
                format = BillFormat.A6_LANDSCAPE_SHEET;
                break;
            case "a5Landscape":
            case "a5-landscape":
                format = BillFormat.A5_LANDSCAPE_SHEET;
                break;
            case "a4Portrait":
            case "a4-portrait":
                format = BillFormat.A4_PORTRAIT_SHEET;
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

    /**
     * Generates an ID that encodes the entire bill data.
     * <p>
     *     The ID is the Base 64 (URL safe version) of the compressed (deflate) JSON data
     *     consisting of version, language and the text string would be embedded in the QR code.
     * </p>
     * <p>
     *     The ID is made URL safe by using the URL-safe RFC4648 Base 64 encoding and replacing
     *     all equal signs (=) with tildes (~).
     * </p>
     * @param qrCodeText the QR code text
     * @param language the ISO language code
     * @return the generated ID
     */
    private String generateID(String qrCodeText, String language) {

        BillPayload payload = new BillPayload();
        payload.setVersion(1);
        payload.setLanguage(language);
        payload.setQrText(qrCodeText);

        Base64.Encoder base64 = Base64.getUrlEncoder();
        byte[] encodedData;
        try (ByteArrayOutputStream buffer = new ByteArrayOutputStream();
             OutputStream intermediate = base64.wrap(buffer);
             DeflaterOutputStream head = new DeflaterOutputStream(intermediate)) {

            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(head, payload);
            head.flush();
            encodedData = buffer.toByteArray();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String id = new String(encodedData, StandardCharsets.US_ASCII);
        return id.replace('=', '~');
    }

    /**
     * Decodes an bill ID and returns the bill data
     * <p>
     *     The bill ID is assumed to have been generated
     *     by {@link #generateID(String, String)}.
     * </p>
     * @param id the ID
     * @return the bill data
     */
    private Bill decodeID(String id) {

        id = id.replace('~', '=');
        byte[] encodedData = id.getBytes(StandardCharsets.US_ASCII);

        Base64.Decoder base64 = Base64.getUrlDecoder();
        BillPayload payload;
        try (InputStream dataStream = new ByteArrayInputStream(encodedData);
             InputStream intermediate = base64.wrap(dataStream);
             InflaterInputStream head = new InflaterInputStream(intermediate)) {

            ObjectMapper mapper = new ObjectMapper();
            payload = mapper.readValue(head, BillPayload.class);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Bill bill = QRBill.decodeQrCodeText(payload.getQrText());
        String lang = payload.getLanguage().toUpperCase(Locale.US);
        bill.setLanguage(Bill.Language.valueOf(lang));
        return bill;
    }
}
