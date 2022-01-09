//
// Swiss QR Bill Generator
// Copyright (c) 2022 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//

package net.codecrete.qrbill.pdf;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class GeneralDict implements Writable {

    private final Map<String, Object> dict = new LinkedHashMap<>();

    public GeneralDict() {
    }

    public GeneralDict(String type) {
        dict.put("Type", new Name(type));
    }

    @Override
    public void write(DocumentWriter writer) throws IOException {
        writer.write("<<\n");
        for (Map.Entry<String, Object> entry : dict.entrySet()) {
            writer.write("/");
            writer.write(entry.getKey());
            writer.write(" ");
            writer.writeObject(entry.getValue());
            writer.write("\n");
        }
        writer.write(">>\n");
    }

    public void add(String key, Object value) {
        dict.put(key, value);
    }
}
