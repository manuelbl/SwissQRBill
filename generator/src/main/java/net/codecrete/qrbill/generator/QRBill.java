//
// Swiss QR Bill Generator
// Copyright (c) 2017 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
package net.codecrete.qrbill.generator;

import java.io.IOException;

import net.codecrete.qrbill.canvas.Canvas;
import net.codecrete.qrbill.canvas.PDFCanvas;
import net.codecrete.qrbill.canvas.SVGCanvas;


/**
 * Generates Swiss QR bill payment slip.
 */
public class QRBill {

    /** Validation message key: currency must be "CHF" or "EUR" */
    public static final String KEY_CURRENCY_IS_CHF_OR_EUR = "currency_is_chf_or_eur";
    /** Validation message key: amount must be between 0.01 and 999999999.99 */
    public static final String KEY_AMOUNT_IS_IN_VALID_RANGE = "amount_in_valid_range";
    /** Validation message key: IBAN must be from bank in Switzerland or Liechtenstein */
    public static final String KEY_ACCOUNT_IS_CH_LI_IBAN = "account_is_ch_li_iban";
    /** Validation message key: IBAN number must have valid format and check digit */
    public static final String KEY_ACCOUNT_IS_VALID_IBAN = "account_is_valid_iban";
    /** Validation message key: ISO 11649 reference number must have valid format and check digit */
    public static final String KEY_VALID_ISO11649_CREDITOR_REF = "valid_iso11649_creditor_ref";
    /** Validation message key: QR reference number must have valid format and check digit */
    public static final String KEY_VALID_QR_REF_NO = "valid_qr_ref_no";
    /** Validation message key: Reference number is mandatory for IBANs with QR-IID */
    public static final String KEY_MANDATORY_FOR_QR_IBAN = "mandatory_for_qr_iban";
    /** Validation message key: Field is mandatory */
    public static final String KEY_FIELD_IS_MANDATORY = "field_is_mandatory";
    /** Validation message key: Country code must consist of two letters */
    public static final String KEY_VALID_COUNTRY_CODE = "valid_country_code";
    /** Validation message key: Field has been clipped to not exceed the maximum length */
    public static final String KEY_FIELD_CLIPPED = "field_clipped";
    /** Validation message key: Unsupported characters have been replaced */
    public static final String KEY_REPLACED_UNSUPPORTED_CHARACTERS = "replaced_unsupported_characters";
    /** Validation message key: Valid data structure starts with "SPC" and consists of 28 to 30 lines of text */
    public static final String KEY_VALID_DATA_STRUCTURE = "valid_data_structure";
    /** Validation message key: Version 01.00 is supported only */
    public static final String KEY_SUPPORTED_VERSION = "supported_version";
    /** Validation message key: Coding type 1 is supported only */
    public static final String KEY_SUPPORTED_CODING_TYPE = "supported_coding_type";
    /** Validation message key: Valid number required (nnnnn.nn) */
    public static final String KEY_VALID_NUMBER = "valid_number";
    /** Validation message key: Valid date required (YYYY-MM_DD) */
    public static final String KEY_VALID_DATE = "valid_date";

    /**
     * Graphics format of generated QR bill.
     */
    public enum GraphicsFormat {
        /** PDF */
        PDF,
        /** SVG */
        SVG
    }

    /**
     * The output size of the QR bill
     */
    public enum BillFormat {
        /** A4 sheet in portrait orientation. The QR bill is in the bottom right. */
        A4_PORTRAIT_SHEET,
        /** A5 sheet in landscape orientation. The QR bill is in the bottom right. */
        A5_LANDSCAPE_SHEET,
        /** A6 sheet in landscape orientation. The QR bill fills the entire sheet. */
        A6_LANDSCAPE_SHEET,
        /** QR code only (46 by 46 mm). */
        QR_CODE_ONLY
    }


    /**
     * Validates and cleans the bill data.
     * <p>
     *     The validation result contains the error and warning
     *     messages (if any) and the cleaned bill data.
     * </p>
     * @param bill bill data
     * @return validation result
     */
    public static ValidationResult validate(Bill bill) {
        return Validator.validate(bill);
    }


