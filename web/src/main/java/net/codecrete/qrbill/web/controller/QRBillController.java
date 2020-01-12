//
// Swiss QR Bill Generator
// Copyright (c) 2017 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
package net.codecrete.qrbill.web.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import net.codecrete.qrbill.generator.Bill;
import net.codecrete.qrbill.generator.GraphicsFormat;
import net.codecrete.qrbill.generator.Language;
import net.codecrete.qrbill.generator.MultilingualText;
import net.codecrete.qrbill.generator.OutputSize;
import net.codecrete.qrbill.generator.QRBill;
import net.codecrete.qrbill.generator.QRBillValidationError;
import net.codecrete.qrbill.generator.SeparatorType;
import net.codecrete.qrbill.generator.ValidationResult;
import net.codecrete.qrbill.web.api.BillApi;
import net.codecrete.qrbill.web.model.BillFormat;
import net.codecrete.qrbill.web.model.QrBill;
import net.codecrete.qrbill.web.model.QrCodeInformation;
import net.codecrete.qrbill.web.model.ValidationMessage;
import net.codecrete.qrbill.web.model.ValidationResponse;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.NativeWebRequest;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

@RestController
public class QRBillController implements BillApi {

    private final MessageLocalizer messageLocalizer;

    private final NativeWebRequest request;

    /**
     * Creates an instance.
     * <p>
     * Single constructor for Spring dependency injection.
     * </p>
     */
    public QRBillController(MessageLocalizer messageLocalizer, NativeWebRequest request) {
        this.messageLocalizer = messageLocalizer;
        this.request = request;
    }

    /**
     * Validates the QR bill data
     *
     * @param qrBill the QR bill data
     * @return returns the validation result: validated, possibly modified bill, the
     * validation messages (if any), a bill ID (if the bill is valid) and
     * the QR code text (if the bill is valid)
     */
    @Override
    public ResponseEntity<ValidationResponse> validateBill(QrBill qrBill) {
        ValidationResult result = QRBill.validate(QrBillDTOConverter.fromDtoQrBill(qrBill));
        return new ResponseEntity<>(createValidationResponse(result), HttpStatus.OK);
    }

    /**
     * Decodes the text from the QR code and validates the information.
     *
     * @param qrCodeInformation the text from the QR code
     * @return returns the validation result: decoded bill data, the validation
     * messages (if any), a bill ID (if the bill is valid) and the QR code
     * text
     */
    @Override
    public ResponseEntity<ValidationResponse> decodeQRCode(QrCodeInformation qrCodeInformation) {
        ValidationResult result;
        try {
            Bill bill = QRBill.decodeQrCodeText(qrCodeInformation.getText());
            result = QRBill.validate(bill);
        } catch (QRBillValidationError e) {
            result = e.getValidationResult();
        }
        return new ResponseEntity<>(createValidationResponse(result), HttpStatus.OK);
    }

    private ValidationResponse createValidationResponse(ValidationResult result) {
        // Get validated data
        Bill validatedBill = result.getCleanedBill();

        ValidationResponse response = new ValidationResponse();
        response.setValid(result.isValid());

        // Generate localized messages
        if (result.hasMessages()) {
            List<ValidationMessage> messages
                    = QrBillDTOConverter.toDtoValidationMessageList(result.getValidationMessages());
            messageLocalizer.addLocalMessages(messages);
            response.setValidationMessages(messages);
        }
        response.setValidatedBill(QrBillDTOConverter.toDTOQrBill(validatedBill));

        // generate QR code text and bill ID
        if (!result.hasErrors()) {
            String qrCodeText = QRBill.encodeQrCodeText(validatedBill);
            response.setQrCodeText(qrCodeText);
            response.setBillID(generateID(qrCodeText, QrBillDTOConverter.toDtoBillFormat(validatedBill.getFormat())));
        }

        return response;
    }

    /**
     * Generates the QR bill as an SVG or PDF.
     *
     * @param qrBill the QR bill data
     * @return the generated bill if the data is valid; a list of validation
     * messages otherwise
     */
    @Override
    public ResponseEntity<Resource> generateBill(QrBill qrBill) {
        Bill bill = QrBillDTOConverter.fromDtoQrBill(qrBill);
        setFormatDefaults(bill);
        updateForAdviceOnly(bill);
        byte[] result = QRBill.generate(bill);
        MediaType contentType = getContentType(bill.getFormat().getGraphicsFormat());
        return ResponseEntity.ok().contentType(contentType).body(new ByteArrayResource(result));
    }

    private static CacheControl IMAGE_CACHE_CONTROL = CacheControl.maxAge(10, TimeUnit.DAYS);

    /**
     * Generates the QR bill as an SVG or PDF.
     *
     * @param billID         the bill format (qrCodeOnly, a6Landscape, a5Landscape, a4Portrait)
     * @param outputSize     output size for QR bill (overrides the one specified in the *billID*, optional)
     * @param graphicsFormat graphics format for QR bill (overrides the one specified in the *billID*, optional)
     * @return the generated bill
     */
    @Override
    public ResponseEntity<Resource> getBillImage(String billID, String outputSize, String graphicsFormat) {
        Bill bill;
        try {
            bill = decodeID(billID);
            if (bill == null)
                throw new BadRequestException("Invalid bill ID. Validate bill data to get a valid ID");
            setFormatDefaults(bill);
        } catch (Exception e) {
            throw new BadRequestException("Invalid bill ID. Validate bill data to get a valid ID");
        }

        if (outputSize != null)
            bill.getFormat().setOutputSize(getOutputSize(outputSize));
        if (graphicsFormat != null)
            bill.getFormat().setGraphicsFormat(getGraphicsFormat(graphicsFormat));
        updateForAdviceOnly(bill);

        byte[] result = QRBill.generate(bill);
        MediaType contentType = getContentType(bill.getFormat().getGraphicsFormat());
        return ResponseEntity.ok()
                .contentType(contentType)
                .cacheControl(IMAGE_CACHE_CONTROL)
                .body(new ByteArrayResource(result));
    }

