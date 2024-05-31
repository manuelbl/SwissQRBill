//
// Swiss QR Bill Generator
// Copyright (c) 2017 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
package net.codecrete.qrbill.generator;

/**
 * The output size of the QR bill or QR code.
 *
 * @see <a href="https://github.com/manuelbl/SwissQRBill/wiki/Output-Sizes">Output Sizes (in Wiki)</a>
 */
public enum OutputSize {
    /**
     * A4 sheet in portrait orientation. The QR bill is at the bottom.
     */
    A4_PORTRAIT_SHEET,
    /**
     * QR bill only (about 210 by 105 mm).
     * <p>
     * This size is suitable if the QR bill has no horizontal line.
     * If the horizontal line is needed and the A4 sheet size is not
     * suitable, use {@link #QR_BILL_EXTRA_SPACE} instead.
     * </p>
     */
    QR_BILL_ONLY,
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
    QR_BILL_EXTRA_SPACE,

    /**
     * QR code only with 5mm of white space on all sides (56 by 56 mm).
     * <p>
     * This format applies a white background (as opposed to a transparent one).
     * </p>
     */
    QR_CODE_WITH_QUIET_ZONE,
    /**
     * Payment part only (about 148 by 105 mm).
     * <p>
     * This size does not include separator lines. It is suitable for displaying the QR bill in online channels.
     * See <i>Implementation Guidelines QR Bill v2.3</i>, ch. <i>3.8 Layout rules for the online use of the QR-bill</i>
     * for additional requirements when using this size.
     * </p>
     */
    PAYMENT_PART_ONLY,
}
