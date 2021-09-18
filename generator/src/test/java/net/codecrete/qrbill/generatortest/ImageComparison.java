//
// Swiss QR Bill Generator
// Copyright (c) 2018 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//

package net.codecrete.qrbill.generatortest;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Compares two images pixel by pixel
 */
class ImageComparison {

    static void assertGrayscaleImageContentEquals(byte[] expectedContent, byte[] actualContent, int maxDiff) {
        // quick check based on file content
        if (Arrays.equals(expectedContent, actualContent))
            return;

        // read images
        BufferedImage expectedImage;
        BufferedImage actualImage;
        try {
            try (ByteArrayInputStream ios = new ByteArrayInputStream(expectedContent)) {
                expectedImage = ImageIO.read(ios);
            }
            try (ByteArrayInputStream ios = new ByteArrayInputStream(actualContent)) {
                actualImage = ImageIO.read(ios);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // compare meta data
        assertEquals(expectedImage.getWidth(), actualImage.getWidth(), "matching width");
        assertEquals(expectedImage.getHeight(), actualImage.getHeight(), "matching height");
        assertEquals(expectedImage.getType(), actualImage.getType(), "matching image type");
        assertEquals(expectedImage.getColorModel(), actualImage.getColorModel(), "matching color model");
        assertEquals(expectedImage.getSampleModel(), actualImage.getSampleModel(), "matching sample model");

        // retrieve pixels
        int[] expectedPixels = expectedImage.getData().getPixels(0, 0, expectedImage.getWidth(),
                expectedImage.getHeight(), (int[]) null);
        int[] actualPixels = actualImage.getData().getPixels(0, 0, actualImage.getWidth(), actualImage.getHeight(),
                (int[]) null);

        // compare pixels
        int length = expectedPixels.length;
        long diff = 0;
        for (int i = 0; i < length; i++) {
            if (expectedPixels[i] != actualPixels[i]) {
                int d = Math.abs(expectedPixels[i] - actualPixels[i]);
                diff += (long) d * d;
            }
        }

        assertTrue(diff <= maxDiff, "Pixel value difference " + diff);
    }
}
