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
 * Unit tests for generation of QR code (as SVG)
 */
@DisplayName("QR code (as SVG)")
class QRCodeTest {

    @Test
    void qrCodeAsSVG1() {
        Bill bill = SampleData.getExample1();
        bill.getFormat().setOutputSize(OutputSize.QR_CODE_ONLY);
        bill.getFormat().setGraphicsFormat(GraphicsFormat.SVG);
        byte[] svg = QRBill.generate(bill);
        FileComparison.assertFileContentsEqual(svg, "qrcode_ex1.svg");
    }

    @Test
    void qrCodeAsSVG2() {
        Bill bill = SampleData.getExample2();
        bill.getFormat().setOutputSize(OutputSize.QR_CODE_ONLY);
        bill.getFormat().setGraphicsFormat(GraphicsFormat.SVG);
        byte[] svg = QRBill.generate(bill);
        FileComparison.assertFileContentsEqual(svg, "qrcode_ex2.svg");
    }

    @Test
    void qrCodeAsSVG3() {
        Bill bill = SampleData.getExample3();
        bill.getFormat().setOutputSize(OutputSize.QR_CODE_ONLY);
        bill.getFormat().setGraphicsFormat(GraphicsFormat.SVG);
        byte[] svg = QRBill.generate(bill);
        FileComparison.assertFileContentsEqual(svg, "qrcode_ex3.svg");
    }

    @Test
    void qrCodeAsSVG4() {
        Bill bill = SampleData.getExample4();
        bill.getFormat().setOutputSize(OutputSize.QR_CODE_ONLY);
        bill.getFormat().setGraphicsFormat(GraphicsFormat.SVG);
        byte[] svg = QRBill.generate(bill);
        FileComparison.assertFileContentsEqual(svg, "qrcode_ex4.svg");
    }

    @Test
    void qrCodeWithQuietZone() {
        Bill bill = SampleData.getExample3();
        bill.getFormat().setOutputSize(OutputSize.QR_CODE_WITH_QUIET_ZONE);
        bill.getFormat().setGraphicsFormat(GraphicsFormat.SVG);
        byte[] svg = QRBill.generate(bill);
        FileComparison.assertFileContentsEqual(svg, "qrcode_quiet_zone.svg");
    }
}
