//
// Swiss QR Bill Generator
// Copyright (c) 2022 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
package net.codecrete.qrbill.generatortest;

import net.codecrete.qrbill.pdf.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for ContentWriter class
 */
@DisplayName("PDF ContentWriter tests")
public class PdfContentStreamTest {

    private static final Charset ENCODING = Charset.forName("Cp1252");

    @Test
    void simpleOperators_produceContent() {
        assertEquals("q\n", streamContents(ContentStream::saveGraphicsState));
        assertEquals("Q\n", streamContents(ContentStream::restoreGraphicsState));
        assertEquals("h\n", streamContents(ContentStream::closePath));
        assertEquals("S\n", streamContents(ContentStream::stroke));
        assertEquals("f\n", streamContents(ContentStream::fill));
        assertEquals("BT\n", streamContents(ContentStream::beginText));
        assertEquals("ET\n", streamContents(ContentStream::endText));
    }

    @Test
    void transform_producesContent() {
        double[] matrix = new double[]{1, 0.3, 2, 0.4, -0.3, 0.35};
        assertEquals("1 0.3 2 0.4 -0.3 0.35 cm\n", streamContents((cm) -> cm.transform(matrix)));
    }

    @Test
    void setColors_produceContent() {
        assertEquals("0.1 0 0.75 RG\n", streamContents((cm) -> cm.setStrokingColor(0.1, 0, 0.75)));
        assertEquals("1 0.3 0 rg\n", streamContents((cm) -> cm.setNonStrokingColor(1, 0.3, 0)));
    }

    @Test
    void setLineAttributes_produceContent() {
        assertEquals("3.5 w\n", streamContents((cm) -> cm.setLineWidth(3.5)));
        assertEquals("1 J\n", streamContents((cm) -> cm.setLineCapStyle(1)));
        double[] pattern = new double[]{1, 2};
        assertEquals("[1 2 ] 0 d\n", streamContents((cm) -> cm.setLineDashPattern(pattern, 0)));
    }

    @Test
    void pathOperators_produceContent() {
        assertEquals("100 200.5 m\n", streamContents((cm) -> cm.moveTo(100, 200.5)));
        assertEquals("150.7 37.8 l\n", streamContents((cm) -> cm.lineTo(150.7, 37.8)));
        assertEquals("10 20 15 25 20 35 c\n", streamContents((cm) -> cm.curveTo(10, 20, 15, 25, 20, 35)));
    }

    @Test
    void addRect_producesContent() {
        assertEquals("100 30 37.5 19.2 re\n", streamContents((cm) -> cm.addRect(100, 30, 37.5, 19.2)));
    }

    @Test
    void setFont_producesContent() {
        assertEquals("/F1 16 Tf\n", streamContents((cm) -> cm.setFont(Font.HelveticaBold, 16)));
    }

    @Test
    void textOperators_produceContent() {
        assertEquals("12.7 45.9 Td\n", streamContents((cm) -> cm.newLineAtOffset(12.7, 45.9)));
        assertEquals("(Hello, world!) Tj\n", streamContents((cm) -> cm.showText("Hello, world!")));
    }

    private static final byte[] STREAM_START = "stream\r\n".getBytes(StandardCharsets.US_ASCII);
    private static final byte[] STREAM_END = "\r\nendstream\n".getBytes(StandardCharsets.US_ASCII);

    private static String streamContents(BlockWithIOException<ContentStream> block) {
        try {
            Document document = new Document("title");
            ResourceDict resources = new ResourceDict(document);
            ContentStream stream = new ContentStream(resources);
            block.apply(stream);

            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            DocumentWriter documentWriter = new DocumentWriter(buffer);
            stream.write(documentWriter);
            documentWriter.close();
            byte[] documentContents = buffer.toByteArray();
            int start = find(documentContents, STREAM_START) + 8;
            int end = find(documentContents, STREAM_END);
            return decompress(documentContents, start, end);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static int find(byte[] haystack, byte[] needle) {
        int haystackLen = haystack.length;
        int needleLen = needle.length;

        nextHaystackPos:
        for (int i = 0; i <= haystackLen - needleLen; i++) {
            for (int j = 0; j < needleLen; j++) {
                if (haystack[i + j] != needle[j]) continue nextHaystackPos;
            }
            return i;
        }
        return -1;
    }

    private static String decompress(byte[] data, int start, int end) {
        try {
            Inflater inflater = new Inflater();
            inflater.setInput(data, start, end - start);
            byte[] result = new byte[2000];
            int len = inflater.inflate(result);
            inflater.end();
            return new String(result, 0, len, ENCODING);

        } catch (DataFormatException e) {
            throw new RuntimeException(e);
        }
    }


    @FunctionalInterface
    public interface BlockWithIOException<T> {
        void apply(T t) throws IOException;
    }
}
