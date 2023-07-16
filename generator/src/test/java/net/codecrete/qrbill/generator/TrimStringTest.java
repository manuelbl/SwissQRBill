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
 * Unit tests for {@link Strings#trimmed(String)}
 */
@DisplayName("String trimming")
class TrimStringTest {

    @Test
    void nullString() {
        assertNull(Strings.trimmed(null));
    }

    @Test
    void emptyString() {
        assertNull(Strings.trimmed(""));
    }

    @Test
    void oneSpace() {
        assertNull(Strings.trimmed(" "));
    }

    @Test
    void severalSpaces() {
        assertNull(Strings.trimmed("   "));
    }

    @Test
    void noWhitespace() {
        assertEquals("ghj", Strings.trimmed("ghj"));
    }

    @Test
    void leadingSpace() {
        assertEquals("klm", Strings.trimmed(" klm"));
    }

    @Test
    void multipleLeadingSpaces() {
        assertEquals("mnop", Strings.trimmed("   mnop"));
    }

    @Test
    void trailingSpace() {
        assertEquals("pqrs", Strings.trimmed("pqrs "));
    }

    @Test
    void multipleTrailingSpaces() {
        assertEquals("rstu", Strings.trimmed("rstu    "));
    }

    @Test
    void leadingAndTrailingSpaces() {
        assertEquals("xyz", Strings.trimmed(" xyz    "));
    }

    @Test
    void internalSpace() {
        assertEquals("cd ef", Strings.trimmed("cd ef"));
    }

    @Test
    void multipleInternalSpaces() {
        assertEquals("fg  hi", Strings.trimmed("fg  hi"));
    }

    @Test
    void copyAvoidanceWithoutSpaces() {
        String value = "cvbn";
        assertSame(value, Strings.trimmed(value));
    }

    @Test
    void copyAvoidanceWithSpaces() {
        String value = "i o p";
        assertSame(value, Strings.trimmed(value));
    }
}