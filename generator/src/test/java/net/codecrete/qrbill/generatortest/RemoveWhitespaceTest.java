//
// Swiss QR Bill Generator
// Copyright (c) 2018 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//

package net.codecrete.qrbill.generatortest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import net.codecrete.qrbill.generator.Strings;


class RemoveWhitespaceTest {

    @Test
    void emptyStringTest() {
        assertEquals("", Strings.whiteSpaceRemoved(""));
    }

    @Test
    void oneSpaceTest() {
        assertEquals("", Strings.whiteSpaceRemoved(" "));
    }

    @Test
    void severalSpacesTest() {
        assertEquals("", Strings.whiteSpaceRemoved("   "));
    }

    @Test
    void noWhitespaceTest() {
        assertEquals("abcd", Strings.whiteSpaceRemoved("abcd"));
    }

    @Test
    void leadingSpaceTest() {
        assertEquals("fggh", Strings.whiteSpaceRemoved(" fggh"));
    }

    @Test
    void multipleLeadingSpacesTest() {
        assertEquals("jklm", Strings.whiteSpaceRemoved("   jklm"));
    }

    @Test
    void trailingSpaceTest() {
        assertEquals("nppo", Strings.whiteSpaceRemoved("nppo "));
    }

    @Test
    void multipleTrailingSpacesTest() {
        assertEquals("qrs", Strings.whiteSpaceRemoved("qrs    "));
    }

    @Test
    void leadingAndTrailingSpacesTest() {
        assertEquals("guj", Strings.whiteSpaceRemoved(" guj    "));
    }

    @Test
    void singleSpaceTest() {
        assertEquals("abde", Strings.whiteSpaceRemoved("ab de"));
    }

    @Test
    void multipleSpacesTest() {
        assertEquals("cdef", Strings.whiteSpaceRemoved("cd    ef"));
    }

    @Test
    void multipleGroupsOfSpacesTest() {
        assertEquals("ghijkl", Strings.whiteSpaceRemoved("gh ij  kl"));
    }

    @Test
    void leadingAndMultipleGroupsOfSpacesTest() {
        assertEquals("opqrst", Strings.whiteSpaceRemoved("  op   qr s t"));
    }

    @Test
    void leadingAndTrailingAndMultipleGroupsOfSpacesTest() {
        assertEquals("uvxyz", Strings.whiteSpaceRemoved(" uv x  y z  "));
    }

    @Test
    void copyAvoidanceTest() {
        String value = "qwerty";
        assertTrue(value == Strings.whiteSpaceRemoved(value));
    }

    @Test
    void nullStringTest() {
        assertThrows(NullPointerException.class, () -> {
            Strings.whiteSpaceRemoved(null);
        });
    }
}