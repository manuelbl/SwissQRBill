//
// Swiss QR Bill Generator
// Copyright (c) 2018 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//

package net.codecrete.qrbill.generatortest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import net.codecrete.qrbill.generator.Strings;


class TrimStringTest {

    @Test
    void nullStringTest() {
        assertEquals(null, Strings.trimmed(null));
    }

    @Test
    void emptyStringTest() {
        assertEquals(null, Strings.trimmed(""));
    }

    @Test
    void oneSpaceTest() {
        assertEquals(null, Strings.trimmed(" "));
    }

    @Test
    void severalSpacesTest() {
        assertEquals(null, Strings.trimmed("   "));
    }

    @Test
    void noWhitespaceTest() {
        assertEquals("ghj", Strings.trimmed("ghj"));
    }

    @Test
    void leadingSpaceTest() {
        assertEquals("klm", Strings.trimmed(" klm"));
    }

    @Test
    void multipleLeadingSpacesTest() {
        assertEquals("mnop", Strings.trimmed("   mnop"));
    }

    @Test
    void trailingSpaceTest() {
        assertEquals("pqrs", Strings.trimmed("pqrs "));
    }

    @Test
    void multipleTrailingSpacesTest() {
        assertEquals("rstu", Strings.trimmed("rstu    "));
    }

    @Test
    void leadingAndTrailingSpacesTest() {
        assertEquals("xyz", Strings.trimmed(" xyz    "));
    }

    @Test
    void internalSpaceTest() {
        assertEquals("cd ef", Strings.trimmed("cd ef"));
    }

    @Test
    void multipleInternalSpacesTest() {
        assertEquals("fg  hi", Strings.trimmed("fg  hi"));
    }

    @Test
    void copyAvoidanceWithoutSpacesTest() {
        String value = "cvbn";
        assertTrue(value == Strings.trimmed(value));
    }

    @Test
    void copyAvoidanceWithSpacesTest() {
        String value = "i o p";
        assertTrue(value == Strings.trimmed(value));
    }
}