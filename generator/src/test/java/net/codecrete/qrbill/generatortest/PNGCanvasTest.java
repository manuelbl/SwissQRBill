//
// Swiss QR Bill Generator
// Copyright (c) 2017 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
package net.codecrete.qrbill.generatortest;

import net.codecrete.qrbill.generator.GraphicsFormat;
import net.codecrete.qrbill.generator.OutputSize;
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
    void pngBillQRBill() {
        Bill bill = SampleData.getExample1();
        bill.getFormat().setFontFamily("Arial");
        PNGCanvas canvas = new PNGCanvas(300);
        bill.getFormat().setOutputSize(OutputSize.QR_BILL_ONLY);
        byte[] svg = QRBill.generate(bill, canvas);
        FileComparison.assertGrayscaleImageContentsEqual(svg, "qrbill_ex1.png");
    }

    @Test
    void pngBillA4() {
        Bill bill = SampleData.getExample3();
        bill.getFormat().setFontFamily("Arial,Helvetica");
        PNGCanvas canvas = new PNGCanvas(144);
        bill.getFormat().setOutputSize(OutputSize.A4_PORTRAIT_SHEET);
        byte[] svg = QRBill.generate(bill, canvas);
        FileComparison.assertGrayscaleImageContentsEqual(svg, "a4bill_ex3.png");
    }
}
