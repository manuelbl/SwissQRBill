//
// Swiss QR Bill Generator
// Copyright (c) 2018 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//

package net.codecrete.qrbill.generatortest;

import net.codecrete.qrbill.canvas.SVGCanvas;
import net.codecrete.qrbill.generator.Bill;
import net.codecrete.qrbill.generator.GraphicsFormat;
import net.codecrete.qrbill.generator.OutputSize;
import net.codecrete.qrbill.generator.QRBill;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Paths;

/**
 * Unit tests with characters challening for SVG (XML relevant characters)
 */
@DisplayName("SVG special tests")
class SVGTest {

    @Test
    void svgWithChallengingCharacters() {
        Bill bill = SampleData.getExample1();
        bill.setUnstructuredMessage("<h1>&&\"ff\"'t'");
        bill.getFormat().setOutputSize(OutputSize.QR_BILL_ONLY);
        bill.getFormat().setGraphicsFormat(GraphicsFormat.SVG);
        byte[] svg = QRBill.generate(bill);
        FileComparison.assertFileContentsEqual(svg, "qrbill_sc1.svg");
    }

    @Test
    void svgWriteTo() throws IOException {
        Bill bill = SampleData.getExample1();
        try (SVGCanvas canvas =
                     new SVGCanvas(QRBill.A4_PORTRAIT_WIDTH, QRBill.A4_PORTRAIT_HEIGHT, "Helvetica, Arial, Sans")) {
            QRBill.draw(bill, canvas);
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            canvas.writeTo(os);
        }
    }

    @Test
    void svgSaveAs() throws IOException {
        Bill bill = SampleData.getExample2();
        try (SVGCanvas canvas =
                     new SVGCanvas(QRBill.A4_PORTRAIT_WIDTH, QRBill.A4_PORTRAIT_HEIGHT, "Helvetica, Arial, Sans")) {
            QRBill.draw(bill, canvas);
            canvas.saveAs(Paths.get("test-qrbill.svg"));
        }
    }
}