//
// Swiss QR Bill Generator
// Copyright (c) 2019 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
package net.codecrete.qrbill.generatortest;

import net.codecrete.qrbill.canvas.PDFCanvas;
import net.codecrete.qrbill.generator.Bill;
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
@DisplayName("PDF canvas test")
class PdfCanvasTest {

    @Test
    void pdfWriteTo() throws IOException {
        Bill bill = SampleData.getExample3();
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try (PDFCanvas canvas = new PDFCanvas(QRBill.A4_PORTRAIT_WIDTH, QRBill.A4_PORTRAIT_HEIGHT)) {
            QRBill.draw(bill, canvas);
            canvas.writeTo(os);
        }

        byte[] data = os.toByteArray();
        assertTrue(data.length > 2000);
        checkForPdfHeader(data);
    }

    @Test
    void pdfSaveAs() throws IOException {
        Bill bill = SampleData.getExample4();
        Path path = Paths.get("test-qrbill.pdf");
        try (PDFCanvas canvas = new PDFCanvas(QRBill.A4_PORTRAIT_WIDTH, QRBill.A4_PORTRAIT_HEIGHT)) {
            QRBill.draw(bill, canvas);
            canvas.saveAs(path);
        }

        byte[] data = Files.readAllBytes(path);
        assertTrue(data.length > 2000);
        checkForPdfHeader(data);

        Files.delete(path);
    }

    private void checkForPdfHeader(byte[] data) {
        assertEquals((byte) '%', data[0]);
        assertEquals((byte) 'P', data[1]);
        assertEquals((byte) 'D', data[2]);
        assertEquals((byte) 'F', data[3]);
        assertEquals((byte) '-', data[4]);
    }
}
