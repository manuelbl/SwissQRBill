//
// Swiss QR Bill Generator
// Copyright (c) 2018 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//

package net.codecrete.qrbill.generatortest;

import net.codecrete.qrbill.generator.Address;
import net.codecrete.qrbill.generator.Bill;
import net.codecrete.qrbill.generator.QRBill;
import net.codecrete.qrbill.generator.ValidationMessage;
import net.codecrete.qrbill.generator.ValidationMessage.Type;
import net.codecrete.qrbill.generator.ValidationResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Base class for bill data validation tests
 */
class BillDataValidationBase {
    Bill bill;
    ValidationResult result;
    Bill validatedBill;

    void validate() {
        result = QRBill.validate(bill);
        validatedBill = result.getCleanedBill();
    }

    /**
     * Asserts that the validation succeeded with no messages
     */
    void assertNoMessages() {
        assertFalse(result.hasErrors());
        assertFalse(result.hasWarnings());
        assertFalse(result.hasMessages());
        assertEquals(0, result.getValidationMessages().size());
    }

    /**
     * Asserts that the validation produced a single validation error message
     *
     * @param field      the field that triggered the validation error
     * @param messageKey the message key of the validation error
     */
    void assertSingleErrorMessage(String field, String messageKey) {
        assertTrue(result.hasErrors());
        assertFalse(result.hasWarnings());
        assertTrue(result.hasMessages());
        assertEquals(1, result.getValidationMessages().size());

        ValidationMessage msg = result.getValidationMessages().get(0);
        assertEquals(Type.ERROR, msg.getType());
        assertEquals(field, msg.getField());
        assertEquals(messageKey, msg.getMessageKey());
    }

    /**
     * Asserts thta the validation succeeded with a single warning
     *
     * @param field      the field that triggered the validation warning
     * @param messageKey the message key of the validation warning
     */
    void assertSingleWarningMessage(String field, String messageKey) {
        assertFalse(result.hasErrors());
        assertTrue(result.hasWarnings());
        assertTrue(result.hasMessages());
        assertEquals(1, result.getValidationMessages().size());

        ValidationMessage msg = result.getValidationMessages().get(0);
        assertEquals(Type.WARNING, msg.getType());
        assertEquals(field, msg.getField());
        assertEquals(messageKey, msg.getMessageKey());
    }

    /**
     * Creates an address with valid person data
     *
     * @return the address
     */
    Address createValidPerson() {
        Address address = new Address();
        address.setName("Zuppinger AG");
        address.setStreet("Industriestrasse");
        address.setHouseNo("34a");
        address.setPostalCode("9548");
        address.setTown("Matzingen");
        address.setCountryCode("CH");
        return address;
    }
}
