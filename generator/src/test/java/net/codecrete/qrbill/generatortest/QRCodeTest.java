//
// Swiss QR Bill Generator
// Copyright (c) 2017 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
package net.codecrete.qrbill.generatortest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.codecrete.qrbill.generator.Bill;
import net.codecrete.qrbill.generator.QRBill;

/**
 * Unit tests for generation of QR code (as SVG)
 */
@DisplayName("QR code (as SVG)")
class QRCodeTest {

    @Test
    void qrCodeAsSVG1() {
        Bill bill = SampleData.getExample1();
        byte[] svg = QRBill.generate(bill, QRBill.BillFormat.QR_CODE_ONLY, QRBill.GraphicsFormat.SVG);
        FileComparison.assertFileContentsEqual(svg, "qrcode_ex1.svg");
    }

    @Test
    void qrCodeAsSVG2() {
        Bill bill = SampleData.getExample2();
        byte[] svg = QRBill.generate(bill, QRBill.BillFormat.QR_CODE_ONLY, QRBill.GraphicsFormat.SVG);
        FileComparison.assertFileContentsEqual(svg, "qrcode_ex2.svg");
    }

    @Test
    void qrCodeAsSVG3() {
        Bill bill = SampleData.getExample3();
        byte[] svg = QRBill.generate(bill, QRBill.BillFormat.QR_CODE_ONLY, QRBill.GraphicsFormat.SVG);
        FileComparison.assertFileContentsEqual(svg, "qrcode_ex3.svg");
    }

    @Test
    void qrCodeAsSVG4() {
        Bill bill = SampleData.getExample4();
        byte[] svg = QRBill.generate(bill, QRBill.BillFormat.QR_CODE_ONLY, QRBill.GraphicsFormat.SVG);
        FileComparison.assertFileContentsEqual(svg, "qrcode_ex4.svg");
    }
}
