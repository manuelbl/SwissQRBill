//
// Swiss QR Bill Generator
// Copyright (c) 2018 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//

package net.codecrete.qrbill.generatortest;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

import net.codecrete.qrbill.generator.Strings;

public class StringsTest {

    @Test
    public void nullTest() {
        assertTrue(Strings.isNullOrEmpty(null));
    }

    @Test
    public void emptyTest() {
        assertTrue(Strings.isNullOrEmpty(""));
    }

    @Test
    public void spaceTest() {
        assertTrue(Strings.isNullOrEmpty(" "));
    }

    @Test
    public void multipleSpacesTest() {
        assertTrue(Strings.isNullOrEmpty("   "));
    }

    @Test
    public void nonEmptyTest() {
        assertFalse(Strings.isNullOrEmpty("a"));
    }
}