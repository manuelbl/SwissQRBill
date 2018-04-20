//
// Swiss QR Bill Generator
// Copyright (c) 2018 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//

package net.codecrete.qrbill.generatortest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import net.codecrete.qrbill.generator.Strings;

public class StringsTest {

    @Test
    public void trimEmpty() {
        assertEquals(null, Strings.trimmed(null));
        assertEquals(null, Strings.trimmed(""));
        assertEquals(null, Strings.trimmed(" "));
        assertEquals(null, Strings.trimmed("  "));
    }

    @Test
    public void trimNonEmpty() {
        assertEquals("abc", Strings.trimmed(" abc"));
        assertEquals("abc", Strings.trimmed("abc "));
        assertEquals("abc", Strings.trimmed(" abc "));
        assertEquals("abc", Strings.trimmed("  abc    "));
        assertEquals("ab c", Strings.trimmed("ab c"));
        assertEquals("abc", Strings.trimmed("abc"));
    }

    @Test
    public void removeWhitepsace() {
        assertEquals("abc", Strings.whiteSpaceRemoved("abc"));
        assertEquals("abc", Strings.whiteSpaceRemoved(" abc "));
        assertEquals("abc", Strings.whiteSpaceRemoved("abc "));
        assertEquals("abc", Strings.whiteSpaceRemoved(" abc "));
        assertEquals("abc", Strings.whiteSpaceRemoved("abc"));
        assertEquals("abc", Strings.whiteSpaceRemoved("abc"));
        assertEquals("abc", Strings.whiteSpaceRemoved("a   b   c"));
        assertEquals("abc", Strings.whiteSpaceRemoved("   a   b   c   "));
    }

    @Test
    public void nullOrEmpty() {
        assertTrue(Strings.isNullOrEmpty(null));
        assertTrue(Strings.isNullOrEmpty(""));
        assertTrue(Strings.isNullOrEmpty("  "));
        assertFalse(Strings.isNullOrEmpty("a"));
    }
}