//
// Swiss QR Bill Generator
// Copyright (c) 2022 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//

package net.codecrete.qrbill.pdf;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.List;

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
    private final OutputStreamWithPosition outputStream;
    private final Writer writer;

    /**
     * Creates a new document writer writing to the specified output stream.
     *
     * @param os output stream
     */
    public DocumentWriter(OutputStream os) {
        outputStream = new OutputStreamWithPosition(os);
        writer = PdfEncoding.createWriter(outputStream);
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
            write((Double) obj);
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
        PdfEncoding.writeText(str, writer);
    }

    /**
     * Writes the specified integer number.
     *
     * @param num the number
     * @throws IOException thrown if error occurs on the output stream
     */
    public void write(int num) throws IOException {
        writer.write(Integer.toString(num));
    }

    /**
     * Writes the specified number to the output stream.
     *
     * @param num the number
     * @throws IOException thrown if error occurs on the output stream
     */
    public void write(double num) throws IOException {
        PdfEncoding.writeNumber(num, writer);
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
        writer.write("[ ");
        for (Object obj : list) {
            writeObject(obj);
            writer.write(' ');
        }
        writer.write(']');
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
        writer.write("[ ");
        for (double d : array) {
            write(d);
            writer.write(' ');
        }
        writer.write(']');
    }

    /**
     * Writes the specified text.
     *
     * @param text the text
     * @throws IOException thrown if error occurs on the output stream
     */
    public void write(String text) throws IOException {
        writer.write(text);
    }

    /**
     * Writes the specified binary data to the output stream.
     *
     * @param data binary data.
     * @throws IOException thrown if error occurs on the output stream
     */
    public void write(byte[] data) throws IOException {
        writer.flush();
        outputStream.write(data);
    }

    /**
     * Closes this instance and the underlying output stream.
     *
     * @throws IOException thrown if error occurs on the output stream
     */
    public void close() throws IOException {
        writer.flush();
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
        writer.flush();
        return outputStream.position();
    }

    /**
     * Output stream tracking the writing position.
     */
    private static class OutputStreamWithPosition extends OutputStream {

        private final OutputStream inner;
        private int numBytes;

        /**
         * Creates a new instance wrapping the specified output stream
         *
         * @param inner the output stream
         */
        private OutputStreamWithPosition(OutputStream inner) {
            this.inner = inner;
        }

        /**
         * Gets the writing position.
         * <p>
         * The writing position is the same as the number
         * of bytes written to the output stream.
         * </p>
         *
         * @return the position
         */
        private int position() {
            return numBytes;
        }

        @Override
        public void write(int b) throws IOException {
            inner.write(b);
            numBytes += 1;
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            inner.write(b, off, len);
            numBytes += len;
        }

        @Override
        public void flush() throws IOException {
            inner.flush();
        }

        @Override
        public void close() throws IOException {
            inner.close();
        }
    }
}
