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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import net.codecrete.qrbill.canvas.Canvas;
import net.codecrete.qrbill.canvas.FontMetrics;


/**
 * Layouting and drawing of QR bill payment slip
 */
class PaymentSlipLayout {

    private static final double PT_TO_MM = 25.4 / 72;
    private static final double MM_TO_PT = 72 / 25.4;
    private static final int FONT_SIZE_TITLE = 11; // pt
    private static final int FONT_SIZE_LABEL = 8; // pt
    private static final int FONT_SIZE_TEXT = 10; // pt
    private static final double SLIP_WIDTH = 148.5; // mm
    private static final double SLIP_HEIGHT = 105; // mm
    private static final double HORIZ_BORDER = 8; // mm
    private static final double VERT_BORDER = 8; // mm
    private static final double MIDDLE_SPACING = 5; // mm
    private static final double LEFT_COLUMN_WIDTH = 56; // mm
    private static final double AMOUNT_WIDTH = 40; // mm (must not be smaller than 40)
    private static final double AMOUNT_HEIGHT = 15; // mm (must not be smaller than 15)
    private static final double RIGHT_COLUMN_WIDTH
            = SLIP_WIDTH - 2 * HORIZ_BORDER - MIDDLE_SPACING - LEFT_COLUMN_WIDTH; // mm (must not be smaller than 65)
    private static final double DEBTOR_HEIGHT = 25; // mm (must not be smaller than 25)
    private static final double PREFERRED_LABEL_PADDING_TOP = 8 * PT_TO_MM;
    private static final double PREFERRED_TEXT_PADDING_TOP = 5 * PT_TO_MM;
    private static final double PREFERRED_LEADING = 0.2; // relative to font size


    private Bill bill;
    private QRCode qrCode;
    private Canvas graphics;

    private String account;
    private String[] creditor;
    private String[] finalCreditor;
    private String refNo;
    private String[] additionalInfo;
    private String[] debtor;
    private String dueDate;

    private double yPos;
    private double labelPaddingTop;
    private double textPaddingTop;
    private double textLeading;
    private int fontSizeLabel;
    private int fontSizeText;
    private double rightColumnPaddingTop;


    PaymentSlipLayout(Bill bill, Canvas graphics) {
        this.bill = bill;
        this.qrCode = new QRCode(bill);
        this.graphics = graphics;
    }


