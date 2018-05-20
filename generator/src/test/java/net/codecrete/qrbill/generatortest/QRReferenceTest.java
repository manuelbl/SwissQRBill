//
// Swiss QR Bill Generator
// Copyright (c) 2018 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//

package net.codecrete.qrbill.generatortest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.codecrete.qrbill.generator.Payments;

/**
 * Unit tests for QR reference related methods in {@link Payments}
 */
@DisplayName("QR reference validation and formatting")
class QRReferenceTest {

    @Test
    void validQRReference() {
        assertTrue(Payments.isValidQRReferenceNo("210000000003139471430009017"));
    }

    @Test
    void invalidLengthQRReference() {
        assertFalse(Payments.isValidQRReferenceNo("2100000003139471430009017"));
    }

    @Test
    void validQRReferenceWithSpaces() {
        assertTrue(Payments.isValidQRReferenceNo("21 00000 00003 13947 14300 09017"));
    }

    @Test
    void invalidQRReferenceWithLetters() {
        assertFalse(Payments.isValidQRReferenceNo("210000S00003139471430009017"));
    }

    @Test
    void invalidQRReferenceWithSpecialChar() {
        assertFalse(Payments.isValidQRReferenceNo("210000000%03139471430009017"));
    }

    @Test
    void invalidCheckDigitQRReference() {
        assertFalse(Payments.isValidQRReferenceNo("210000000003139471430009016"));
    }

    @Test
    void formatQRReference() {
        assertEquals("12 34560 00000 00129 11462 90514", Payments.formatQRReferenceNumber("123456000000001291146290514"));
    }
}
