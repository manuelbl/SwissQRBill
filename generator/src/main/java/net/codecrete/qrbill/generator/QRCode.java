//
// Swiss QR Bill Generator
// Copyright (c) 2017 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
package net.codecrete.qrbill.generator;

import java.io.IOException;

import net.codecrete.io.nayuki.qrcodegen.QrCode;
import net.codecrete.qrbill.canvas.Canvas;

/**
 * Generates the QR code for the Swiss QR bill.
 * <p>
 * Also provides functions to generate and decode the string embedded in the QR
 * code.
 * </p>
 */
class QRCode {

    static final double SIZE = 46; // mm

    private String embeddedText;

    /**
     * Creates an instance of the QR code for the specified bill data.
     * <p>
     * The bill data must have been validated and cleaned.
     * </p>
     * 
     * @param bill bill data
     */
    QRCode(Bill bill) {
        embeddedText = QRCodeText.create(bill);
    }

    /**
     * Draws the QR code to the specified graphics context (canvas). The QR code is
     * always 46 mm by 46 mm.
     * 
     * @param graphics graphics context
     * @param offsetX  x offset
     * @param offsetY  y offset
     * @throws IOException exception thrown in case of error in graphics context
     */
    void draw(Canvas graphics, double offsetX, double offsetY) throws IOException {
        QrCode qrCode = QrCode.encodeText(embeddedText, QrCode.Ecc.MEDIUM);

        boolean[][] modules = copyModules(qrCode);
        clearSwissCrossArea(modules);

        graphics.setTransformation(offsetX, offsetY, 0, SIZE / modules.length / 25.4 * 72, SIZE / modules.length / 25.4 * 72);
        graphics.startPath();
        drawModulesPath(graphics, modules);
        graphics.fillPath(0);
        graphics.setTransformation(offsetX, offsetY, 0, 1, 1);

        // Swiss cross
        graphics.startPath();
        graphics.addRectangle(20, 20, 6, 6);
        graphics.fillPath(0);
        final double BAR_WIDTH = 7 / 6.0;
        final double BAR_LENGTH = 35 / 9.0;
        graphics.startPath();
        graphics.addRectangle(23 - BAR_WIDTH / 2, 23 - BAR_LENGTH / 2, BAR_WIDTH, BAR_LENGTH);
        graphics.addRectangle(23 - BAR_LENGTH / 2, 23 - BAR_WIDTH / 2, BAR_LENGTH, BAR_WIDTH);
        graphics.fillPath(0xffffff);
    }

    private void drawModulesPath(Canvas graphics, boolean[][] modules) throws IOException {
        // Simple algorithm to reduce the number of drawn rectangles
        int size = modules.length;
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                if (modules[y][x]) {
                    drawLargestRectangle(graphics, modules, x, y);
                }
            }
        }
    }

    // Simple algorithms to reduce the number of rectangles for drawing the QR code
    // and reduce SVG size
    private void drawLargestRectangle(Canvas graphics, boolean[][] modules, int x, int y) throws IOException {
        int size = modules.length;

        int bestW = 1;
        int bestH = 1;
        int maxArea = 1;

        int xLimit = size;
        int iy = y;
        while (iy < size && modules[iy][x]) {
            int w = 0;
            while (x + w < xLimit && modules[iy][x + w])
                w++;
            int area = w * (iy - y + 1);
            if (area > maxArea) {
                maxArea = area;
                bestW = w;
                bestH = iy - y + 1;
            }
            xLimit = x + w;
            iy++;
        }

        final double unit = 25.4 / 72;
        graphics.addRectangle(x * unit, (size - y - bestH) * unit, bestW * unit, bestH * unit);
        clearRectangle(modules, x, y, bestW, bestH);
    }

    private static void clearSwissCrossArea(boolean[][] modules) {
        // The Swiss cross area is supposed to be 7 by 7 mm in the center of
        // the QR code, which is 46 by 46 mm.
        // We clear sufficient modules to make room for the cross.
        int size = modules.length;
        int start = (int) Math.floor((46 - 6.8) / 2 * size / 46);
        clearRectangle(modules, start, start, size - 2 * start, size - 2 * start);
    }

    private static boolean[][] copyModules(QrCode qrCode) {
        int size = qrCode.size;
        boolean[][] modules = new boolean[size][size];
        for (int y = 0; y < size; y++)
            for (int x = 0; x < size; x++)
                modules[y][x] = qrCode.getModule(x, y);
        return modules;
    }

    private static void clearRectangle(boolean[][] modules, int x, int y, int width, int height) {
        for (int iy = y; iy < y + height; iy++)
            for (int ix = x; ix < x + width; ix++)
                modules[iy][ix] = false;
    }

}
