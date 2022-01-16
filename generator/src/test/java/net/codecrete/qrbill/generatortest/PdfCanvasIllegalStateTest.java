//
// Swiss QR Bill Generator
// Copyright (c) 2022 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
package net.codecrete.qrbill.generatortest;

import net.codecrete.qrbill.canvas.PDFCanvas;
import net.codecrete.qrbill.generator.Bill;
import net.codecrete.qrbill.generator.OutputSize;
import net.codecrete.qrbill.generator.QRBill;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit tests for generating QR bills as PDF
 */
@DisplayName("PDF canvas exception test")
class PdfCanvasIllegalStateTest {

    private static final float MM_TO_PT = 72 / 25.4f;

    @Test
    void toByteArray_throwsException() throws IOException {
        PDDocument document = new PDDocument();
        Bill bill = SampleData.getExample1();
        bill.getFormat().setOutputSize(OutputSize.QR_BILL_EXTRA_SPACE);
        try (PDFCanvas canvas = new PDFCanvas(document, PDFCanvas.NEW_PAGE_AT_END)) {
            QRBill.draw(bill, canvas);
            assertThrows(IllegalStateException.class, canvas::toByteArray);
        }
    }

    @Test
    void saveAs_throwsException() throws IOException {
        PDDocument document = new PDDocument();
        Bill bill = SampleData.getExample1();
        bill.getFormat().setOutputSize(OutputSize.QR_BILL_EXTRA_SPACE);
        try (PDFCanvas canvas = new PDFCanvas(document, PDFCanvas.NEW_PAGE_AT_END)) {
            QRBill.draw(bill, canvas);
            assertThrows(IllegalStateException.class, () -> canvas.saveAs(Paths.get("some.pdf")));
        }
    }

    @Test
    void writeTo_throwsException() throws IOException {
        PDDocument document = new PDDocument();
        PDPage page = new PDPage(new PDRectangle(210 * MM_TO_PT, 297 * MM_TO_PT));
        document.addPage(page);
        try (PDPageContentStream stream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.OVERWRITE, true)) {
            Bill bill = SampleData.getExample1();
            bill.getFormat().setOutputSize(OutputSize.QR_BILL_EXTRA_SPACE);
            try (PDFCanvas canvas = new PDFCanvas(stream)) {
                QRBill.draw(bill, canvas);
                assertThrows(IllegalStateException.class, () -> canvas.writeTo(new ByteArrayOutputStream()));
            }
        }
    }
}
