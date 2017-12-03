//
// Swiss QR Bill Generator
// Copyright (c) 2017 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
package net.codecrete.qrbill.generator;

import java.io.Closeable;
import java.io.IOException;

public interface GraphicsPort extends Closeable {
    void startQRCode(double x, double y, double actualSize, double virtualSize) throws IOException;
    void endQRCode() throws IOException;
    void startPath() throws IOException;
    void addRectangle(double x, double y, double width, double height) throws IOException;
    void fillPath(int color) throws IOException;
    byte[] getResult() throws IOException;
}
