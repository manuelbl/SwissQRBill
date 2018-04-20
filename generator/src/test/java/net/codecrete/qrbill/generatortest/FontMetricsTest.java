//
// Swiss QR Bill Generator
// Copyright (c) 2017 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
package net.codecrete.qrbill.generatortest;

import org.junit.Assert;
import org.junit.Test;

import net.codecrete.qrbill.canvas.FontMetrics;

public class FontMetricsTest {

    @Test
    public void oneLiners() {
        String[] lines = FontMetrics.splitLines("abc", 50, 10);
        Assert.assertEquals(1, lines.length);
        Assert.assertEquals("abc", lines[0]);

        lines = FontMetrics.splitLines("abcdefghij", 50, 10);
        Assert.assertEquals(1, lines.length);
        Assert.assertEquals("abcdefghij", lines[0]);

        lines = FontMetrics.splitLines("abcdef ghij", 50, 10);
        Assert.assertEquals(1, lines.length);
        Assert.assertEquals("abcdef ghij", lines[0]);
    }

    @Test
    public void trickyOneLiners() {
        String[] lines = FontMetrics.splitLines(" abcdefghij", 50, 10);
        Assert.assertEquals(1, lines.length);
        Assert.assertEquals("abcdefghij", lines[0]);

        lines = FontMetrics.splitLines("abcdefghij ", 50, 10);
        Assert.assertEquals(1, lines.length);
        Assert.assertEquals("abcdefghij", lines[0]);

        lines = FontMetrics.splitLines("", 50, 10);
        Assert.assertEquals(1, lines.length);
        Assert.assertEquals("", lines[0]);

        lines = FontMetrics.splitLines(" ", 50, 10);
        Assert.assertEquals(1, lines.length);
        Assert.assertEquals("", lines[0]);

        lines = FontMetrics.splitLines("                           ", 50, 10);
        Assert.assertEquals(1, lines.length);
        Assert.assertEquals("", lines[0]);

        lines = FontMetrics.splitLines("éà£$\uD83D\uDE03", 50, 10);
        Assert.assertEquals(1, lines.length);
        Assert.assertEquals("éà£$\uD83D\uDE03", lines[0]);
    }

    @Test
    public void twoLiners() {
        String[] lines = FontMetrics.splitLines("abcde fghijk", 50, 10);
        Assert.assertEquals(2, lines.length);
        Assert.assertEquals("abcde", lines[0]);
        Assert.assertEquals("fghijk", lines[1]);

        lines = FontMetrics.splitLines("abcde\nfghijk", 50, 10);
        Assert.assertEquals(2, lines.length);
        Assert.assertEquals("abcde", lines[0]);
        Assert.assertEquals("fghijk", lines[1]);

        lines = FontMetrics.splitLines("abcde\n", 50, 10);
        Assert.assertEquals(2, lines.length);
        Assert.assertEquals("abcde", lines[0]);
        Assert.assertEquals("", lines[1]);
    }

    @Test
    public void trickyTwoliners() {
        String[] lines = FontMetrics.splitLines("\n", 50, 10);
        Assert.assertEquals(2, lines.length);
        Assert.assertEquals("", lines[0]);
        Assert.assertEquals("", lines[1]);

        lines = FontMetrics.splitLines("  \n ", 50, 10);
        Assert.assertEquals(2, lines.length);
        Assert.assertEquals("", lines[0]);
        Assert.assertEquals("", lines[1]);

        lines = FontMetrics.splitLines(" abc \n", 50, 10);
        Assert.assertEquals(2, lines.length);
        Assert.assertEquals("abc", lines[0]);
        Assert.assertEquals("", lines[1]);
    }

    @Test
    public void trickyOnes() {
        String[] lines = FontMetrics.splitLines("abcde", 2, 10);
        Assert.assertEquals(5, lines.length);
        Assert.assertEquals("a", lines[0]);
        Assert.assertEquals("b", lines[1]);
        Assert.assertEquals("c", lines[2]);
        Assert.assertEquals("d", lines[3]);
        Assert.assertEquals("e", lines[4]);

        lines = FontMetrics.splitLines("  abcde  ", 2, 10);
        Assert.assertEquals(5, lines.length);
        Assert.assertEquals("a", lines[0]);
        Assert.assertEquals("b", lines[1]);
        Assert.assertEquals("c", lines[2]);
        Assert.assertEquals("d", lines[3]);
        Assert.assertEquals("e", lines[4]);
    }
}
