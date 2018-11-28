//
// Swiss QR Bill Generator
// Copyright (c) 2018 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//

package net.codecrete.qrbill.generatortest;

import net.codecrete.qrbill.generator.Strings;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit tests for {@link Strings#whiteSpaceRemoved(String)}
 */
@DisplayName("White space removal")
class RemoveWhitespaceTest {

    @Test
    void emptyString() {
        assertEquals("", Strings.whiteSpaceRemoved(""));
    }

    @Test
    void oneSpace() {
        assertEquals("", Strings.whiteSpaceRemoved(" "));
    }

    @Test
    void severalSpaces() {
        assertEquals("", Strings.whiteSpaceRemoved("   "));
    }

    @Test
    void noWhitespace() {
        assertEquals("abcd", Strings.whiteSpaceRemoved("abcd"));
    }

    @Test
    void leadingSpace() {
        assertEquals("fggh", Strings.whiteSpaceRemoved(" fggh"));
    }

    @Test
    void multipleLeadingSpaces() {
        assertEquals("jklm", Strings.whiteSpaceRemoved("   jklm"));
    }

    @Test
    void trailingSpace() {
        assertEquals("nppo", Strings.whiteSpaceRemoved("nppo "));
    }

    @Test
    void multipleTrailingSpaces() {
        assertEquals("qrs", Strings.whiteSpaceRemoved("qrs    "));
    }

    @Test
    void leadingAndTrailingSpaces() {
        assertEquals("guj", Strings.whiteSpaceRemoved(" guj    "));
    }

    @Test
    void singleSpace() {
        assertEquals("abde", Strings.whiteSpaceRemoved("ab de"));
    }

    @Test
    void multipleSpaces() {
        assertEquals("cdef", Strings.whiteSpaceRemoved("cd    ef"));
    }

    @Test
    void multipleGroupsOfSpaces() {
        assertEquals("ghijkl", Strings.whiteSpaceRemoved("gh ij  kl"));
    }

    @Test
    void leadingAndMultipleGroupsOfSpaces() {
        assertEquals("opqrst", Strings.whiteSpaceRemoved("  op   qr s t"));
    }

    @Test
    void leadingAndTrailingAndMultipleGroupsOfSpaces() {
        assertEquals("uvxyz", Strings.whiteSpaceRemoved(" uv x  y z  "));
    }

    @Test
    void copyAvoidance() {
        String value = "qwerty";
        assertSame(value, Strings.whiteSpaceRemoved(value));
    }

    @Test
    void nullString() {
        assertThrows(NullPointerException.class, () -> Strings.whiteSpaceRemoved(null));
    }
}