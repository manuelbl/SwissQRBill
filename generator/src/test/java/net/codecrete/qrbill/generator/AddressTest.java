//
// Swiss QR Bill Generator
// Copyright (c) 2018 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//

package net.codecrete.qrbill.generator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test for class {@link Address}
 */
@DisplayName("Address class")
class AddressTest {

    @Test
    void testUndetermined() {
        Address address = new Address();
        assertEquals(Address.Type.UNDETERMINED, address.getType());
    }

    @Test
    void setName() {
        Address address = new Address();
        address.setName("ABC");
        assertEquals("ABC", address.getName());
    }

    @Test
    @SuppressWarnings("deprecation")
    void setAddressLine1() {
        Address address = new Address();
        address.setAddressLine1("TYUI");
        assertEquals("TYUI", address.getAddressLine1());
        assertEquals(Address.Type.COMBINED_ELEMENTS, address.getType());
    }

    @Test
    @SuppressWarnings("deprecation")
    void setAddressLine2() {
        Address address = new Address();
        address.setAddressLine2("vbnm");
        assertEquals("vbnm", address.getAddressLine2());
        assertEquals(Address.Type.COMBINED_ELEMENTS, address.getType());
    }

    @Test
    void setStreet() {
        Address address = new Address();
        address.setStreet("DEFGH");
        assertEquals("DEFGH", address.getStreet());
        assertEquals(Address.Type.STRUCTURED, address.getType());
    }

    @Test
    void setHouseNo() {
        Address address = new Address();
        address.setHouseNo("fiekdd");
        assertEquals("fiekdd", address.getHouseNo());
        assertEquals(Address.Type.STRUCTURED, address.getType());
    }

    @Test
    void setPostalCode() {
        Address address = new Address();
        address.setPostalCode("BG19283");
        assertEquals("BG19283", address.getPostalCode());
        assertEquals(Address.Type.STRUCTURED, address.getType());
    }

    @Test
    void setTown() {
        Address address = new Address();
        address.setTown("IOPU-KU");
        assertEquals("IOPU-KU", address.getTown());
        assertEquals(Address.Type.STRUCTURED, address.getType());
    }

    @Test
    void setCountryCode() {
        Address address = new Address();
        address.setCountryCode("XY");
        assertEquals("XY", address.getCountryCode());
    }

    @Test
    @SuppressWarnings("deprecation")
    void conflictTest1() {
        Address address = new Address();
        address.setStreet("XY");
        address.setAddressLine1("abc");
        assertEquals(Address.Type.CONFLICTING, address.getType());
    }

    @Test
    @SuppressWarnings("deprecation")
    void conflictTest2() {
        Address address = new Address();
        address.setHouseNo("XY");
        address.setAddressLine1("abc");
        assertEquals(Address.Type.CONFLICTING, address.getType());
    }

    @Test
    @SuppressWarnings("deprecation")
    void conflictTest3() {
        Address address = new Address();
        address.setPostalCode("XY");
        address.setAddressLine2("abc");
        assertEquals(Address.Type.CONFLICTING, address.getType());
    }

    @Test
    @SuppressWarnings("deprecation")
    void conflictTest4() {
        Address address = new Address();
        address.setTown("XY");
        address.setAddressLine2("abc");
        assertEquals(Address.Type.CONFLICTING, address.getType());
    }

    @Test
    void equalObjectsStructured() {
        Address address1 = createStructuredAddress();
        Address address2 = createStructuredAddress();
        assertEquals(address1, address2);
    }

    @Test
    void equalObjectsCombined() {
        Address address1 = createCombinedElementAddress();
        Address address2 = createCombinedElementAddress();
        assertEquals(address1, address2);
    }

    @Test
    void hashObjectStructured() {
        Address address1 = createStructuredAddress();
        Address address2 = createStructuredAddress();
        assertEquals(address1.hashCode(), address2.hashCode());
    }

    @Test
    void hashObjectCombined() {
        Address address1 = createCombinedElementAddress();
        Address address2 = createCombinedElementAddress();
        assertEquals(address1.hashCode(), address2.hashCode());
    }

    @Test
    @SuppressWarnings("deprecation")
    void clearTestStructured() {
        Address address1 = createStructuredAddress();
        address1.clear();
        assertEquals(Address.Type.UNDETERMINED, address1.getType());
        assertNull(address1.getName());
        assertNull(address1.getAddressLine1());
        assertNull(address1.getAddressLine2());
        assertNull(address1.getStreet());
        assertNull(address1.getHouseNo());
        assertNull(address1.getPostalCode());
        assertNull(address1.getTown());
        assertNull(address1.getCountryCode());
    }

    @Test
    @SuppressWarnings("deprecation")
    void clearTestCombined() {
        Address address1 = createCombinedElementAddress();
        address1.clear();
        assertEquals(Address.Type.UNDETERMINED, address1.getType());
        assertNull(address1.getName());
        assertNull(address1.getAddressLine1());
        assertNull(address1.getAddressLine2());
        assertNull(address1.getStreet());
        assertNull(address1.getHouseNo());
        assertNull(address1.getPostalCode());
        assertNull(address1.getTown());
        assertNull(address1.getCountryCode());
    }

    @Test
    void toStringTest() {
        Address address = createStructuredAddress();
        String addressText = address.toString();
        assertEquals("Address{type=STRUCTURED, name='Cornelia Singer', addressLine1='null', addressLine2='null', street='Alte Landstrasse', houseNo='73', postalCode='3410', town='Hunzenschwil', countryCode='CH'}", addressText);
    }

    @Test
    @SuppressWarnings({"EqualsWithItself", "AssertBetweenInconvertibleTypes"})
    void testEqualsTrivial() {
        Address address = createCombinedElementAddress();
        assertEquals(address, address);
        assertNotEquals(null, address);
        assertNotEquals("xxx", address);
    }

    @Test
    void testEquals() {
        Address address1 = createCombinedElementAddress();
        Address address2 = createCombinedElementAddress();
        assertEquals(address1, address2);
        assertEquals(address2, address1);

        address2.setCountryCode("FR");
        assertNotEquals(address1, address2);
    }

    private Address createStructuredAddress() {
        Address address = new Address();
        address.setName("Cornelia Singer");
        address.setStreet("Alte Landstrasse");
        address.setHouseNo("73");
        address.setPostalCode("3410");
        address.setTown("Hunzenschwil");
        address.setCountryCode("CH");
        return address;
    }

    @SuppressWarnings("deprecation")
    private Address createCombinedElementAddress() {
        Address address = new Address();
        address.setName("Cornelia Singer");
        address.setAddressLine1("Alte Landstrasse 75");
        address.setAddressLine2("8702 Zollikon");
        address.setCountryCode("CH");
        return address;
    }
}
