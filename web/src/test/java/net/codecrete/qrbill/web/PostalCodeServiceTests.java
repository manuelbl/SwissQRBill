//
// Swiss QR Bill Generator
// Copyright (c) 2018 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
package net.codecrete.qrbill.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import net.codecrete.qrbill.web.api.PostalCode;


/**
 * Unit test for postal code lookup API
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayName("Postal code lookup service")
class PostalCodeServiceTests {

    private final TestRestTemplate restTemplate;

    PostalCodeServiceTests(@Autowired TestRestTemplate template) {
        restTemplate = template;
    }

    private PostalCode[] suggestPostalCodes(String country, String substring) {
        if (country == null)
            country = "";
        // Somehow the request isn't pass through the normal processing pipeline. If we correctly escape
        // query parameters with characters outside the ASCII range, it doesn't work anymore.
        return restTemplate.getForObject(String.format("/postal-codes/suggest?country=%s&substring=%s",
                country, substring), PostalCode[].class);
    }

    @Test
    void singleMatch() {
        PostalCode[] postalCodes = suggestPostalCodes("CH", "8302");
        assertEquals(1, postalCodes.length);
        assertEquals("8302", postalCodes[0].getPostalCode());
        assertEquals("Kloten", postalCodes[0].getTown());
    }

    @Test
    void multipleNumericMatches() {
        PostalCode[] postalCodes = suggestPostalCodes("CH", "1475");
        assertEquals(3, postalCodes.length);
        for (PostalCode pc : postalCodes) {
            assertEquals("1475", pc.getPostalCode());
            assertNotNull(pc.getTown());
        }
    }

    @Test
    void noMatchOutsideSwitzerland() {
        PostalCode[] postalCodes = suggestPostalCodes("FR", "123");
        assertEquals(0, postalCodes.length);
    }

    @Test
    void getZurichSubstring() {
        PostalCode[] postalCodes = suggestPostalCodes(null, "Züri");
        assertEquals(20, postalCodes.length);
        for (PostalCode pc : postalCodes) {
            assertEquals("Zürich", pc.getTown());
            assertTrue("8000".compareTo(pc.getPostalCode()) <= 0);
            assertTrue("8099".compareTo(pc.getPostalCode()) >= 0);
        }
    }
}
