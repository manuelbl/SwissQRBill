//
// Swiss QR Bill Generator
// Copyright (c) 2018 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//

package net.codecrete.qrbill.canvas;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Unit test for checking that resources are properly released
 */
@DisplayName("Cleanup of resources")
class CleanupTest {

    @Test
    void closePNGFreesResources()
            throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        PNGCanvas pngCanvas;
        try (PNGCanvas canvas = new PNGCanvas(200, 100, 300, "Arial")) {
            pngCanvas = canvas;
        }

        Field field = PNGCanvas.class.getDeclaredField("graphics");
        field.setAccessible(true);
        assertNull(field.get(pngCanvas));
    }

    @Test
    void closePDFFreesResources() throws IOException, SecurityException, IllegalArgumentException,
            IllegalAccessException, NoSuchFieldException {
        PDFCanvas pdfCanvas;
        try (PDFCanvas canvas = new PDFCanvas(200, 100)) {
            pdfCanvas = canvas;
        }

        Field field = PDFCanvas.class.getDeclaredField("contentStream");
        field.setAccessible(true);
        assertNull(field.get(pdfCanvas));
        field = PDFCanvas.class.getDeclaredField("document");
        field.setAccessible(true);
        assertNull(field.get(pdfCanvas));
    }
}