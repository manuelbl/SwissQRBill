//
// Swiss QR Bill Generator
// Copyright (c) 2022 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//

package net.codecrete.qrbill.pdf;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * PDF document
 */
public class Document {

    private final List<Reference> references;
    private final Reference catalogRef;
    private final Reference documentInfoRef;
    private final PageCollection pages;
    private final Reference pagesRef;
    private final Map<Font, Reference> fontReferences;

    /**
     * Create a new instance with the specified title.
     *
     * @param title the title of the document
     */
    public Document(String title) {
        references = new ArrayList<>();
        createReference(null); // dummy reference with index 0

        GeneralDict catalog = new GeneralDict("Catalog");
        catalog.add("Version", new Name("1.4"));
        catalogRef = createReference(catalog);

        GeneralDict documentInfo = new GeneralDict();
        documentInfoRef = createReference(documentInfo);
        documentInfo.add("Title", title);

        pages = new PageCollection(this);
        pagesRef = createReference(pages);
        catalog.add("Pages", pagesRef);

        fontReferences = new LinkedHashMap<>();
    }

    /**
     * Creates a new page with the specified dimensions.
     *
     * @param width  the width, in point.
     * @param height the height, in point.
     * @return the new page
     */
    public Page createPage(double width, double height) {
        Page page = new Page(this, pagesRef, width, height);
        pages.add(page);
        return page;
    }

    /**
     * Saves the document to the specified stream.
     *
     * @param stream the output stream
     */
    public void save(OutputStream stream) throws IOException {
        DocumentWriter writer = new DocumentWriter(stream);
        writer.write("%PDF-1.4\n");
        writer.write("%öäüß\n");
        writeBody(writer);
        int xrefOffset = writer.position();
        writeCrossReferenceTable(writer);
        writeTrailer(writer, xrefOffset);
        writer.close();
    }

    public Reference createReference(Object target) {
        int index = references.size();
        Reference reference = new Reference(index, target);
        references.add(reference);
        return reference;
    }

    public Reference getOrCreateFontReference(Font font) {
        Reference reference = fontReferences.get(font);
        if (reference != null)
            return reference;

        reference = createReference(font);
        fontReferences.put(font, reference);
        return reference;
    }

    private void writeBody(DocumentWriter writer) throws IOException {
        for (Reference reference : references) {
            reference.setOffset(writer.position());
            reference.writeDefinition(writer);
        }
    }

    private void writeCrossReferenceTable(DocumentWriter writer) throws IOException {
        writer.write("xref\n");
        writer.write("0 ");
        writer.write(references.size());
        writer.write("\n");
        for (Reference reference : references) {
            reference.writeCrossReference(writer);
        }
    }

    private void writeTrailer(DocumentWriter writer, int xrefOffset) throws IOException {
        GeneralDict dict = new GeneralDict();
        dict.add("Root", catalogRef);
        dict.add("Info", documentInfoRef);
        dict.add("Size", references.size());
        writer.write("trailer\n");
        dict.write(writer);
        writer.write("startxref\n");
        writer.write(xrefOffset);
        writer.write("\n");
        writer.write("%%EOF\n");
    }
}
