//
// Swiss QR Bill Generator
// Copyright (c) 2018 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//

package net.codecrete.qrbill.generatortest;

import net.codecrete.qrbill.generator.Payments;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for QR reference related methods in {@link Payments}
 */
@DisplayName("QR reference validation and formatting")
class QRReferenceTest {

    @Test
    void validQRReference() {
        assertTrue(Payments.isValidQRReference("210000000003139471430009017"));
    }

    @Test
    void invalidLengthQRReference() {
        assertFalse(Payments.isValidQRReference("2100000003139471430009017"));
    }

    @Test
    void validQRReferenceWithSpaces() {
        assertTrue(Payments.isValidQRReference("21 00000 00003 13947 14300 09017"));
    }

    @Test
    void invalidQRReferenceWithLetters() {
        assertFalse(Payments.isValidQRReference("210000S00003139471430009017"));
    }

    @Test
    void invalidQRReferenceWithSpecialChar() {
        assertFalse(Payments.isValidQRReference("210000000%03139471430009017"));
    }

    @Test
    void invalidCheckDigitQRReference() {
        assertFalse(Payments.isValidQRReference("210000000003139471430009016"));
    }

    @Test
    void formatQRReference() {
        assertEquals("12 34560 00000 00129 11462 90514",
                Payments.formatQRReferenceNumber("123456000000001291146290514"));
    }

    @Test
    void createQRReference() {
        assertEquals(
                "000000000000000000001234565",
                Payments.createQRReference("123456"));
    }

    @Test
    void createQRReferenceWithWhitespace() {
        assertEquals(
                "000000000000000000001234565",
                Payments.createQRReference("  12 3456 "));
    }

    @Test
    void rawReferenceWithInvalidCharacters() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            Payments.createQRReference("1134a56");
        });
        assertEquals("Invalid character in reference (digits allowed only)", ex.getMessage());
    }

    @Test
    void rawReferenceTooLong() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            Payments.createQRReference("123456789012345678901234567");
        });
        assertEquals("Reference number is too long", ex.getMessage());
    }
}
