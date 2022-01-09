//
// Swiss QR Bill Generator
// Copyright (c) 2022 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//

package net.codecrete.qrbill.pdf;

import java.io.IOException;

/**
 * Object capable of writing itself to output
 */
public interface Writable {
    /**
     * Write this instance to the specified writer
     *
     * @param writer writer instance
     */
    void write(DocumentWriter writer) throws IOException;
}
