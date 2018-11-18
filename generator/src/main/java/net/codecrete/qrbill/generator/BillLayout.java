//
// Swiss QR Bill Generator
// Copyright (c) 2018 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
package net.codecrete.qrbill.generator;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import net.codecrete.qrbill.canvas.Canvas;
import net.codecrete.qrbill.canvas.FontMetrics;

/**
 * Layouting and drawing of QR bill payment slip
 */
class BillLayout {

    private static final double PT_TO_MM = 25.4 / 72;
    private static final double MM_TO_PT = 72 / 25.4;
    private static final int FONT_SIZE_TITLE = 11; // pt
    private static final int PP_LABEL_PREF_FONT_SIZE = 8; // pt
    private static final int PP_TEXT_PREF_FONT_SIZE = 10; // pt
    private static final int RC_LABEL_PREF_FONT_SIZE = 6; // pt
    private static final int RC_TEXT_PREF_FONT_SIZE = 8; // pt
    private static final double SLIP_WIDTH = 210.22; // mm
    private static final double SLIP_HEIGHT = 105.11; // mm
    private static final double PAYMEMT_PART_WIDTH = 148.65; // mm
    private static final double RECEIPT_WIDTH = SLIP_WIDTH - PAYMEMT_PART_WIDTH; // mm
    private static final double MARGIN = 5; // mm
    private static final double QR_CODE_SIZE = QRCode.SIZE; // 46 mm
    private static final double INFO_SECTION_WIDTH = 81; // mm (must not be smaller than 65)
    private static final double CURRENCY_AMOUNT_BASE_LINE = 32; // mm (from to bottom)
    private static final double CURRENCY_WIDTH_PP = 15; // mm
    private static final double CURRENCY_WIDTH_RC = 13; // mm
    private static final double AMOUNT_BOX_WIDTH_PP = 40; // mm
    private static final double AMOUNT_BOX_HEIGHT_PP = 15; // mm
    private static final double AMOUNT_BOX_WIDTH_RC = 30; // mm
    private static final double AMOUNT_BOX_HEIGHT_RC = 10; // mm
    private static final double DEBTOR_BOX_HEIGHT_PP = 25; // mm (must not be smaller than 25)
    private static final double DEBTOR_BOX_HEIGHT_RC = 25; // mm (must not be smaller than 25)
    private static final double PREFERRED_LEADING = 0.2; // relative to font size

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
    private int textFontSize;
    private double labelLeading;
    private double textLeading;
    private double textBottomPadding;

    BillLayout(Bill bill, Canvas graphics) {
        this.bill = bill;
        this.qrCode = new QRCode(bill);
        this.graphics = graphics;
    }

    void draw() throws IOException {

        prepareText();

        // payment part

        labelFontSize = PP_LABEL_PREF_FONT_SIZE;
        textFontSize = PP_TEXT_PREF_FONT_SIZE;

        breakLines(INFO_SECTION_WIDTH);

        textLeading = PREFERRED_LEADING * textFontSize * PT_TO_MM;
        labelLeading = textLeading + FontMetrics.getDescender(textFontSize) - FontMetrics.getDescender(labelFontSize);
        textBottomPadding = textFontSize * 0.5 * PT_TO_MM;

        drawPaymentPart(0, 0);

        // receipt

        labelFontSize = RC_LABEL_PREF_FONT_SIZE;
        textFontSize = RC_TEXT_PREF_FONT_SIZE;

        breakLines(RECEIPT_WIDTH - 2 * MARGIN);

        textLeading = PREFERRED_LEADING * textFontSize * PT_TO_MM;
        labelLeading = textLeading + FontMetrics.getDescender(textFontSize) - FontMetrics.getDescender(labelFontSize);
        textBottomPadding = textFontSize * 0.5 * PT_TO_MM;

        drawReceipt(0, 0);

        // border
        if (true)
            drawBorder(0, 0);
    }

