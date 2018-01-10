//
// Swiss QR Bill Generator
// Copyright (c) 2017 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
package net.codecrete.qrbill.web;

import net.codecrete.qrbill.web.api.QrBill;
import net.codecrete.qrbill.web.api.ValidationMessage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BillGenerationTests {

    @Autowired
    private TestRestTemplate restTemplate = new TestRestTemplate();

    @Test
    public void generateSVGTest() {

        QrBill bill = SampleData.createBill1();
        byte[] response = restTemplate.postForObject("/bill/svg/a6-landscape", bill, byte[].class);

        assertNotNull(response);
        assertTrue(response.length > 1000);

        String text = new String(response, StandardCharsets.UTF_8);
        assertTrue(text.startsWith("<?xml"));
        assertTrue(text.indexOf("<svg") > 0);
        assertTrue(text.indexOf("Meierhans AG") > 0);
    }

    @Test
    public void generatePDFTest() {

        QrBill bill = SampleData.createBill1();
        byte[] response = restTemplate.postForObject("/bill/pdf/a6-landscape", bill, byte[].class);

        assertNotNull(response);
        assertTrue(response.length > 1000);

        String text = new String(response, 0, 8, StandardCharsets.UTF_8);
        assertEquals("%PDF-1.4", text);
    }

    @Test
    public void truncationTest() {
        QrBill bill = SampleData.createBill1();
        bill.getCreditor().setTown("city56789012345678901234567890123456");

        byte[] response = restTemplate.postForObject("/bill/svg/a5Landscape", bill, byte[].class);

        assertNotNull(response);
        assertTrue(response.length > 1000);

        String text = new String(response, StandardCharsets.UTF_8);
        assertTrue(text.startsWith("<?xml"));
        assertTrue(text.indexOf("<svg") > 0);
        assertTrue(text.indexOf("font-size=\"10\">city5678901234567890123456789012345</text>") > 0);
    }

    @Test
    public void validationTest() {
        QrBill bill = SampleData.createBill1();
        bill.getCreditor().setTown(null);

        ValidationMessage[] response = restTemplate.postForObject("/bill/svg/a4Portrait", bill, ValidationMessage[].class);

        assertNotNull(response);
        assertEquals(1, response.length);
        assertEquals(ValidationMessage.Type.Error, response[0].getType());
        assertEquals("creditor.town", response[0].getField());
        assertEquals("field_is_mandatory", response[0].getMessageKey());
    }
}
