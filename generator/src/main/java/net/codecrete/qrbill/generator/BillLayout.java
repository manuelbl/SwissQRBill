//
// Swiss QR Bill Generator
// Copyright (c) 2018 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
package net.codecrete.qrbill.generator;

import net.codecrete.qrbill.canvas.Canvas;

import java.awt.geom.AffineTransform;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * Layouting and drawing of QR bill payment slip
 */
class BillLayout {

    private static final double PT_TO_MM = 25.4 / 72;
    private static final double MM_TO_PT = 72 / 25.4;
    private static final int FONT_SIZE_TITLE = 11; // pt
    private static final double SLIP_WIDTH = 210; // mm
    private static final double SLIP_HEIGHT = 105; // mm
    private static final double MARGIN = 5; // mm
    private static final double RECEIPT_WIDTH = 62; // mm
    private static final double RECEIPT_TEXT_WIDTH = 52; // mm
    private static final double PAYMEMT_PART_WIDTH = 148; // mm
    private static final double PP_AMOUNT_SECTION_WIDTH = 46; // mm
    private static final double PP_INFO_SECTION_WIDTH = 87; // mm
    private static final double AMOUNT_SECTION_TOP = 37; // mm (from bottom)
    private static final double BOX_TOP_PADDING = 2 * PT_TO_MM; // mm
    private static final double DEBTOR_BOX_WIDTH_PP = 65; // mm
    private static final double DEBTOR_BOX_HEIGHT_PP = 25; // mm
    private static final double DEBTOR_BOX_WIDTH_RC = 52; // mm
    private static final double DEBTOR_BOX_HEIGHT_RC = 20; // mm


    private Bill bill;
    private QRCode qrCode;
    private Canvas graphics;

    private String accountPayableTo;
    private String reference;
    private String additionalInfo;
    private String payableBy;
    private String amount;

    private String[] accountPayableToLines;
    private String[] additionalInfoLines;
    private String[] payableByLines;

    private double yPos;

    private int labelFontSize;
    private double labelAscender;
    private int textFontSize;
    private double textAscender;
    private double lineSpacing;
    private double extraSpacing;


    BillLayout(Bill bill, Canvas graphics) {
        this.bill = bill;
        this.qrCode = new QRCode(bill);
        this.graphics = graphics;
    }

    void draw() throws IOException {

        prepareText();

        // payment part

        final int PP_LABEL_PREF_FONT_SIZE = 8; // pt
        final int PP_TEXT_PREF_FONT_SIZE = 10; // pt
        final int PP_TEXT_MIN_FONT_SIZE = 8; // pt

        labelFontSize = PP_LABEL_PREF_FONT_SIZE;
        textFontSize = PP_TEXT_PREF_FONT_SIZE;

        boolean isTooTight;
        while (true) {
            breakLines(PP_INFO_SECTION_WIDTH);
            isTooTight = computePaymentPartSpacing();
            if (!isTooTight || textFontSize == PP_TEXT_MIN_FONT_SIZE)
                break;
            labelFontSize--;
            textFontSize--;
        }
        drawPaymentPart();

        // receipt

        final int RC_LABEL_PREF_FONT_SIZE = 6; // pt
        final int RC_TEXT_PREF_FONT_SIZE = 8; // pt

        labelFontSize = RC_LABEL_PREF_FONT_SIZE;
        textFontSize = RC_TEXT_PREF_FONT_SIZE;
        breakLines(RECEIPT_TEXT_WIDTH);
        isTooTight = computeReceiptSpacing();
        if (isTooTight) {
            prepareReducedReceiptText(false);
            breakLines(RECEIPT_TEXT_WIDTH);
            isTooTight = computeReceiptSpacing();
        }
        if (isTooTight) {
            prepareReducedReceiptText(true);
            breakLines(RECEIPT_TEXT_WIDTH);
            computeReceiptSpacing();
        }
        drawReceipt();

        // border
        drawBorder();
    }

