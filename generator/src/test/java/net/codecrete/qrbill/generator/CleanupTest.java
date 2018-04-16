//
// Swiss QR Bill Generator
// Copyright (c) 2018 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//

package net.codecrete.qrbill.generator;

import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.lang.reflect.Field;

import org.junit.Test;

import net.codecrete.qrbill.canvas.PDFCanvas;
import net.codecrete.qrbill.canvas.PNGCanvas;

public class CleanupTest {

    @Test
    public void closePNG() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        PNGCanvas pngCanvas = null;
        try (PNGCanvas canvas = new PNGCanvas(300)) {
            canvas.setupPage(200, 100);
            pngCanvas = canvas;
        }

        Field field = PNGCanvas.class.getDeclaredField("graphics");
        field.setAccessible(true);
        assertNull(field.get(pngCanvas));
    }

    @Test
    public void closePDF() throws IOException, SecurityException, IllegalArgumentException, IllegalAccessException, NoSuchFieldException {
        PDFCanvas pdfCanvas = null;
        try (PDFCanvas canvas = new PDFCanvas()) {
            canvas.setupPage(200, 100);
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