//
// Swiss QR Bill Generator
// Copyright (c) 2020 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
package net.codecrete.qrbill.generatortest;

import net.codecrete.qrbill.canvas.PDFCanvas;
import net.codecrete.qrbill.generator.QRBill;
import net.codecrete.qrbill.generator.SeparatorType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Unit tests for adding separator lines
 */
@DisplayName("PDF canvas test")
class AddSeparatorLinesTest {

    @Test
    void addBothSeparators() throws IOException {
        try (PDFCanvas canvas = new PDFCanvas(210, 297)) {
            QRBill.drawSeparators(SeparatorType.DOTTED_LINE_WITH_SCISSORS, true, canvas);
            byte[] imageData = canvas.toByteArray();
            FileComparison.assertFileContentsEqual(imageData, "dotted_lines_only.pdf");
        }
    }

    @Test
    void addVerticalSeparator() throws IOException {
        try (PDFCanvas canvas = new PDFCanvas(210, 297)) {
            QRBill.drawSeparators(SeparatorType.DASHED_LINE_WITH_SCISSORS, false, canvas);
            byte[] imageData = canvas.toByteArray();
            FileComparison.assertFileContentsEqual(imageData, "dashed_lines_only.pdf");
        }
    }
}