    private void drawPaymentPart() throws IOException {

        final double QR_CODE_BOTTOM = 43.5; // mm

        // title section
        graphics.setTransformation(RECEIPT_WIDTH + MARGIN, 0, 0, 1, 1);
        yPos = SLIP_HEIGHT - MARGIN - graphics.getAscender(FONT_SIZE_TITLE);
        graphics.putText(getText(MultilingualText.KEY_PAYMENT_PART), 0, yPos, FONT_SIZE_TITLE, true);

        // Swiss QR code section
        qrCode.draw(graphics, RECEIPT_WIDTH + MARGIN, QR_CODE_BOTTOM);

        // amount section
        drawPaymentPartAmountSection();

        // information section
        drawPaymentPartInformationSection();

        // further information section
        drawFurtherInformationSection();
    }

    private void drawPaymentPartAmountSection() throws IOException {

        final double CURRENCY_WIDTH_PP = 15; // mm
        final double AMOUNT_BOX_WIDTH_PP = 40; // mm
        final double AMOUNT_BOX_HEIGHT_PP = 15; // mm

        graphics.setTransformation(RECEIPT_WIDTH + MARGIN, 0, 0, 1, 1);

        // currency
        double y = AMOUNT_SECTION_TOP - labelAscender;
        String label = getText(MultilingualText.KEY_CURRENCY);
        graphics.putText(label, 0, y, labelFontSize, true);

        y -= (textFontSize + 3) * PT_TO_MM;
        graphics.putText(bill.getCurrency(), 0, y, textFontSize, false);

        // amount
        y = AMOUNT_SECTION_TOP - labelAscender;
        label = getText(MultilingualText.KEY_AMOUNT);
        graphics.putText(label, CURRENCY_WIDTH_PP, y, labelFontSize, true);

        y -= (textFontSize + 3) * PT_TO_MM;
        if (amount != null) {
            graphics.putText(amount, CURRENCY_WIDTH_PP, y, textFontSize, false);
        } else {
            y -= -textAscender + AMOUNT_BOX_HEIGHT_PP;
            drawCorners(PP_AMOUNT_SECTION_WIDTH + MARGIN - AMOUNT_BOX_WIDTH_PP, y, AMOUNT_BOX_WIDTH_PP, AMOUNT_BOX_HEIGHT_PP);
        }
    }

    private void drawPaymentPartInformationSection() throws IOException {

        graphics.setTransformation(SLIP_WIDTH - PP_INFO_SECTION_WIDTH - MARGIN, 0, 0, 1, 1);
        yPos = SLIP_HEIGHT - MARGIN - labelAscender;

        // account and creditor
        drawLabelAndTextLines(MultilingualText.KEY_ACCOUNT_PAYABLE_TO, accountPayableToLines);

        // reference
        if (reference != null)
            drawLabelAndText(MultilingualText.KEY_REFERENCE, reference);

        // additional information
        if (additionalInfo != null)
            drawLabelAndTextLines(MultilingualText.KEY_ADDITIONAL_INFORMATION, additionalInfoLines);

        // payable by
        if (payableBy != null) {
            drawLabelAndTextLines(MultilingualText.KEY_PAYABLE_BY, payableByLines);
        } else {
            drawLabel(MultilingualText.KEY_PAYABLE_BY_NAME_ADDRESS);
            yPos -= -textAscender + BOX_TOP_PADDING;
            yPos -= DEBTOR_BOX_HEIGHT_PP;
            drawCorners(0, yPos, DEBTOR_BOX_WIDTH_PP, DEBTOR_BOX_HEIGHT_PP);
        }
    }

    private void drawFurtherInformationSection() throws IOException {

        final int FONT_SIZE = 7;
        final int LINE_SPACING = 8;
        final double FURTHER_INFORMATION_SECTION_TOP = 15; // mm

        if (bill.getAlternativeSchemes() == null || bill.getAlternativeSchemes().length == 0)
            return;

        graphics.setTransformation(RECEIPT_WIDTH + MARGIN, 0, 0, 1, 1);
        double y = FURTHER_INFORMATION_SECTION_TOP - graphics.getAscender(FONT_SIZE);
        double maxWidth = PAYMEMT_PART_WIDTH - 2 * MARGIN;

        for (AlternativeScheme scheme : bill.getAlternativeSchemes()) {
            String boldText = String.format("%s: ", scheme.getName());
            double boldTextWidth = graphics.getTextWidth(boldText, FONT_SIZE, true);
            graphics.putText(boldText, 0, y, FONT_SIZE, true);

            String normalText = truncateText(scheme.getInstruction(), maxWidth - boldTextWidth, FONT_SIZE);
            graphics.putText(normalText, boldTextWidth, y, FONT_SIZE, false);
            y -= LINE_SPACING * PT_TO_MM;
        }
    }

