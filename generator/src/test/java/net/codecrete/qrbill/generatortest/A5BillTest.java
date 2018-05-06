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
 * Unit tests for generation of A5 bills (PDF and SVG)
 * <p>
 *     Resulting output is compared byte by byte.
 * </p>
 */
@DisplayName("A5 bill generation (PDF and SVG)")
class A5BillTest {

    @Test
    void createA5Bill1() {
        Bill bill = SampleData.getExample1();
        byte[] svg = QRBill.generate(bill, QRBill.BillFormat.A5_LANDSCAPE_SHEET, QRBill.GraphicsFormat.SVG);
        FileComparison.assertFileContentsEqual(svg, "a5bill_ex1.svg");
    }

    @Test
    void createA5Bill2() {
        Bill bill = SampleData.getExample2();
        byte[] svg = QRBill.generate(bill, QRBill.BillFormat.A5_LANDSCAPE_SHEET, QRBill.GraphicsFormat.SVG);
        FileComparison.assertFileContentsEqual(svg, "a5bill_ex2.svg");
    }

    @Test
    void createA5Bill3() {
        Bill bill = SampleData.getExample3();
        byte[] svg = QRBill.generate(bill, QRBill.BillFormat.A5_LANDSCAPE_SHEET, QRBill.GraphicsFormat.SVG);
        FileComparison.assertFileContentsEqual(svg, "a5bill_ex3.svg");
    }

    @Test
    void createA5Bill4() {
        Bill bill = SampleData.getExample4();
        byte[] svg = QRBill.generate(bill, QRBill.BillFormat.A5_LANDSCAPE_SHEET, QRBill.GraphicsFormat.SVG);
        FileComparison.assertFileContentsEqual(svg, "a5bill_ex4.svg");
    }
}