    void draw(double offsetX, double offsetY, boolean hasBorder) throws IOException {

        /*
            Intro to layout:
            - Main title, labels and remaining text use separate font weight and size.
            - Depending on the text that needs to fit:
              - spacing is reduced
              - if spacing reduction is not sufficient, font size is reduced
              - even with smaller font size, spacing might still need to be reduced
            - There is spacing is above labels (labelPaddingTop), spacing above text (textPaddingTop)
              and leading between lines of multi-line text blocks (creditors and debitors mainly).
            - If sufficient space is available (few text lines), then the right column starts at
              the same vertical position as the "Supports" label in the left column. Otherwise it
              starts at the top (same as main title).
            - In the left column, the title and the first label/text part is aligned at the top
              and the currency and amount are aligned at the bottom. The QR code is then vertically
              centered in-between.
         */

        // test regular font size
        fontSizeLabel = FONT_SIZE_LABEL;
        fontSizeText = FONT_SIZE_TEXT;
        prepareRightColumnText();
        double factor = computeSpacing();

        if (factor < 0.6) {
            // go to smaller font size
            fontSizeLabel = FONT_SIZE_LABEL - 1;
            fontSizeText = FONT_SIZE_TEXT - 1;
            prepareRightColumnText();
            computeSpacing();
        }

        // border
        if (hasBorder) {
            graphics.setTransformation(offsetX, offsetY, 1);
            graphics.startPath();
            graphics.moveTo(0, 0);
            graphics.lineTo(0, SLIP_HEIGHT);
            graphics.lineTo(SLIP_WIDTH, SLIP_HEIGHT);
            graphics.strokePath(0.5, 0);
        }

        // title section
        graphics.setTransformation(offsetX + HORIZ_BORDER, offsetY, 1);
        yPos = SLIP_HEIGHT - VERT_BORDER - FontMetrics.getAscender(FONT_SIZE_TITLE);
        graphics.putText(MultilingualText.getText(MultilingualText.KEY_QR_BILL_PAYMENT_PART, bill.getLanguage()),
                0, yPos, FONT_SIZE_TITLE, true);
        yPos -= FontMetrics.getDescender(FONT_SIZE_TITLE);
        double upperTextHeight = VERT_BORDER + FontMetrics.getLineHeight(FONT_SIZE_TITLE)
                + labelPaddingTop + FontMetrics.getLineHeight(fontSizeLabel)
                + textPaddingTop + FontMetrics.getLineHeight(fontSizeText);

        // scheme section
        drawLabelAndText(MultilingualText.KEY_SUPPORTS,
                MultilingualText.getText(MultilingualText.KEY_CREDIT_TRANSFER, bill.getLanguage()));

        // currency
        double lowerTextHeight = VERT_BORDER + AMOUNT_HEIGHT
                + textPaddingTop + FontMetrics.getLineHeight(fontSizeLabel);
        yPos = lowerTextHeight + labelPaddingTop;
        drawLabelAndText(MultilingualText.KEY_CURRENCY, bill.getCurrency());

        // amount
        graphics.setTransformation(offsetX + HORIZ_BORDER + LEFT_COLUMN_WIDTH - AMOUNT_WIDTH, offsetY, 1);
        yPos = lowerTextHeight + labelPaddingTop;
        if (bill.getAmount() != null) {
            drawLabelAndText(MultilingualText.KEY_AMOUNT, formatAmountForDisplay(bill.getAmount()));
        } else {
            drawLabel(MultilingualText.KEY_AMOUNT);
            drawCorners(0, VERT_BORDER, AMOUNT_WIDTH, AMOUNT_HEIGHT);
        }

        // QR code section
        // Vertically center QR code between upper and lower text area
        double qrCodeSpacing = (SLIP_HEIGHT - upperTextHeight - QRCode.SIZE - lowerTextHeight) / 2;
        yPos = lowerTextHeight + qrCodeSpacing;
        qrCode.draw(graphics, offsetX + HORIZ_BORDER, offsetY + yPos);
        graphics.setTransformation(offsetX + HORIZ_BORDER, offsetY, 1); // restore transformation

        // information section
        graphics.setTransformation(offsetX + HORIZ_BORDER + LEFT_COLUMN_WIDTH + MIDDLE_SPACING, offsetY, 1);
        yPos = SLIP_HEIGHT - VERT_BORDER - rightColumnPaddingTop + labelPaddingTop;

        // account
        drawLabelAndText(MultilingualText.KEY_ACCOUNT, account);

        // creditor
        drawLabelAndTextLines(MultilingualText.KEY_CREDITOR, creditor);

        // final creditor
        if (finalCreditor != null)
            drawLabelAndTextLines(MultilingualText.KEY_FINAL_CREDITOR, finalCreditor);

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
    }

    // Draws a label at (0, yPos) and advances vertically
    private void drawLabel(String labelKey) throws IOException {
        yPos -= labelPaddingTop + FontMetrics.getAscender(fontSizeLabel);
        graphics.putText(MultilingualText.getText(labelKey, bill.getLanguage()),0, yPos, fontSizeLabel, true);
        yPos -= FontMetrics.getDescender(fontSizeLabel);
    }

    // Draws a label and a single line of text at (0, yPos) and advances vertically
    private void drawLabelAndText(String labelKey, String text) throws IOException {
        drawLabel(labelKey);
        yPos -= textPaddingTop + FontMetrics.getAscender(fontSizeText);
        graphics.putText(text, 0, yPos, fontSizeText, false);
        yPos -= FontMetrics.getDescender(fontSizeText);
    }

    // Draws a label and a multiple lines of text at (0, yPos) and advances vertically
    private void drawLabelAndTextLines(String labelKey, String[] textLines) throws IOException {
        drawLabel(labelKey);
        yPos -= textPaddingTop + FontMetrics.getAscender(fontSizeText);
        graphics.putTextLines(textLines, 0, yPos, fontSizeText, textLeading);
        yPos -= FontMetrics.getDescender(fontSizeText) + (textLines.length - 1) * (FontMetrics.getLineHeight(fontSizeText) + textLeading);
    }