    private void drawPaymentPart(double offsetX, double offsetY) throws IOException {

        // QR code section
        qrCode.draw(graphics, offsetX + RECEIPT_WIDTH + MARGIN, offsetY + SLIP_HEIGHT - 17 - QR_CODE_SIZE);

        // "Payment part" title
        graphics.setTransformation(offsetX + RECEIPT_WIDTH + MARGIN, offsetY, 1, 1);
        yPos = SLIP_HEIGHT - MARGIN - FontMetrics.getAscender(FONT_SIZE_TITLE);
        graphics.putText(MultilingualText.getText(MultilingualText.KEY_PAYMENT_PART, bill.getLanguage()), 0,
                yPos, FONT_SIZE_TITLE, true);

        // currency
        yPos = CURRENCY_AMOUNT_BASE_LINE + FontMetrics.getAscender(labelFontSize);
        drawLabelAndText(MultilingualText.KEY_CURRENCY, bill.getCurrency());

        // amount
        graphics.setTransformation(offsetX + RECEIPT_WIDTH + MARGIN + CURRENCY_WIDTH_PP, offsetY, 1, 1);
        yPos = CURRENCY_AMOUNT_BASE_LINE + FontMetrics.getAscender(labelFontSize);
        if (amount != null) {
            drawLabelAndText(MultilingualText.KEY_AMOUNT, amount);
        } else {
            drawLabel(MultilingualText.KEY_AMOUNT);
            drawCorners(0, yPos - AMOUNT_BOX_HEIGHT_PP, AMOUNT_BOX_WIDTH_PP, AMOUNT_BOX_HEIGHT_PP);
        }

        // right column
        graphics.setTransformation(offsetX + SLIP_WIDTH - INFO_SECTION_WIDTH - MARGIN, offsetY, 1, 1);
        yPos = SLIP_HEIGHT - MARGIN;

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
            yPos -= DEBTOR_BOX_HEIGHT_PP;
            drawCorners(0, yPos, INFO_SECTION_WIDTH, DEBTOR_BOX_HEIGHT_PP);
        }
    }

    private void drawReceipt(double offsetX, double offsetY) throws  IOException {

        // "Receipt" title
        graphics.setTransformation(offsetX + MARGIN, offsetY, 1, 1);
        yPos = SLIP_HEIGHT - MARGIN - FontMetrics.getAscender(FONT_SIZE_TITLE);
        graphics.putText(MultilingualText.getText(MultilingualText.KEY_RECEIPT, bill.getLanguage()), 0,
                yPos, FONT_SIZE_TITLE, true);

        // account and creditor
        yPos = SLIP_HEIGHT - MARGIN - 7;
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
            yPos -= DEBTOR_BOX_HEIGHT_RC;
            drawCorners(0, yPos, RECEIPT_WIDTH - 2 * MARGIN, DEBTOR_BOX_HEIGHT_RC);
        }

        // currency
        yPos = CURRENCY_AMOUNT_BASE_LINE + FontMetrics.getAscender(labelFontSize);
        drawLabelAndText(MultilingualText.KEY_CURRENCY, bill.getCurrency());

        // amount
        graphics.setTransformation(offsetX + MARGIN + CURRENCY_WIDTH_RC, offsetY, 1, 1);
        yPos = CURRENCY_AMOUNT_BASE_LINE + FontMetrics.getAscender(labelFontSize);
        if (amount != null) {
            drawLabelAndText(MultilingualText.KEY_AMOUNT, amount);
        } else {
            drawLabel(MultilingualText.KEY_AMOUNT);
            graphics.setTransformation(offsetX, offsetY, 1, 1);
            drawCorners(RECEIPT_WIDTH - MARGIN - AMOUNT_BOX_WIDTH_RC,
                    CURRENCY_AMOUNT_BASE_LINE + 2 - AMOUNT_BOX_HEIGHT_RC,
                    AMOUNT_BOX_WIDTH_RC, AMOUNT_BOX_HEIGHT_RC);
        }

        // acceptance point
        graphics.setTransformation(offsetX, offsetY, 1, 1);
        String label = MultilingualText.getText(MultilingualText.KEY_ACCEPTANCE_POINT, bill.getLanguage());
        double w = FontMetrics.getTextWidth(label, labelFontSize) * 1.05; // TODO: proper text width for bold font
        graphics.putText(label, RECEIPT_WIDTH - MARGIN - w, 21, labelFontSize, true);
    }

    private void drawBorder(double offsetX, double offsetY) throws IOException {
		graphics.setTransformation(offsetX, offsetY, 1, 1);
		graphics.startPath();
		graphics.moveTo(RECEIPT_WIDTH, 0);
//		graphics.lineTo(0, SLIP_HEIGHT - 16);
//		graphics.moveTo(0, SLIP_HEIGHT - 9);
		graphics.lineTo(RECEIPT_WIDTH, SLIP_HEIGHT);
		graphics.moveTo(0, SLIP_HEIGHT);
		graphics.lineTo(SLIP_WIDTH, SLIP_HEIGHT);
        graphics.strokePath(0.5, 0);
        
//        drawScissors(offsetX, offsetY + SLIP_HEIGHT - 16, 6);
    }

    /*
    private void drawScissors(double x, double y, double size) throws IOException {
        drawScissorsBlade(x, y, size, false);
        drawScissorsBlade(x, y, size, true);
    }

    private void drawScissorsBlade(double x, double y, double size, boolean mirrored) throws IOException {
        graphics.setTransformation(x + size * (mirrored ? 0.37 : -0.37), y, (mirrored ? -size : size) / 476.0, size / 476.0);
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
    */

    // Draws a label at (0, yPos) and advances vertically
    private void drawLabel(String labelKey) throws IOException {
        yPos -= FontMetrics.getAscender(labelFontSize);
        graphics.putText(MultilingualText.getText(labelKey, bill.getLanguage()), 0, yPos, labelFontSize, true);
        yPos -= FontMetrics.getDescender(labelFontSize) + labelLeading;
    }

    // Draws a label and a single line of text at (0, yPos) and advances vertically
    private void drawLabelAndText(String labelKey, String text) throws IOException {
        drawLabel(labelKey);
        yPos -= FontMetrics.getAscender(textFontSize);
        graphics.putText(text, 0, yPos, textFontSize, false);
        yPos -= FontMetrics.getDescender(textFontSize) + textLeading + textBottomPadding;
    }

    // Draws a label and a multiple lines of text at (0, yPos) and advances
    // vertically
    private void drawLabelAndTextLines(String labelKey, String[] textLines) throws IOException {
        drawLabel(labelKey);
        yPos -= FontMetrics.getAscender(textFontSize);
        graphics.putTextLines(textLines, 0, yPos, textFontSize, textLeading);
        yPos -= textLines.length * (FontMetrics.getLineHeight(textFontSize) + textLeading)
                - FontMetrics.getAscender(textFontSize) + textBottomPadding;
    }

    // Prepare the formatted text
    private void prepareText() {
        String account = Payments.formatIBAN(bill.getAccount());
        accountPayableTo = account + "\n" + formatPersonForDisplay(bill.getCreditor());

        reference = formatReferenceNumber(bill.getReferenceNo());

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

    // Prepare the text (by breaking it into lines where necessary)
    private void breakLines(double maxWidth) {
        accountPayableToLines = FontMetrics.splitLines(accountPayableTo, maxWidth * MM_TO_PT, textFontSize);
        if (additionalInfo != null)
            additionalInfoLines = FontMetrics.splitLines(additionalInfo, maxWidth * MM_TO_PT, textFontSize);
        if (payableBy != null)
            payableByLines = FontMetrics.splitLines(payableBy, maxWidth * MM_TO_PT,textFontSize);
    }

    /*
    // Compute the padding and leading for the given font size
    private double computeSpacing() {
        int numLabels = 3;
        int numTextLines = 1;

        numTextLines += creditor.length;
        if (refNo != null) {
            numLabels++;
            numTextLines += 1;
        }
        if (ppAdditionalInfo != null) {
            numLabels++;
            numTextLines += ppAdditionalInfo.length;
        }
        if (debtor != null) {
            numTextLines += debtor.length;
        }

        final int numExtraLines = debtor != null ? numTextLines - numLabels : (numTextLines + 1) - numLabels;
        final double preferredTextLeading = PREFERRED_LEADING * fontSizeText * PT_TO_MM;

        double heightWithoutSpacing = numLabels * FontMetrics.getLineHeight(fontSizeLabel)
                + numTextLines * FontMetrics.getLineHeight(fontSizeText) + (debtor == null ? DEBTOR_HEIGHT : 0);
        double uncompressedSpacing = (numLabels - 1) * PREFERRED_LABEL_PADDING_TOP
                + numLabels * PREFERRED_TEXT_PADDING_TOP + numExtraLines * preferredTextLeading;

        double regularHeight = heightWithoutSpacing + uncompressedSpacing;
        if (regularHeight <= SLIP_HEIGHT - 2 * VERT_BORDER) {
            // text fits without compressed spacing
            labelPaddingTop = PREFERRED_LABEL_PADDING_TOP;
            textPaddingTop = PREFERRED_TEXT_PADDING_TOP;
            textLeading = preferredTextLeading;

            double titleHeight = FontMetrics.getLineHeight(FONT_SIZE_TITLE) + labelPaddingTop;
            if (regularHeight <= SLIP_HEIGHT - 2 * VERT_BORDER - titleHeight) {
                // align right column with "Supports" line
                rightColumnPaddingTop = titleHeight;
            } else {
                // align right column at the top
                rightColumnPaddingTop = 0;
            }
            return 1;
        }

        // compressed spacing
        double remainingSpacing = SLIP_HEIGHT - 2 * VERT_BORDER - heightWithoutSpacing;
        double factor = remainingSpacing / uncompressedSpacing;
        labelPaddingTop = factor * PREFERRED_LABEL_PADDING_TOP;
        textPaddingTop = factor * PREFERRED_TEXT_PADDING_TOP;
        textLeading = factor * preferredTextLeading;
        return factor;
    }
    */

    private void drawCorners(double x, double y, double width, double height) throws IOException {
        final double lwh = 0.5 / 72 * 25.4;
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

        graphics.strokePath(1, 0);
    }

    private static final DecimalFormat amountDisplayFormat;

    static {
        amountDisplayFormat = new DecimalFormat("###,##0.00");
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        symbols.setDecimalSeparator('.');
        symbols.setGroupingSeparator(' ');
        amountDisplayFormat.setDecimalFormatSymbols(symbols);
    }

    private static String formatAmountForDisplay(double amount) {
        return amountDisplayFormat.format(amount);
    }

    private static String formatPersonForDisplay(Address address) {
        StringBuilder sb = new StringBuilder();
        sb.append(address.getName());
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
        sb.append(address.getCountryCode());
        sb.append("-");
        sb.append(address.getPostalCode());
        sb.append(" ");
        sb.append(address.getTown());
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
}
