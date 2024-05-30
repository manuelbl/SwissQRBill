//
// Swiss QR Bill Generator
// Copyright (c) 2017 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
package net.codecrete.qrbill.generator;

import net.codecrete.qrbill.testhelper.FileComparison;
import net.codecrete.qrbill.testhelper.SampleData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for generation of A4 bills (PDF and SVG)
 * <p>
 * Resulting output is compared byte by byte.
 * </p>
 */
@DisplayName("A4 bill generation (PDF and SVG)")
class A4BillTest {

    @Test
    void createA4SVGBill1() {
        generateAndCompareBill(SampleData.getExample1(), GraphicsFormat.SVG, "a4bill_ex1.svg");
    }

    @Test
    void createA4PDFBill1() {
        generateAndCompareBill(SampleData.getExample1(), GraphicsFormat.PDF, "a4bill_ex1.pdf");
    }

    @Test
    void createA4SVGBill2() {
        Bill bill = SampleData.getExample2();
        bill.getFormat().setFontFamily("Liberation Sans, Arial, Helvetica");
        generateAndCompareBill(bill, GraphicsFormat.SVG, "a4bill_ex2.svg");
    }

    @Test
    void createA4PDFBill2() {
        generateAndCompareBill(SampleData.getExample2(), GraphicsFormat.PDF, "a4bill_ex2.pdf");
    }

    @Test
    void createA4SVGBill3() {
        generateAndCompareBill(SampleData.getExample3(), GraphicsFormat.SVG, "a4bill_ex3.svg");
    }

    @Test
    void createA4PDFBill3() {
        Bill bill = SampleData.getExample3();
        bill.getFormat().setFontFamily("Arial");
        generateAndCompareBill(bill, GraphicsFormat.PDF, "a4bill_ex3.pdf");
    }

    @Test
    void createA4SVGBill4() {
        Bill bill = SampleData.getExample4();
        bill.getFormat().setFontFamily("Frutiger");
        generateAndCompareBill(bill, GraphicsFormat.SVG, "a4bill_ex4.svg");
    }

    @Test
    void createA4PDFBill4() {
        generateAndCompareBill(SampleData.getExample4(), GraphicsFormat.PDF, "a4bill_ex4.pdf");
    }

    @Test
    void createA4SVGBill5() {
        generateAndCompareBill(SampleData.getExample5(), GraphicsFormat.SVG, "a4bill_ex5.svg");
    }

    @Test
    void createA4PDFBill5() {
        generateAndCompareBill(SampleData.getExample5(), GraphicsFormat.PDF, "a4bill_ex5.pdf");
    }

    @Test
    void createA4SVGBill6() {
        generateAndCompareBill(SampleData.getExample6(), GraphicsFormat.SVG, "a4bill_ex6.svg");
    }

    @Test
    void createA4PDFBill6() {
        generateAndCompareBill(SampleData.getExample6(), GraphicsFormat.PDF, "a4bill_ex6.pdf");
    }

    @Test
    void createA4PDFBill8a() {
        generateAndCompareBill(SampleData.getExample8(), GraphicsFormat.PDF, "a4bill_ex8a.pdf");
    }

    @Test
    void createA4PDFBill8b() {
        Bill bill = SampleData.getExample8();
        bill.setCharacterSet(SPSCharacterSet.EXTENDED_LATIN);
        generateAndCompareBill(bill, GraphicsFormat.PDF, "a4bill_ex8b.pdf");
    }

    private void generateAndCompareBill(Bill bill, GraphicsFormat graphicsFormat,
                                        String expectedFileName) {
        bill.getFormat().setOutputSize(OutputSize.A4_PORTRAIT_SHEET);
        bill.getFormat().setGraphicsFormat(graphicsFormat);
        byte[] imageData = QRBill.generate(bill);
        FileComparison.assertFileContentsEqual(imageData, expectedFileName);
    }
}
