//
// Swiss QR Bill Generator
// Copyright (c) 2017 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
package net.codecrete.qrbill.generator;

import org.junit.Test;

public class A4BillTest {

    @Test
    public void createA4Bill1() {
        Bill bill = SampleData.getExample1();
        byte[] svg = QRBill.generate(bill, QRBill.BillFormat.A4PortraitSheet, QRBill.GraphicsFormat.SVG);
        TestHelper.assertFileContentsEqual(svg, "a4bill_ex1.svg");
    }

    @Test
    public void createA4Bill2() {
        Bill bill = SampleData.getExample2();
        byte[] svg = QRBill.generate(bill, QRBill.BillFormat.A4PortraitSheet, QRBill.GraphicsFormat.SVG);
        TestHelper.assertFileContentsEqual(svg, "a4bill_ex2.svg");
    }

    @Test
    public void createA4Bill3() {
        Bill bill = SampleData.getExample3();
        byte[] svg = QRBill.generate(bill, QRBill.BillFormat.A4PortraitSheet, QRBill.GraphicsFormat.SVG);
        TestHelper.assertFileContentsEqual(svg, "a4bill_ex3.svg");
    }

    @Test
    public void createA4Bill4() {
        Bill bill = SampleData.getExample4();
        byte[] svg = QRBill.generate(bill, QRBill.BillFormat.A4PortraitSheet, QRBill.GraphicsFormat.SVG);
        TestHelper.assertFileContentsEqual(svg, "a4bill_ex4.svg");
    }
}
