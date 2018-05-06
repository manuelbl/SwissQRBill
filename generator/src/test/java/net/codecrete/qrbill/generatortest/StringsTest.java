//
// Swiss QR Bill Generator
// Copyright (c) 2018 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//

package net.codecrete.qrbill.generatortest;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.codecrete.qrbill.generator.Strings;

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
}