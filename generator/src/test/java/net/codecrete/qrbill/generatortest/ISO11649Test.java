package net.codecrete.qrbill.generatortest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

import net.codecrete.qrbill.generator.Payments;

public class ISO11649Test {

    @Test
    void valid() {
        assertTrue(Payments.isValidISO11649Reference("RF49N73GBST73AKL38ZX"));
    }

    @Test
    void validWithSpaces() {
        assertTrue(Payments.isValidISO11649Reference("RF08 B370 0321"));
    }

    @Test
    void validWithTrailingAndLeadingSpaces() {
        assertTrue(Payments.isValidISO11649Reference(" RF19N8BG33KQ9HSS7BG "));
    }

    @Test
    void validWithLowercase() {
        assertTrue(Payments.isValidISO11649Reference("RF66qs9H7NJ4fvs99SPO"));
    }

    @Test
    void validShort() {
        assertTrue(Payments.isValidISO11649Reference("RF040"));
    }

    @Test
    void tooShort() {
        assertFalse(Payments.isValidISO11649Reference("RF04"));
    }

    @Test
    void tooShortWithSpaces() {
        assertFalse(Payments.isValidISO11649Reference("RF 04"));
    }

    @Test
    void tooLong() {
        assertFalse(Payments.isValidISO11649Reference("RF04GHJ74CV9B4DFH99RXPLMMQ43JKL0"));
    }

    @Test
    void invalidChars() {
        assertFalse(Payments.isValidISO11649Reference("RF20.0000.3"));
    }

    @Test
    void invalidCharCodeOnPos1() {
        assertFalse(Payments.isValidISO11649Reference("DK5750510001322617"));
    }

    @Test
    void invalidCharCodeOnPos2() {
        assertFalse(Payments.isValidISO11649Reference(" RO49AAAA1B31007593840000"));
    }

    @Test
    void invalidCheckDigitOnPos3() {
        assertFalse(Payments.isValidISO11649Reference(" RFA8FN3DD938494"));
    }

    @Test
    void invalidCheckDigitOnPos4() {
        assertFalse(Payments.isValidISO11649Reference("RF0CNHF"));
    }

    @Test
    void invalidChecksum() {
        assertFalse(Payments.isValidIBAN("RF43029348BDEF3823"));
    }

    @Test
    void invalidChecksum00() {
        assertFalse(Payments.isValidIBAN("RF0072"));
    }

    @Test
    void invalidChecksum01() {
        assertFalse(Payments.isValidIBAN("RF0154"));
    }

    @Test
    void invalidChecksum99() {
        assertFalse(Payments.isValidIBAN("RF991X"));
    }

    @Test
    void formatShort() {
        assertEquals("RF15 093", Payments.formatIBAN("RF15093"));
    }

    @Test
    void formatLong() {
        assertEquals("RF41 BD93 DJ3Q GGD9 JI22 D", Payments.formatIBAN("RF41BD93DJ3QGGD9JI22D"));
    }

    @Test
    void createCreditorReference() {
        assertEquals("RF91B334BOPQE39D902DC", Payments.createISO11649Reference("B334BOPQE39D902DC"));
    }

    @Test
    void createCreditorReferenceWithLeadingZero() {
        assertEquals("RF097", Payments.createISO11649Reference("7"));
    }
}
