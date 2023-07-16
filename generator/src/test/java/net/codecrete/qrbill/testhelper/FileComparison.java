//
// Swiss QR Bill Generator
// Copyright (c) 2017 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
package net.codecrete.qrbill.testhelper;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

/**
 * Compares a generated file with a reference file.
 */
public class FileComparison {

    /**
     * Asserts that the specified file content matches the content of a reference
     * file.
     * <p>
     * If the actual content differs from the expected file content, an assertion
     * exception is thrown and the actual content is saved to a file starting with
     * the name "actual_".
     * </p>
     * <p>
     * If the actual content matches the expected file content, the file system is
     * checked for a file starting with the name "actual_". If it exists, it is
     * deleted.
     * </p>
     *
     * @param actualContent    content of actual file
     * @param expectedFileName file name of expected file (reference file)
     */
    public static void assertFileContentsEqual(byte[] actualContent, String expectedFileName) {

        try {
            String fileExtension = expectedFileName.substring(expectedFileName.lastIndexOf('.'));
            byte[] expectedContent = loadReferenceFile(expectedFileName, fileExtension.equals(".svg"));

            if (fileExtension.equals(".pdf")) {
                clearPdfID(expectedContent);
                clearPdfID(actualContent);
            }
            assertArrayEquals(expectedContent, actualContent);

        } catch (AssertionError e) {
            saveActualFile(actualContent, expectedFileName);
            throw e;
        } catch (IOException e) {
            saveActualFile(actualContent, expectedFileName);
            throw new RuntimeException(e);
        }

        deleteActualFile(expectedFileName);
    }

    public static void assertGrayscaleImageContentsEqual(byte[] actualContent, String expectedFileName, int maxDiff) {

        try {
            byte[] expectedContent = loadReferenceFile(expectedFileName, false);
            ImageComparison.assertGrayscaleImageContentEquals(expectedContent, actualContent, maxDiff);

        } catch (AssertionError e) {
            saveActualFile(actualContent, expectedFileName);
            throw e;
        } catch (IOException e) {
            saveActualFile(actualContent, expectedFileName);
            throw new RuntimeException(e);
        }

        deleteActualFile(expectedFileName);
    }

    private static byte[] loadReferenceFile(String filename, boolean doRemoveCR) throws IOException {
        try (InputStream is = FileComparison.class.getResourceAsStream("/" + filename)) {
            if (is == null)
                throw new FileNotFoundException(filename);
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            byte[] chunk = new byte[8192];
            while (true) {
                int len = is.read(chunk);
                if (len == -1)
                    break;
                if (doRemoveCR)
                    len = removeCR(chunk, len);
                buffer.write(chunk, 0, len);
            }
            return buffer.toByteArray();
        }
    }

    private static int removeCR(byte[] data, int len) {
        int tgt = 0;
        for (int i = 0; i < len; i++) {
            if (data[i] != '\r') {
                data[tgt] = data[i];
                tgt++;
            }
        }
        return tgt;
    }

    private static void saveActualFile(byte[] data, String expectedFileName) {
        Path file = Paths.get("actual_" + expectedFileName);
        try (OutputStream os = Files.newOutputStream(file, StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING)) {
            os.write(data);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void deleteActualFile(String expectedFileName) {
        Path file = Paths.get("actual_" + expectedFileName);
        try {
            if (Files.exists(file))
                Files.delete(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void clearPdfID(byte[] pdfData) {
        int len = pdfData.length;
        int offset = Math.max(len - 128, 0);
        while (offset < len - 74) {
            if (pdfData[offset] == '/' && pdfData[offset + 1] == 'I' && pdfData[offset + 2] == 'D'
                    && pdfData[offset + 3] == ' ' && pdfData[offset + 4] == '[' && pdfData[offset + 5] == '<') {
                for (int i = offset + 6; i < offset + 73; i++)
                    pdfData[i] = '0';
                return;
            }
            offset++;
        }

        throw new AssertionError("PDF ID not found");
    }
}
