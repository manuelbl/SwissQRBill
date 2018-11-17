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
 * Unit tests for generation of A6 bills (PDF and SVG)
 * <p>
 * Resulting output is compared byte by byte.
 * </p>
 */
@DisplayName("Bill generation (QR bill only, PDF and SVG)")
class QRBillTest {

    @Test
    void createQRBill1() {
        Bill bill = SampleData.getExample1();
        byte[] svg = QRBill.generate(bill, QRBill.BillFormat.QR_BILL_ONLY, QRBill.GraphicsFormat.SVG);
        FileComparison.assertFileContentsEqual(svg, "qrbill_ex1.svg");
    }

    @Test
    void createQRBill2() {
        Bill bill = SampleData.getExample2();
        byte[] svg = QRBill.generate(bill, QRBill.BillFormat.QR_BILL_ONLY, QRBill.GraphicsFormat.SVG);
        FileComparison.assertFileContentsEqual(svg, "qrbill_ex2.svg");
    }

    @Test
    void createQRBill3() {
        Bill bill = SampleData.getExample3();
        byte[] svg = QRBill.generate(bill, QRBill.BillFormat.QR_BILL_ONLY, QRBill.GraphicsFormat.SVG);
        FileComparison.assertFileContentsEqual(svg, "qrbill_ex3.svg");
    }

    @Test
    void createQRBill4() {
        Bill bill = SampleData.getExample4();
        byte[] svg = QRBill.generate(bill, QRBill.BillFormat.QR_BILL_ONLY, QRBill.GraphicsFormat.SVG);
        FileComparison.assertFileContentsEqual(svg, "qrbill_ex4.svg");
    }
}
