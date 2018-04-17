//
// Swiss QR Bill Generator
// Copyright (c) 2018 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
package net.codecrete.qrbill.web;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Locale;

import org.junit.BeforeClass;
import org.junit.Test;

import net.codecrete.qrbill.web.controller.PostalCodeData;

public class PostalCodeDataTests {

    private static PostalCodeData postalCodeData;

    @BeforeClass
    public static void setup() {
        postalCodeData = new PostalCodeData();
    }

    @Test
    public void getPostalCodeMatch() {
        List<PostalCodeData.PostalCode> result = postalCodeData.suggestPostalCodes("CH", "8302");
        assertEquals(1, result.size());
        assertEquals("8302", result.get(0).code);
        assertEquals("Kloten", result.get(0).town);
    }

    @Test
    public void getZurich() {
        List<PostalCodeData.PostalCode> result = postalCodeData.suggestPostalCodes("CH", "Zürich");
        assertEquals(20, result.size());
        String previousCode = "";
        for (PostalCodeData.PostalCode pc : result) {
            assertEquals("Zürich", pc.town);
            assertTrue("8000".compareTo(pc.code) <= 0);
            assertTrue("8099".compareTo(pc.code) >= 0);
            assertTrue(previousCode.compareTo(pc.code) < 0);
            previousCode = pc.code;
        }
    }

    @Test
    public void getZurichSubstring() {
        List<PostalCodeData.PostalCode> result = postalCodeData.suggestPostalCodes("CH", "Züri");
        assertEquals(20, result.size());
        String previousCode = "";
        for (PostalCodeData.PostalCode pc : result) {
            assertEquals("Zürich", pc.town);
            assertTrue("8000".compareTo(pc.code) <= 0);
            assertTrue("8099".compareTo(pc.code) >= 0);
            assertTrue(previousCode.compareTo(pc.code) < 0);
            previousCode = pc.code;
        }
    }

    @Test
    public void getDorfSubstring() {
        List<PostalCodeData.PostalCode> result = postalCodeData.suggestPostalCodes("CH", " dorf");
        assertEquals(20, result.size());
        String previousTown = "";
        for (PostalCodeData.PostalCode pc : result) {
            assertTrue(pc.town.toLowerCase(Locale.FRENCH).contains("dorf"));
            assertNotNull(pc.code);
            if (previousTown.startsWith("Dorf") && !pc.town.startsWith("Dorf"))
                previousTown = ""; // Reset between "Dorf...." towns and "...dorf..." towns
            assertTrue(String.format("%s alphabetically before %s", previousTown, pc.town),
                    previousTown.compareTo(pc.town) < 0);
            previousTown = pc.town;
        }
    }

    @Test
    public void getNumberSubstring() {
        List<PostalCodeData.PostalCode> result = postalCodeData.suggestPostalCodes("CH", "203");
        assertEquals(12, result.size());
        String previousCode = "";
        for (PostalCodeData.PostalCode pc : result) {
            assertNotNull(pc.town);
            assertTrue(pc.code.contains("203"));
            if (previousCode.startsWith("203") && !pc.code.startsWith("203"))
                previousCode = "";
            assertTrue(String.format("%s <= %s", previousCode, pc.code),
                    previousCode.compareTo(pc.code) <= 0);
            previousCode = pc.code;
        }
    }

    @Test
    public void getNumber880Start() {
        List<PostalCodeData.PostalCode> result = postalCodeData.suggestPostalCodes("", "880 ");
        assertEquals(8, result.size());
        String previousCode = "";
        for (PostalCodeData.PostalCode pc : result) {
            assertNotNull(pc.town);
            assertTrue(pc.code.startsWith("880"));
            assertTrue(String.format("%s <= %s", previousCode, pc.code),
                    previousCode.compareTo(pc.code) <= 0);
            previousCode = pc.code;
        }
    }

    @Test
    public void getRickenbachStart() {
        List<PostalCodeData.PostalCode> result = postalCodeData.suggestPostalCodes(null, " Rickenbach");
        assertEquals(8, result.size());
        String previousTown = "";
        for (PostalCodeData.PostalCode pc : result) {
            assertTrue(pc.town.startsWith("Rickenbach"));
            assertNotNull(pc.code);
            assertTrue(String.format("%s alphabetically before %s", previousTown, pc.town),
                    previousTown.compareTo(pc.town.toLowerCase(Locale.FRENCH)) <= 0);
            previousTown = pc.town.toLowerCase(Locale.FRENCH);
        }
    }

    @Test
    public void noMatch() {
        List<PostalCodeData.PostalCode> result = postalCodeData.suggestPostalCodes("CH", "abc");
        assertEquals(0, result.size());
    }

    @Test
    public void noMatchNumeric() {
        List<PostalCodeData.PostalCode> result = postalCodeData.suggestPostalCodes("CH", "0123");
        assertEquals(0, result.size());
    }

    @Test
    public void unsupportedCountry() {
        List<PostalCodeData.PostalCode> result = postalCodeData.suggestPostalCodes("DE", "12");
        assertEquals(0, result.size());
    }
}
