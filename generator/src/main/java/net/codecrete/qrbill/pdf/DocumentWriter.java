//
// Swiss QR Bill Generator
// Copyright (c) 2022 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//

package net.codecrete.qrbill.pdf;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;

/**
 * PDF document writer.
 * <p>
 * Supports switching between writing text and writing binary data.
 * Additionally, the current position (bytes written) can be queried.
 * </p>
 * <p>
 * Text is encoded using the CP1252 (Windows Latin 1) encoding.
 * </p>
 */
public class DocumentWriter {
    private static final DecimalFormat NUMBER_FORMAT = new DecimalFormat("0.###", new DecimalFormatSymbols(Locale.UK));
    private static final Charset ENCODING;

    static {
        ENCODING = Charset.forName("Cp1252");
    }

    private final OutputStream outputStream;
    private final CharsetEncoder encoder;
    private final ByteBuffer outputBuffer;
    private int numBytes;
    private boolean isEncoderFlushed;

    /**
     * Creates a new document writer writing to the specified output stream.
     *
     * @param os output stream
     */
    public DocumentWriter(OutputStream os) {
        outputStream = os;
        encoder = ENCODING.newEncoder();
        outputBuffer = ByteBuffer.allocate(1024);
        isEncoderFlushed = true;
    }

    /**
     * Writes the specified object to the output stream.
     * <p>
     * The object is written according to its PDF type.
     * Supported types are numbers, strings, names, arrays,
     * dictionaries and streams.
     * </p>
     *
     * @param obj the object
     * @throws IOException thrown if error occurs on the output stream
     */
    public void writeObject(Object obj) throws IOException {
        if (obj instanceof Writable) {
            ((Writable) obj).write(this);
        } else if (obj instanceof List<?>) {
            writeList((List<?>) obj);
        } else if (obj instanceof double[]) {
            writeDoubleArray((double[]) obj);
        } else if (obj instanceof String) {
            writeString((String) obj);
        } else if (obj instanceof Double) {
            writeNumber((Double) obj);
        } else {
            this.write(obj.toString());
        }
    }

    /**
     * Writes the specified string to the output stream.
     * <p>
     * The string is properly escaped and enclosed in parentheses.
     * </p>
     *
     * @param str the string
     * @throws IOException thrown if error occurs on the output stream
     */
    public void writeString(String str) throws IOException {
        write("(");
        writeEscapedString(str);
        write(")");
    }

    /**
     * Writes the specified number to the output stream.
     *
     * @param num the number
     * @throws IOException thrown if error occurs on the output stream
     */
    public void writeNumber(double num) throws IOException {
        write(num);
    }

    /**
     * Writes the specified list to the output stream.
     * <p>
     * The list is enclosed in square brackets (as a PDF array).
     * </p>
     *
     * @param list the list
     * @throws IOException thrown if error occurs on the output stream
     */
    public void writeList(List<?> list) throws IOException {
        write("[ ");
        for (Object obj : list) {
            writeObject(obj);
            write(" ");
        }
        write("]");
    }

    /**
     * Writes the specified array of numbers to the output stream.
     * <p>
     * The array is enclosed in square brackets.
     * </p>
     *
     * @param array the array
     * @throws IOException thrown if error occurs on the output stream
     */
    public void writeDoubleArray(double[] array) throws IOException {
        write("[ ");
        for (double d : array) {
            write(d);
            write(" ");
        }
        write("]");
    }

