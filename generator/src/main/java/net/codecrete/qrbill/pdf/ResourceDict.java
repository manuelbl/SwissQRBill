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

/**
 * Dictionary for resources
 */
public class ResourceDict implements Writable {

    private final GeneralDict resources;
    private final Map<Font, Name> fontNames;
    private final Document document;

    public ResourceDict(Document document) {
        this.document = document;
        resources = new GeneralDict("Resources");
        fontNames = new LinkedHashMap<>();
    }

    public Name addFont(Font font) {
        Name name = fontNames.get(font);
        if (name != null)
            return name;

        String fname = String.format("F%d", fontNames.size() + 1);
        name = new Name(fname);
        fontNames.put(font, name);
        document.getOrCreateFontReference(font);
        return name;
    }

    public void write(DocumentWriter writer) throws IOException {
        GeneralDict fontDict = new GeneralDict();
        for (Map.Entry<Font, Name> entry : fontNames.entrySet()) {
            fontDict.add(entry.getValue().getValue(), document.getOrCreateFontReference(entry.getKey()));
        }
        resources.add("Font", fontDict);

        resources.write(writer);
    }
}