    /**
     * Generates a QR bill payment slip.
     * <p>
     *     If the bill data does not validate, a {@link QRBillValidationError} is thrown,
     *     which contains the validation result.
     * </p>
     * @param bill the bill data
     * @param billFormat the bill's output size
     * @param graphicsFormat the bill's output format
     * @return the generated QR bill (as a byte array)
     */
    public static byte[] generate(Bill bill, BillFormat billFormat, GraphicsFormat graphicsFormat) {
        try (Canvas canvas = createCanvas(graphicsFormat)) {
            return validateAndGenerate(bill, billFormat, canvas);
        } catch (IOException e) {
            throw new QRBillUnexpectedException(e);
        }
    }


    /**
     * Generates a QR bill payment slip using the specified canvas.
     * <p>
     *     If the bill data does not validate, a {@link QRBillValidationError} is thrown,
     *     which contains the validation result.
     * </p>
     * <p>
     *     The canvas will be initialized with {@code Canvas#setupPage} and it will
     *     be closed before returning the generated QR bill
     * </p>
     * @param bill the bill data
     * @param billFormat the bill's output size
     * @param canvas the canvas to draw to
     * @return the generated QR bill (as a byte array)
     */
    public static byte[] generate(Bill bill, BillFormat billFormat, Canvas canvas) {
        try (Canvas c = canvas) {
            return validateAndGenerate(bill, billFormat, c);
        } catch (IOException e) {
            throw new QRBillUnexpectedException(e);
        }
    }


    private static byte[] validateAndGenerate(Bill bill, BillFormat billFormat, Canvas canvas) throws IOException {
        ValidationResult result = Validator.validate(bill);
        Bill cleanedBill = result.getCleanedBill();
        if (result.hasErrors())
            throw new QRBillValidationError(result);

        if (billFormat == BillFormat.QR_CODE_ONLY) {
            return generateQRCode(cleanedBill, canvas);
        } else {
            return generatePaymentSlip(cleanedBill, billFormat, canvas);
        }
    }


    /**
     * Encodes the text embedded in the QR code from the specified bill data.
     * <p>
     *     The specified bill data is first validated and cleaned.
     * </p>
     * <p>
     *     If the bill data does not validate, a {@link QRBillValidationError} is thrown,
     *     which contains the validation result.
     * </p>
     * @param bill the bill data to encode
     * @return the QR code text
     */
    public static String encodeQrCodeText(Bill bill) {
        ValidationResult result = Validator.validate(bill);
        Bill cleanedBill = result.getCleanedBill();
        if (result.hasErrors())
            throw new QRBillValidationError(result);

        QRCode qrCode = new QRCode(cleanedBill);
        return qrCode.getText();
    }


    /**
     * Decodes the text embedded in the QR code and fills it into a {@link Bill} data structure.
     * @param text the text to decode
     * @return the decoded bill data
     */
    public static Bill decodeQrCodeText(String text) {
        return QRCode.decodeQRCodeText(text);
    }


    /**
     * Generates the payment slip as a byte array
     *
     * @param bill the cleaned bill data
     * @param billFormat the output size
     * @param canvas the canvas to draw to
     * @return byte array containing the binary data in the selected format
     */
    private static byte[] generatePaymentSlip(Bill bill, BillFormat billFormat, Canvas canvas) throws IOException {

        double drawingWidth;
        double drawingHeight;

        // define page size
        switch (billFormat) {
            case A6_LANDSCAPE_SHEET:
                drawingWidth = 148.5;
                drawingHeight = 105;
                break;
            case A5_LANDSCAPE_SHEET:
                drawingWidth = 210;
                drawingHeight = 148.5;
                break;
            case A4_PORTRAIT_SHEET:
            default:
                drawingWidth = 210;
                drawingHeight = 297;
                break;
        }

        canvas.setupPage(drawingWidth, drawingHeight);
        PaymentSlipLayout layout = new PaymentSlipLayout(bill, canvas);
        layout.draw(drawingWidth - 148.5, 0, drawingWidth > 148.5 || drawingHeight > 105);
        return canvas.getResult();
    }


    /**
     * Generate the payment slip only
     * 
     * @param bill the bill data
     * @param canvas the canvas to draw to
     * @return byte array containing the binary data in the selected format
     */
    private static byte[] generateQRCode(Bill bill, Canvas canvas) throws IOException {

        canvas.setupPage(QRCode.SIZE, QRCode.SIZE);
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
                throw new QRBillUnexpectedException("Invalid graphics format specified");
        }
        return canvas;
    }
}   
