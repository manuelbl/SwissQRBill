//
// Swiss QR Bill Generator
// Copyright (c) 2018 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//

package net.codecrete.qrbill.canvas;

import net.codecrete.qrbill.generator.Bill;
import net.codecrete.qrbill.generator.GraphicsFormat;
import net.codecrete.qrbill.generator.OutputSize;
import net.codecrete.qrbill.generator.QRBill;
import net.codecrete.qrbill.testhelper.FileComparison;
import net.codecrete.qrbill.testhelper.SampleData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try (SVGCanvas canvas =
                     new SVGCanvas(QRBill.A4_PORTRAIT_WIDTH, QRBill.A4_PORTRAIT_HEIGHT, "Helvetica, Arial, Sans")) {
            QRBill.draw(bill, canvas);
            canvas.writeTo(os);
        }


        byte[] data = os.toByteArray();
        assertTrue(data.length > 2000);
        checkForSvgHeader(data);
    }

    @Test
    void svgSaveAs() throws IOException {
        Bill bill = SampleData.getExample2();
        Path path = Paths.get("test-qrbill.svg");
        try (SVGCanvas canvas =
                     new SVGCanvas(QRBill.A4_PORTRAIT_WIDTH, QRBill.A4_PORTRAIT_HEIGHT, "Helvetica, Arial, Sans")) {
            QRBill.draw(bill, canvas);
            canvas.saveAs(path);
        }

        byte[] data = Files.readAllBytes(path);
        assertTrue(data.length > 2000);
        checkForSvgHeader(data);

        Files.delete(path);
    }

    private void checkForSvgHeader(byte[] data) {
        assertEquals((byte) '<', data[0]);
        assertEquals((byte) '?', data[1]);
        assertEquals((byte) 'x', data[2]);
        assertEquals((byte) 'm', data[3]);
        assertEquals((byte) 'l', data[4]);
    }
}
