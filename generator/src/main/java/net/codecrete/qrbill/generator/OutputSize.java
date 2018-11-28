//
// Swiss QR Bill Generator
// Copyright (c) 2017 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
package net.codecrete.qrbill.generator;

/**
 * The output size of the QR bill
 */
public enum OutputSize {
    /**
     * A4 sheet in portrait orientation. The QR bill is at the bottom.
     */
    A4_PORTRAIT_SHEET,
    /**
     * QR bill only (about 105 by 210 mm).
     */
    QR_BILL_ONLY,
    /**
     * QR code only (46 by 46 mm).
     */
    QR_CODE_ONLY
}
