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
 * Unit tests for generation of non-standard margins.
 * <p>
 * Resulting output is compared byte by byte.
 * </p>
 */
@DisplayName("Bill generation with non-standard margin (SVG)")
class MarginTest {

    @Test
    void createA4SVGBill1() {
        Bill bill = SampleData.getExample1();
        bill.getFormat().setMarginLeft(8.0);
        bill.getFormat().setMarginRight(8.0);
        generateAndCompareBill(bill, "a4bill_ma1.svg");
    }

    @Test
    void createA4SVGBill2() {
        Bill bill = SampleData.getExample2();
        bill.getFormat().setMarginLeft(12.0);
        bill.getFormat().setMarginRight(12.0);
        generateAndCompareBill(bill, "a4bill_ma2.svg");
    }

    @Test
    void createA4SVGBill6() {
        Bill bill = SampleData.getExample6();
        bill.getFormat().setMarginLeft(10.0);
        bill.getFormat().setMarginRight(9.0);
        generateAndCompareBill(bill, "a4bill_ma6.svg");
    }

    private void generateAndCompareBill(Bill bill,
                                        String expectedFileName) {
        bill.getFormat().setOutputSize(OutputSize.A4_PORTRAIT_SHEET);
        bill.getFormat().setGraphicsFormat(GraphicsFormat.SVG);
        byte[] imageData = QRBill.generate(bill);
        FileComparison.assertFileContentsEqual(imageData, expectedFileName);
    }
}
