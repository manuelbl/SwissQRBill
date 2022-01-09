//
// Swiss QR Bill Generator
// Copyright (c) 2022 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//

package net.codecrete.qrbill.pdf;

import java.io.IOException;

/**
 * Page in PDF document.
 */
public class Page implements Writable {

    private final GeneralDict dict;
    private final ContentStream contents;

    public Page(Document document, Reference parent, double width, double height) {
        dict = new GeneralDict("Page");
        dict.add("Parent", parent);
        dict.add("MediaBox", new double[]{0, 0, width, height});

        ResourceDict resources = new ResourceDict(document);
        Reference resourcesRef = document.createReference(resources);
        dict.add("Resources", resourcesRef);

        contents = new ContentStream(resources);
        Reference contentsRef = document.createReference(contents);
        dict.add("Contents", contentsRef);
    }

    public ContentStream getContents() {
        return contents;
    }

    public void write(DocumentWriter writer) throws IOException {
        dict.write(writer);
    }
}
