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
    public void createA6Bill() {
        Bill bill = SampleData.getExample1();
        byte[] svg = QRBill.generate(bill, QRBill.BillFormat.A6LandscapeSheet, QRBill.GraphicsFormat.SVG);
        TestHelper.assertFileContentsEqual(svg, "a6bill_ex1.svg");
    }
}
