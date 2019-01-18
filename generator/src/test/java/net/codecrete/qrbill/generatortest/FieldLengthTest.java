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

/**
 * Unit tests for field length validation and clipping
 */
@DisplayName("Field length validation and clipping")
class FieldLengthTest extends BillDataValidationBase {

    @Test
    void maximumNameLength() {
        bill = SampleData.getExample1();
        Address address = createValidPerson();
        address.setName("Name567890123456789012345678901234567890123456789012345678901234567890");
        bill.setCreditor(address);
        validate();
        assertNoMessages();
        assertEquals("Name567890123456789012345678901234567890123456789012345678901234567890",
                validatedBill.getCreditor().getName());
    }

    @Test
    void clippedName() {
        bill = SampleData.getExample1();
        Address address = createValidPerson();
        address.setName("Name5678901234567890123456789012345678901234567890123456789012345678901");
        bill.setCreditor(address);
        validate();
        assertSingleWarningMessage(ValidationConstants.FIELD_CREDITOR_NAME, "field_clipped");
        assertEquals("Name567890123456789012345678901234567890123456789012345678901234567890",
                validatedBill.getCreditor().getName());
    }

    @Test
    void maximumStreetLength() {
        bill = SampleData.getExample1();
        Address address = createValidPerson();
        address.setStreet("Street7890123456789012345678901234567890123456789012345678901234567890");
        bill.setCreditor(address);
        validate();
        assertNoMessages();
        assertEquals("Street7890123456789012345678901234567890123456789012345678901234567890",
                validatedBill.getCreditor().getStreet());
    }

    @Test
    void clippedStreet() {
        bill = SampleData.getExample1();
        Address address = createValidPerson();
        address.setStreet("Street78901234567890123456789012345678901234567890123456789012345678901");
        bill.setCreditor(address);
        validate();
        assertSingleWarningMessage(ValidationConstants.FIELD_CREDITOR_STREET, "field_clipped");
        assertEquals("Street7890123456789012345678901234567890123456789012345678901234567890",
                validatedBill.getCreditor().getStreet());
    }

    @Test
    void maximumHouseNoLength() {
        bill = SampleData.getExample1();
        Address address = createValidPerson();
        address.setHouseNo("HouseNo890123456");
        bill.setCreditor(address);
        validate();
        assertNoMessages();
        assertEquals("HouseNo890123456", validatedBill.getCreditor().getHouseNo());
    }

    @Test
    void clippedHouseNo() {
        bill = SampleData.getExample1();
        Address address = createValidPerson();
        address.setHouseNo("HouseNo8901234567");
        bill.setCreditor(address);
        validate();
        assertSingleWarningMessage(ValidationConstants.FIELD_CREDITOR_HOUSE_NO, "field_clipped");
        assertEquals("HouseNo890123456", validatedBill.getCreditor().getHouseNo());
    }

    @Test
    void maximumPostalCodeLength() {
        bill = SampleData.getExample1();
        Address address = createValidPerson();
        address.setPostalCode("Postal7890123456");
        bill.setCreditor(address);
        validate();
        assertNoMessages();
        assertEquals("Postal7890123456", validatedBill.getCreditor().getPostalCode());
    }

    @Test
    void clippedPostalCode() {
        bill = SampleData.getExample1();
        Address address = createValidPerson();
        address.setPostalCode("Postal78901234567");
        bill.setCreditor(address);
        validate();
        assertSingleWarningMessage(ValidationConstants.FIELD_CREDITOR_POSTAL_CODE, "field_clipped");
        assertEquals("Postal7890123456", validatedBill.getCreditor().getPostalCode());
    }

    @Test
    void maximumTownLength() {
        bill = SampleData.getExample1();
        Address address = createValidPerson();
        address.setTown("City5678901234567890123456789012345");
        bill.setCreditor(address);
        validate();
        assertNoMessages();
        assertEquals("City5678901234567890123456789012345", validatedBill.getCreditor().getTown());
    }

    @Test
    void clippedTown() {
        bill = SampleData.getExample1();
        Address address = createValidPerson();
        address.setTown("City56789012345678901234567890123456");
        bill.setCreditor(address);
        validate();
        assertSingleWarningMessage(ValidationConstants.FIELD_CREDITOR_TOWN, "field_clipped");
        assertEquals("City5678901234567890123456789012345", validatedBill.getCreditor().getTown());
    }

}
