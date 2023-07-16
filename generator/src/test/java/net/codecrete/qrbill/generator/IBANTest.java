//
// Swiss QR Bill Generator
// Copyright (c) 2018 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//

package net.codecrete.qrbill.generator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for IBAN related methods in {@link Payments}
 */
@DisplayName("IBAN validation and formatting")
class IBANTest {

    @Test
    void valid() {
        assertTrue(Payments.isValidIBAN("FR7630066100410001057380116"));
    }

    @Test
    void validWithSpaces() {
        assertTrue(Payments.isValidIBAN("FR76 3006 6100 4100 0105 7380 116"));
    }

    @Test
    void validWithTrailingAndLeadingSpaces() {
        assertTrue(Payments.isValidIBAN(" DE12500105170648489890 "));
    }

    @Test
    void validWithLowercase() {
        assertTrue(Payments.isValidIBAN("MT98mmeb44093000000009027293051"));
    }

    @Test
    void tooShort() {
        assertFalse(Payments.isValidIBAN("CH04"));
    }

    @Test
    void tooShortWithSpaces() {
        assertFalse(Payments.isValidIBAN("CH 04"));
    }

    @Test
    void invalidChars() {
        assertFalse(Payments.isValidIBAN("SE64-1200-0000-0121-7014-5230"));
    }

    @Test
    void invalidCountryCodeOnPos1() {
        assertFalse(Payments.isValidIBAN("0K9311110000001057361004"));
    }

    @Test
    void invalidCountryCodeOnPos2() {
        assertFalse(Payments.isValidIBAN("S 056031001001300933"));
    }

    @Test
    void invalidCheckDigitOnPos3() {
        assertFalse(Payments.isValidIBAN(" GBF2ESSE40486562136016"));
    }

    @Test
    void invalidCheckDigitOnPos4() {
        assertFalse(Payments.isValidIBAN("FR7A30066100410001057380116"));
    }

    @Test
    void invalidChecksum() {
        assertFalse(Payments.isValidIBAN("DK5650510001322617"));
    }

    @Test
    void formatIBAN1() {
        assertEquals("BE68 8440 1037 0034", Payments.formatIBAN("BE68844010370034"));
    }

    @Test
    void formatIBAN2() {
        assertEquals("IT68 D030 0203 2800 0040 0162 854", Payments.formatIBAN("IT68D0300203280000400162854"));
    }
}
