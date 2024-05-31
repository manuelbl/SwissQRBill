//
// Swiss QR Bill Generator
// Copyright (c) 2017 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
package net.codecrete.qrbill.generator;

import net.codecrete.qrbill.canvas.*;

import java.io.IOException;

/**
 * Generates Swiss QR bill payment part.
 * <p>
 * Can also validate the bill data and encode and decode the text embedded in the QR code.
 * </p>
 */
public class QRBill {

    /**
     * The width of an A4 sheet in portrait orientation, in mm
     *
     * @see OutputSize#A4_PORTRAIT_SHEET
     */
    public static final double A4_PORTRAIT_WIDTH = 210;

    /**
     * The height of an A4 sheet in portrait orientation, in mm
     *
     * @see OutputSize#A4_PORTRAIT_SHEET
     */
    public static final double A4_PORTRAIT_HEIGHT = 297;

    /**
     * The width of a QR bill (payment part and receipt), in mm
     *
     * @see OutputSize#QR_BILL_ONLY
     */
    public static final double QR_BILL_WIDTH = 210;

    /**
     * The height of a QR bill (payment part and receipt), in mm
     *
     * @see OutputSize#QR_BILL_ONLY
     */
    public static final double QR_BILL_HEIGHT = 105;

    /**
     * The width of the output format with extra space for horizontal separator line
     * (payment part and receipt plus space for line and scissors), in mm
     *
     * @see OutputSize#QR_BILL_EXTRA_SPACE
     */
    public static final double QR_BILL_WITH_HORI_LINE_WIDTH = 210;

    /**
     * The height of the output format with extra space for horizontal separator line
     * (payment part and receipt plus space for line and scissors), in mm
     *
     * @see OutputSize#QR_BILL_EXTRA_SPACE
     */
    public static final double QR_BILL_WITH_HORI_LINE_HEIGHT = 110;

    /**
     * The width of the QR code, in mm
     *
     * @see OutputSize#QR_CODE_ONLY
     */
    public static final double QR_CODE_WIDTH = 46;

    /**
     * The height of the QR code, in mm
     *
     * @see OutputSize#QR_CODE_ONLY
     */
    public static final double QR_CODE_HEIGHT = 46;

    /**
     * The width of the QR code with quiet zone, in mm
     *
     * @see OutputSize#QR_CODE_WITH_QUIET_ZONE
     */
    public static final double QR_CODE_WITH_QUIET_ZONE_WIDTH = 56;

    /**
     * The height of the QR code with quiet zone, in mm
     *
     * @see OutputSize#QR_CODE_WITH_QUIET_ZONE
     */
    public static final double QR_CODE_WITH_QUIET_ZONE_HEIGHT = 56;

    /**
     * The width of the payment part, in mm
     *
     * @see OutputSize#PAYMENT_PART_ONLY
     */
    public static final double PAYMENT_PART_WDITH = 148;

