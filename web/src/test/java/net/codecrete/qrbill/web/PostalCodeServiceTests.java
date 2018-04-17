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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import net.codecrete.qrbill.web.api.PostalCode;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PostalCodeServiceTests {

    @Autowired
    private TestRestTemplate restTemplate;

    private PostalCode[] suggestPostalCodes(String country, String substring) {
        if (country == null)
            country = "";
        // Somehow the request isn't pass through the normal processing pipeline. If we correctly escape
        // query parameters with characters outside the ASCII range, it doesn't work anymore.
        return restTemplate.getForObject(String.format("/postal-codes/suggest?country=%s&substring=%s",
                country, substring), PostalCode[].class);
    }

    @Test
    public void singleMatch() {
        PostalCode[] postalCodes = suggestPostalCodes("CH", "8302");
        assertEquals(1, postalCodes.length);
        assertEquals("8302", postalCodes[0].getPostalCode());
        assertEquals("Kloten", postalCodes[0].getTown());
    }

    @Test
    public void multipleNumericMatches() {
        PostalCode[] postalCodes = suggestPostalCodes("CH", "1475");
        assertEquals(3, postalCodes.length);
        for (PostalCode pc : postalCodes) {
            assertEquals("1475", pc.getPostalCode());
            assertNotNull(pc.getTown());
        }
    }

    @Test
    public void noMatchOutsideSwitzerland() {
        PostalCode[] postalCodes = suggestPostalCodes("FR", "123");
        assertEquals(0, postalCodes.length);
    }

    @Test
    public void getZurichSubstring() {
        PostalCode[] postalCodes = suggestPostalCodes(null, "Züri");
        assertEquals(20, postalCodes.length);
        for (PostalCode pc : postalCodes) {
            assertEquals("Zürich", pc.getTown());
            assertTrue("8000".compareTo(pc.getPostalCode()) <= 0);
            assertTrue("8099".compareTo(pc.getPostalCode()) >= 0);
        }
    }
}
