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
class PaymentPartLayout {

    private static final double PT_TO_MM = 25.4 / 72;
    private static final double MM_TO_PT = 72 / 25.4;
    private static final int FONT_SIZE_TITLE = 11; // pt
    private static final int FONT_SIZE_LABEL = 8; // pt
    private static final int FONT_SIZE_TEXT = 10; // pt
    private static final double SLIP_WIDTH = 210.22; // mm
    private static final double SLIP_HEIGHT = 105.11; // mm
    private static final double PAYMEMT_PART_WIDTH = 148.65; // mm
    private static final double RECEIPT_WIDTH = SLIP_WIDTH - PAYMEMT_PART_WIDTH; // mm
    private static final double MARGIN = 5; // mm
    private static final double QR_CODE_SIZE = 46; // mm
    private static final double INFO_SECTION_WIDTH = 81; // mm
    private static final double CURRENCY_WIDTH = 15; // mm
    private static final double LEFT_COLUMN_WIDTH = 56; // mm
    private static final double AMOUNT_WIDTH = 40; // mm (must not be smaller than 40)
    private static final double AMOUNT_HEIGHT = 15; // mm (must not be smaller than 15)
    //private static final double RIGHT_COLUMN_WIDTH = SLIP_WIDTH - 2 * HORIZ_BORDER - MIDDLE_SPACING - LEFT_COLUMN_WIDTH; // mm
    // (must not be smaller than 65)
    private static final double DEBTOR_HEIGHT = 25; // mm (must not be smaller than 25)
    private static final double PREFERRED_LABEL_PADDING_TOP = 8 * PT_TO_MM;
    private static final double PREFERRED_TEXT_PADDING_TOP = 5 * PT_TO_MM;
    private static final double PREFERRED_LEADING = 0.2; // relative to font size

    private Bill bill;
    private QRCode qrCode;
    private Canvas graphics;

    private String[] account_payable_to;
    private String reference;
    private String[] additionalInfo;
    private String[] payable_by;

    private double yPos;
    private double labelPaddingTop;
    private double textPaddingTop;
    private double textLeading;
    private int fontSizeLabel;
    private int fontSizeText;
    private double rightColumnPaddingTop;

    PaymentPartLayout(Bill bill, Canvas graphics) {
        this.bill = bill;
        this.qrCode = new QRCode(bill);
        this.graphics = graphics;
    }

    void draw() throws IOException {

        // test regular font size
        fontSizeLabel = FONT_SIZE_LABEL;
        fontSizeText = FONT_SIZE_TEXT;
        prepareRightColumnText();

        /*
        double factor = computeSpacing();

        if (factor < 0.6) {
            // go to smaller font size
            fontSizeLabel = FONT_SIZE_LABEL - 1;
            fontSizeText = FONT_SIZE_TEXT - 1;
            prepareRightColumnText();
            computeSpacing();
        }
        */
        labelPaddingTop = PREFERRED_LABEL_PADDING_TOP;
        textPaddingTop = PREFERRED_TEXT_PADDING_TOP;
        textLeading = PREFERRED_LEADING * fontSizeText * PT_TO_MM;

        // border
        if (true)
            drawBorder(0, 0);

        drawPaymenPart(0, 0);

        drawReceipt(0, 0);
    }

