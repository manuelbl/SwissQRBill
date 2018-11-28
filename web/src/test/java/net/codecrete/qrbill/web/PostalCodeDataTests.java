//
// Swiss QR Bill Generator
// Copyright (c) 2018 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
package net.codecrete.qrbill.web;

import net.codecrete.qrbill.web.controller.PostalCodeData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit test for the {@link PostalCodeData} class
 */
@DisplayName("Postal code lookup")
class PostalCodeDataTests {

    private static PostalCodeData postalCodeData;

    @BeforeAll
    static void setup() {
        postalCodeData = new PostalCodeData();
    }

    @Test
    void singleMatch() {
        List<PostalCodeData.PostalCode> result = postalCodeData.suggestPostalCodes("CH", "8302");
        assertEquals(1, result.size());
        assertEquals("8302", result.get(0).code);
        assertEquals("Kloten", result.get(0).town);
    }

    @Test
    void zurichFullName() {
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
    void zurichSubstring() {
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
    void dorfSubstring() {
        List<PostalCodeData.PostalCode> result = postalCodeData.suggestPostalCodes("CH", " dorf");
        assertEquals(20, result.size());
        String previousTown = "";
        for (PostalCodeData.PostalCode pc : result) {
            assertTrue(pc.town.toLowerCase(Locale.FRENCH).contains("dorf"));
            assertNotNull(pc.code);
            if (previousTown.startsWith("Dorf") && !pc.town.startsWith("Dorf"))
                previousTown = ""; // Reset between "Dorf...." towns and "...dorf..." towns
            assertTrue(previousTown.compareTo(pc.town) < 0,
                    String.format("%s alphabetically before %s", previousTown, pc.town));
            previousTown = pc.town;
        }
    }

    @Test
    void numericSubstring() {
        List<PostalCodeData.PostalCode> result = postalCodeData.suggestPostalCodes("CH", "203");
        assertEquals(12, result.size());
        String previousCode = "";
        for (PostalCodeData.PostalCode pc : result) {
            assertNotNull(pc.town);
            assertTrue(pc.code.contains("203"));
            if (previousCode.startsWith("203") && !pc.code.startsWith("203"))
                previousCode = "";
            assertTrue(previousCode.compareTo(pc.code) <= 0, String.format("%s <= %s", previousCode, pc.code));
            previousCode = pc.code;
        }
    }

    @Test
    void startsWith880() {
        List<PostalCodeData.PostalCode> result = postalCodeData.suggestPostalCodes("", "880 ");
        assertEquals(8, result.size());
        String previousCode = "";
        for (PostalCodeData.PostalCode pc : result) {
            assertNotNull(pc.town);
            assertTrue(pc.code.startsWith("880"));
            assertTrue(previousCode.compareTo(pc.code) <= 0, String.format("%s <= %s", previousCode, pc.code));
            previousCode = pc.code;
        }
    }

    @Test
    void startsWithRickenbach() {
        List<PostalCodeData.PostalCode> result = postalCodeData.suggestPostalCodes(null, " Rickenbach");
        assertEquals(8, result.size());
        String previousTown = "";
        for (PostalCodeData.PostalCode pc : result) {
            assertTrue(pc.town.startsWith("Rickenbach"));
            assertNotNull(pc.code);
            assertTrue(previousTown.compareTo(pc.town.toLowerCase(Locale.FRENCH)) <= 0,
                    String.format("%s alphabetically before %s", previousTown, pc.town));
            previousTown = pc.town.toLowerCase(Locale.FRENCH);
        }
    }

    @Test
    void noMatch() {
        List<PostalCodeData.PostalCode> result = postalCodeData.suggestPostalCodes("CH", "abc");
        assertEquals(0, result.size());
    }

    @Test
    void noMatchNumeric() {
        List<PostalCodeData.PostalCode> result = postalCodeData.suggestPostalCodes("CH", "0123");
        assertEquals(0, result.size());
    }

    @Test
    void unsupportedCountry() {
        List<PostalCodeData.PostalCode> result = postalCodeData.suggestPostalCodes("DE", "12");
        assertEquals(0, result.size());
    }
}
