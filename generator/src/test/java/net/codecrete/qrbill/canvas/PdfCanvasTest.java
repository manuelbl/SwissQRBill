//
// Swiss QR Bill Generator
// Copyright (c) 2019 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
package net.codecrete.qrbill.canvas;

import net.codecrete.qrbill.generator.Bill;
import net.codecrete.qrbill.generator.OutputSize;
import net.codecrete.qrbill.generator.QRBill;
import net.codecrete.qrbill.testhelper.FileComparison;
import net.codecrete.qrbill.testhelper.SampleData;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.util.Matrix;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * Unit tests for generating QR bills as PDF
 */
@DisplayName("PDF canvas test")
class PdfCanvasTest {

    private static final float MM_TO_PT = 72 / 25.4f;

    @Test
    void pdfWriteTo() throws IOException {
        Bill bill = SampleData.getExample3();
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try (PDFCanvas canvas = new PDFCanvas(QRBill.A4_PORTRAIT_WIDTH, QRBill.A4_PORTRAIT_HEIGHT)) {
            QRBill.draw(bill, canvas);
            canvas.writeTo(os);
        }

        byte[] data = os.toByteArray();
        compareResult(data, "pdfcanvas-writeto.pdf");
    }

    @Test
    void pdfSaveAs() throws IOException {
        Bill bill = SampleData.getExample4();
        Path path = Files.createTempFile("pdfcanvas-", ".pdf");
        try {
            try (PDFCanvas canvas = new PDFCanvas(QRBill.A4_PORTRAIT_WIDTH, QRBill.A4_PORTRAIT_HEIGHT)) {
                QRBill.draw(bill, canvas);
                canvas.saveAs(path);
            }

            compareResult(path, "pdfcanvas-saveas.pdf");
        } finally {
            Files.deleteIfExists(path);
        }
    }

    @Test
    void addPageToOpenPdfDocument() throws IOException {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(new PDRectangle(210 * MM_TO_PT, 297 * MM_TO_PT));
            document.addPage(page);
            try (PDPageContentStream stream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.OVERWRITE, true)) {
                stream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 18);
                stream.beginText();
                stream.newLineAtOffset(20 * MM_TO_PT, 220 * MM_TO_PT);
                stream.showText("Swiss QR Bill");
                stream.endText();
            }

            Bill bill = SampleData.getExample1();
            bill.getFormat().setOutputSize(OutputSize.QR_BILL_EXTRA_SPACE);
            try (PDFCanvas canvas = new PDFCanvas(document, PDFCanvas.NEW_PAGE_AT_END)) {
                QRBill.draw(bill, canvas);
            }

            Path path = Files.createTempFile("pdfcanvas-", ".pdf");
            try {
                document.save(path.toFile());
                compareResult(path, "pdfcanvas-opendoc.pdf");
            } finally {
                Files.deleteIfExists(path);
            }
        }
    }

    @Test
    void coloredLines() throws IOException {
        Path path = Files.createTempFile("pdfcanvas-", ".pdf");
        try (PDFCanvas canvas = new PDFCanvas(100, 100)) {
            canvas.moveTo(10, 10);
            canvas.lineTo(90, 90);
            canvas.strokePath(2, 0xFF00FF, Canvas.LineStyle.Dashed, true);
            canvas.saveAs(path);

            compareResult(path, "pdfcanvas-colored-line.pdf");
        } finally {
            Files.deleteIfExists(path);
        }
    }

    @Test
    void addPageToOpenContentStream() throws IOException {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(new PDRectangle(210 * MM_TO_PT, 297 * MM_TO_PT));
            document.addPage(page);
            try (PDPageContentStream stream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.OVERWRITE, true)) {
                // offset from bottom
                stream.saveGraphicsState();
                stream.transform(Matrix.getTranslateInstance(0, 20 * MM_TO_PT));

                Bill bill = SampleData.getExample1();
                bill.getFormat().setOutputSize(OutputSize.QR_BILL_EXTRA_SPACE);
                try (PDFCanvas canvas = new PDFCanvas(stream)) {
                    QRBill.draw(bill, canvas);
                }

                stream.restoreGraphicsState();

                stream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 18);
                stream.beginText();
                stream.newLineAtOffset(20 * MM_TO_PT, 220 * MM_TO_PT);
                stream.showText("Swiss QR Bill");
                stream.endText();
            }

            Path path = Files.createTempFile("pdfcanvas-", ".pdf");
            try {
                document.save(path.toFile());
                compareResult(path, "pdfcanvas-openstream.pdf");
            } finally {
                Files.deleteIfExists(path);
            }
        }
    }

    @Test
    void customFonts() throws IOException {
        Path regularFontPath = unpackFont("/fonts/LiberationSans-Regular.ttf");
        Path boldFontPath = unpackFont("/fonts/LiberationSans-Bold.ttf");
        try {
            Bill bill = SampleData.getExample1();
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            try (PDFCanvas canvas = new PDFCanvas(QRBill.A4_PORTRAIT_WIDTH, QRBill.A4_PORTRAIT_HEIGHT,
                    PDFFontSettings.embeddedCustomFont("Liberation Sans", regularFontPath, boldFontPath))) {
                QRBill.draw(bill, canvas);
                canvas.writeTo(os);
            }
            byte[] data = os.toByteArray();
            compareResult(data, "pdfcanvas-custom-font.pdf");

        } finally {
            Files.delete(regularFontPath);
            Files.delete(boldFontPath);
        }

    }

    @SuppressWarnings("DataFlowIssue")
    private Path unpackFont(String fontResource) throws IOException {
        Path fontPath = Files.createTempFile("font-", ".ttf");
        try (InputStream is = PdfCanvasTest.class.getResource(fontResource).openStream()) {
            Files.copy(is, fontPath, StandardCopyOption.REPLACE_EXISTING);
        }
        return fontPath;
    }

    private void compareResult(byte[] imageData, String expectedFileName) {
        FileComparison.assertFileContentsEqual(imageData, expectedFileName);
    }

    private void compareResult(Path filename, String expectedFileName) throws IOException {
        byte[] imageData = Files.readAllBytes(filename);
        compareResult(imageData, expectedFileName);
        Files.delete(filename);
    }
}