    private void drawReceipt() throws IOException {

        // "Receipt" title
        graphics.setTransformation(MARGIN, 0, 0, 1, 1);
        yPos = SLIP_HEIGHT - MARGIN - graphics.getAscender(FONT_SIZE_TITLE);
        graphics.putText(getText(MultilingualText.KEY_RECEIPT), 0, yPos, FONT_SIZE_TITLE, true);

        // information section
        drawReceiptInformationSection();

        /// amount section
        drawReceiptAmountSection();

        // acceptance point
        drawReceiptAcceptancePointSection();
    }

    private void drawReceiptInformationSection() throws IOException {

        final double TITLE_HEIGHT = 7; // mm

        // payable to
        yPos = SLIP_HEIGHT - MARGIN - TITLE_HEIGHT - labelAscender;
        drawLabelAndTextLines(MultilingualText.KEY_ACCOUNT_PAYABLE_TO, accountPayableToLines);

        // reference
        if (reference != null)
            drawLabelAndText(MultilingualText.KEY_REFERENCE, reference);

        // payable by
        if (payableBy != null) {
            drawLabelAndTextLines(MultilingualText.KEY_PAYABLE_BY, payableByLines);
        } else {
            drawLabel(MultilingualText.KEY_PAYABLE_BY_NAME_ADDRESS);
            yPos -= -textAscender + BOX_TOP_PADDING;
            yPos -= DEBTOR_BOX_HEIGHT_RC;
            drawCorners(0, yPos, DEBTOR_BOX_WIDTH_RC, DEBTOR_BOX_HEIGHT_RC);
        }
    }

    private void drawReceiptAmountSection() throws IOException {

        final double CURRENCY_WIDTH_RC = 12; // mm
        final double AMOUNT_BOX_WIDTH_RC = 30; // mm
        final double AMOUNT_BOX_HEIGHT_RC = 10; // mm

        // currency
        double y = AMOUNT_SECTION_TOP - labelAscender;
        String label = getText(MultilingualText.KEY_CURRENCY);
        graphics.putText(label, 0, y, labelFontSize, true);

        y -= (textFontSize + 3) * PT_TO_MM;
        graphics.putText(bill.getCurrency(), 0, y, textFontSize, false);

        // amount
        y = AMOUNT_SECTION_TOP - labelAscender;
        label = getText(MultilingualText.KEY_AMOUNT);
        graphics.putText(label, CURRENCY_WIDTH_RC, y, labelFontSize, true);

        if (amount != null) {
            y -= (textFontSize + 3) * PT_TO_MM;
            graphics.putText(amount, CURRENCY_WIDTH_RC, y, textFontSize, false);
        } else {
            drawCorners(RECEIPT_TEXT_WIDTH - AMOUNT_BOX_WIDTH_RC,
                    AMOUNT_SECTION_TOP - AMOUNT_BOX_HEIGHT_RC,
                    AMOUNT_BOX_WIDTH_RC, AMOUNT_BOX_HEIGHT_RC);
        }
    }

    private void drawReceiptAcceptancePointSection() throws IOException {

        final double ACCEPTANCE_POINT_SECTION_TOP = 23; // mm (from bottom)

        String label = getText(MultilingualText.KEY_ACCEPTANCE_POINT);
        double y = ACCEPTANCE_POINT_SECTION_TOP - labelAscender;
        double w = graphics.getTextWidth(label, labelFontSize, true);
        graphics.putText(label, RECEIPT_TEXT_WIDTH - w, y, labelFontSize, true);
    }

    private boolean computePaymentPartSpacing() {

        final double PP_INFO_SECTION_MAX_HEIGHT = 85; // mm

        // numExtraLines: the number of lines between text blocks
        int numTextLines = 0;
        int numExtraLines = 0;
        double fixedHeight = 0;

        numTextLines += 1 + accountPayableToLines.length;
        if (reference != null) {
            numExtraLines++;
            numTextLines += 2;
        }
        if (additionalInfo != null) {
            numExtraLines++;
            numTextLines += 1 + additionalInfoLines.length;
        }
        numExtraLines++;
        if (payableBy != null) {
            numTextLines += 1 + payableByLines.length;
        } else {
            numTextLines += 1;
            fixedHeight += DEBTOR_BOX_HEIGHT_PP;
        }

        // extra spacing line if there are alternative schemes
        if (bill.getAlternativeSchemes() != null && bill.getAlternativeSchemes().length > 0)
            numExtraLines++;

        return computeSpacing(PP_INFO_SECTION_MAX_HEIGHT, fixedHeight, numTextLines, numExtraLines);
    }

