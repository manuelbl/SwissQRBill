//
// Swiss QR Bill Generator
// Copyright (c) 2017 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
package net.codecrete.qrbill.generatortest;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

/**
 * Compares a generated file with an reference file.
 */
class FileComparison {

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
    static void assertFileContentsEqual(byte[] actualContent, String expectedFileName) {

        try {
            byte[] expectedContent = loadReferenceFile(expectedFileName);
            String fileExtension = expectedFileName.substring(expectedFileName.lastIndexOf('.'));

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

    static void assertGrayscaleImageContentsEqual(byte[] actualContent, String expectedFileName) {

        try {
            byte[] expectedContent = loadReferenceFile(expectedFileName);
            ImageComparison.assertGrayscaleImageContentEquals(expectedContent, actualContent);

        } catch (AssertionError e) {
            saveActualFile(actualContent, expectedFileName);
            throw e;
        } catch (IOException e) {
            saveActualFile(actualContent, expectedFileName);
            throw new RuntimeException(e);
        }

        deleteActualFile(expectedFileName);
    }

    private static byte[] loadReferenceFile(String filename) throws IOException {
        try (InputStream is = FileComparison.class.getResourceAsStream("/" + filename)) {
            if (is == null)
                throw new FileNotFoundException(filename);
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            byte[] chunk = new byte[8192];
            while (true) {
                int len = is.read(chunk);
                if (len == -1)
                    break;
                buffer.write(chunk, 0, len);
            }
            return buffer.toByteArray();
        }
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
