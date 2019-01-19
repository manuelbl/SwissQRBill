//
// Swiss QR Bill Generator
// Copyright (c) 2017 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
package net.codecrete.qrbill.generatortest;

import net.codecrete.qrbill.canvas.PNGCanvas;
import net.codecrete.qrbill.generator.Bill;
import net.codecrete.qrbill.generator.OutputSize;
import net.codecrete.qrbill.generator.QRBill;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

/**
 * Unit tests for generating QR bills as PNG
 */
@DisplayName("Default locale independence")
class PNGCanvasTest {

    @Test
    void pngBillQRBill() throws IOException {
        Bill bill = SampleData.getExample1();
        PNGCanvas canvas = new PNGCanvas(QRBill.QR_BILL_WIDTH, QRBill.QR_BILL_HEIGHT, 300, "Arial");
        bill.getFormat().setOutputSize(OutputSize.QR_BILL_ONLY);
        QRBill.draw(bill, canvas);
        byte[] png = canvas.toByteArray();
        FileComparison.assertGrayscaleImageContentsEqual(png, "qrbill_ex1.png");
    }

    @Test
    void pngBillA4() throws IOException {
        Bill bill = SampleData.getExample3();
        PNGCanvas canvas = new PNGCanvas(QRBill.A4_PORTRAIT_WIDTH, QRBill.A4_PORTRAIT_HEIGHT, 144, "Arial,Helvetica");
        bill.getFormat().setOutputSize(OutputSize.A4_PORTRAIT_SHEET);
        QRBill.draw(bill, canvas);
        byte[] png = canvas.toByteArray();
        FileComparison.assertGrayscaleImageContentsEqual(png, "a4bill_ex3.png");
    }
}
