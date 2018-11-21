//
// Swiss QR Bill Generator
// Copyright (c) 2017 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
package net.codecrete.qrbill.generatortest;

import net.codecrete.qrbill.generator.Bill;
import net.codecrete.qrbill.generator.GraphicsFormat;
import net.codecrete.qrbill.generator.OutputSize;
import net.codecrete.qrbill.generator.QRBill;
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
        generateAndCompareBill(SampleData.getExample1(), OutputSize.A4_PORTRAIT_SHEET, GraphicsFormat.SVG, "a4bill_ex1.svg");
    }

    @Test
    void createA4PDFBill1() {
        generateAndCompareBill(SampleData.getExample1(), OutputSize.A4_PORTRAIT_SHEET, GraphicsFormat.PDF, "a4bill_ex1.pdf");
    }

    @Test
    void createA4SVGBill2() {
        generateAndCompareBill(SampleData.getExample2(), OutputSize.A4_PORTRAIT_SHEET, GraphicsFormat.SVG, "a4bill_ex2.svg");
    }

    @Test
    void createA4PDFBill2() {
        generateAndCompareBill(SampleData.getExample2(), OutputSize.A4_PORTRAIT_SHEET, GraphicsFormat.PDF, "a4bill_ex2.pdf");
    }

    @Test
    void createA4SVGBill3() {
        generateAndCompareBill(SampleData.getExample3(), OutputSize.A4_PORTRAIT_SHEET, GraphicsFormat.SVG, "a4bill_ex3.svg");
    }

    @Test
    void createA4PDFBill3() {
        generateAndCompareBill(SampleData.getExample3(), OutputSize.A4_PORTRAIT_SHEET, GraphicsFormat.PDF, "a4bill_ex3.pdf");
    }

    @Test
    void createA4SVGBill4() {
        generateAndCompareBill(SampleData.getExample4(), OutputSize.A4_PORTRAIT_SHEET, GraphicsFormat.SVG, "a4bill_ex4.svg");
    }

    @Test
    void createA4PDFBill4() {
        generateAndCompareBill(SampleData.getExample4(), OutputSize.A4_PORTRAIT_SHEET, GraphicsFormat.PDF, "a4bill_ex4.pdf");
    }

    @Test
    void createA4SVGBill5() {
        generateAndCompareBill(SampleData.getExample5(), OutputSize.A4_PORTRAIT_SHEET, GraphicsFormat.SVG, "a4bill_ex5.svg");
    }

    @Test
    void createA4PDFBill5() {
        generateAndCompareBill(SampleData.getExample5(), OutputSize.A4_PORTRAIT_SHEET, GraphicsFormat.PDF, "a4bill_ex5.pdf");
    }

    @Test
    void createA4SVGBill6() {
        generateAndCompareBill(SampleData.getExample6(), OutputSize.A4_PORTRAIT_SHEET, GraphicsFormat.SVG, "a4bill_ex6.svg");
    }

    @Test
    void createA4PDFBill6() {
        generateAndCompareBill(SampleData.getExample6(), OutputSize.A4_PORTRAIT_SHEET, GraphicsFormat.PDF, "a4bill_ex6.pdf");
    }

    private void generateAndCompareBill(Bill bill, OutputSize outputSize, GraphicsFormat graphicsFormat,
                                          String expectedFileName) {
        bill.getFormat().setOutputSize(outputSize);
        bill.getFormat().setGraphicsFormat(graphicsFormat);
        byte[] imageData = QRBill.generate(bill);
        FileComparison.assertFileContentsEqual(imageData, expectedFileName);
    }
}
