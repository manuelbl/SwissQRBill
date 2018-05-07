//
// Swiss QR Bill Generator
// Copyright (c) 2018 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//

package net.codecrete.qrbill.generatortest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.codecrete.qrbill.generator.Address;
import net.codecrete.qrbill.generator.Bill;
import net.codecrete.qrbill.generator.Bill.Language;
import net.codecrete.qrbill.generator.Bill.Version;

/**
 * Unit tests for class {@link Bill}
 */
@DisplayName("Bill class")
class BillTest {

    @Test
    void setLanguage() {
        Bill bill = new Bill();
        bill.setLanguage(Language.FR);
        assertEquals(Language.FR, bill.getLanguage());
    }

    @Test
    void setVersion() {
        Bill bill = new Bill();
        bill.setVersion(null);
        assertNull(bill.getVersion());
        bill.setVersion(Version.V1_0);
        assertEquals(Version.V1_0, bill.getVersion());
    }

    @Test
    void setAmount() {
        Bill bill = new Bill();
        bill.setAmount(37.45);
        assertEquals(Double.valueOf(37.45), bill.getAmount());
    }

    @Test
    void setCurrency() {
        Bill bill = new Bill();
        bill.setCurrency("EUR");
        assertEquals("EUR", bill.getCurrency());
    }

    @Test
    void setAccount() {
        Bill bill = new Bill();
        bill.setAccount("BD93020293480234");
        assertEquals("BD93020293480234", bill.getAccount());
    }

    @Test
    void setCreditor() {
        Bill bill = new Bill();
        Address address = createAddress();
        bill.setCreditor(address);
        assertSame(address, bill.getCreditor());
        assertEquals(createAddress(), bill.getCreditor());
    }

    @Test
    void setFinalCreditor() {
        Bill bill = new Bill();
        Address address = createAddress();
        bill.setFinalCreditor(address);
        assertSame(address, bill.getFinalCreditor());
        assertEquals(createAddress(), bill.getFinalCreditor());
    }

    @Test
    void setReferenceNo() {
        Bill bill = new Bill();
        bill.setReferenceNo("RF839DF38202934");
        assertEquals("RF839DF38202934", bill.getReferenceNo());
    }

    @Test
    void createCreditorReference() {
        Bill bill = new Bill();
        bill.createAndSetCreditorReference("ABCD3934803");
        assertEquals("RF93ABCD3934803", bill.getReferenceNo());
    }

    @Test
    void setAdditionalInfo() {
        Bill bill = new Bill();
        bill.setAdditionalInfo("Rechnung 3849-2001");
        assertEquals("Rechnung 3849-2001", bill.getAdditionalInfo());
    }

    @Test
    void setDebtor() {
        Bill bill = new Bill();
        Address address = createAddress();
        bill.setDebtor(address);
        assertSame(address, bill.getDebtor());
        assertEquals(createAddress(), bill.getDebtor());
    }

    @Test
    void setDueDate() {
        Bill bill = new Bill();
        LocalDate dueDate = LocalDate.of(2019, 8, 31);
        bill.setDueDate(dueDate);
        assertEquals(dueDate, bill.getDueDate());
    }

    private Address createAddress() {
        Address address = new Address();
        address.setName("Vision Consult GmbH");
        address.setStreet("Hintergasse");
        address.setHouseNo("7b");
        address.setPostalCode("8400");
        address.setTown("Winterthur");
        address.setCountryCode("CH");
        return address;
    }
}
