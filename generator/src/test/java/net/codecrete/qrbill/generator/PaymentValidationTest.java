//
// Swiss QR Bill Generator
// Copyright (c) 2018 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//

package net.codecrete.qrbill.generator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class PaymentValidationTest {

    @Test
    public void validIBAN1() {
        assertTrue(PaymentValidation.isValidIBAN("DE67500700100093008102"));
    }

    @Test
    public void validIBAN2() {
        assertTrue(PaymentValidation.isValidIBAN("de67500700100093008102"));
    }

    @Test
    public void tooShortIBAN() {
        assertFalse(PaymentValidation.isValidIBAN("DE67"));
    }

    @Test
    public void invalidChar1() {
        assertFalse(PaymentValidation.isValidIBAN("DE67 5007 0010 0093 0081 02"));
    }

    @Test
    public void invalidChar2() {
        assertFalse(PaymentValidation.isValidIBAN("DE67-5007-0010-0093-0081-02"));
    }

    @Test
    public void invalidCheckDigit() {
        assertFalse(PaymentValidation.isValidIBAN("DE67-5007-0010-0093-0081-03"));
    }

    @Test
    public void validQRReference() {
        assertTrue(PaymentValidation.isValidQRReferenceNo("210000000003139471430009017"));
    }

    @Test
    public void invalidLengthQRReference() {
        assertFalse(PaymentValidation.isValidQRReferenceNo("2100000003139471430009017"));
    }

    @Test
    public void invalidCharsQRReference1() {
        assertFalse(PaymentValidation.isValidQRReferenceNo("21 00000 00003 13947 14300 09017"));
    }

    @Test
    public void invalidCharsQRReference2() {
        assertFalse(PaymentValidation.isValidQRReferenceNo("210000S00003139471430009017"));
    }

    @Test
    public void invalidCheckDigitQRReference() {
        assertFalse(PaymentValidation.isValidQRReferenceNo("210000000003139471430009016"));
    }
}
