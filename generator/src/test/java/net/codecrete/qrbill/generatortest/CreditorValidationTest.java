//
// Swiss QR Bill Generator
// Copyright (c) 2018 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//

package net.codecrete.qrbill.generatortest;

import net.codecrete.qrbill.generator.Address;
import net.codecrete.qrbill.generator.ValidationConstants;
import net.codecrete.qrbill.generator.ValidationMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit test for the validation of the creditor data
 */
@DisplayName("Creditor validation")
class CreditorValidationTest extends BillDataValidationBase {
    @Test
    void validCreditor() {
        bill = SampleData.getExample1();
        Address address = createValidPerson();
        bill.setCreditor(address);
        validate();
        assertNoMessages();
        assertNotNull(validatedBill.getCreditor());
        assertEquals("Zuppinger AG", validatedBill.getCreditor().getName());
        assertEquals("Industriestrasse", validatedBill.getCreditor().getStreet());
        assertEquals("34a", validatedBill.getCreditor().getHouseNo());
        assertEquals("9548", validatedBill.getCreditor().getPostalCode());
        assertEquals("Matzingen", validatedBill.getCreditor().getTown());
        assertEquals("CH", validatedBill.getCreditor().getCountryCode());
    }

    @Test
    void missingCreditor() {
        bill = SampleData.getExample1();
        bill.setCreditor(null);
        validate();
        assertMandatoryPersonMessages();
    }

    @Test
    void emptyCreditor() {
        bill = SampleData.getExample1();
        Address emptyAddress = new Address();
        bill.setCreditor(emptyAddress);
        validate();
        assertMandatoryPersonMessages();
    }

    @Test
    void emptyCreditorWithSpaces() {
        bill = SampleData.getExample1();
        Address emptyAddress = new Address();
        emptyAddress.setName("  ");
        bill.setCreditor(emptyAddress);
        validate();
        assertMandatoryPersonMessages();
    }

    @Test
    void missingCreditorName() {
        bill = SampleData.getExample1();
        Address address = createValidPerson();
        address.setName("  ");
        bill.setCreditor(address);
        validate();
        assertSingleErrorMessage(ValidationConstants.FIELD_CREDITOR_NAME, ValidationConstants.KEY_FIELD_VALUE_MISSING);
    }

    @Test
    void creditorWithoutStreet() {
        bill = SampleData.getExample1();
        Address address = createValidPerson();
        address.setStreet(null);
        bill.setCreditor(address);
        validate();
        assertNoMessages();
    }

    @Test
    void creditorWithoutHouseNo() {
        bill = SampleData.getExample1();
        Address address = createValidPerson();
        address.setHouseNo(null);
        bill.setCreditor(address);
        validate();
        assertNoMessages();
    }

    @Test
    void creditorWithMissingPostalCode() {
        bill = SampleData.getExample1();
        Address address = createValidPerson();
        address.setPostalCode("");
        bill.setCreditor(address);
        validate();
        assertSingleErrorMessage(ValidationConstants.FIELD_CREDITOR_POSTAL_CODE, ValidationConstants.KEY_FIELD_VALUE_MISSING);
    }

    @Test
    void creditorWithMissingTown() {
        bill = SampleData.getExample1();
        Address address = createValidPerson();
        address.setTown(null);
        bill.setCreditor(address);
        validate();
        assertSingleErrorMessage(ValidationConstants.FIELD_CREDITOR_TOWN, ValidationConstants.KEY_FIELD_VALUE_MISSING);
    }

    @ParameterizedTest
    @CsvSource({
            "  ,field_value_missing",
            "Schweiz,country_code_invalid",
            "R!,country_code_invalid",
            "00,country_code_invalid",
            "aà,country_code_invalid"
    })
    void creditorWithInvalidCountryCode(String countryCode, String messageKey) {
        bill = SampleData.getExample1();
        Address address = createValidPerson();
        address.setCountryCode(countryCode);
        bill.setCreditor(address);
        validate();
        assertSingleErrorMessage(ValidationConstants.FIELD_CREDITOR_COUNTRY_CODE, messageKey);
    }

    @Test
    void creditorWithConflictingAddress() {
        bill = SampleData.getExample1();
        bill.getCreditor().setAddressLine1("Conflict");
        validate();
        assertTrue(result.hasErrors());
        assertFalse(result.hasWarnings());
        assertTrue(result.hasMessages());
        assertEquals(5, result.getValidationMessages().size());
        for (ValidationMessage msg : result.getValidationMessages()) {
            assertEquals(ValidationMessage.Type.ERROR, msg.getType());
            assertEquals(ValidationConstants.KEY_ADDRESS_TYPE_CONFLICT, msg.getMessageKey());
            assertTrue(msg.getField().startsWith(ValidationConstants.FIELDROOT_CREDITOR));
        }
    }

    private void assertMandatoryPersonMessages() {
        assertTrue(result.hasErrors());
        assertFalse(result.hasWarnings());
        assertTrue(result.hasMessages());
        assertEquals(5, result.getValidationMessages().size());
        for (ValidationMessage msg : result.getValidationMessages()) {
            assertEquals(ValidationMessage.Type.ERROR, msg.getType());
            assertEquals(ValidationConstants.KEY_FIELD_VALUE_MISSING, msg.getMessageKey());
            assertTrue(msg.getField().startsWith(ValidationConstants.FIELDROOT_CREDITOR));
        }
    }
}
