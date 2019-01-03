//
// Swiss QR Bill Generator
// Copyright (c) 2017 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
package net.codecrete.qrbill.generator;

import net.codecrete.qrbill.canvas.Canvas;
import net.codecrete.qrbill.canvas.PDFCanvas;
import net.codecrete.qrbill.canvas.SVGCanvas;

import java.io.IOException;

/**
 * Generates Swiss QR bill payment part.
 * <p>
 * Can also validate the bill data and encode and decode the text embedded in the QR code.
 * </p>
 */
public class QRBill {

    /**
     * Validation message key: currency must be "CHF" or "EUR"
     */
    public static final String KEY_CURRENCY_IS_CHF_OR_EUR = "currency_is_chf_or_eur";
    /**
     * Validation message key: amount must be between 0.01 and 999999999.99
     */
    public static final String KEY_AMOUNT_IS_IN_VALID_RANGE = "amount_in_valid_range";
    /**
     * Validation message key: IBAN must be from bank in Switzerland or
     * Liechtenstein
     */
    public static final String KEY_ACCOUNT_IS_CH_LI_IBAN = "account_is_ch_li_iban";
    /**
     * Validation message key: IBAN must have valid format and check digit
     */
    public static final String KEY_ACCOUNT_IS_VALID_IBAN = "account_is_valid_iban";
    /**
     * Validation message key: Due to regular IBAN (outside QR-IID range) an ISO 11649 references is expected
     * but it has invalid format or check digit
     */
    public static final String KEY_VALID_ISO11649_CREDITOR_REF = "valid_iso11649_creditor_ref";
    /**
     * Validation message key: Due to QR-IBAN (IBAN in QR-IID range) a QR reference number is expected
     * but it has invalid format or check digit
     */
    public static final String KEY_VALID_QR_REF_NO = "valid_qr_ref_no";
    /**
     * Validation message key: For QR-IBANs (IBAN in QR-IID range) a QR reference is mandatory
     */
    public static final String KEY_MANDATORY_FOR_QR_IBAN = "mandatory_for_qr_iban";
    /**
     * Validation message key: Field is mandatory
     */
    public static final String KEY_FIELD_IS_MANDATORY = "field_is_mandatory";
    /**
     * Validation message key: Conflicting fields for both structured and combined elements address type have been used
     */
    public static final String KEY_ADDRESS_TYPE_CONFLICT = "adress_type_conflict";
    /**
     * Validation message key: Country code must consist of two letters
     */
    public static final String KEY_VALID_COUNTRY_CODE = "valid_country_code";
    /**
     * Validation message key: Field has been clipped to not exceed the maximum
     * length
     */
    public static final String KEY_FIELD_CLIPPED = "field_clipped";
    /**
     * Validation message key: Field value exceed the maximum length
     */
    public static final String KEY_FIELD_TOO_LONG = "field_value_too_long";
    /**
     * Validation message key: Unsupported characters have been replaced
     */
    public static final String KEY_REPLACED_UNSUPPORTED_CHARACTERS = "replaced_unsupported_characters";
    /**
     * Validation message key: Valid data structure starts with "SPC" and consists
     * of 32 to 34 lines of text
     */
    public static final String KEY_VALID_DATA_STRUCTURE = "valid_data_structure";
    /**
     * Validation message key: Version 02.00 is supported only
     */
    public static final String KEY_SUPPORTED_VERSION = "supported_version";
    /**
     * Validation message key: Coding type 1 is supported only
     */
    public static final String KEY_SUPPORTED_CODING_TYPE = "supported_coding_type";
    /**
     * Validation message key: Valid number required (nnnnn.nn)
     */
    public static final String KEY_VALID_NUMBER = "valid_number";
    /**
     * Validation message key: The maximum of 2 alternative schemes has been exceeded
     */
    public static final String KEY_ALT_SCHEME_MAX_EXCEEDED = "alt_scheme_max_exceed";
    /**
     * Validation message key: The bill information is invalid (does not start with // or is too short)
     */
    public static final String KEY_BILL_INFO_INVALID = "bill_info_invalid";

    private QRBill() {
        // do not instantiate
    }

    /**
     * Validates and cleans the bill data.
     * <p>
     * The validation result contains the error and warning messages (if any) and
     * the cleaned bill data.
     * </p>
     * <p>
     * For details about the validation result, see <a href=
     * "https://github.com/manuelbl/SwissQRBill/wiki/Bill-data-validation">Bill data
     * validation</a>
     * </p>
     *
     * @param bill bill data
     * @return validation result
     */
    public static ValidationResult validate(Bill bill) {
        return Validator.validate(bill);
    }

    /**
     * Generates a QR bill payment part.
     * <p>
     * If the bill data does not validate, a {@link QRBillValidationError} is
     * thrown, which contains the validation result. For details about the
     * validation result, see <a href=
     * "https://github.com/manuelbl/SwissQRBill/wiki/Bill-data-validation">Bill data
     * validation</a>
     * </p>
     *
     * @param bill the bill data
     * @return the generated QR bill (as a byte array)
     * @throws QRBillValidationError thrown if the bill data does not validate
     */
    public static byte[] generate(Bill bill) {
        try (Canvas canvas = createCanvas(bill.getFormat().getGraphicsFormat())) {
            return validateAndGenerate(bill, canvas);
        } catch (IOException e) {
            throw new QRBillGenerationException(e);
        }
    }

