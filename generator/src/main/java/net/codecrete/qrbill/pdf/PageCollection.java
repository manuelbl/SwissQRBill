//
// Swiss QR Bill Generator
// Copyright (c) 2022 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//

package net.codecrete.qrbill.pdf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PageCollection implements Writable {
    private final List<Reference> pages;
    private final Document document;

    public PageCollection(Document document) {
        pages = new ArrayList<>();
        this.document = document;
    }

    public void add(Object node) {
        Reference pageRef = document.createReference(node);
        pages.add(pageRef);
    }

    public void write(DocumentWriter writer) throws IOException {
        GeneralDict dict = new GeneralDict("Pages");
        dict.add("Count", pages.size());
        dict.add("Kids", pages);
        dict.write(writer);
    }
}
