//
// Swiss QR Bill Generator
// Copyright (c) 2019 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//

package net.codecrete.qrbill.canvas;

import net.codecrete.qrbill.generator.Bill;
import net.codecrete.qrbill.generator.QRBill;
import net.codecrete.qrbill.testhelper.FileComparison;
import net.codecrete.qrbill.testhelper.SampleData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


/**
 * Unit test for adding a QR bill to an existing PDF document
 */
@DisplayName("Append invoice to PDF")
class AppendToPdfTest {

    private Path invoicePath;

    @BeforeEach
    void init() throws URISyntaxException {
        invoicePath = Paths.get(AppendToPdfTest.class.getResource("/invoice.pdf").toURI());
    }

    @Test
    void addToPage2() throws IOException {
        Bill bill = SampleData.getExample7();
        try (PDFCanvas canvas = new PDFCanvas(invoicePath, 1)) {
            QRBill.draw(bill, canvas);
            byte[] imageData = canvas.toByteArray();
            FileComparison.assertFileContentsEqual(imageData, "invoice-01.pdf");
        }
    }

    @Test
    void addToLastPage() throws IOException {
        Bill bill = SampleData.getExample7();
        try (PDFCanvas canvas = new PDFCanvas(invoicePath, PDFCanvas.LAST_PAGE)) {
            QRBill.draw(bill, canvas);
            byte[] imageData = canvas.toByteArray();
            FileComparison.assertFileContentsEqual(imageData, "invoice-02.pdf");
        }
    }

    @Test
    void appendNewPage() throws IOException {
        Bill bill = SampleData.getExample7();
        try (PDFCanvas canvas = new PDFCanvas(invoicePath, PDFCanvas.NEW_PAGE_AT_END)) {
            QRBill.draw(bill, canvas);
            byte[] imageData = canvas.toByteArray();
            FileComparison.assertFileContentsEqual(imageData, "invoice-03.pdf");
        }
    }

    @Test
    void appendNewPageBinary() throws IOException {
        Bill bill = SampleData.getExample7();
        byte[] pdfDocument = Files.readAllBytes(invoicePath);
        try (PDFCanvas canvas = new PDFCanvas(pdfDocument, PDFCanvas.NEW_PAGE_AT_END)) {
            QRBill.draw(bill, canvas);
            byte[] imageData = canvas.toByteArray();
            FileComparison.assertFileContentsEqual(imageData, "invoice-04.pdf");
        }
    }
}
