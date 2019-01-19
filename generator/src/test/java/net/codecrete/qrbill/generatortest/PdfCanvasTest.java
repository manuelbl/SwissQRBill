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
import java.nio.file.Paths;

/**
 * Unit tests for generating QR bills as PNG
 */
@DisplayName("PDF canvas test")
class PdfCanvasTest {

    @Test
    void pdfWriteTo() throws IOException {
        Bill bill = SampleData.getExample3();
        try (PDFCanvas canvas = new PDFCanvas(QRBill.A4_PORTRAIT_WIDTH, QRBill.A4_PORTRAIT_HEIGHT)) {
            QRBill.draw(bill, canvas);
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            canvas.writeTo(os);
        }
    }

    @Test
    void pdfSaveAs() throws IOException {
        Bill bill = SampleData.getExample4();
        try (PDFCanvas canvas = new PDFCanvas(QRBill.A4_PORTRAIT_WIDTH, QRBill.A4_PORTRAIT_HEIGHT)) {
            QRBill.draw(bill, canvas);
            canvas.saveAs(Paths.get("test-qrbill.pdf"));
        }
    }
}