    /**
     * Generates a QR bill payment part using the specified canvas.
     * <p>
     * If the bill data does not validate, a {@link QRBillValidationError} is
     * thrown, which contains the validation result. For details about the
     * validation result, see <a href=
     * "https://github.com/manuelbl/SwissQRBill/wiki/Bill-data-validation">Bill data
     * validation</a>
     * </p>
     * <p>
     * The canvas will be initialized with {@code Canvas#setupPage} and it will be
     * closed before returning the generated QR bill
     * </p>
     *
     * @param bill   the bill data
     * @param canvas the canvas to draw to
     * @return the generated QR bill (as a byte array)
     * @throws QRBillValidationError thrown if the bill data does not validate
     */
    public static byte[] generate(Bill bill, Canvas canvas) {
        try (Canvas c = canvas) {
            return validateAndGenerate(bill, c);
        } catch (IOException e) {
            throw new QRBillGenerationException(e);
        }
    }

    private static byte[] validateAndGenerate(Bill bill, Canvas canvas) throws IOException {
        ValidationResult result = Validator.validate(bill);
        Bill cleanedBill = result.getCleanedBill();
        if (result.hasErrors())
            throw new QRBillValidationError(result);

        if (bill.getFormat().getOutputSize() == OutputSize.QR_CODE_ONLY) {
            return generateQRCode(cleanedBill, canvas);
        } else {
            return generatePaymentPart(cleanedBill, canvas);
        }
    }

    /**
     * Encodes the text embedded in the QR code from the specified bill data.
     * <p>
     * The specified bill data is first validated and cleaned.
     * </p>
     * <p>
     * If the bill data does not validate, a {@link QRBillValidationError} is
     * thrown, which contains the validation result. For details about the
     * validation result, see <a href=
     * "https://github.com/manuelbl/SwissQRBill/wiki/Bill-data-validation">Bill data
     * validation</a>
     * </p>
     *
     * @param bill the bill data to encode
     * @return the QR code text
     * @throws QRBillValidationError thrown if the bill data does not validate
     */
    public static String encodeQrCodeText(Bill bill) {
        ValidationResult result = Validator.validate(bill);
        Bill cleanedBill = result.getCleanedBill();
        if (result.hasErrors())
            throw new QRBillValidationError(result);

        return QRCodeText.create(cleanedBill);
    }

    /**
     * Decodes the text embedded in the QR code and fills it into a {@link Bill}
     * data structure.
     * <p>
     * A subset of the validations related to embedded QR code text is run. It the
     * validation fails, a {@link QRBillValidationError} is thrown, which contains
     * the validation result. See the error messages marked with a dagger in
     * <a href=
     * "https://github.com/manuelbl/SwissQRBill/wiki/Bill-data-validation">Bill data
     * validation</a>.
     * </p>
     *
     * @param text the text to decode
     * @return the decoded bill data
     * @throws QRBillValidationError thrown if the bill data does not validate
     */
    public static Bill decodeQrCodeText(String text) {
        return QRCodeText.decode(text);
    }

    /**
     * Generates the payment part as a byte array
     *
     * @param bill   the cleaned bill data
     * @param canvas the canvas to draw to
     * @return byte array containing the binary data in the selected format
     */
    private static byte[] generatePaymentPart(Bill bill, Canvas canvas) throws IOException {

        double drawingWidth;
        double drawingHeight;

        // define page size
        switch (bill.getFormat().getOutputSize()) {
            case QR_BILL_ONLY:
                drawingWidth = 210;
                drawingHeight = 105;
                break;
            case A4_PORTRAIT_SHEET:
            default:
                drawingWidth = 210;
                drawingHeight = 297;
                break;
        }

        canvas.setupPage(drawingWidth, drawingHeight, bill.getFormat().getFontFamily());
        BillLayout layout = new BillLayout(bill, canvas);
        layout.draw();
        return canvas.getResult();
    }

    /**
     * Generate the QR code only
     *
     * @param bill   the bill data
     * @param canvas the canvas to draw to
     * @return byte array containing the binary data in the selected format
     */
    private static byte[] generateQRCode(Bill bill, Canvas canvas) throws IOException {

        canvas.setupPage(QRCode.SIZE, QRCode.SIZE, bill.getFormat().getFontFamily());
        QRCode qrCode = new QRCode(bill);
        qrCode.draw(canvas, 0, 0);
        return canvas.getResult();
    }

    private static Canvas createCanvas(GraphicsFormat graphicsFormat) {
        Canvas canvas;
        switch (graphicsFormat) {
            case SVG:
                canvas = new SVGCanvas();
                break;
            case PDF:
                canvas = new PDFCanvas();
                break;
            default:
                throw new QRBillGenerationException("Invalid graphics format specified");
        }
        return canvas;
    }
}
