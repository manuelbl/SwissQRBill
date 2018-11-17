//
// Swiss QR Bill Generator
// Copyright (c) 2018 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//

package net.codecrete.qrbill.generatortest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.codecrete.qrbill.generator.Bill;
import net.codecrete.qrbill.generator.QRBill;

/**
 * Unit tests with characters challening for SVG (XML relevant characters)
 */
@DisplayName("SVG special characters")
class SVGTest {

    @Test
    void svgWithChallengingCharacters() {
        Bill bill = SampleData.getExample1();
        bill.setUnstructuredMessage("<h1>&&\"ff\"'t'");
        byte[] svg = QRBill.generate(bill, QRBill.BillFormat.QR_BILL_ONLY, QRBill.GraphicsFormat.SVG);
        FileComparison.assertFileContentsEqual(svg, "qrbill_sc1.svg");
    }
}