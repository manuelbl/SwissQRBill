//
// Swiss QR Bill Generator
// Copyright (c) 2018 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//

package net.codecrete.qrbill.generatortest;

import net.codecrete.qrbill.generator.BillFormat;
import net.codecrete.qrbill.generator.GraphicsFormat;
import net.codecrete.qrbill.generator.Language;
import net.codecrete.qrbill.generator.OutputSize;
import net.codecrete.qrbill.generator.SeparatorType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class BillFormatTest {

    @Test
    void defaultValueTest() {
        BillFormat format = new BillFormat();
        assertEquals(Language.EN, format.getLanguage());
        assertEquals(GraphicsFormat.SVG, format.getGraphicsFormat());
        assertEquals(OutputSize.QR_BILL_ONLY, format.getOutputSize());
        assertEquals("Helvetica,Arial,\"Liberation Sans\"", format.getFontFamily());
        assertEquals(SeparatorType.DASHED_LINE_WITH_SCISSORS, format.getSeparatorType());
        assertEquals(144, format.getResolution());
        assertEquals(5.0, format.getMarginLeft());
        assertEquals(5.0, format.getMarginRight());
    }

    @Test
    void hashCodeTest() {
        BillFormat format1 = new BillFormat();
        BillFormat format2 = new BillFormat();
        assertEquals(format1.hashCode(), format2.hashCode());
    }

    @Test
    void toStringTest() {
        BillFormat format = new BillFormat();
        String text = format.toString();
        assertEquals("BillFormat{outputSize=QR_BILL_ONLY, language=EN, separatorType=DASHED_LINE_WITH_SCISSORS, fontFamily='Helvetica,Arial,\"Liberation Sans\"', graphicsFormat=SVG, resolution=144, marginLeft=5.0, marginRight=5.0, localCountryCode='CH'}", text);
    }

    @Test
    void testEqualsTrivial() {
        BillFormat format = new BillFormat();
        assertEquals(format, format);
        assertNotEquals(null, format);
        assertNotEquals("xxx", format);
    }

    @Test
    void testEquals() {
        BillFormat format1 = new BillFormat();
        BillFormat format2 = new BillFormat();
        assertEquals(format1, format2);
        assertEquals(format1, format2);

        format2.setOutputSize(OutputSize.A4_PORTRAIT_SHEET);
        assertNotEquals(format1, format2);
    }
}
