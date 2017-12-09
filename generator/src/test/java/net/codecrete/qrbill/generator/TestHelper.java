//
// Swiss QR Bill Generator
// Copyright (c) 2017 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
package net.codecrete.qrbill.generator;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import static org.junit.Assert.assertArrayEquals;

public class TestHelper {

    public static void assertFileContentsEqual(byte[] actualContent, String expectedFileName) {
        byte[] expectedContent = loadReferenceFile(expectedFileName);
        String fileExtension = expectedFileName.substring(expectedFileName.lastIndexOf('.'));

        try {
            if (fileExtension.equals(".pdf")) {
                clearPdfID(expectedContent);
                clearPdfID(actualContent);
            }
            assertArrayEquals(expectedContent, actualContent);

        } catch (AssertionError e) {
            saveActualFile(actualContent, fileExtension);
            throw e;
        }
    }

    private static byte[] loadReferenceFile(String filename) {
        try (InputStream is = QRCodeTest.class.getResourceAsStream("/" + filename)) {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            byte[] chunk = new byte[8192];
            while (true) {
                int len = is.read(chunk);
                if (len == -1)
                    break;
                buffer.write(chunk, 0, len);
            }
            return buffer.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void saveActualFile(byte[] data, String fileExtension) {
        Path file = Paths.get("actual" + fileExtension);
        try (OutputStream os = Files.newOutputStream(file, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            os.write(data);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void clearPdfID(byte[] pdfData) {
        int len = pdfData.length;
        int offset = Math.max(len - 128, 0);
        while (offset < len - 74) {
            if (pdfData[offset] == '/' && pdfData[offset+1] == 'I' && pdfData[offset+2] == 'D'
                    && pdfData[offset+3] == ' ' && pdfData[offset+4] == '[' && pdfData[offset+5] == '<') {
                for (int i = offset + 6; i < offset + 73; i++)
                    pdfData[i] = '0';
                return;
            }
            offset++;
        }

        throw new AssertionError("PDF ID not found");
    }
}
