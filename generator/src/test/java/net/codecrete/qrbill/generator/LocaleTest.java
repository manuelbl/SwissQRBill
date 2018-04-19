//
// Swiss QR Bill Generator
// Copyright (c) 2018 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//

package net.codecrete.qrbill.generator;

import java.util.Locale;

import org.junit.Test;

public class LocaleTest {

    @Test
    public void testIssue1() {
        Locale defaultLocale = Locale.getDefault();
        try {
            Locale.setDefault(Locale.GERMANY);
            Bill bill = SampleData.getExample3();
            byte[] svg = QRBill.generate(bill, QRBill.BillFormat.A6_LANDSCAPE_SHEET, QRBill.GraphicsFormat.SVG);
            TestHelper.assertFileContentsEqual(svg, "a6bill_issue1.svg");
            } finally {
            Locale.setDefault(defaultLocale);
        }
    }
}
