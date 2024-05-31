//
// Swiss QR Bill Generator
// Copyright (c) 2024 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
package net.codecrete.qrbill.generator;

import net.codecrete.qrbill.testhelper.FileComparison;
import net.codecrete.qrbill.testhelper.SampleData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for generation of payment part only (PDF, PNG and SVG)
 * <p>
 * Resulting output is compared byte by byte.
 * </p>
 */
@DisplayName("Generation of payment part only")
class PaymentPartTest {

    @Test
    void createQRBill1() {
        Bill bill = SampleData.getExample1();
        bill.getFormat().setOutputSize(OutputSize.PAYMENT_PART_ONLY);
        bill.getFormat().setGraphicsFormat(GraphicsFormat.SVG);
        byte[] svg = QRBill.generate(bill);
        FileComparison.assertFileContentsEqual(svg, "payment_part1.svg");
    }

    @Test
    void createQRBill2() {
        Bill bill = SampleData.getExample2();
        bill.getFormat().setOutputSize(OutputSize.PAYMENT_PART_ONLY);
        bill.getFormat().setGraphicsFormat(GraphicsFormat.PDF);
        byte[] pdf = QRBill.generate(bill);
        FileComparison.assertFileContentsEqual(pdf, "payment_part2.pdf");
    }
}
