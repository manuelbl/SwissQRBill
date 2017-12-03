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
        QRBill qrBill = new QRBill();
        SampleData.fillExample1(qrBill);
        byte[] svgReference = loadReferenceFile("qrcode_ex1.svg");
        byte[] svg = qrBill.generate(QRBill.BillFormat.QRCodeOnly, QRBill.GraphicsFormat.SVG);
        try {
            assertArrayEquals(svgReference, svg);
        } catch (AssertionError e) {
            saveActualFile(svg);
            throw e;
        }
    }

    @Test
    public void SVGTest2() {
        QRBill qrBill = new QRBill();
        SampleData.fillExample2(qrBill);
        byte[] svgReference = loadReferenceFile("qrcode_ex2.svg");
        byte[] svg = qrBill.generate(QRBill.BillFormat.QRCodeOnly, QRBill.GraphicsFormat.SVG);
        try {
            assertArrayEquals(svgReference, svg);
        } catch (AssertionError e) {
            saveActualFile(svg);
            throw e;
        }
    }

    @Test
    public void SVGTest3() {
        QRBill qrBill = new QRBill();
        SampleData.fillExample3(qrBill);
        byte[] svgReference = loadReferenceFile("qrcode_ex3.svg");
        byte[] svg = qrBill.generate(QRBill.BillFormat.QRCodeOnly, QRBill.GraphicsFormat.SVG);
        try {
            assertArrayEquals(svgReference, svg);
        } catch (AssertionError e) {
            saveActualFile(svg);
            throw e;
        }
    }

    @Test
    public void SVGTest4() {
        QRBill qrBill = new QRBill();
        SampleData.fillExample3(qrBill);
        byte[] svgReference = loadReferenceFile("qrcode_ex4.svg");
        byte[] svg = qrBill.generate(QRBill.BillFormat.QRCodeOnly, QRBill.GraphicsFormat.SVG);
        try {
            assertArrayEquals(svgReference, svg);
        } catch (AssertionError e) {
            saveActualFile(svg);
            throw e;
        }
    }


    public static byte[] loadReferenceFile(String filename) {
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

    public static void saveActualFile(byte[] data) {
        Path file = Paths.get("actual.svg");
        try (OutputStream os = Files.newOutputStream(file, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            os.write(data);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
