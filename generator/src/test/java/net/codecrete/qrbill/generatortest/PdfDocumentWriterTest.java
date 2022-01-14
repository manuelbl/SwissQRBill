//
// Swiss QR Bill Generator
// Copyright (c) 2022 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
package net.codecrete.qrbill.generatortest;

import net.codecrete.qrbill.pdf.DocumentWriter;
import net.codecrete.qrbill.pdf.Writable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for DocumentWriter class
 */
@DisplayName("PDF DocumentWriter tests")
public class PdfDocumentWriterTest {

    private static final Charset ENCODING = Charset.forName("Cp1252");

    @Test
    void strings_areWritten() {
        assertEquals("ABCD", stringWithWriter((w) -> w.write("ABCD")));
        assertEquals("Š€éÃ", stringWithWriter((w) -> w.write("Š€éÃ")));
        assertEquals("\r\n\t", stringWithWriter((w) -> w.write("\r\n\t")));
    }

    @Test
    void numbers_areWritten() {
        assertEquals("123", stringWithWriter((w) -> w.write(123)));
        assertEquals("1.25", stringWithWriter((w) -> w.write(1.25)));
        assertEquals("0.346", stringWithWriter((w) -> w.write(0.34567)));
    }

    @Test
    void pdfStrings_areWritten() {
        assertEquals("(ABCD)", stringWithWriter((w) -> w.writeString("ABCD")));
        assertEquals("(Š€éÃ)", stringWithWriter((w) -> w.writeString("Š€éÃ")));
        assertEquals("(\\r\\n\\t)", stringWithWriter((w) -> w.writeString("\r\n\t")));
    }

    @Test
    void list_isWritten() {
        List<Integer> list = Arrays.asList(3, 2, 1);
        assertEquals("[ 3 2 1 ]", stringWithWriter((w) -> w.writeList(list)));
    }

    @Test
    void array_isWritten() {
        double[] array = new double[]{12.0, 0.456, 73222.1};
        assertEquals("[ 12 0.456 73222.1 ]", stringWithWriter((w) -> w.writeDoubleArray(array)));
    }

    @Test
    void object_areWritten() {
        List<Integer> list = Arrays.asList(3, 2, 1);
        double[] array = new double[]{12.0, 0.456, 73222.1};
        assertEquals("[ 3 2 1 ]", stringWithWriter((w) -> w.writeObject(list)));
        assertEquals("[ 12 0.456 73222.1 ]", stringWithWriter((w) -> w.writeObject(array)));
        assertEquals("(xyz)", stringWithWriter((w) -> w.writeObject("xyz")));
        assertEquals("123.456", stringWithWriter((w) -> w.writeObject(123.456)));
        assertEquals("ffgghh", stringWithWriter((w) -> w.writeObject((Writable) writer -> writer.write("ffgghh"))));
    }

    @Test
    void binaryData_isWritten() throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        DocumentWriter documentWriter = new DocumentWriter(buffer);
        documentWriter.write("abc");
        documentWriter.write(new byte[]{3, 4, 5});
        documentWriter.write("def");
        documentWriter.close();
        assertArrayEquals(new byte[]{97, 98, 99, 3, 4, 5, 100, 101, 102}, buffer.toByteArray());
    }

    @Test
    void position_isCorrect() {
        assertEquals(3, positionWithWriter((w) -> w.write("jkl")));
        assertEquals(7, positionWithWriter((w) -> w.write(345.678)));
    }

    private static String stringWithWriter(BlockWithIOException<DocumentWriter> block) {
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            DocumentWriter documentWriter = new DocumentWriter(buffer);
            block.apply(documentWriter);
            documentWriter.close();
            return new String(buffer.toByteArray(), ENCODING);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static int positionWithWriter(BlockWithIOException<DocumentWriter> block) {
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            DocumentWriter documentWriter = new DocumentWriter(buffer);
            block.apply(documentWriter);
            int pos = documentWriter.position();
            documentWriter.close();
            return pos;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FunctionalInterface
    public interface BlockWithIOException<T> {
        void apply(T t) throws IOException;
    }
}
