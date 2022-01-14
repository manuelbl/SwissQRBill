//
// Swiss QR Bill Generator
// Copyright (c) 2022 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//

package net.codecrete.qrbill.pdf;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * Helper class for encoding PDF streams.
 */
public class PdfEncoding {

    private static final Charset ENCODING = Charset.forName("Cp1252");

    private static final DecimalFormat NUMBER_FORMAT = new DecimalFormat("0.###", new DecimalFormatSymbols(Locale.UK));

    /**
     * Creates a new writer for the specified output stream.
     * <p>
     * The writer is configured to use Windows Latin-1 (Cp1252) encoding.
     * </p>
     *
     * @param outputStream the output stream
     * @return the new writer
     */
    public static Writer createWriter(OutputStream outputStream) {
        return new OutputStreamWriter(outputStream, ENCODING);
    }

    /**
     * Writes text to the output writer.
     * <p>
     * The text is put in parentheses and escaped if needed.
     * </p>
     *
     * @param text   the text
     * @param writer the output writer
     * @throws IOException thrown if the error occurs during writing
     */
    public static void writeText(String text, Writer writer) throws IOException {
        writer.write('(');
        int length = text.length();
        int lastCopiedPosition = 0;
        for (int i = 0; i < length; i++) {
            char ch = text.charAt(i);
            if (ch >= ' ' && ch != '(' && ch != ')' && ch != '\\') continue;

            if (i > lastCopiedPosition) writer.write(text, lastCopiedPosition, i - lastCopiedPosition);

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
            writer.write(replacement);
            lastCopiedPosition = i + 1;
        }

        if (length > lastCopiedPosition) writer.write(text, lastCopiedPosition, length - lastCopiedPosition);
        writer.write(')');
    }

    /**
     * Write a number to the output writer.
     * <p>
     * The number will be written a maximum of 3 fractional digits.
     * </p>
     *
     * @param val    the number
     * @param writer the output writer
     * @throws IOException thrown if the error occurs during writing
     */
    public static void writeNumber(double val, Writer writer) throws IOException {
        writer.write(NUMBER_FORMAT.format(val));
    }
}
