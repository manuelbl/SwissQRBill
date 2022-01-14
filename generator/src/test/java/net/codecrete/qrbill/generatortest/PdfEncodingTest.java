//
// Swiss QR Bill Generator
// Copyright (c) 2022 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
package net.codecrete.qrbill.generatortest;

import net.codecrete.qrbill.pdf.PdfEncoding;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for PdfEncoding class
 */
@DisplayName("PdfEncoder test")
class PdfEncodingTest {

    @Test
    void createWriter_hasCorrectEncoding() throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try (Writer writer = PdfEncoding.createWriter(buffer)) {
            writer.write("ÀÁÂÃÄÅÆÇÈÉÊËÌÍÎÏÐÑÒÓÔÕÖ×ØÙÚÛÜÝÞßàáâãäåæçèéêëìíîïðñòóôõö÷øùúûüýþÿ");
        }

        byte[] expected = new byte[64];
        for (int i = 0; i < 64; i++)
            expected[i] = (byte) (0xc0 + i);

        assertArrayEquals(expected, buffer.toByteArray());
    }

    @Test
    void writeText_addsParentheses() throws IOException {
        assertEquals("(abc)", toPdfString("abc"));
        assertEquals("()", toPdfString(""));
    }

    @Test
    void writeText_escapesSpecialCharacters() throws IOException {
        assertEquals("(abc\\(x\\)def)", toPdfString("abc(x)def"));
        assertEquals("(abc\\r\\ndef\\tghi)", toPdfString("abc\r\ndef\tghi"));
        assertEquals("(--\\\\--)", toPdfString("--\\--"));
        assertEquals("(--\\003--)", toPdfString("--\003--"));
        assertEquals("(\\r)", toPdfString("\r"));
        assertEquals("(abc\\r)", toPdfString("abc\r"));
        assertEquals("(\\tdef)", toPdfString("\tdef"));
    }

    @Test
    void writeNumber_usesFewFractionalDigits() throws IOException {
        assertEquals("123", toNumberString(123.0));
        assertEquals("-123", toNumberString(-123.0));
        assertEquals("456.1", toNumberString(456.1));
        assertEquals("789.12", toNumberString(789.12));
        assertEquals("345.678", toNumberString(345.678));
        assertEquals("678.901", toNumberString(678.9012));
        assertEquals("234.568", toNumberString(234.56789));
        assertEquals("0", toNumberString(0.0));
        assertEquals("0.123", toNumberString(0.123));
        assertEquals("0", toNumberString(0.00049));
        assertEquals("0", toNumberString(-0.00049));
    }

    private static String toPdfString(String input) throws IOException {
        StringWriter writer = new StringWriter();
        PdfEncoding.writeText(input, writer);
        return writer.toString();
    }

    private static String toNumberString(double val) throws IOException {
        StringWriter writer = new StringWriter();
        PdfEncoding.writeNumber(val, writer);
        return writer.toString();
    }
}