    private boolean computeReceiptSpacing() {

        final double RECEIPT_MAX_HEIGHT = 56; // mm

        // numExtraLines: the number of lines between text blocks
        int numTextLines = 0;
        int numExtraLines = 0;
        double fixedHeight = 0;

        numTextLines += 1 + accountPayableToLines.length;
        if (reference != null) {
            numExtraLines++;
            numTextLines += 2;
        }
        numExtraLines++;
        if (payableBy != null) {
            numTextLines += 1 + payableByLines.length;
        } else {
            numTextLines += 1;
            fixedHeight += DEBTOR_BOX_HEIGHT_RC;
        }

        numExtraLines++;

        return computeSpacing(RECEIPT_MAX_HEIGHT, fixedHeight, numTextLines, numExtraLines);
    }

    private boolean computeSpacing(double maxHeight, double fixedHeight, int numTextLines, int numExtraLines) {

        lineSpacing = (textFontSize + 1) * PT_TO_MM;
        extraSpacing = (maxHeight - fixedHeight - numTextLines * lineSpacing) / numExtraLines;
        extraSpacing = Math.min(Math.max(extraSpacing, 0), lineSpacing);

        labelAscender = graphics.getAscender(labelFontSize);
        textAscender = graphics.getAscender(textFontSize);

        return extraSpacing / lineSpacing < 0.8;
    }

    private void drawBorder() throws IOException {
        SeparatorType separatorType = bill.getFormat().getSeparatorType();
        OutputSize outputSize = bill.getFormat().getOutputSize();

        if (separatorType == SeparatorType.NONE)
            return;

        graphics.setTransformation(0, 0, 0, 1, 1);

        // draw vertical separator line between receipt and payment part
        graphics.startPath();
        graphics.moveTo(RECEIPT_WIDTH, 0);
        if (separatorType == SeparatorType.SOLID_LINE_WITH_SCISSORS) {
            graphics.lineTo(RECEIPT_WIDTH, SLIP_HEIGHT - 8);
            graphics.moveTo(RECEIPT_WIDTH, SLIP_HEIGHT - 5);
        }
        graphics.lineTo(RECEIPT_WIDTH, SLIP_HEIGHT);

        // draw horizontal separator line between bill and rest of A4 sheet
        if (outputSize != OutputSize.QR_BILL_ONLY) {
            graphics.moveTo(0, SLIP_HEIGHT);
            if (separatorType == SeparatorType.SOLID_LINE_WITH_SCISSORS) {
                graphics.lineTo(5, SLIP_HEIGHT);
                graphics.moveTo(8, SLIP_HEIGHT);
            }
            graphics.lineTo(SLIP_WIDTH, SLIP_HEIGHT);
        }
        graphics.strokePath(0.5, 0);

        // draw scissors
        if (separatorType == SeparatorType.SOLID_LINE_WITH_SCISSORS) {
            drawScissors(RECEIPT_WIDTH, SLIP_HEIGHT - 5, 3, 0);
            if (outputSize != OutputSize.QR_BILL_ONLY)
                drawScissors(5, SLIP_HEIGHT, 3, Math.PI / 2.0);
        }
    }

    private void drawScissors(double x, double y, double size, double angle) throws IOException {
        drawScissorsBlade(x, y, size, angle, false);
        drawScissorsBlade(x, y, size, angle, true);
    }

