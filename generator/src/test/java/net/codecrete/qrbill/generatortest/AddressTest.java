//
// Swiss QR Bill Generator
// Copyright (c) 2018 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//

package net.codecrete.qrbill.generatortest;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.codecrete.qrbill.generator.Address;

/**
 * Unit test for class {@link Address}
 */
@DisplayName("Address class")
class AddressTest {

    @Test
    void setName() {
        Address address = new Address();
        address.setName("ABC");
        assertEquals("ABC", address.getName());
    }

    @Test
    void setStreet() {
        Address address = new Address();
        address.setStreet("DEFGH");
        assertEquals("DEFGH", address.getStreet());
    }

    @Test
    void setHouseNo() {
        Address address = new Address();
        address.setHouseNo("fiekdd");
        assertEquals("fiekdd", address.getHouseNo());
    }

    @Test
    void setPostalCode() {
        Address address = new Address();
        address.setPostalCode("BG19283");
        assertEquals("BG19283", address.getPostalCode());
    }

    @Test
    void setTown() {
        Address address = new Address();
        address.setTown("IOPU-KU");
        assertEquals("IOPU-KU", address.getTown());
    }

    @Test
    void setCountryCode() {
        Address address = new Address();
        address.setCountryCode("XY");
        assertEquals("XY", address.getCountryCode());
    }

    @Test
    void equalObjects() {
        Address address1 = createAddress();
        Address address2 = createAddress();
        assertEquals(address1, address2);
    }

    @Test
    void hashObject() {
        Address address1 = createAddress();
        Address address2 = createAddress();
        assertEquals(address1.hashCode(), address2.hashCode());
    }

    private Address createAddress() {
        Address address = new Address();
        address.setName("Cornelia Singer");
        address.setStreet("Alte Landstrasse");
        address.setHouseNo("73");
        address.setPostalCode("3410");
        address.setTown("Hunzenschwil");
        address.setCountryCode("CH");
        return address;
    }
}