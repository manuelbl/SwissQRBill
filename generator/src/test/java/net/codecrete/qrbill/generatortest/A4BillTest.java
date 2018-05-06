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
 * Unit tests for generation of A4 bills (PDF and SVG)
 * <p>
 *     Resulting output is compared byte by byte.
 * </p>
 */
@DisplayName("A4 bill generation (PDF and SVG)")
class A4BillTest {

    @Test
    void createA4SVGBill1() {
        Bill bill = SampleData.getExample1();
        byte[] svg = QRBill.generate(bill, QRBill.BillFormat.A4_PORTRAIT_SHEET, QRBill.GraphicsFormat.SVG);
        FileComparison.assertFileContentsEqual(svg, "a4bill_ex1.svg");
    }

    @Test
    void createA4PDFBill1() {
        Bill bill = SampleData.getExample1();
        byte[] pdf = QRBill.generate(bill, QRBill.BillFormat.A4_PORTRAIT_SHEET, QRBill.GraphicsFormat.PDF);
        FileComparison.assertFileContentsEqual(pdf, "a4bill_ex1.pdf");
    }

    @Test
    void createA4SVGBill2() {
        Bill bill = SampleData.getExample2();
        byte[] svg = QRBill.generate(bill, QRBill.BillFormat.A4_PORTRAIT_SHEET, QRBill.GraphicsFormat.SVG);
        FileComparison.assertFileContentsEqual(svg, "a4bill_ex2.svg");
    }

    @Test
    void createA4PDFBill2() {
        Bill bill = SampleData.getExample2();
        byte[] svg = QRBill.generate(bill, QRBill.BillFormat.A4_PORTRAIT_SHEET, QRBill.GraphicsFormat.PDF);
        FileComparison.assertFileContentsEqual(svg, "a4bill_ex2.pdf");
    }

    @Test
    void createA4SVGBill3() {
        Bill bill = SampleData.getExample3();
        byte[] svg = QRBill.generate(bill, QRBill.BillFormat.A4_PORTRAIT_SHEET, QRBill.GraphicsFormat.SVG);
        FileComparison.assertFileContentsEqual(svg, "a4bill_ex3.svg");
    }

    @Test
    void createA4PDFBill3() {
        Bill bill = SampleData.getExample3();
        byte[] svg = QRBill.generate(bill, QRBill.BillFormat.A4_PORTRAIT_SHEET, QRBill.GraphicsFormat.PDF);
        FileComparison.assertFileContentsEqual(svg, "a4bill_ex3.pdf");
    }

    @Test
    void createA4SVGBill4() {
        Bill bill = SampleData.getExample4();
        byte[] svg = QRBill.generate(bill, QRBill.BillFormat.A4_PORTRAIT_SHEET, QRBill.GraphicsFormat.SVG);
        FileComparison.assertFileContentsEqual(svg, "a4bill_ex4.svg");
    }

    @Test
    void createA4PDFBill4() {
        Bill bill = SampleData.getExample4();
        byte[] svg = QRBill.generate(bill, QRBill.BillFormat.A4_PORTRAIT_SHEET, QRBill.GraphicsFormat.PDF);
        FileComparison.assertFileContentsEqual(svg, "a4bill_ex4.pdf");
    }
}
