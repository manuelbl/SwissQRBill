//
// Swiss QR Bill Generator
// Copyright (c) 2022 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//

package net.codecrete.qrbill.pdf;

import java.io.IOException;

/**
 * Reference to an object
 */
public class Reference implements Writable {

    private final int index;
    private final Object target;
    private int offset;

    public Reference(int index, Object target) {
        this.index = index;
        this.target = target;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public void writeCrossReference(DocumentWriter writer) throws IOException {
        if (index == 0) {
            writer.write("0000000000 65535 f\r\n");
        } else {
            writer.write(String.format("%010d", offset));
            writer.write(" 00000 n\r\n");
        }
    }

    public void writeDefinition(DocumentWriter writer) throws IOException {
        if (index == 0)
            return;

        writer.write(index);
        writer.write(" 0 obj\n");
        writer.writeObject(target);
        writer.write("endobj\n");
    }

    public void write(DocumentWriter writer) throws IOException {
        writer.write(index);
        writer.write(" 0 R");
    }
}
