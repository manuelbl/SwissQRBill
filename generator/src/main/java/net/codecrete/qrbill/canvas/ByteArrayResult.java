//
// Swiss QR Bill Generator
// Copyright (c) 2017 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
package net.codecrete.qrbill.canvas;

import java.io.IOException;

/**
 * Interface for returning a result as a byte array.
 */
public interface ByteArrayResult {

    /**
     * Gets the resulting graphics as a byte array.
     *
     * @return the byte array
     * @throws IOException thrown if the construction of the byte array fails
     */
    byte[] toByteArray() throws IOException;
}
