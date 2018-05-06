//
// Swiss QR Bill Generator
// Copyright (c) 2017 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
package net.codecrete.qrbill.canvas;

import java.io.IOException;

/**
 * Abstract base class for simplified implementation of {@code Canvas} classes.
 */
public abstract class AbstractCanvas implements Canvas {

    protected static final double MM_TO_PT = 72 / 25.4;

    @Override
    public void putTextLines(String[] lines, double x, double y, int fontSize, double leading) throws IOException {
        for (String line : lines) {
            putText(line, x, y, fontSize, false);
            y -= FontMetrics.getLineHeight(fontSize) + leading;
        }
    }
}
