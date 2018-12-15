//
// Swiss QR Bill Generator
// Copyright (c) 2018 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//

package net.codecrete.qrbill.generatortest;

import net.codecrete.qrbill.generator.Address;
import net.codecrete.qrbill.generator.AlternativeScheme;
import net.codecrete.qrbill.generator.Bill;
import net.codecrete.qrbill.generator.Bill.Version;
import net.codecrete.qrbill.generator.Language;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

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
        bill.setAmount(BigDecimal.valueOf(37.45));
        assertEquals(37.45, bill.getAmountAsDouble().doubleValue());
    }

    @Test
    void setAmountFromDouble() {
        Bill bill = new Bill();
        bill.setAmountFromDouble(37.45);
        assertEquals(37.45, bill.getAmountAsDouble().doubleValue());
        assertEquals(BigDecimal.valueOf(3745, 2), bill.getAmount());
    }

    @Test
    void setAmountFromDoubleWithRounding() {
        Bill bill = new Bill();
        bill.setAmountFromDouble(37.45123);
        assertEquals(37.45, bill.getAmountAsDouble().doubleValue());
        assertEquals(BigDecimal.valueOf(3745, 2), bill.getAmount());
    }

    @Test
    void setAmountFromDoubleNull() {
        Bill bill = new Bill();
        bill.setAmountFromDouble(null);
        assertNull(bill.getAmountAsDouble());
        assertNull(bill.getAmount());
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
    void setBillInformation() {
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

    @Test
    void testEqualsTrivial() {
        Bill bill = new Bill();
        assertEquals(bill, bill);
        assertNotEquals(bill, null);
        assertNotEquals("xxx", bill);
    }

    @Test
    void testEquals() {
        Bill bill1 = createBill();
        Bill bill2 = createBill();
        assertEquals(bill1, bill2);
        assertEquals(bill2, bill1);

        bill2.setUnstructuredMessage("ABC");
        assertNotEquals(bill1, bill2);
    }

    @Test
    void testHashCode() {
        Bill bill1 = createBill();
        Bill bill2 = createBill();
        assertEquals(bill1.hashCode(), bill2.hashCode());
    }

    @Test
    void testToString() {
        Bill bill = createBill();
        String text = bill.toString();
        assertEquals("Bill{version=V2_0, amount=100.30, currency='CHF', account='CH12343345345', creditor=Address{type=STRUCTURED, name='Vision Consult GmbH', addressLine1='null', addressLine2='null', street='Hintergasse', houseNo='7b', postalCode='8400', town='Winterthur', countryCode='CH'}, reference='null', debtor=Address{type=STRUCTURED, name='Vision Consult GmbH', addressLine1='null', addressLine2='null', street='Hintergasse', houseNo='7b', postalCode='8400', town='Winterthur', countryCode='CH'}, unstructuredMessage='null', billInformation='null', alternativeSchemes=null, format=BillFormat{outputSize=QR_BILL_ONLY, language=EN, separatorType=SOLID_LINE_WITH_SCISSORS, fontFamily='Helvetica,Arial,\"Liberation Sans\"', graphicsFormat=SVG}}", text);
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

    private Bill createBill() {
        Bill bill = new Bill();
        bill.setAccount("CH12343345345");
        bill.setCreditor(createAddress());
        bill.setAmountFromDouble(100.3);
        bill.setDebtor(createAddress());
        return bill;
    }

    private AlternativeScheme[] createAlternativeSchemes() {
        return new AlternativeScheme[] {
                new AlternativeScheme("Ultraviolet", "UV;UltraPay005;12345"),
                new AlternativeScheme("Xing Yong", "XY;XYService;54321")
        };
    }
}
