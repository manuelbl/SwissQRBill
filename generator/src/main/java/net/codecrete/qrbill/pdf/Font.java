//
// Swiss QR Bill Generator
// Copyright (c) 2022 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//

package net.codecrete.qrbill.pdf;

import java.io.IOException;
import java.util.Objects;

/**
 * Font for PDF document
 */
public class Font implements Writable {
    /**
     * Helvetica regular base font.
     */
    public static final Font Helvetica = createBaseFont("Helvetica");
    /**
     * Helvetica bold base font.
     */
    public static final Font HelveticaBold = createBaseFont("Helvetica-Bold");
    private final String name;
    private final String subtype;
    private final String encoding;

    private Font(String name, String subtype, String encoding) {
        this.name = name;
        this.subtype = subtype;
        this.encoding = encoding;
    }

    /**
     * Creates a base font for the specified font name.
     * <p>
     * Font subtype "Type1" and WinAnsi Encoding is assumed.
     * </p>
     *
     * @param fontname the font name
     * @return a new font instance
     */
    private static Font createBaseFont(String fontname) {
        return new Font(fontname, "Type1", "WinAnsiEncoding");
    }

    public void write(DocumentWriter writer) throws IOException {
        GeneralDict dict = new GeneralDict("Font");
        dict.add("Subtype", new Name(subtype));
        dict.add("BaseFont", new Name(name));
        dict.add("Encoding", new Name(encoding));
        dict.write(writer);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Font font = (Font) o;
        return name.equals(font.name) && subtype.equals(font.subtype) && encoding.equals(font.encoding);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, subtype, encoding);
    }
}