    private void drawPaymenPart(double offsetX, double offsetY) throws IOException
    {
        // left column
        graphics.setTransformation(offsetX + RECEIPT_WIDTH + MARGIN, offsetY, 1, 1);
        yPos = SLIP_HEIGHT - MARGIN - FontMetrics.getAscender(FONT_SIZE_TITLE);

        // title section
        graphics.putText(MultilingualText.getText(MultilingualText.KEY_PAYMENT_PART, bill.getLanguage()), 0,
                yPos, FONT_SIZE_TITLE, true);

        // QR code section
        yPos = SLIP_HEIGHT - 17 - QR_CODE_SIZE;
        qrCode.draw(graphics, offsetX + RECEIPT_WIDTH + MARGIN, offsetY + yPos);

        // currency
        graphics.setTransformation(offsetX + RECEIPT_WIDTH + MARGIN, offsetY, 1, 1);
        yPos = SLIP_HEIGHT - 70;
        drawLabelAndText(MultilingualText.KEY_CURRENCY, bill.getCurrency());

        // amount
        graphics.setTransformation(offsetX + RECEIPT_WIDTH + MARGIN + CURRENCY_WIDTH, offsetY, 1, 1);
        yPos = SLIP_HEIGHT - 70;
        if (bill.getAmount() != null) {
            drawLabelAndText(MultilingualText.KEY_AMOUNT, formatAmountForDisplay(bill.getAmount()));
        } else {
            drawLabel(MultilingualText.KEY_AMOUNT);
            yPos -= textPaddingTop;
            drawCorners(0, yPos - AMOUNT_HEIGHT, AMOUNT_WIDTH, AMOUNT_HEIGHT);
        }

        // right column
        graphics.setTransformation(offsetX + SLIP_WIDTH - INFO_SECTION_WIDTH - MARGIN, offsetY, 1, 1);
        yPos = SLIP_HEIGHT - MARGIN + labelPaddingTop;

        // account and creditor
        drawLabelAndTextLines(MultilingualText.KEY_ACCOUNT_PAYABLE_TO, account_payable_to);

        // reference
        if (reference != null)
            drawLabelAndText(MultilingualText.KEY_REFERENCE, reference);

        // additional information
        if (additionalInfo != null)
            drawLabelAndTextLines(MultilingualText.KEY_ADDITIONAL_INFORMATION, additionalInfo);

        // payable by
        if (payable_by != null) {
            drawLabelAndTextLines(MultilingualText.KEY_PAYABLE_BY, payable_by);
        } else {
            drawLabel(MultilingualText.KEY_PAYABLE_BY_NAME_ADDRESS);
            yPos -= textPaddingTop + DEBTOR_HEIGHT;
            drawCorners(0, yPos, INFO_SECTION_WIDTH, DEBTOR_HEIGHT);
        }
        /*
        double lowerTextHeight = VERT_BORDER + AMOUNT_HEIGHT + textPaddingTop
                + FontMetrics.getLineHeight(fontSizeLabel);
        yPos = lowerTextHeight + labelPaddingTop;

        // amount
        graphics.setTransformation(offsetX + HORIZ_BORDER + LEFT_COLUMN_WIDTH - AMOUNT_WIDTH, offsetY, 1, 1);
        yPos = lowerTextHeight + labelPaddingTop;



        yPos -= FontMetrics.getDescender(FONT_SIZE_TITLE);
        double upperTextHeight = VERT_BORDER + FontMetrics.getLineHeight(FONT_SIZE_TITLE) + labelPaddingTop
                + FontMetrics.getLineHeight(fontSizeLabel) + textPaddingTop + FontMetrics.getLineHeight(fontSizeText);


        // information section
        graphics.setTransformation(offsetX + HORIZ_BORDER + LEFT_COLUMN_WIDTH + MIDDLE_SPACING, offsetY, 1, 1);
        yPos = SLIP_HEIGHT - VERT_BORDER - rightColumnPaddingTop + labelPaddingTop;

        // account
        drawLabelAndText(MultilingualText.KEY_ACCOUNT, account);

        // creditor
        drawLabelAndTextLines(MultilingualText.KEY_CREDITOR, creditor);

        // reference number
        if (refNo != null)
            drawLabelAndText(MultilingualText.KEY_REFERENCE_NUMBER, refNo);

        // additional information
        if (additionalInfo != null)
            drawLabelAndTextLines(MultilingualText.KEY_ADDITIONAL_INFORMATION, additionalInfo);

        // debtor
        if (debtor != null) {
            drawLabelAndTextLines(MultilingualText.KEY_DEBTOR, debtor);
        } else {
            drawLabel(MultilingualText.KEY_DEBTOR);
            yPos -= textPaddingTop + DEBTOR_HEIGHT;
            drawCorners(0, yPos, RIGHT_COLUMN_WIDTH, DEBTOR_HEIGHT);
        }

        // due date
        if (dueDate != null)
            drawLabelAndText(MultilingualText.KEY_DUE_DATE, dueDate);
        */
    }

    private void drawReceipt(double offsetX, double offsetY) throws  IOException {

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
        yPos -= labelPaddingTop + FontMetrics.getAscender(fontSizeLabel);
        graphics.putText(MultilingualText.getText(labelKey, bill.getLanguage()), 0, yPos, fontSizeLabel, true);
        yPos -= FontMetrics.getDescender(fontSizeLabel);
    }

    // Draws a label and a single line of text at (0, yPos) and advances vertically
    private void drawLabelAndText(String labelKey, String text) throws IOException {
        drawLabel(labelKey);
        yPos -= textPaddingTop + FontMetrics.getAscender(fontSizeText);
        graphics.putText(text, 0, yPos, fontSizeText, false);
        yPos -= FontMetrics.getDescender(fontSizeText);
    }

    // Draws a label and a multiple lines of text at (0, yPos) and advances
    // vertically
    private void drawLabelAndTextLines(String labelKey, String[] textLines) throws IOException {
        drawLabel(labelKey);
        yPos -= textPaddingTop + FontMetrics.getAscender(fontSizeText);
        graphics.putTextLines(textLines, 0, yPos, fontSizeText, textLeading);
        yPos -= FontMetrics.getDescender(fontSizeText)
                + (textLines.length - 1) * (FontMetrics.getLineHeight(fontSizeText) + textLeading);
    }

    // Prepare the text in the right column (mainly formatting and line breaking)
    private void prepareRightColumnText() {
        String account = Payments.formatIBAN(bill.getAccount());
        String text = account + "\n" + formatPersonForDisplay(bill.getCreditor());
        account_payable_to = FontMetrics.splitLines(text, INFO_SECTION_WIDTH * MM_TO_PT, fontSizeText);
        reference = formatReferenceNumber(bill.getReferenceNo());
        additionalInfo = null;
        String info = bill.getUnstructuredMessage();
        if (bill.getBillInformation() != null) {
            if (info == null)
                info = bill.getBillInformation();
            else
                info = info + "\n" + bill.getBillInformation();
        }
        if (info != null)
            additionalInfo = FontMetrics.splitLines(info, INFO_SECTION_WIDTH * MM_TO_PT, fontSizeText);
        payable_by = null;
        if (bill.getDebtor() != null)
            payable_by = FontMetrics.splitLines(formatPersonForDisplay(bill.getDebtor()), INFO_SECTION_WIDTH * MM_TO_PT,
                    fontSizeText);
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
        if (additionalInfo != null) {
            numLabels++;
            numTextLines += additionalInfo.length;
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
