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

import java.util.Locale;

/**
 * Unit tests for verifying the library works with different default locales
 */
@DisplayName("Default locale independence")
class LocaleTest {

    @Test
    @DisplayName("Default locale Germany")
    void defaultIsDE() {
        generateQRBill(Locale.GERMANY);
    }

    @Test
    @DisplayName("Default locale US")
    void defaultIsUS() {
        generateQRBill(Locale.US);
    }

    @Test
    @DisplayName("Default locale de-CH")
    void defaultIsDECH() {
        generateQRBill(Locale.forLanguageTag("de-CH"));
    }

    @Test
    @DisplayName("Default locale fr-CH")
    void defaultIsFRCH() {
        generateQRBill(Locale.forLanguageTag("fr-CH"));
    }

    private void generateQRBill(Locale locale) {
        Locale defaultLocale = Locale.getDefault();
        try {
            Locale.setDefault(locale);
            Bill bill = SampleData.getExample3();
            bill.getFormat().setOutputSize(OutputSize.QR_BILL_ONLY);
            bill.getFormat().setGraphicsFormat(GraphicsFormat.SVG);
            byte[] svg = QRBill.generate(bill);
            FileComparison.assertFileContentsEqual(svg, "a6bill_issue1.svg");
        } finally {
            Locale.setDefault(defaultLocale);
        }
    }
}
