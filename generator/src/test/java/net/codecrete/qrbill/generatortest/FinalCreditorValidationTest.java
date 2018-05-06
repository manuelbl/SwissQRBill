//
// Swiss QR Bill Generator
// Copyright (c) 2018 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//

package net.codecrete.qrbill.generatortest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.codecrete.qrbill.generator.Address;
import net.codecrete.qrbill.generator.Bill;

/**
 * Unit tests for final creditor data validation
 */
@DisplayName("Final creditor data validation")
class FinalCreditorValidationTest extends BillDataValidationBase {

    @Test
    void validFinalCreditor() {
        bill = SampleData.getExample1();

        Address address = createValidPerson();
        bill.setFinalCreditor(address);
        validate();
        assertNoMessages();
        assertNotNull(validatedBill.getFinalCreditor());
        assertEquals("Zuppinger AG", validatedBill.getFinalCreditor().getName());
        assertEquals("Industriestrasse", validatedBill.getFinalCreditor().getStreet());
        assertEquals("34a", validatedBill.getFinalCreditor().getHouseNo());
        assertEquals("9548", validatedBill.getFinalCreditor().getPostalCode());
        assertEquals("Matzingen", validatedBill.getFinalCreditor().getTown());
        assertEquals("CH", validatedBill.getFinalCreditor().getCountryCode());
    }

    @Test
    void noFinalCreditor() {
        bill = SampleData.getExample1();
        bill.setFinalCreditor(null);
        validate();
        assertNoMessages();
        assertNull(validatedBill.getFinalCreditor());
    }

    @Test
    void emptyFinalCreditor() {
        bill = SampleData.getExample1();
        Address emptyAddress = new Address();
        bill.setFinalCreditor(emptyAddress);
        validate();
        assertNoMessages();
        assertNull(validatedBill.getFinalCreditor());
    }

    @Test
    void emptyFinalCreditorWithSpaces() {
        bill = SampleData.getExample1();
        Address emptyAddress = new Address();
        emptyAddress.setName("  ");
        bill.setFinalCreditor(emptyAddress);
        validate();
        assertNoMessages();
        assertNull(validatedBill.getFinalCreditor());
    }

    @Test
    void missingName() {
        bill = SampleData.getExample1();
        Address address = createValidPerson();
        address.setName("  ");
        bill.setFinalCreditor(address);
        validate();
        assertSingleErrorMessage(Bill.FIELD_FINAL_CREDITOR_NAME, "field_is_mandatory");
    }

    @Test
    void missingTown() {
        bill = SampleData.getExample1();
        Address address = createValidPerson();
        address.setTown(null);
        bill.setFinalCreditor(address);
        validate();
        assertSingleErrorMessage(Bill.FIELD_FINAL_CREDITOR_TOWN, "field_is_mandatory");
    }
}