    private void writeEscapedString(String text) throws IOException {
        int length = text.length();
        int lastCopiedPosition = 0;
        for (int i = 0; i < length; i++) {
            char ch = text.charAt(i);
            if (ch >= ' ' && ch != '(' && ch != ')' && ch != '\\') continue;

            if (i > lastCopiedPosition) write(text, lastCopiedPosition, i);

            String replacement;
            switch (ch) {
                case '(':
                    replacement = "\\(";
                    break;
                case ')':
                    replacement = "\\)";
                    break;
                case '\\':
                    replacement = "\\\\";
                    break;
                case '\n':
                    replacement = "\\n";
                    break;
                case '\r':
                    replacement = "\\r";
                    break;
                case '\t':
                    replacement = "\\t";
                    break;
                default:
                    replacement = "000" + Integer.toOctalString(ch);
                    replacement = replacement.substring(replacement.length() - 3);
                    break;
            }
            write(replacement);
            lastCopiedPosition = i + 1;
        }

        if (length > lastCopiedPosition) write(text, lastCopiedPosition, length);
    }

    /**
     * Writes the specified text.
     *
     * @param text the text
     * @throws IOException thrown if error occurs on the output stream
     */
    public void write(String text) throws IOException {
        encodeText(CharBuffer.wrap(text));
    }

    /**
     * Writes the specified range of the text.
     *
     * @param text  the text
     * @param start the start position
     * @param end   the end position
     * @throws IOException thrown if error occurs on the output stream
     */
    public void write(String text, int start, int end) throws IOException {
        encodeText(CharBuffer.wrap(text, start, end));
    }

    /**
     * Writes the specified integer number.
     *
     * @param num the number
     * @throws IOException thrown if error occurs on the output stream
     */
    public void write(int num) throws IOException {
        String text = Integer.toString(num);
        write(text);
    }

    /**
     * Writes the specified floating-point number.
     * <p>The number will be formatted with at most 3 fractional digits.</p>
     *
     * @param num the number
     * @throws IOException thrown if error occurs on the output stream
     */
    public void write(double num) throws IOException {
        String text = NUMBER_FORMAT.format(num);
        write(text);
    }

    /**
     * Writes the specified binary data to the output stream.
     *
     * @param data binary data.
     * @throws IOException thrown if error occurs on the output stream
     */
    public void write(byte[] data) throws IOException {
        flushEncoder();
        outputStream.write(data);
        numBytes += data.length;
    }

    /**
     * Closes this instance and the underlying output stream.
     *
     * @throws IOException thrown if error occurs on the output stream
     */
    public void close() throws IOException {
        flushEncoder();
        outputStream.close();
    }

    /**
     * Gets the current position in the output stream.
     * <p>
     * The position is equivalent to the number of bytes written
     * to the output stream.
     * </p>
     *
     * @return the position
     * @throws IOException thrown if flushing the buffers fails
     */
    public int position() throws IOException {
        flushEncoder();
        return numBytes;
    }

    private void encodeText(CharBuffer text) throws IOException {
        isEncoderFlushed = false;
        while (text.hasRemaining()) {
            CoderResult cr = encoder.encode(text, outputBuffer, false);
            if (cr.isUnderflow()) break;
            if (cr.isOverflow()) {
                flushOutputBuffer();
                continue;
            }
            cr.throwException();
        }

        assert !text.hasRemaining();
    }

    private void flushOutputBuffer() throws IOException {
        if (outputBuffer.position() == 0) return;

        outputStream.write(outputBuffer.array(), 0, outputBuffer.position());
        numBytes += outputBuffer.position();
        outputBuffer.position(0);
    }

    private void flushEncoder() throws IOException {
        if (isEncoderFlushed) return;

        while (true) {
            CoderResult cr = encoder.encode(CharBuffer.wrap(""), outputBuffer, true);
            if (cr.isUnderflow()) break;
            if (cr.isOverflow()) {
                flushOutputBuffer();
                continue;
            }
            cr.throwException();
        }

        while (true) {
            CoderResult cr = encoder.flush(outputBuffer);
            if (cr.isUnderflow()) break;
            if (cr.isOverflow()) {
                flushOutputBuffer();
                continue;
            }
            cr.throwException();
        }

        isEncoderFlushed = true;
        encoder.reset();
        flushOutputBuffer();
    }
}
