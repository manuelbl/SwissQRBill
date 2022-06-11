//
// Swiss QR Bill Generator
// Copyright (c) 2017 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
package net.codecrete.qrbill.generatortest;

import net.codecrete.qrbill.canvas.FontMetrics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for {@link FontMetrics} class
 */
@DisplayName("Font metrics / line wrapping")
class FontMetricsTest {

    private FontMetrics fontMetrics;

    @BeforeEach
    void init() {
        fontMetrics = new FontMetrics("Helvetica");
    }

    @ParameterizedTest
    @ValueSource(strings = { "abc", "abcdefghij", "abcdef ghij", " abcdefghij", "abcdefghij ", "" })
    void invariantTest() {
        String[] lines = fontMetrics.splitLines("abc", 50, 10);
        assertEquals(1, lines.length);
        assertEquals("abc", lines[0]);
    }

    @ParameterizedTest
    @ValueSource(strings = { " ", "                           ", "éà£$\uD83D\uDE03"})
    void input_hasSingleLine(String input) {
        String[] lines = fontMetrics.splitLines(input, 50, 10);
        assertEquals(1, lines.length);
        assertEquals(input.trim(), lines[0]);
    }

    @Test
    void twoLinesFromSpace() {
        String[] lines = fontMetrics.splitLines("abcde fghijk", 50, 10);
        assertEquals(2, lines.length);
        assertEquals("abcde", lines[0]);
        assertEquals("fghijk", lines[1]);
    }

    @Test
    void twoLinesFromNewLine() {
        String[] lines = fontMetrics.splitLines("abcde\nfghijk", 50, 10);
        assertEquals(2, lines.length);
        assertEquals("abcde", lines[0]);
        assertEquals("fghijk", lines[1]);
    }

    @Test
    void twoLinesWithTrailingNewline() {
        String[] lines = fontMetrics.splitLines("abcde\n", 50, 10);
        assertEquals(2, lines.length);
        assertEquals("abcde", lines[0]);
        assertEquals("", lines[1]);
    }

    @Test
    void singleNewline() {
        String[] lines = fontMetrics.splitLines("\n", 50, 10);
        assertEquals(2, lines.length);
        assertEquals("", lines[0]);
        assertEquals("", lines[1]);
    }

    @Test
    void spaceAndNewline() {
        String[] lines = fontMetrics.splitLines("  \n ", 50, 10);
        assertEquals(2, lines.length);
        assertEquals("", lines[0]);
        assertEquals("", lines[1]);
    }

    @Test
    void trailingAndLeadingSpaceAndNewline() {
        String[] lines = fontMetrics.splitLines(" abc \n", 50, 10);
        assertEquals(2, lines.length);
        assertEquals("abc", lines[0]);
        assertEquals("", lines[1]);
    }

    @Test
    void forcedWordBreak() {
        String[] lines = fontMetrics.splitLines("abcde", 2, 10);
        assertEquals(5, lines.length);
        assertEquals("a", lines[0]);
        assertEquals("b", lines[1]);
        assertEquals("c", lines[2]);
        assertEquals("d", lines[3]);
        assertEquals("e", lines[4]);
    }

    @Test
    void forcedWordBreakWithSpaces() {
        String[] lines = fontMetrics.splitLines("  abcde  ", 2, 10);
        assertEquals(5, lines.length);
        assertEquals("a", lines[0]);
        assertEquals("b", lines[1]);
        assertEquals("c", lines[2]);
        assertEquals("d", lines[3]);
        assertEquals("e", lines[4]);
    }

    @Test
    void newlines_hasWidth0() {
        assertEquals(0, fontMetrics.getTextWidth("\n", 10, false));
        assertEquals(0, fontMetrics.getTextWidth("\r", 10, false));
    }
}
