//
// Swiss QR Bill Generator
// Copyright (c) 2018 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//

package net.codecrete.qrbill.generatortest;

import net.codecrete.qrbill.generator.AlternativeScheme;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.codecrete.qrbill.generator.Address;
import net.codecrete.qrbill.generator.Bill;
import net.codecrete.qrbill.generator.Language;
import net.codecrete.qrbill.generator.Bill.Version;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for class {@link Bill}
 */
@DisplayName("Bill class")
class BillTest {

    @Test
    void setLanguage() {
        Bill bill = new Bill();
        bill.getFormat().setLanguage(Language.FR);
        assertEquals(Language.FR, bill.getFormat().getLanguage());
    }

    @Test
    void setVersion() {
        Bill bill = new Bill();
        bill.setVersion(null);
        assertNull(bill.getVersion());
        bill.setVersion(Version.V2_0);
        assertEquals(Version.V2_0, bill.getVersion());
    }

    @Test
    void setAmount() {
        Bill bill = new Bill();
        bill.setAmount(new BigDecimal("37.45").setScale(2, RoundingMode.HALF_UP));
        assertEquals(new BigDecimal(3745).movePointLeft(2), bill.getAmount());
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
    void setReference() {
        Bill bill = new Bill();
        bill.setReference("RF839DF38202934");
        assertEquals("RF839DF38202934", bill.getReference());
    }

    @Test
    void createCreditorReference() {
        Bill bill = new Bill();
        bill.createAndSetCreditorReference("ABCD3934803");
        assertEquals("RF93ABCD3934803", bill.getReference());
    }

    @Test
    void setUnstructuredMessage() {
        Bill bill = new Bill();
        bill.setUnstructuredMessage("Rechnung 3849-2001");
        assertEquals("Rechnung 3849-2001", bill.getUnstructuredMessage());
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
    void setBillInfomration() {
        Bill bill = new Bill();
        bill.setBillInformation("S1/01/20170309/11/10201409/20/14000000/22/369 58/30/CH106017086/40/1020/41/3010");
        assertEquals("S1/01/20170309/11/10201409/20/14000000/22/369 58/30/CH106017086/40/1020/41/3010",
                bill.getBillInformation());
    }

    @Test
    void setAlternativeScheme() {
        Bill bill = new Bill();
        bill.setAlternativeSchemes(createAlternativeSchemes());
        assertArrayEquals(createAlternativeSchemes(), bill.getAlternativeSchemes());
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

    private AlternativeScheme[] createAlternativeSchemes() {
        return new AlternativeScheme[] {
                new AlternativeScheme("Ultraviolet", "UV;UltraPay005;12345"),
                new AlternativeScheme("Xing Yong", "XY;XYService;54321")
        };
    }
}
