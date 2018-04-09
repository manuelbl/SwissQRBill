//
// Swiss QR Bill Generator
// Copyright (c) 2017 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
package net.codecrete.qrbill.generator;

import java.io.IOException;

/**
 * Abstract base class for simplified implementation of {@code Canvas} classes.
 */
public abstract class AbstractCanvas implements Canvas {

    protected static final double MM_TO_PT = 72 / 25.4;

    @Override
    public int putMultilineText(String text, double x, double y, int fontSize, double maxWidth, double leading) throws IOException {
        String[] lines = FontMetrics.splitLines(text, maxWidth * MM_TO_PT, fontSize);
        putTextLines(lines, x, y, fontSize, leading);
        return lines.length;
    }

    @Override
    public void putTextLines(String[] lines, double x, double y, int fontSize, double leading) throws IOException {
        for (String line : lines) {
            putText(line, x, y, fontSize, false);
            y -= FontMetrics.getLineHeight(fontSize) + leading;
        }
    }
}
