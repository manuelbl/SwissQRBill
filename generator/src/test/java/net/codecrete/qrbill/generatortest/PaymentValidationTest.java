//
// Swiss QR Bill Generator
// Copyright (c) 2018 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//

package net.codecrete.qrbill.generatortest;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import net.codecrete.qrbill.generator.Payments;

public class PaymentValidationTest {

    @Test
    public void validIBAN1() {
        assertTrue(Payments.isValidIBAN("DE67500700100093008102"));
    }

    @Test
    public void validIBAN2() {
        assertTrue(Payments.isValidIBAN("de67500700100093008102"));
    }

    @Test
    public void tooShortIBAN() {
        assertFalse(Payments.isValidIBAN("DE67"));
    }

    @Test
    public void invalidChar1() {
        assertFalse(Payments.isValidIBAN("DE67 5007 0010 0093 0081 02"));
    }

    @Test
    public void invalidChar2() {
        assertFalse(Payments.isValidIBAN("DE67-5007-0010-0093-0081-02"));
    }

    @Test
    public void invalidChar3() {
        assertFalse(Payments.isValidIBAN("1E67500700100093008102"));
    }

    @Test
    public void invalidChar4() {
        assertFalse(Payments.isValidIBAN("DE6B500700100093008102"));
    }

    @Test
    public void invalidCheckDigit() {
        assertFalse(Payments.isValidIBAN("DE67-5007-0010-0093-0081-03"));
    }

    @Test
    public void validQRReference() {
        assertTrue(Payments.isValidQRReferenceNo("210000000003139471430009017"));
    }

    @Test
    public void invalidLengthQRReference() {
        assertFalse(Payments.isValidQRReferenceNo("2100000003139471430009017"));
    }

    @Test
    public void invalidCharsQRReference1() {
        assertFalse(Payments.isValidQRReferenceNo("21 00000 00003 13947 14300 09017"));
    }

    @Test
    public void invalidCharsQRReference2() {
        assertFalse(Payments.isValidQRReferenceNo("210000S00003139471430009017"));
    }

    @Test
    public void invalidCheckDigitQRReference() {
        assertFalse(Payments.isValidQRReferenceNo("210000000003139471430009016"));
    }

    @Test
    public void validISO11649Reference() {
        assertTrue(Payments.isValidISO11649ReferenceNo("RF18000000000539007547034"));
    }

    @Test
    public void invalidLengthISO11649Reference1() {
        assertFalse(Payments.isValidISO11649ReferenceNo("RF18"));
    }

    @Test
    public void invalidLengthISO11649Reference2() {
        assertFalse(Payments.isValidISO11649ReferenceNo("RF1800000000000539007547034"));
    }

    @Test
    public void invalidCharsISO11649Reference1() {
        assertFalse(Payments.isValidISO11649ReferenceNo("RF18 0000 0000 0539 0075 4703 4"));
    }

    @Test
    public void invalidCharsISO11649Reference2() {
        assertFalse(Payments.isValidISO11649ReferenceNo("RF18000.00000539.07547034"));
    }

    @Test
    public void invalidCharsISO11649Reference3() {
        assertFalse(Payments.isValidISO11649ReferenceNo("RFAB000000000539007547034"));
    }

    @Test
    public void invalidCheckDigitISO11649Reference() {
        assertFalse(Payments.isValidISO11649ReferenceNo("RF18000000000539007547033"));
    }

}
