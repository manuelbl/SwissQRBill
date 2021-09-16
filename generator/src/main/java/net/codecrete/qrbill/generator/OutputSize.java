//
// Swiss QR Bill Generator
// Copyright (c) 2017 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
package net.codecrete.qrbill.generator;

/**
 * The output size of the QR bill or QR code
 */
public enum OutputSize {
    /**
     * A4 sheet in portrait orientation. The QR bill is at the bottom.
     */
    A4_PORTRAIT_SHEET,
    /**
     * QR bill only (about 105 by 210 mm).
     * <p>
     * This size is suitable if the QR bill has not horizontal line.
     * If the horizontal line is needed and the A4 sheet size is not
     * suitable, use {@link #QR_BILL_EXTRA_SPACE} instead.
     * </p>
     */
    QR_BILL_ONLY,
    /**
     * QR bill with horizontal separator line (about 110 by 210 mm).
     *
     * @deprecated Has been renamed to {@link #QR_BILL_EXTRA_SPACE}.
     */
    QR_BILL_WITH_HORIZONTAL_LINE,
    /**
     * QR code only (46 by 46 mm).
     */
    QR_CODE_ONLY,
    /**
     * QR bill only with additional space at the top for the horizontal line (about 110 by 210 mm).
     * <p>
     * The extra 5 mm at the top create space for the horizontal line and
     * optionally for the scissors.
     * </p>
     */
    QR_BILL_EXTRA_SPACE
}