    private void drawScissorsBlade(double x, double y, double size, double angle, boolean mirrored) throws IOException {
        double scale = size / 476.0;
        double xOffset = 0.36 * size;
        double yOffset = -1.05 * size;
        AffineTransform transform = new AffineTransform();
        transform.translate(x, y);
        transform.rotate(angle);
        transform.translate(mirrored ? xOffset : -xOffset, yOffset);
        transform.scale(mirrored ? -scale : scale, scale);
        graphics.setTransformation(transform.getTranslateX(), transform.getTranslateY(), angle, mirrored ? -scale : scale, scale);

        graphics.startPath();
        graphics.moveTo(46.48, 126.784);
        graphics.cubicCurveTo(34.824, 107.544, 28.0, 87.924, 28.0, 59.0);
        graphics.cubicCurveTo(28.0, 36.88, 33.387, 16.436, 42.507, -0.124);
        graphics.lineTo(242.743, 326.63);
        graphics.cubicCurveTo(246.359, 332.53, 254.836, 334.776, 265.31, 328.678);
        graphics.cubicCurveTo(276.973, 321.89, 290.532, 318.0, 305.0, 318.0);
        graphics.cubicCurveTo(348.63, 318.0, 384.0, 353.37, 384.0, 397.0);
        graphics.cubicCurveTo(384.0, 440.63, 348.63, 476.0, 305.0, 476.0);
        graphics.cubicCurveTo(278.066, 476.0, 254.28, 462.521, 240.02, 441.94);
        graphics.lineTo(46.48, 126.785);
        graphics.closeSubpath();
        graphics.moveTo(303.5, 446.0);
        graphics.cubicCurveTo(330.286, 446.0, 352.0, 424.286, 352.0, 397.5);
        graphics.cubicCurveTo(352.0, 370.714, 330.286, 349.0, 303.5, 349.0);
        graphics.cubicCurveTo(276.714, 349.0, 255.0, 370.714, 255.0, 397.5);
        graphics.cubicCurveTo(255.0, 424.286, 276.714, 446.0, 303.5, 446.0);
        graphics.closeSubpath();
        graphics.fillPath(0);
    }

    // Draws a label at (0, yPos) and advances vertically.
    // yPos is taken as the baseline for the text.
    private void drawLabel(String labelKey) throws IOException {
        graphics.putText(getText(labelKey), 0, yPos, labelFontSize, true);
        yPos -= lineSpacing;
    }

    // Draws a label and a single line of text at (0, yPos) and advances vertically.
    // yPos is taken as the baseline for the text.
    private void drawLabelAndText(String labelKey, String text) throws IOException {
        drawLabel(labelKey);
        graphics.putText(text, 0, yPos, textFontSize, false);
        yPos -= lineSpacing + extraSpacing;
    }

    // Draws a label and a multiple lines of text at (0, yPos) and advances vertically.
    // yPos is taken as the baseline for the text.
    private void drawLabelAndTextLines(String labelKey, String[] textLines) throws IOException {
        drawLabel(labelKey);
        double leading = lineSpacing - graphics.getLineHeight(textFontSize);
        graphics.putTextLines(textLines, 0, yPos, textFontSize, leading);
        yPos -= textLines.length * lineSpacing + extraSpacing;
    }

    // Prepare the formatted text
    private void prepareText() {
        String account = Payments.formatIBAN(bill.getAccount());
        accountPayableTo = account + "\n" + formatPersonForDisplay(bill.getCreditor());

        reference = formatReferenceNumber(bill.getReference());

        String info = bill.getUnstructuredMessage();
        if (bill.getBillInformation() != null) {
            if (info == null)
                info = bill.getBillInformation();
            else
                info = info + "\n" + bill.getBillInformation();
        }
        if (info != null)
            additionalInfo = info;

        if (bill.getDebtor() != null)
            payableBy = formatPersonForDisplay(bill.getDebtor());

        if (bill.getAmount() != null)
            amount = formatAmountForDisplay(bill.getAmount());
    }

    private void prepareReducedReceiptText(boolean reduceBoth) {
        if (reduceBoth) {
            String account = Payments.formatIBAN(bill.getAccount());
            accountPayableTo = account + "\n" + formatPersonForDisplay(createReducedAddress(bill.getCreditor()));
        }

        if (bill.getDebtor() != null)
            payableBy = formatPersonForDisplay(createReducedAddress(bill.getDebtor()));
    }

    private Address createReducedAddress(Address address) {
        // Address without street / house number
        Address reducedAddress = new Address();
        reducedAddress.setName(address.getName());
        reducedAddress.setCountryCode(address.getCountryCode());
        if (address.getType() == Address.Type.STRUCTURED) {
            reducedAddress.setPostalCode(address.getPostalCode());
            reducedAddress.setTown(address.getTown());
        } else if (address.getType() == Address.Type.COMBINED_ELEMENTS) {
            reducedAddress.setAddressLine2(address.getAddressLine2());
        }

        return reducedAddress;
    }