    // Prepare the text in the right column (mainly formatting and line breaking)
    private void prepareRightColumnText() {
        account = formatIBANForDisplay(bill.getAccount());
        creditor = FontMetrics.splitLines(formatPersonForDisplay(bill.getCreditor()), RIGHT_COLUMN_WIDTH * MM_TO_PT, fontSizeText);
        finalCreditor = null;
        if (bill.getFinalCreditor() != null)
            finalCreditor = FontMetrics.splitLines(formatPersonForDisplay(bill.getFinalCreditor()), RIGHT_COLUMN_WIDTH * MM_TO_PT, fontSizeText);
        refNo = formatReferenceNumber(bill.getReferenceNo());
        additionalInfo = null;
        String info = bill.getAdditionalInfo();
        if (info != null) {
            int p = info.indexOf("##");
            if (p > 0)
                info = info.substring(0, p) + '\n' + info.substring(p);
            additionalInfo = FontMetrics.splitLines(info, RIGHT_COLUMN_WIDTH * MM_TO_PT, fontSizeText);
        }
        debtor = null;
        if (bill.getDebtor() != null)
            debtor = FontMetrics.splitLines(formatPersonForDisplay(bill.getDebtor()), RIGHT_COLUMN_WIDTH * MM_TO_PT, fontSizeText);

        dueDate = null;
        if (bill.getDueDate() != null)
            dueDate = formatDateForDisplay(bill.getDueDate());
    }

    // Compute the padding and leading for the given font size
    private double computeSpacing() {
        int numLabels = 3;
        int numTextLines = 1;

        numTextLines += creditor.length;
        if (finalCreditor != null) {
            numLabels++;
            numTextLines += finalCreditor.length;
        }
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
        if (dueDate != null) {
            numLabels++;
            numTextLines += 1;
        }

        final int numExtraLines = debtor != null ? numTextLines - numLabels : (numTextLines + 1) - numLabels;
        final double preferredTextLeading = PREFERRED_LEADING * fontSizeText * PT_TO_MM;

        double heightWithoutSpacing =
                numLabels * FontMetrics.getLineHeight(fontSizeLabel)
                + numTextLines * FontMetrics.getLineHeight(fontSizeText)
                + (debtor == null ? DEBTOR_HEIGHT : 0);
        double uncompressedSpacing =
                (numLabels - 1) * PREFERRED_LABEL_PADDING_TOP
                + numLabels * PREFERRED_TEXT_PADDING_TOP
                + numExtraLines * preferredTextLeading;

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


    private static DecimalFormat amountDisplayFormat;
    private static DateTimeFormatter dateDisplayFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    static {
        amountDisplayFormat = new DecimalFormat("###,##0.00");
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        symbols.setDecimalSeparator('.');
        symbols.setGroupingSeparator('\'');
        amountDisplayFormat.setDecimalFormatSymbols(symbols);
    }

    private static String formatAmountForDisplay(double amount) {
        return amountDisplayFormat.format(amount);
    }

    private static String formatDateForDisplay(LocalDate date) {
        return date.format(dateDisplayFormat);
    }

    private static String formatIBANForDisplay(String iban) {
        StringBuilder sb = new StringBuilder(25);
        sb.append(iban, 0, 4);
        sb.append(" ");
        sb.append(iban, 4, 8);
        sb.append(" ");
        sb.append(iban, 8, 12);
        sb.append(" ");
        sb.append(iban, 12, 16);
        sb.append(" ");
        sb.append(iban, 16, 21);
        return sb.toString();
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
            return refNo;

        StringBuilder sb = new StringBuilder();
        int t = 0;
        while (t < len) {
            int n = t + (len - t - 1) % 4 + 1;
            if (t != 0)
                sb.append(" ");
            sb.append(refNo, t, n);
            t = n;
        }

        return sb.toString();
    }
}
