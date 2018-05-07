//
// Swiss QR Bill Generator
// Copyright (c) 2017 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
package net.codecrete.qrbill.generatortest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.codecrete.qrbill.canvas.PNGCanvas;
import net.codecrete.qrbill.generator.Bill;
import net.codecrete.qrbill.generator.QRBill;

/**
 * Unit tests for generating QR bills as PNG
 */
@DisplayName("Default locale independence")
class PNGCanvasTest {

    @Test
    void pngBillA6() {
        Bill bill = SampleData.getExample1();
        PNGCanvas canvas = new PNGCanvas(300);
        byte[] svg = QRBill.generate(bill, QRBill.BillFormat.A6_LANDSCAPE_SHEET, canvas);
        FileComparison.assertGrayscaleImageContentsEqual(svg, "a6bill_ex1.png");
    }

    @Test
    void pngBillA5() {
        Bill bill = SampleData.getExample3();
        PNGCanvas canvas = new PNGCanvas(144);
        byte[] svg = QRBill.generate(bill, QRBill.BillFormat.A5_LANDSCAPE_SHEET, canvas);
        FileComparison.assertGrayscaleImageContentsEqual(svg, "a5bill_ex3.png");
    }
}
