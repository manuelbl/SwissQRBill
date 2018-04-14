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
     * Validates the bill data.
     * <p>
     *     The validation result contains the error and warning
     *     messages (if any).
     * </p>
     * @param bill bill data
     * @return validation result
     */
    public static ValidationResult validate(Bill bill) {
        ValidationResult result = new ValidationResult();
        Validator validator = new Validator(bill, result);
        validator.validate();
        return result;
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
            throw new QrBillRuntimeException(e);
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
            throw new QrBillRuntimeException(e);
        }
    }


    private static byte[] validateAndGenerate(Bill bill, BillFormat billFormat, Canvas canvas) throws IOException {
        ValidationResult result = new ValidationResult();
        Validator validator = new Validator(bill, result);
        Bill cleanedBill = validator.validate();
        if (result.hasErrors())
            throw new QRBillValidationError(result);

        if (billFormat == BillFormat.QR_CODE_ONLY) {
            return generateQRCode(cleanedBill, canvas);
        } else {
            return generatePaymentSlip(cleanedBill, billFormat, canvas);
        }
    }


    /**
     * Generates the text that is embedded in the QR code.
     * <p>
     *     If the bill data does not validate, a {@link QRBillValidationError} is thrown,
     *     which contains the validation result.
     * </p>
     * @param bill the bill data
     * @return the QR code text
     */
    public static String generateQrCodeText(Bill bill) {
        ValidationResult result = new ValidationResult();
        Validator validator = new Validator(bill, result);
        Bill cleanedBill = validator.validate();
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
                throw new QrBillRuntimeException("Invalid graphics format specified");
        }
        return canvas;
    }
}   
