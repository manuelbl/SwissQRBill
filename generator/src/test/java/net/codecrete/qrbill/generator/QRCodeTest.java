//
// Swiss QR Bill Generator
// Copyright (c) 2017 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
package net.codecrete.qrbill.generator;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.*;

import static org.junit.Assert.assertArrayEquals;

public class QRCodeTest {

    @Test
    public void SVGTest1() {
        Bill bill = SampleData.getExample1();
        byte[] svg = QRBill.generate(bill, QRBill.BillFormat.QRCodeOnly, QRBill.GraphicsFormat.SVG);
        compareFileContents(svg, "qrcode_ex1.svg");
    }

    @Test
    public void SVGTest2() {
        Bill bill = SampleData.getExample2();
        byte[] svg = QRBill.generate(bill, QRBill.BillFormat.QRCodeOnly, QRBill.GraphicsFormat.SVG);
        compareFileContents(svg, "qrcode_ex2.svg");
    }

    @Test
    public void SVGTest3() {
        Bill bill = SampleData.getExample3();
        byte[] svg = QRBill.generate(bill, QRBill.BillFormat.QRCodeOnly, QRBill.GraphicsFormat.SVG);
        compareFileContents(svg, "qrcode_ex3.svg");
    }

    @Test
    public void SVGTest4() {
        Bill bill = SampleData.getExample4();
        byte[] svg = QRBill.generate(bill, QRBill.BillFormat.QRCodeOnly, QRBill.GraphicsFormat.SVG);
        compareFileContents(svg, "qrcode_ex4.svg");
    }


    private void compareFileContents(byte[] actualContent, String expectedFileName) {
        byte[] exptectedContent = loadReferenceFile(expectedFileName);
        try {
            assertArrayEquals(exptectedContent, actualContent);
        } catch (AssertionError e) {
            saveActualFile(actualContent);
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

    private static void saveActualFile(byte[] data) {
        Path file = Paths.get("actual.svg");
        try (OutputStream os = Files.newOutputStream(file, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            os.write(data);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
