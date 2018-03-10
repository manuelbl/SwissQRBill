//
// Swiss QR Bill Generator
// Copyright (c) 2017 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
package net.codecrete.qrbill.generator;

import org.junit.Test;

public class QRCodeTest {

    @Test
    public void SVGTest1() {
        Bill bill = SampleData.getExample1();
        byte[] svg = QRBill.generate(bill, QRBill.BillFormat.QR_CODE_ONLY, QRBill.GraphicsFormat.SVG);
        TestHelper.assertFileContentsEqual(svg, "qrcode_ex1.svg");
    }

    @Test
    public void SVGTest2() {
        Bill bill = SampleData.getExample2();
        byte[] svg = QRBill.generate(bill, QRBill.BillFormat.QR_CODE_ONLY, QRBill.GraphicsFormat.SVG);
        TestHelper.assertFileContentsEqual(svg, "qrcode_ex2.svg");
    }

    @Test
    public void SVGTest3() {
        Bill bill = SampleData.getExample3();
        byte[] svg = QRBill.generate(bill, QRBill.BillFormat.QR_CODE_ONLY, QRBill.GraphicsFormat.SVG);
        TestHelper.assertFileContentsEqual(svg, "qrcode_ex3.svg");
    }

    @Test
    public void SVGTest4() {
        Bill bill = SampleData.getExample4();
        byte[] svg = QRBill.generate(bill, QRBill.BillFormat.QR_CODE_ONLY, QRBill.GraphicsFormat.SVG);
        TestHelper.assertFileContentsEqual(svg, "qrcode_ex4.svg");
    }
}
