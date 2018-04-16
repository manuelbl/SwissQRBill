//
// Swiss QR Bill Generator
// Copyright (c) 2018 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//

package net.codecrete.qrbill.generator;

import org.junit.Test;

public class SVGTest {

    @Test
    public void createSVGWithDifficultCharacters() {
        Bill bill = SampleData.getExample1();
        bill.setAdditionalInfo("<h1>&&\"ff\"'t'");
        byte[] svg = QRBill.generate(bill, QRBill.BillFormat.A6_LANDSCAPE_SHEET, QRBill.GraphicsFormat.SVG);
        TestHelper.assertFileContentsEqual(svg, "a6bill_sc1.svg");
    }
}