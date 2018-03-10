//
// Swiss QR Bill Generator
// Copyright (c) 2017 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
package net.codecrete.qrbill.generator;

import org.junit.Test;

public class A6BillTest {

    @Test
    public void createA6Bill1() {
        Bill bill = SampleData.getExample1();
        byte[] svg = QRBill.generate(bill, QRBill.BillFormat.A6_LANDSCAPE_SHEET, QRBill.GraphicsFormat.SVG);
        TestHelper.assertFileContentsEqual(svg, "a6bill_ex1.svg");
    }

    @Test
    public void createA6Bill2() {
        Bill bill = SampleData.getExample2();
        byte[] svg = QRBill.generate(bill, QRBill.BillFormat.A6_LANDSCAPE_SHEET, QRBill.GraphicsFormat.SVG);
        TestHelper.assertFileContentsEqual(svg, "a6bill_ex2.svg");
    }

    @Test
    public void createA6Bill3() {
        Bill bill = SampleData.getExample3();
        byte[] svg = QRBill.generate(bill, QRBill.BillFormat.A6_LANDSCAPE_SHEET, QRBill.GraphicsFormat.SVG);
        TestHelper.assertFileContentsEqual(svg, "a6bill_ex3.svg");
    }

    @Test
    public void createA6Bill4() {
        Bill bill = SampleData.getExample4();
        byte[] svg = QRBill.generate(bill, QRBill.BillFormat.A6_LANDSCAPE_SHEET, QRBill.GraphicsFormat.SVG);
        TestHelper.assertFileContentsEqual(svg, "a6bill_ex4.svg");
    }
}
