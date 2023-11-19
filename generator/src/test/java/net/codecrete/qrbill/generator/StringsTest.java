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
 * Unit tests for {@link Strings#isNullOrEmpty(String)}
 */
@DisplayName("Null or empty strings")
class StringsTest {

    @Test
    void nullValue() {
        assertTrue(Strings.isNullOrEmpty(null));
    }

    @Test
    void empty() {
        assertTrue(Strings.isNullOrEmpty(""));
    }

    @Test
    void space() {
        assertTrue(Strings.isNullOrEmpty(" "));
    }

    @Test
    void multipleSpaces() {
        assertTrue(Strings.isNullOrEmpty("   "));
    }

    @Test
    void nonEmpty() {
        assertFalse(Strings.isNullOrEmpty("a"));
    }

    @Test
    void noSpaces_noChange() {
        assertEquals("abc", Strings.spacesCleaned("abc"));
    }

    @Test
    void leadingSpaces_removed() {
        assertEquals("abc", Strings.spacesCleaned("  abc"));
    }

    @Test
    void trailingSpaces_removed() {
        assertEquals("abc", Strings.spacesCleaned("abc  "));
    }

    @Test
    void singleSpaces_noChange() {
        assertEquals("a b c", Strings.spacesCleaned("a b c"));
    }

    @Test
    void multipleSpaces_merged() {
        assertEquals("a b c", Strings.spacesCleaned("a  b    c"));
    }


    @Test
    void manySpaces_cleaned() {
        assertEquals("a b c", Strings.spacesCleaned("  a b   c "));
    }
}