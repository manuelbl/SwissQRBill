//
// Swiss QR Bill Generator
// Copyright (c) 2018 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//

package net.codecrete.qrbill.generatortest;

import net.codecrete.qrbill.generator.Bill;
import net.codecrete.qrbill.generator.GraphicsFormat;
import net.codecrete.qrbill.generator.OutputSize;
import net.codecrete.qrbill.generator.QRBill;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit tests with characters challening for SVG (XML relevant characters)
 */
@DisplayName("SVG special characters")
class SVGTest {

    @Test
    void svgWithChallengingCharacters() {
        Bill bill = SampleData.getExample1();
        bill.setUnstructuredMessage("<h1>&&\"ff\"'t'");
        bill.getFormat().setOutputSize(OutputSize.QR_BILL_ONLY);
        bill.getFormat().setGraphicsFormat(GraphicsFormat.SVG);
        byte[] svg = QRBill.generate(bill);
        FileComparison.assertFileContentsEqual(svg, "qrbill_sc1.svg");
    }
}