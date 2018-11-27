//
// Swiss QR Bill Generator
// Copyright (c) 2018 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//

package net.codecrete.qrbill.generatortest;

import net.codecrete.qrbill.generator.AlternativeScheme;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class AlternativeSchemeTest {

    @Test
    void defaultConstructorTest() {
        AlternativeScheme scheme = new AlternativeScheme();
        assertNull(scheme.getName());
        assertNull(scheme.getInstruction());
    }

    @Test
    void constructorTest() {
        AlternativeScheme scheme = new AlternativeScheme("Paymit", "PM,12341234,1241234");
        assertEquals("Paymit", scheme.getName());
        assertEquals("PM,12341234,1241234", scheme.getInstruction());
    }

    @Test
    void toStringTest() {
        AlternativeScheme scheme = new AlternativeScheme("Paymit", "PM,12341234,1241234");
        String text = scheme.toString();
        assertEquals("AlternativeScheme{name='Paymit', instruction='PM,12341234,1241234'}", text);
    }

    @Test
    void testEqualsTrivial() {
        AlternativeScheme scheme = new AlternativeScheme("Paymit", "PM,12341234,1241234");
        assertEquals(scheme, scheme);
        assertNotEquals(scheme, null);
        assertNotEquals("xxx", scheme);
    }

    @Test
    void testEquals() {
        AlternativeScheme scheme1 = new AlternativeScheme("Paymit", "PM,12341234,1241234");
        AlternativeScheme scheme2 = new AlternativeScheme("Paymit", "PM,12341234,1241234");
        assertEquals(scheme1, scheme2);
        assertEquals(scheme1, scheme2);

        scheme2.setName("TWINT");
        assertNotEquals(scheme1, scheme2);
    }

    @Test
    void testHashCode() {
        AlternativeScheme scheme = new AlternativeScheme("Paymit", "PM,12341234,1241234");
        assertEquals(1266162573, scheme.hashCode());
    }

}