    private static OutputSize getOutputSize(String value) {
        BillFormat.OutputSizeEnum outputSizeEnum = BillFormat.OutputSizeEnum.fromValue(value);
        return OutputSize.valueOf(outputSizeEnum.name());
    }

    private static GraphicsFormat getGraphicsFormat(String value) {
        BillFormat.GraphicsFormatEnum graphicsFormatEnum = BillFormat.GraphicsFormatEnum.fromValue(value);
        return GraphicsFormat.valueOf(graphicsFormatEnum.name());
    }

    private static final MediaType MEDIA_TYPE_SVG = MediaType.valueOf("image/svg+xml;charset=UTF-8");

    private static MediaType getContentType(GraphicsFormat graphicsFormat) {
        return graphicsFormat == GraphicsFormat.SVG ? MEDIA_TYPE_SVG : MediaType.APPLICATION_PDF;
    }

    private void updateForAdviceOnly(Bill bill) {
        if (bill.getAmount() == null || BigDecimal.ZERO.compareTo(bill.getAmount()) != 0)
            return;

        bill.setUnstructuredMessage(
                MultilingualText.getText(MultilingualText.KEY_DO_NOT_USE_FOR_PAYMENT, bill.getFormat().getLanguage()));
    }

    // --- ID Generation and decoding

    /**
     * Generates an ID that encodes the entire bill data.
     * <p>
     * The ID is the Base 64 (URL safe version) of the compressed (deflate) JSON
     * data consisting of version, language and the text string would be embedded in
     * the QR code.
     * </p>
     * <p>
     * The ID is made URL safe by using the URL-safe RFC4648 Base 64 encoding and
     * replacing all equal signs (=) with tildes (~).
     * </p>
     *
     * @param qrCodeText the QR code text
     * @param billFormat the billFormat
     * @return the generated ID
     */
    private String generateID(String qrCodeText, BillFormat billFormat) {

        BillPayload payload = new BillPayload();
        payload.setVersion(1);
        payload.setFormat(billFormat);
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
            throw new RuntimeException(e); // NOSONAR
        }

        String id = new String(encodedData, StandardCharsets.US_ASCII);
        return id.replace('=', '~');
    }

    /**
     * Decodes an bill ID and returns the bill data
     * <p>
     * The bill ID is assumed to have been generated by
     * {@link #generateID(String, BillFormat)}.
     * </p>
     *
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
            return null; // invalid ID
        }

        Bill bill = QRBill.decodeQrCodeText(payload.getQrText());
        bill.setFormat(QrBillDTOConverter.fromDtoBillFormat(payload.getFormat()));
        return bill;
    }

    private void setFormatDefaults(Bill bill) {
        net.codecrete.qrbill.generator.BillFormat format = bill.getFormat();
        OutputSize outputSize = null;
        Language language = null;
        SeparatorType separatorType = null;
        GraphicsFormat graphicsFormat = null;
        String fontFamily = null;

        if (format != null) {
            outputSize = format.getOutputSize();
            language = format.getLanguage();
            separatorType = format.getSeparatorType();
            graphicsFormat = format.getGraphicsFormat();
            fontFamily = format.getFontFamily();
        }

        if (outputSize == null)
            outputSize = OutputSize.A4_PORTRAIT_SHEET;
        if (language == null)
            language = languageFromRequestHeader();
        if (language == null)
            language = Language.EN;
        if (separatorType == null)
            separatorType = SeparatorType.SOLID_LINE_WITH_SCISSORS;
        if (fontFamily == null)
            fontFamily = "Helvetica,Arial,\"Liberation Sans\"";

        if (graphicsFormat == null)
            graphicsFormat = graphicsFormatFromRequestHeader();
        if (graphicsFormat == null)
            graphicsFormat = GraphicsFormat.SVG;

        if (format == null) {
            format = new net.codecrete.qrbill.generator.BillFormat();
            bill.setFormat(format);
        }
        format.setOutputSize(outputSize);
        format.setLanguage(language);
        format.setSeparatorType(separatorType);
        format.setFontFamily(fontFamily);
        format.setGraphicsFormat(graphicsFormat);
    }

    private GraphicsFormat graphicsFormatFromRequestHeader() {
        for (MediaType mediaType : MediaType.parseMediaTypes(request.getHeader("Accept"))) {
            if (mediaType.isCompatibleWith(MediaType.valueOf("image/svg+xml")))
                return GraphicsFormat.SVG;
            if (mediaType.isCompatibleWith(MediaType.valueOf("application/pdf")))
                return GraphicsFormat.PDF;
        }

        return null;
    }

    private Language languageFromRequestHeader() {
        String languages = request.getHeader("Accept-Language");
        if (languages == null)
            return null;

        List<Locale.LanguageRange> languageList = Locale.LanguageRange.parse(languages);
        for (Locale.LanguageRange range : languageList) {
            if (range.getRange().startsWith("en"))
                return Language.EN;
            if (range.getRange().startsWith("de"))
                return Language.DE;
            if (range.getRange().startsWith("fr"))
                return Language.FR;
            if (range.getRange().startsWith("it"))
                return Language.IT;
        }

        return null;
    }
}
