//
// Swiss QR Bill Generator
// Copyright (c) 2017 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
package net.codecrete.qrbill.generatortest;

import net.codecrete.qrbill.canvas.PNGCanvas;
import net.codecrete.qrbill.generator.Bill;
import net.codecrete.qrbill.generator.OutputSize;
import net.codecrete.qrbill.generator.QRBill;
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
 * Unit tests for generating QR bills as PNG
 */
@DisplayName("PNG canvas test")
class PNGCanvasTest {

    @Test
    void pngBillQRBill() throws IOException {
        Bill bill = SampleData.getExample1();
        PNGCanvas canvas = new PNGCanvas(QRBill.QR_BILL_WIDTH, QRBill.QR_BILL_HEIGHT, 300, "Arial");
        bill.getFormat().setOutputSize(OutputSize.QR_BILL_ONLY);
        QRBill.draw(bill, canvas);
        byte[] png = canvas.toByteArray();
        FileComparison.assertGrayscaleImageContentsEqual(png, "qrbill_ex1.png");
    }

    @Test
    void pngBillA4() throws IOException {
        Bill bill = SampleData.getExample3();
        PNGCanvas canvas = new PNGCanvas(QRBill.A4_PORTRAIT_WIDTH, QRBill.A4_PORTRAIT_HEIGHT, 144, "Arial,Helvetica");
        bill.getFormat().setOutputSize(OutputSize.A4_PORTRAIT_SHEET);
        QRBill.draw(bill, canvas);
        byte[] png = canvas.toByteArray();
        FileComparison.assertGrayscaleImageContentsEqual(png, "a4bill_ex3.png");
    }

    @Test
    void pngWriteTo() throws IOException {
        Bill bill = SampleData.getExample5();
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try (PNGCanvas canvas =
                     new PNGCanvas(QRBill.A4_PORTRAIT_WIDTH, QRBill.A4_PORTRAIT_HEIGHT, 144, "Helvetica, Arial, Sans")) {
            QRBill.draw(bill, canvas);
            canvas.writeTo(os);
        }

        byte[] data = os.toByteArray();
        assertTrue(data.length > 2000);
        checkForPngHeader(data);
    }

    @Test
    void pngSaveAs() throws IOException {
        Bill bill = SampleData.getExample6();
        Path path = Paths.get("test-qrbill.png");
        try (PNGCanvas canvas =
                     new PNGCanvas(QRBill.A4_PORTRAIT_WIDTH, QRBill.A4_PORTRAIT_HEIGHT, 144, "Helvetica, Arial, Sans")) {
            QRBill.draw(bill, canvas);
            canvas.saveAs(path);
        }

        byte[] data = Files.readAllBytes(path);
        assertTrue(data.length > 2000);
        checkForPngHeader(data);

        Files.delete(path);
    }

    private void checkForPngHeader(byte[] data) {
        assertEquals((byte)137, data[0]);
        assertEquals((byte)80, data[1]);
        assertEquals((byte)78, data[2]);
        assertEquals((byte)71, data[3]);
        assertEquals((byte)13, data[4]);
        assertEquals((byte)10, data[5]);
        assertEquals((byte)26, data[6]);
        assertEquals((byte)10, data[7]);
    }
}
