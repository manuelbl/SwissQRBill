//
// Swiss QR Bill Generator
// Copyright (c) 2018 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//

package net.codecrete.qrbill.generatortest;

import net.codecrete.qrbill.generator.Address;
import net.codecrete.qrbill.generator.ValidationConstants;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Unit test for the debtor data validation
 */
@DisplayName("Debtor validation")
class DebtorValidationTest extends BillDataValidationBase {

    @Test
    void validDebtor() {
        bill = SampleData.getExample1();

        Address address = createValidPerson();
        bill.setDebtor(address);
        validate();
        assertNoMessages();
        assertNotNull(validatedBill.getDebtor());
        assertEquals("Zuppinger AG", validatedBill.getDebtor().getName());
        assertEquals("Industriestrasse", validatedBill.getDebtor().getStreet());
        assertEquals("34a", validatedBill.getDebtor().getHouseNo());
        assertEquals("9548", validatedBill.getDebtor().getPostalCode());
        assertEquals("Matzingen", validatedBill.getDebtor().getTown());
        assertEquals("CH", validatedBill.getDebtor().getCountryCode());
    }

    @Test
    void emptyDebtor() {
        bill = SampleData.getExample1();
        Address emptyAddress = new Address();
        bill.setDebtor(emptyAddress);
        validate();
        assertNoMessages();
        assertNull(validatedBill.getDebtor());
    }

    @Test
    void emptyDebtorWithSpaces() {
        bill = SampleData.getExample1();
        Address emptyAddress = new Address();
        emptyAddress.setName("  ");
        bill.setDebtor(emptyAddress);
        validate();
        assertNoMessages();
        assertNull(validatedBill.getDebtor());
    }

    @Test
    void missingName() {
        bill = SampleData.getExample1();
        Address address = createValidPerson();
        address.setName("  ");
        bill.setDebtor(address);
        validate();
        assertSingleErrorMessage(ValidationConstants.FIELD_DEBTOR_NAME, "field_is_mandatory");
    }

    @Test
    void missingTown() {
        bill = SampleData.getExample1();
        Address address = createValidPerson();
        address.setTown(null);
        bill.setDebtor(address);
        validate();
        assertSingleErrorMessage(ValidationConstants.FIELD_DEBTOR_TOWN, "field_is_mandatory");
    }

    @Test
    void openDebtor() {
        bill = SampleData.getExample1();

        bill.setDebtor(null);
        validate();
        assertNoMessages();
        assertNull(validatedBill.getDebtor());
    }

    @Test
    void debtorWithInvalidCountryCode() {
        bill = SampleData.getExample1();
        Address address = createValidPerson();
        address.setCountryCode("00");
        bill.setDebtor(address);
        validate();
        assertSingleErrorMessage(ValidationConstants.FIELD_DEBTOR_COUNTRY_CODE, "valid_country_code");
    }
}