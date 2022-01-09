//
// Swiss QR Bill Generator
// Copyright (c) 2022 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//

package net.codecrete.qrbill.pdf;

import java.io.IOException;

/**
 * Name object
 */
public class Name implements Writable {

    private final String value;

    public Name(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void write(DocumentWriter writer) throws IOException {
        writer.write("/");
        writer.write(value);
    }
}
