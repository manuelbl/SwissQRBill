//
// Swiss QR Bill Generator
// Copyright (c) 2017 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
package net.codecrete.qrbill.generator;

import org.junit.Test;

import net.codecrete.qrbill.generator.PNGCanvas;

public class PNGCanvasTest {

    @Test
    public void createA6Bill1() {
        Bill bill = SampleData.getExample1();
        PNGCanvas canvas = new PNGCanvas(300);
        byte[] svg = QRBill.generate(bill, QRBill.BillFormat.A6_LANDSCAPE_SHEET, canvas);
        TestHelper.assertFileContentsEqual(svg, "a6bill_ex1.png");
    }

    @Test
    public void createA5Bill3() {
        Bill bill = SampleData.getExample3();
        PNGCanvas canvas = new PNGCanvas(144);
        byte[] svg = QRBill.generate(bill, QRBill.BillFormat.A5_LANDSCAPE_SHEET, canvas);
        TestHelper.assertFileContentsEqual(svg, "a5bill_ex3.png");
    }
}