    /**
     * The height of the payment part, in mm
     *
     * @see OutputSize#PAYMENT_PART_ONLY
     */
    public static final double PAYMENT_PART_HEIGHT = 105;


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
     * Generates a QR bill (payment part and receipt) or QR code as an SVG image or PDF document.
     * <p>
     * If the bill data is not valid, a {@link QRBillValidationError} is
     * thrown, which contains the validation result. For details about the
     * validation result, see <a href=
     * "https://github.com/manuelbl/SwissQRBill/wiki/Bill-data-validation">Bill data
     * validation</a>
     * </p>
     * <p>
     * The graphics format is specified with {@code bill.getFormat().setGraphicsFormat(...)}.
     * This method only supports the generation of SVG images and PDF files. For other graphics
     * formats (in particular PNG), use {@link #draw}
     * </p>
     *
     * @param bill the bill data
     * @return the generated QR bill (as a byte array encoded in the specified graphics format)
     * @throws QRBillValidationError thrown if the bill data does not validate
     * @see #draw
     */
    public static byte[] generate(Bill bill) {
        try (Canvas canvas = createCanvas(bill)) {
            validateAndGenerate(bill, canvas);
            return ((ByteArrayResult) canvas).toByteArray();
        } catch (IOException e) {
            throw new QRBillGenerationException(e);
        }
    }

    /**
     * Draws the QR bill (payment part and receipt) or QR code for the specified bill data onto the specified canvas.
     * <p>
     * The QR bill or code are drawn at position (0, 0) extending to the top and to the right.
     * Typically, the position (0, 0) is the bottom left corner of the canvas.
     * </p>
     * <p>
     * This methods ignores the formatting properties {@code bill.getFormat().getFontFamily()}
     * and {@code bill.getFormat().getGraphicsFormat()}.
     * They can be set when the canvas instance passed to this method is created.
     * </p>
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
     * @throws QRBillValidationError thrown if the bill data does not validate
     */
    public static void draw(Bill bill, Canvas canvas) {
        try {
            validateAndGenerate(bill, canvas);
        } catch (IOException e) {
            throw new QRBillGenerationException(e);
        }
    }

    /**
     * Draws the separator line(s) to the specified canvas.
     * <p>
     * The separator lines are drawn assuming that the QR bill starts at position (0, 0)
     * and extends the top and right. So position (0, 0) should be in the bottom left corner.
     * </p>
     * <p>
     * This method allows to add separator lines to an existing QR bill,
     * e.g. on to an archived QR bill document.
     * </p>
     *
     * @param separatorType      type of separator lines
     * @param withHorizontalLine {@code true} if both the horizontal or vertical separator should be drawn,
     *                           {@code false} for the vertical separator only
     * @param canvas             the canvas to draw to
     */
    public static void drawSeparators(SeparatorType separatorType, boolean withHorizontalLine, Canvas canvas) {
        BillFormat format = new BillFormat();
        format.setSeparatorType(separatorType);
        format.setOutputSize(withHorizontalLine ? OutputSize.QR_BILL_EXTRA_SPACE : OutputSize.QR_BILL_ONLY);
        Bill bill = new Bill();
        bill.setFormat(format);

        BillLayout layout = new BillLayout(bill, canvas);
        try {
            layout.drawBorder();
        } catch (IOException e) {
            throw new QRBillGenerationException(e);
        }
    }

    private static void validateAndGenerate(Bill bill, Canvas canvas) throws IOException {
        ValidationResult result = Validator.validate(bill);
        Bill cleanedBill = result.getCleanedBill();
        if (result.hasErrors())
            throw new QRBillValidationError(result);

        if (bill.getFormat().getOutputSize() == OutputSize.QR_CODE_ONLY) {
            QRCode qrCode = new QRCode(cleanedBill);
            qrCode.draw(canvas, 0, 0);

        } else if (bill.getFormat().getOutputSize() == OutputSize.QR_CODE_WITH_QUIET_ZONE) {
                QRCode qrCode = new QRCode(cleanedBill);
                canvas.startPath();
                canvas.addRectangle(0, 0, QR_CODE_WITH_QUIET_ZONE_WIDTH, QR_CODE_WITH_QUIET_ZONE_HEIGHT);
                canvas.fillPath(0xffffff, false);
                qrCode.draw(canvas, 5, 5);

        } else {
            BillLayout layout = new BillLayout(cleanedBill, canvas);
            layout.draw();
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
     * A subset of the validations related to embedded QR code text is run. If the
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

    private static Canvas createCanvas(Bill bill) throws IOException {
        double drawingWidth;
        double drawingHeight;
        BillFormat format = bill.getFormat();

        // define page size
        switch (format.getOutputSize()) {
            case QR_BILL_ONLY:
                drawingWidth = QR_BILL_WIDTH;
                drawingHeight = QR_BILL_HEIGHT;
                break;
            case QR_BILL_EXTRA_SPACE:
                drawingWidth = QR_BILL_WITH_HORI_LINE_WIDTH;
                drawingHeight = QR_BILL_WITH_HORI_LINE_HEIGHT;
                break;
            case PAYMENT_PART_ONLY:
                drawingWidth = PAYMENT_PART_WDITH;
                drawingHeight = PAYMENT_PART_HEIGHT;
                break;
            case QR_CODE_ONLY:
                drawingWidth = QR_CODE_WIDTH;
                drawingHeight = QR_CODE_HEIGHT;
                break;
            case QR_CODE_WITH_QUIET_ZONE:
                drawingWidth = QR_CODE_WITH_QUIET_ZONE_WIDTH;
                drawingHeight = QR_CODE_WITH_QUIET_ZONE_HEIGHT;
                break;
            case A4_PORTRAIT_SHEET:
            default:
                drawingWidth = A4_PORTRAIT_WIDTH;
                drawingHeight = A4_PORTRAIT_HEIGHT;
                break;
        }

        Canvas canvas;
        switch (format.getGraphicsFormat()) {
            case SVG:
                canvas = new SVGCanvas(drawingWidth, drawingHeight, format.getFontFamily());
                break;
            case PDF:
                canvas = new PDFCanvas(drawingWidth, drawingHeight,
                        bill.getCharacterSet() != SPSCharacterSet.LATIN_1_SUBSET
                                ? PDFFontSettings.embeddedLiberationSans()
                                : PDFFontSettings.standardHelvetica());
                break;
            case PNG:
                canvas = new PNGCanvas(drawingWidth, drawingHeight, format.getResolution(), format.getFontFamily());
                break;
            default:
                throw new QRBillGenerationException("Invalid graphics format specified");
        }
        return canvas;
    }
}