    // Prepare the text (by breaking it into lines where necessary)
    private void breakLines(double maxWidth) {
        accountPayableToLines = graphics.splitLines(accountPayableTo, maxWidth * MM_TO_PT, textFontSize);
        if (additionalInfo != null)
            additionalInfoLines = graphics.splitLines(additionalInfo, maxWidth * MM_TO_PT, textFontSize);
        if (payableBy != null)
            payableByLines = graphics.splitLines(payableBy, maxWidth * MM_TO_PT, textFontSize);
    }


    private static final double CORNER_STROKE_WIDTH = 0.75;

    private void drawCorners(double x, double y, double width, double height) throws IOException {
        final double lwh = CORNER_STROKE_WIDTH * 0.5 / 72 * 25.4;
        final double s = 3;

        graphics.startPath();

        graphics.moveTo(x + lwh, y + s);
        graphics.lineTo(x + lwh, y + lwh);
        graphics.lineTo(x + s, y + lwh);

        graphics.moveTo(x + width - s, y + lwh);
        graphics.lineTo(x + width - lwh, y + lwh);
        graphics.lineTo(x + width - lwh, y + s);

        graphics.moveTo(x + width - lwh, y + height - s);
        graphics.lineTo(x + width - lwh, y + height - lwh);
        graphics.lineTo(x + width - s, y + height - lwh);

        graphics.moveTo(x + s, y + height - lwh);
        graphics.lineTo(x + lwh, y + height - lwh);
        graphics.lineTo(x + lwh, y + height - s);

        graphics.strokePath(CORNER_STROKE_WIDTH, 0);
    }

    private static final DecimalFormat amountDisplayFormat;

    static {
        amountDisplayFormat = new DecimalFormat("###,##0.00");
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        symbols.setDecimalSeparator('.');
        symbols.setGroupingSeparator(' ');
        amountDisplayFormat.setDecimalFormatSymbols(symbols);
    }

    private static String formatAmountForDisplay(BigDecimal amount) {
        return amountDisplayFormat.format(amount);
    }

    private static String formatPersonForDisplay(Address address) {
        StringBuilder sb = new StringBuilder();
        sb.append(address.getName());

        if (address.getType() == Address.Type.STRUCTURED) {
            String street = address.getStreet();
            if (street != null) {
                sb.append("\n");
                sb.append(street);
            }
            String houseNo = address.getHouseNo();
            if (houseNo != null) {
                sb.append(street != null ? " " : "\n");
                sb.append(houseNo);
            }
            sb.append("\n");
            if (!"CH".equals(address.getCountryCode()) && !"LI".equals(address.getCountryCode())) {
                sb.append(address.getCountryCode());
                sb.append(" - ");
            }
            sb.append(address.getPostalCode());
            sb.append(" ");
            sb.append(address.getTown());

        } else if (address.getType() == Address.Type.COMBINED_ELEMENTS) {
            if (address.getAddressLine1() != null) {
                sb.append("\n");
                sb.append(address.getAddressLine1());
            }
            sb.append("\n");
            if (!"CH".equals(address.getCountryCode()) && !"LI".equals(address.getCountryCode())) {
                sb.append(address.getCountryCode());
                sb.append(" ");
            }
            sb.append(address.getAddressLine2());
        }
        return sb.toString();
    }

    private static String formatReferenceNumber(String refNo) {
        if (refNo == null)
            return null;
        refNo = refNo.trim();
        int len = refNo.length();
        if (len == 0)
            return null;
        if (refNo.startsWith("RF"))
            // same format as IBAN
            return Payments.formatIBAN(refNo);

        return Payments.formatQRReferenceNumber(refNo);
    }

    private String truncateText(String text, double maxWidth, int fontSize) {

        final double ELLIPSIS_WIDTH = 0.3528; // mm * font size

        if (graphics.getTextWidth(text, fontSize, false) < maxWidth)
            return text;

        String[] lines = graphics.splitLines(text, maxWidth - fontSize * ELLIPSIS_WIDTH, fontSize);
        return lines[0] + "…";
    }

    private String getText(String textKey) {
        return MultilingualText.getText(textKey, bill.getFormat().getLanguage());
    }
}
